package org.objectweb.proactive.extra.forwardingv2.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.remoteobject.http.util.HttpMarshaller;
import org.objectweb.proactive.core.remoteobject.http.util.HttpMessage;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwarding.exceptions.ForwardingException;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.MessageInputStream;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message.MessageType;


/**
 * The ForwardingAgent is the implementation of the Agent interface.
 * @author A. Fawaz, J. Martin
 *
 */
public class ForwardingAgentV2 implements AgentV2, Runnable {
    // FIELDS
    /**
     * Number of retries to connect to the registry.
     */
    public static final int NB_RETRY = 3;
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);

    /** Delay before aborting the response waiting. **/
    private static final long WAITING_DELAY = 5;

    /**
     * ThreadPool options:
     * - CORE_POOL_SIZE: Initial size of the thread pool
     * - MAX_POOL_SIZE: number of request served at the same time
     * - KEEP_ALIVE_TIME: Time in seconds to keep the threads before decreasing the core pool size.
     */
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 4;
    private static final long KEEP_ALIVE_TIME = 10;
    private final ThreadPoolExecutor pool;

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

    public ForwardingAgentV2() {
        boxes = new ConcurrentHashMap<Long, LocalMailBox>();
        requestIDGenerator = new AtomicLong(0);
        pool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    }

    /**
     * Initialize the tunnel to the registry and get the agentID from it.
     * 
     * @param registryAddress {@link InetAddress} of the registry
     * @param registryPort port to connect.
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

        Message reg = Message.registrationRequestMessage();
        try {
            synchronized (tunnel) {
                tunnel.getOutputStream().write(reg.toByteArray());
                tunnel.getOutputStream().flush();
            }
        } catch (IOException e) {
            logger.warn("Exception during registration", e);
        }

        Message resp = new Message(input.readMessage(), 0);
        if (resp.getType() == Message.MessageType.REGISTRATION_REPLY.getValue()) {
            // Registration successfull.
            agentID = resp.getDstAgentID();
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

    }

    /**
     * Send a message to the target AgentID, containing data.
     * 
     * if oneWay, the the result returned is null.
     * if not, the this call is blocked until an answer is provided.
     * 
     * @param targetID the destination of the data.
     * @param data the data to send.
     * @param oneWay
     * @return the data response.
     * @throws ForwardingException if the timeout is reached.
     */
    public byte[] sendMsg(AgentID targetID, byte[] data, boolean oneWay) throws ForwardingException {
        if (logger.isDebugEnabled()) {
            logger.trace("Sending a message to " + targetID);
        }

        // Generate a requestID
        Long requestID = requestIDGenerator.incrementAndGet();

        Message msg = Message.dataMessage(agentID, targetID, requestID, data);

        if (oneWay) { // No response needed, just send it.
            internalSendMsg(msg);
            return null;
        } else {
            // Put a mailbox, waiting for the result
            LocalMailBox mb = new LocalMailBox(requestID, msg.getDstAgentID());
            boxes.put(requestID, mb);
            internalSendMsg(msg);
            // block until the result arrives
            if (mb.waitForResponse(WAITING_DELAY)) {
                // return the response
                return mb.getValue();
            } else {
                logger.warn("Unable to get the response from request: " + requestID);
                throw new ForwardingException("Unable to get the response from request");
            }
        }
    }

    public byte[] sendMsg(URI targetURI, byte[] data, boolean oneWay) throws ForwardingException {
        String path = targetURI.getPath();
        String remoteAgentId = path.substring(0, path.indexOf('/'));

        AgentID agentID = new AgentID(Long.parseLong(remoteAgentId));
        return sendMsg(agentID, data, oneWay);
    }

    /**
     * Send really the message through the tunnel
     * @param msg
     */
    private void internalSendMsg(Message msg) {
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
     * -> Read messages
     * L> Execute requests or dispatch responses
     * -> TODO handle other cases
     */
    public void run() {
        while (isRunning) {
            Message msg = null;
            try {
                msg = new Message(input.readMessage(), 0);
            } catch (IOException e) {
                logger.error("Error while reading a message from the tunnel", e);
                return;
            }

            if (logger.isTraceEnabled()) {
                logger.trace("Message received: " + msg);
            }
            // TODO Handle different message types.

            if (msg.getType() == MessageType.DATA.getValue()) {
                LocalMailBox mbox = boxes.remove(msg.getMsgID());
                if (mbox == null) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Message corresponding to request");
                    }
                    // That message is a request, handle it.
                    pool.execute(new MessageProcessor(msg));
                } else {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Message corresponding to response");
                    }
                    // this is a reply containing data
                    mbox.setAndUnlock(msg.getData());
                }
            }
        }

    }

    /**
     * @return the local {@link AgentID}.
     */
    public AgentID getAgentID() {
        return agentID;
    }

    /**
     * Execute the request received and send the response.
     * 
     * @author J. Martin, A. Fawaz
     */
    public class MessageProcessor implements Runnable {
        private final Message _toProcess;

        public MessageProcessor(Message msg) {
            _toProcess = msg;
        }

        public void run() {
            ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

                // Handle the message
                HttpMessage message = (HttpMessage) HttpMarshaller.unmarshallObject(_toProcess.getData());
                Object result = null;
                if (message != null) {
                    result = message.processMessage();
                } else {
                    logger.debug("message " + _toProcess + " doesn't unmarshal itself correctly.");
                }
                byte[] resultBytes = HttpMarshaller.marshallObject(result);

                Message reply = Message.dataMessage(agentID, _toProcess.getSrcAgentID(), _toProcess
                        .getMsgID(), resultBytes);
                internalSendMsg(reply);

            } catch (Exception e) {
                logger.warn("HTTP Failed to serve a message", e);
            } finally {
                Thread.currentThread().setContextClassLoader(savedClassLoader);
            }
        }

    }

    public class LocalMailBox {
        private final CountDownLatch latch;
        private byte[] response = null;
        private final long requestID;
        private boolean aborted;
        private final AgentID targetID;

        public LocalMailBox(long requestID, AgentID targetID) {
            this.requestID = requestID;
            this.latch = new CountDownLatch(1);
            this.aborted = false;
            this.targetID = targetID;
        }

        public boolean waitForResponse(long timeInSeconds) {
            try {
                if (latch.await(timeInSeconds, TimeUnit.SECONDS)) {
                    boxes.remove(requestID);
                    return !aborted;
                } else {
                    boxes.remove(requestID);
                    return false;
                }
            } catch (InterruptedException e) {
                logger.warn("Mailbox was interrupted while waiting for a reply", e);
                boxes.remove(requestID);
                return false;
            }
        }

        public void setAndUnlock(byte[] response) {
            this.response = response;
            latch.countDown();
        }

        public void abort() {
            this.aborted = true;
            latch.countDown();
        }

        public byte[] getValue() {
            return response;
        }

        public AgentID getTargetAgentID() {
            return this.targetID;
        }
    }

}
