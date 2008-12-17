package org.objectweb.proactive.extra.forwardingv2.client;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.util.TimeoutAccounter;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.exceptions.AgentNotConnectedException;
import org.objectweb.proactive.extra.forwardingv2.exceptions.ExecutionException;
import org.objectweb.proactive.extra.forwardingv2.exceptions.RoutingException;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.DataReplyMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.DataRequestMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.ExceptionMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.MessageInputStream;
import org.objectweb.proactive.extra.forwardingv2.protocol.RegistrationReplyMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.RegistrationRequestMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message.MessageType;


/**
 * The ForwardingAgent is the implementation of the Agent interface.
 *
 * @author A. Fawaz, J. Martin
 *
 */
public class ForwardingAgentV2 implements AgentV2Internal, Runnable {
    // FIELDS
    /**
     * Number of retries to connect to the registry.
     */
    public static final int NB_RETRY = 3;
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);

    /** Delay before aborting the response waiting. **/
    private static final long WAITING_DELAY = 5000;

    /** Local Mailboxes used to block the threads waiting for a response. **/
    private final ConcurrentHashMap<Long, LocalMailBox> boxes;

    /** Request ID Generator **/
    private final AtomicLong requestIDGenerator;

    /** Local AgentID, set after initialization. **/
    private AgentID agentID;

    /** Socket connected to the registry. **/
    private Socket tunnel = null;

    /** Read part of the tunnel. **/
    private MessageInputStream input = null;

    private volatile boolean isRunning; // Stopping main Thread
    private volatile boolean agentConnected;

    /** Every data message will be handled by this object */
    final private MessageHandler messageHandler;

    /** The list of Valve that will process each message */
    final private List<Valve> valves;

    public ForwardingAgentV2(Class<? extends MessageHandler> messageHandlerClass, List<Valve> valves) {
        this.valves = valves;

        try {
            Constructor<? extends MessageHandler> mhConstructor;
            mhConstructor = messageHandlerClass.getConstructor(AgentV2.class);
            this.messageHandler = mhConstructor.newInstance(this);
        } catch (Exception e) {
            throw new ProActiveRuntimeException("Agent failed to create the message handler", e);
        }

        boxes = new ConcurrentHashMap<Long, LocalMailBox>();
        requestIDGenerator = new AtomicLong(0);
        agentConnected = false;
    }

    public ForwardingAgentV2(Class<? extends MessageHandler> messageHandlerClass) {
        this(messageHandlerClass, new ArrayList<Valve>());
    }

    /**
     * Initialize the tunnel to the registry and get the agentID from it.
     * 
     * @param registryAddress
     *            {@link InetAddress} of the registry
     * @param registryPort
     *            port to connect.
     */
    public void initialize(InetAddress registryAddress, int registryPort) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Connecting to registry on " + registryAddress + ":" + registryPort);
        }

        int retry = NB_RETRY;
        // Connect a socket.
        while (tunnel == null && retry-- > 0) {
            try {
                tunnel = new Socket(registryAddress, registryPort);
                input = new MessageInputStream(tunnel.getInputStream());
            } catch (IOException e) {
                if (retry > 0) {
                    logger.warn("Connection to registry failed. Retrying...");
                } else {
                    logger.error("Connection to registry failed.", e);
                    throw e;
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Connection successful. getting a Unique Agent ID");
        }

        RegistrationRequestMessage reg = new RegistrationRequestMessage();
        try {
            synchronized (tunnel) {
                tunnel.getOutputStream().write(reg.toByteArray());
                tunnel.getOutputStream().flush();
            }
        } catch (IOException e) {
            logger.warn("Exception during registration", e);
        }

        Message resp = Message.constructMessage(input.readMessage(), 0);
        if (resp.getType() == MessageType.REGISTRATION_REPLY) {
            RegistrationReplyMessage reply = (RegistrationReplyMessage) resp;
            // Registration successfull.
            agentID = reply.getAgentID();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Local Unique Agent ID is " + agentID);
        }

        // Start handling incoming messages
        isRunning = true;
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();

        // TODO Put a way to stop the localAgent in a better way.

        agentConnected = true;
    }

    public byte[] sendMsg(AgentID targetID, byte[] data, boolean oneWay) throws RoutingException,
            ExecutionException {
        if (!agentConnected) {
            throw new AgentNotConnectedException();
        }
        if (logger.isDebugEnabled()) {
            logger.trace("Sending a message to " + targetID);
        }

        // Generate a requestID
        Long requestID = requestIDGenerator.incrementAndGet();

        DataRequestMessage msg = new DataRequestMessage(agentID, targetID, requestID, data, oneWay);

        if (oneWay) { // No response needed, just send it.
            internalSendMsg(msg);
            return null;
        } else {
            // Put a mailbox, waiting for the result
            LocalMailBox mb = new LocalMailBox(requestID);
            boxes.put(requestID, mb);
            internalSendMsg(msg);
            // block until the result arrives

            byte[] response = mb.waitForResponse(WAITING_DELAY);
            if (response == null) {
                logger.debug("Timeout reached while waiting a response for " + msg);
                throw new RoutingException("Timeout reached while waiting a response for " + msg);
            }
            return response;
        }
    }

    public byte[] sendMsg(URI targetURI, byte[] data, boolean oneWay) throws RoutingException,
            ExecutionException {
        if (!agentConnected) {
            throw new AgentNotConnectedException();
        }
        String path = targetURI.getPath();
        String remoteAgentId = path.substring(0, path.indexOf('/'));

        AgentID agentID = new AgentID(Long.parseLong(remoteAgentId));
        return sendMsg(agentID, data, oneWay);
    }

    public void sendReply(DataRequestMessage request, byte[] data) throws RoutingException {
        DataReplyMessage reply = new DataReplyMessage(this.getAgentID(), request.getSrcAgentID(), request
                .getMsgID(), data);
        if (!agentConnected) {
            throw new AgentNotConnectedException();
        }
        internalSendMsg(reply);
    }

    public void sendExceptionReply(DataRequestMessage request, Exception e) throws RoutingException {
        ExceptionMessage reply = new ExceptionMessage(this.getAgentID(), request.getSrcAgentID(), request
                .getMsgID(), e);
        if (!agentConnected) {
            throw new AgentNotConnectedException();
        }
        internalSendMsg(reply);
    }

    /**
     * Send really the message through the tunnel
     *
     * @param msg
     */
    private void internalSendMsg(Message msg) {
        for (Valve valve : this.valves) {
            msg = valve.invokeOutgoing(msg);
            if (logger.isTraceEnabled()) {
                logger
                        .trace("Applied valve " + valve.getInfo() + ", resulting message is: " +
                            msg.toString());
            }
        }

        // Serialize the message
        byte[] sMessage = msg.toByteArray();
        try {
            if (logger.isTraceEnabled())
                logger.trace("Sending message on tunnel");
            synchronized (tunnel) {
                tunnel.getOutputStream().write(sMessage);
                tunnel.getOutputStream().flush();
            }
            if (logger.isTraceEnabled())
                logger.trace("Message sent.");
        } catch (IOException e) {
            logger.warn("Error while writing message into the tunnel: " + msg, e);
        }
    }

    /**
     * -> Read messages L> Execute requests or dispatch responses -> TODO handle
     * other cases
     */
    public void run() {
        while (isRunning) {
            Message msg = null;
            try {
                msg = Message.constructMessage(input.readMessage(), 0);
            } catch (IOException e) {
                logger.error("Error while reading a message from the tunnel", e);
                return;
            }

            if (logger.isTraceEnabled()) {
                logger.trace("Message received: " + msg);
            }

            for (Valve valve : this.valves) {
                msg = valve.invokeIncoming(msg);
                if (logger.isTraceEnabled()) {
                    logger.trace("Applied valve " + valve.getInfo() + ", resulting message is: " +
                        msg.toString());
                }
            }

            // TODO Handle different message types.

            if (msg.getType() == MessageType.DATA_REPLY) {
                DataReplyMessage reply = (DataReplyMessage) msg;
                // Have to lookup in the hashtable
                LocalMailBox mbox = boxes.remove(reply.getMsgID());
                if (mbox == null) {
                    logger.warn("Received reply message for unknown request: " + msg);
                } else {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Message corresponding to response");
                    }
                    // this is a reply containing data
                    mbox.setAndUnlock(reply.getData());
                }
            } else if (msg.getType() == MessageType.DATA_REQUEST) {

                // That message is a request, handle it.
                messageHandler.pushMessage((DataRequestMessage) msg);
            }
        }

    }

    /**
     * @return the local {@link AgentID}.
     */
    public AgentID getAgentID() {
        return agentID;
    }


    public class LocalMailBox {
        private final CountDownLatch latch;
        volatile private byte[] response = null;
        private final long requestID;

        public LocalMailBox(long requestID) {
            this.requestID = requestID;
            this.latch = new CountDownLatch(1);
        }

        public byte[] waitForResponse(long timeout) {
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
            return response;
        }

        public void setAndUnlock(byte[] response) {
            this.response = response;
            latch.countDown();
        }
    }

}
