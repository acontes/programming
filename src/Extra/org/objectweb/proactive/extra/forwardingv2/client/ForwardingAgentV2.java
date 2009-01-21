package org.objectweb.proactive.extra.forwardingv2.client;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.util.Sleeper;
import org.objectweb.proactive.core.util.TimeoutAccounter;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DataReplyMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DataRequestMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ErrorMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationReplyMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationRequestMessage;


/**
 * A local message routing agent in charge of sending and receiving the messages
 * 
 * It will contact the router as soon as created and will try to maintain the
 * connection open (eg. if the connection is closed then it will be reopened).
 */
public class ForwardingAgentV2 implements AgentV2Internal {

    final private InetAddress routerAddr;
    final private int routerPort;

    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_CLIENT);

    /** Local Mailboxes used to block the threads waiting for a response. **/
    private final ConcurrentHashMap<Long, LocalMailBox> boxes;

    /** Request ID Generator **/
    private final AtomicLong requestIDGenerator;

    /** Local AgentID, set after initialization. **/
    private AgentID agentID = null;

    private Tunnel t = null;
    final private List<Tunnel> failedTunnels;

    /** Every data message will be handled by this object */
    final private MessageHandler messageHandler;

    /** The list of Valve that will process each message */
    final private List<Valve> valves;

    public ForwardingAgentV2(InetAddress routerAddr, int routerPort,
            Class<? extends MessageHandler> messageHandlerClass) throws ProActiveException {
        this(routerAddr, routerPort, messageHandlerClass, new ArrayList<Valve>());
    }

    public ForwardingAgentV2(InetAddress routerAddr, int routerPort,
            Class<? extends MessageHandler> messageHandlerClass, List<Valve> valves)
            throws ProActiveException {
        this.routerAddr = routerAddr;
        this.routerPort = routerPort;
        this.valves = valves;
        this.boxes = new ConcurrentHashMap<Long, LocalMailBox>();
        this.requestIDGenerator = new AtomicLong(0);
        this.failedTunnels = new LinkedList<Tunnel>();

        try {
            Constructor<? extends MessageHandler> mhConstructor;
            mhConstructor = messageHandlerClass.getConstructor(AgentV2Internal.class);
            this.messageHandler = mhConstructor.newInstance(this);
        } catch (Exception e) {
            throw new ProActiveException("Message routing agent failed to create the message handler", e);
        }

        // Avoid lazy connection to spot invalid host/port ASAP
        try {
            this.t = reconnectToRouter();
        } catch (IOException e) {
            throw new ProActiveException("Failed to create the tunnel to " + routerAddr + ":" + routerPort, e);
        }

        // Start the message receiver even if connection failed
        // Message reader will try to open the tunnel later
        Thread mrThread = new Thread(new MessageReader(this));
        mrThread.setDaemon(true);
        mrThread.setName("Message routing: message reader for agent " + this.agentID);
        mrThread.start();
    }

    /**
     * Returns a new tunnel to the router
     * 
     * The tunnel instance field is updated. The agentID instance field is set
     * on the first call
     * 
     * @return the new tunnel
     * @throws IOException
     *             if not able to contact the router or if an erroneous response
     *             if received
     */
    synchronized private Tunnel reconnectToRouter() throws IOException {
        Tunnel tunnel = new Tunnel(this.routerAddr, this.routerPort);

        // if call for the first time then agentID is null
        RegistrationRequestMessage reg = new RegistrationRequestMessage(this.agentID);
        tunnel.write(reg.toByteArray());

        // Waiting the router response
        byte[] reply = tunnel.readMessage();
        Message replyMsg = Message.constructMessage(reply, 0);

        if (!(replyMsg instanceof RegistrationReplyMessage)) {
            throw new IOException("Invalid router response: expected a " +
                RegistrationReplyMessage.class.getName() + " message but got " +
                replyMsg.getClass().getName());
        }

        AgentID replyAgentID = ((RegistrationReplyMessage) replyMsg).getAgentID();
        if (this.agentID == null) {
            this.agentID = replyAgentID;
            logger.debug("AgentID is " + this.agentID);
        } else {
            if (!this.agentID.equals(replyAgentID)) {
                throw new IOException("Invalid router response: Local ID is " + this.agentID +
                    " but server told " + replyAgentID);
            }
        }

        this.t = tunnel;
        return this.t;
    }

    /**
     * Report a tunnel failure to the agent
     * 
     * Threads get from the local agent a tunnel to use. If this tunnel fails
     * then they have to notify this failure to the local agent. It will try to
     * reconnect to the router and returns a new Tunnel.
     * 
     * Since several threads can encounter and report the same failure this
     * method checks if the error has already been fixed.
     * 
     * @param brokenTunnel
     *            the tunnel that threw an IOException
     * @return a new tunnel to use
     * @throws IOException
     *             if not able to reconnect to the router
     */
    private synchronized Tunnel reportTunnelFailure(Tunnel brokenTunnel, Throwable failure)
            throws IOException {
        if (!this.failedTunnels.contains(brokenTunnel)) {
            this.failedTunnels.add(brokenTunnel);
            logger.debug("Creating a new tunnel because" + brokenTunnel + "failed due to", failure);

            // TODO: Add a several retry algorithm with an exponential backoff
            this.t.shutdown();
            this.t = reconnectToRouter();
        } else {
            logger.debug("This failure for tunnel" + brokenTunnel + "has already been reported");
        }

        return this.t;
    }

    /**
     * @return the local {@link AgentID}.
     */
    public AgentID getAgentID() {
        return agentID;
    }

    public byte[] sendMsg(URI targetURI, byte[] data, boolean oneWay) throws MessageRoutingException {
        String remoteAgentId = targetURI.getHost();
        AgentID agentID = new AgentID(Long.parseLong(remoteAgentId));

        return sendMsg(agentID, data, oneWay);
    }

    public byte[] sendMsg(AgentID targetID, byte[] data, boolean oneWay) throws MessageRoutingException {
        if (logger.isDebugEnabled()) {
            logger.trace("Sending a message to " + targetID);
        }

        // Generate a requestID
        Long requestID = requestIDGenerator.getAndIncrement();
        DataRequestMessage msg = new DataRequestMessage(agentID, targetID, requestID, data, oneWay);

        byte[] response = null;
        if (oneWay) { // No response needed, just send it.
            internalSendMsg(msg);
        } else {
            // Put a mailbox, waiting for the result
            LocalMailBox mb = new LocalMailBox(requestID);
            boxes.put(requestID, mb);
            internalSendMsg(msg);

            // block until the result arrives
            response = mb.waitForResponse(0);
        }

        return response;
    }

    public void sendReply(DataRequestMessage request, byte[] data) throws MessageRoutingException {
        DataReplyMessage reply = new DataReplyMessage(this.getAgentID(), request.getSrcAgentID(), request
                .getMsgID(), data);

        internalSendMsg(reply);
    }

    /**
     * Apply each valve to the message and write it into the tunnel
     * 
     * This method throws a {@link MessageRoutingException} if:
     * <ol>
     * 	<li>The tunnel fails and cannot be recreated</li>
     * 	<li>The tunnel fails, can be recreated but the second tunnel fails too</li>
     * </ol>
     * 
     * @param msg
     *            The message to be sent
     * @exception MessageRoutingException
     *                if the message cannot be sent
     */
    private void internalSendMsg(Message msg) throws MessageRoutingException {
        for (Valve valve : this.valves) {
            msg = valve.invokeOutgoing(msg);
            if (logger.isTraceEnabled()) {
                logger
                        .trace("Applied valve " + valve.getInfo() + ", resulting message is: " +
                            msg.toString());
            }
        }

        // Serialize the message
        byte[] msgBuf = msg.toByteArray();

        // this.t can change at any time, get a ref on it
        Tunnel tunnel = this.t;

        try {
            tunnel.write(msgBuf);
            if (logger.isTraceEnabled()) {
                logger.trace("Sent message " + msg);
            }
        } catch (IOException e) {
            // tunnel failed try to reopen the connection
            try {
                tunnel = this.reportTunnelFailure(tunnel, e);
            } catch (IOException e1) {
                // reportTunnelFailure throw an IOException only when
                // the router cannot be contacted again.
                // Notify the sender this message cannot be send. It will be able
                // to-retry later if he wants.
                throw new MessageRoutingException(
                    "Message cannot be sent because tunnel failed and the agent is unable to recreate it", e1);
            }

            // Try again to send the message
            try {
                tunnel.write(msgBuf);
            } catch (IOException e1) {
                // Message sending failed twice with two different tunnels.
                throw new MessageRoutingException("Message cannot be sent. Failed twice", e1);
            }
        }
    }

    public class LocalMailBox {
        private final CountDownLatch latch;
        volatile private byte[] response = null;
        volatile private MessageRoutingException exception = null;

        private final long requestID;

        public LocalMailBox(long requestID) {
            this.requestID = requestID;
            this.latch = new CountDownLatch(1);
        }

        public byte[] waitForResponse(long timeout) throws MessageRoutingException {
            TimeoutAccounter ta = TimeoutAccounter.getAccounter(timeout);

            boolean b = false;
            do {
                try {
                    b = latch.await(ta.getRemainingTimeout(), TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    // Miam miam miam, don't care we are looping
                }
            } while (!b & !ta.isTimeoutElapsed());

            boxes.remove(this.requestID);
            if (exception != null) {
                throw exception;
            }
            return response;
        }

        public void setAndUnlock(byte[] response) {
            this.response = response;
            latch.countDown();
        }

        public void setAndUnlock(MessageRoutingException exception) {
            this.exception = exception;
            latch.countDown();
        }

    }

    public class MessageReader implements Runnable {
        final ForwardingAgentV2 agent;

        public MessageReader(ForwardingAgentV2 agent) {
            this.agent = agent;
        }

        public void run() {
            while (true) {
                Message msg = readMessage();

                for (Valve valve : valves) {
                    msg = valve.invokeIncoming(msg);
                    if (logger.isTraceEnabled()) {
                        logger.trace("Applied valve " + valve.getInfo() + ", resulting message is: " +
                            msg.toString());
                    }
                }

                handleMessage(msg);
            }
        }

        /** Block until a message arrive
         * 
         * This method is also in charge of handling tunnel failures
         * 
         * @return a received message
         */
        public Message readMessage() {
            while (true) {
                Tunnel tunnel = this.agent.t;
                try {
                    byte[] msgBuf = tunnel.readMessage();
                    return Message.constructMessage(msgBuf, 0);
                } catch (IOException e) {
                    logger.trace("Tunnel failed while waiting for a message asking for a new one", e);
                    // Create a new tunnel
                    boolean newTunnelReady = false;
                    while (!newTunnelReady) {
                        try {
                            tunnel = this.agent.reportTunnelFailure(tunnel, e);
                            newTunnelReady = true;
                        } catch (IOException e1) {
                            logger.error("Failed to create a new tunnel, sleeping for 10 seconds", e1);
                            new Sleeper(10000).sleep();
                        }
                    }

                }
            }
        }

        public void handleMessage(Message msg) {
            switch (msg.getType()) {
                case DATA_REPLY:
                    DataReplyMessage reply = (DataReplyMessage) msg;
                    // Have to lookup in the hashtable
                    LocalMailBox mbox = boxes.remove(reply.getMsgID());
                    if (mbox == null) {
                        logger.error("Received reply for an unknown request: " + msg);
                    } else {
                        if (logger.isTraceEnabled()) {
                            logger.trace("Received reply: " + msg);
                        }
                        // this is a reply containing data
                        mbox.setAndUnlock(reply.getData());
                    }
                    break;
                case DATA_REQUEST:
                    // That message is a request, handle it.
                    messageHandler.pushMessage((DataRequestMessage) msg);
                    break;
                case ERR_UNKNOW_RCPT:
                    ErrorMessage errorMsg = (ErrorMessage) msg;
                    logger.error("Router notified that reciptient " + errorMsg.getSrcAgentID() +
                        " is unknown");
                    // try to unlock the sender
                    mbox = boxes.remove(errorMsg.getMsgID());
                    if (mbox == null) {
                        logger.error("Received an unknow recipient error for an unknown request: " + msg);
                    } else {
                        mbox.setAndUnlock(errorMsg.getException());
                    }

                    // TODO
                    break;
                default:
                    // Bad message type. Log it.
                    logger.error("Invalid Message received, wrong type: " + msg);
            }
        }
    }

}
