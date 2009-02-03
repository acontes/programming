package org.objectweb.proactive.extra.forwardingv2.client;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    final private Mailboxes mailboxes;

    /** Request ID Generator **/
    private final AtomicLong requestIDGenerator;

    /** Local AgentID, set after initialization. **/
    private AgentID agentID = null;

    private Tunnel t = null;
    final private Object t_lock = new Object();
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
        this.mailboxes = new Mailboxes();
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
        if (this.getTunnel() == null) {
            throw new ProActiveException("Failed to create the tunnel to " + routerAddr + ":" + routerPort);
        }

        // Start the message receiver even if connection failed
        // Message reader will try to open the tunnel later
        Thread mrThread = new Thread(new MessageReader(this));
        mrThread.setDaemon(true);
        mrThread.setName("Message routing: message reader for agent " + this.agentID);
        mrThread.start();
    }

    /** Get the current tunnel
     *
     * @return the current tunnel or null is a tunnel cannot be open
     */
    synchronized private Tunnel getTunnel() {
        if (this.t != null)
            return this.t;

        this.__reconnectToRouter();
        return this.t;
    }

    /**
     * Returns a new tunnel to the router
     * 
     * The tunnel instance field is updated. The agentID instance field is set
     * on the first call. If the agent cannot reconnect to the router, this.t is set
     * to null.
     * 
     * <b>This method must only be called by getNewTunnel</b>
     */
    private void __reconnectToRouter() {
        try {
            Tunnel tunnel = new Tunnel(this.routerAddr, this.routerPort);

            // if call for the first time then agentID is null
            RegistrationRequestMessage reg = new RegistrationRequestMessage(this.agentID, requestIDGenerator
                    .getAndIncrement());
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
                logger.debug("Router assigned agentID=" + this.agentID + " to this client");
            } else {
                if (!this.agentID.equals(replyAgentID)) {
                    throw new IOException("Invalid router response: Local ID is " + this.agentID +
                        " but server told " + replyAgentID);
                }
            }

            this.t = tunnel;
        } catch (IOException exception) {
            logger.debug("Failed to reconnect to the router", exception);
            this.t = null;
        }
    }

    /**
     * Report a tunnel failure to the agent
     * 
     * Threads get from the local agent a tunnel to use. If this tunnel fails
     * then they have to notify this failure to the local agent.
     * 
     * Since several threads can encounter and report the same failure this
     * method checks if the error has already been fixed.
     * 
     * <b>This method must only be called by getNewTunnel</b>
     *
     * @param brokenTunnel
     *            the tunnel that threw an IOException
     */
    synchronized private void reportTunnelFailure(Tunnel brokenTunnel, Throwable failure) {
        if (brokenTunnel == null)
            return;

        if (!this.failedTunnels.contains(brokenTunnel)) {
            this.failedTunnels.add(brokenTunnel);

            this.t.shutdown();
            this.t = null;
        }
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
            logger.debug("Sending a message to agentId=" + targetID);
        }

        // Generate a requestID
        Long requestID = requestIDGenerator.getAndIncrement();
        DataRequestMessage msg = new DataRequestMessage(agentID, targetID, requestID, data);

        byte[] response = null;
        if (oneWay) { // No response needed, just send it.
            internalSendMsg(msg);
        } else {

            LocalMailBox mb = mailboxes.createMailbox(targetID, requestID);
            internalSendMsg(msg);

            // block until the result arrives
            response = mb.waitForResponse(0);
        }

        return response;
    }

    public void sendReply(DataRequestMessage request, byte[] data) throws MessageRoutingException {
        DataReplyMessage reply = new DataReplyMessage(this.getAgentID(), request.getSrcAgentID(), request
                .getMessageID(), data);

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
    protected void internalSendMsg(Message msg) throws MessageRoutingException {
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
        Tunnel tunnel = this.getTunnel();
        if (tunnel != null) {
            try {
                tunnel.write(msgBuf);
                if (logger.isTraceEnabled()) {
                    logger.trace("Sent message " + msg);
                }
            } catch (IOException e) {
                // Fail fast
                this.reportTunnelFailure(tunnel, e);
                throw new MessageRoutingException("Failed to send a message using the tunnel " + tunnel, e);

            }
        } else {
            throw new MessageRoutingException("Agent is not connected to the router");
        }
    }

    public class Mailboxes {
        final private Map<AgentID, Map<Long, LocalMailBox>> byRemoteAgent;

        final private Object lock = new Object();

        public Mailboxes() {
            this.byRemoteAgent = new HashMap<AgentID, Map<Long, LocalMailBox>>();
        }

        public LocalMailBox createMailbox(AgentID remoteAgentId, long messageId) {
            LocalMailBox mb = new LocalMailBox(this, remoteAgentId, messageId);
            synchronized (this.lock) {
                Map<Long, LocalMailBox> byMessageId;
                byMessageId = this.byRemoteAgent.get(remoteAgentId);
                if (byMessageId == null) {
                    byMessageId = new HashMap<Long, LocalMailBox>();
                    this.byRemoteAgent.put(remoteAgentId, byMessageId);
                }
                byMessageId.put(messageId, mb);
            }

            return mb;
        }

        public void unlockDueToDisconnection(AgentID agentID) {
            synchronized (this.lock) {
                MessageRoutingException e = new MessageRoutingException("Remote agent disconnected");

                Map<Long, LocalMailBox> map = this.byRemoteAgent.get(agentID);
                if (map != null) {
                    for (LocalMailBox mb : map.values()) {
                        if (logger.isTraceEnabled()) {
                            logger.trace("Unlocked request " + mb.requestID + " because remote agent" +
                                mb.getRemoteAgent() + " disconnected");
                        }
                        mb.setAndUnlock(e);
                    }
                }
            }
        }

        public LocalMailBox remove(AgentID agentId, long messageId) {
            LocalMailBox mb = null;
            synchronized (this.lock) {
                Map<Long, LocalMailBox> map;
                map = this.byRemoteAgent.get(agentId);
                if (map != null) {
                    mb = map.remove(messageId);
                }
            }

            return mb;
        }
    }

    public class LocalMailBox {
        final private CountDownLatch latch;
        volatile private byte[] response = null;
        volatile private MessageRoutingException exception = null;

        final private Mailboxes mailboxes;

        final private long requestID;
        final private AgentID agentId;

        public LocalMailBox(Mailboxes mailboxes, AgentID agentId, long requestID) {
            this.mailboxes = mailboxes;
            this.latch = new CountDownLatch(1);

            this.requestID = requestID;
            this.agentId = agentId;
        }

        public long getMessageId() {
            return this.requestID;
        }

        public AgentID getRemoteAgent() {
            return this.agentId;
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

            if (exception != null) {
                throw exception;
            }
            return response;
        }

        public void setAndUnlock(byte[] response) {
            this.response = response;
            unlock();
        }

        public void setAndUnlock(MessageRoutingException exception) {
            this.exception = exception;
            unlock();
        }

        private void unlock() {
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
                    tunnel = null;
                    do {
                        this.agent.reportTunnelFailure(this.agent.t, e);
                        tunnel = this.agent.getTunnel();

                        if (tunnel == null) {
                            logger.error("Failed to create a new tunnel, sleeping for 10 seconds");
                            new Sleeper(10000).sleep();
                        }
                    } while (tunnel == null);
                }
            }
        }

        public void handleMessage(Message msg) {
            switch (msg.getType()) {
                case DATA_REPLY:
                    DataReplyMessage reply = (DataReplyMessage) msg;
                    handleDataReply(reply);
                    break;
                case DATA_REQUEST:
                    DataRequestMessage request = (DataRequestMessage) msg;
                    handleDataRequest(request);
                    break;
                case ERR_:
                    ErrorMessage error = (ErrorMessage) msg;
                    handleError(error);
                    break;
                default:
                    // Bad message type. Log it.
                    logger.error("Invalid Message received, wrong type: " + msg);
            }
        }

        private void handleError(ErrorMessage error) {
            switch (error.getErrorType()) {
                case ERR_DISCONNECTED_RCPT_BROADCAST:
                    /* An agent disconnected. To avoid blocked thread we have
                     * to unlock all thread that are waiting a response from this
                     * agent
                     */
                    mailboxes.unlockDueToDisconnection(error.getDstAgentID());
                    break;
                case ERR_NOT_CONNECTED_RCPT:
                    /* The recipient of a given message is not connected to the router
                     * Unlock the sender
                     */
                    AgentID sender = error.getSrcAgentID();
                    long messageId = error.getMessageID();

                    LocalMailBox mbox = mailboxes.remove(sender, messageId);
                    if (mbox == null) {
                        logger.error("Received error for an unknown request: " + error);
                    } else {
                        if (logger.isTraceEnabled()) {
                            logger.trace("Unlocled " + mbox + " because of a non connected recipient");
                        }

                        // this is a reply containing data
                        mbox.setAndUnlock(new MessageRoutingException("Recipient not connected " + sender));
                    }
                    break;
                default:
                    logger.warn("Unexpected error received by agent from the router: " + error);
                    break;
            }
        }

        public void handleDataReply(DataReplyMessage reply) {
            // Have to lookup in the hashtable

            LocalMailBox mbox = mailboxes.remove(reply.getSrcAgentID(), reply.getMessageID());
            if (mbox == null) {
                logger.error("Received reply for an unknown request: " + reply);
            } else {
                if (logger.isTraceEnabled()) {
                    logger.trace("Received reply: " + reply);
                }
                // this is a reply containing data
                mbox.setAndUnlock(reply.getData());
            }
        }

        public void handleDataRequest(DataRequestMessage request) {
            // That message is a request, handle it.
            messageHandler.pushMessage(request);
        }

    }

}
