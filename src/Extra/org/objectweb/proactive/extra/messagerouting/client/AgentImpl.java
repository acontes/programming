/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of
 * 						   Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2.
 *
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.extra.messagerouting.client;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.util.Sleeper;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.messagerouting.client.dc.client.DirectConnectionManager;
import org.objectweb.proactive.extra.messagerouting.exceptions.MalformedMessageException;
import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DataReplyMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DataRequestMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.ErrorMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;
import org.objectweb.proactive.extra.messagerouting.protocol.message.RegistrationMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.RegistrationReplyMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.RegistrationRequestMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.ErrorMessage.ErrorType;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;
import org.objectweb.proactive.extra.messagerouting.remoteobject.util.socketfactory.MessageRoutingSocketFactorySPI;
import org.objectweb.proactive.extra.messagerouting.router.Router;


/**
 * Implementation of the local message routing client.
 * 
 * 
 * It contacts the router as soon as created and try to maintain the connection
 * open (eg. if the connection is closed then it will be reopened).
 * 
 * @since ProActive 4.1.0
 */
public class AgentImpl extends AgentInternal implements AgentImplMBean {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_CLIENT);

    /** Address of the router */
    final private InetAddress routerAddr;
    /** Port of the router */
    final private int routerPort;

    /** Local AgentID, set after initialization. */
    private AgentID agentID = null;
    /** Remote router ID, set after initialization */
    private long routerID = 0;

    /** Current tunnel, can be null */
    private Tunnel t = null;
    /** List of tunnel reported as failed */
    final private List<Tunnel> failedTunnels;

    /** Every received data message will be handled by this object */
    final private RoutedMessageHandler messageHandler;

    /** List of Valves that will process each message */
    final private List<Valve> valves;

    /** Manager of direct communications */
    final private DirectConnectionManager dcManager;

    /** the handler of incoming messages*/
    final private IncomingMessageHandler incomingHandler;

    /** the thread pool available for this agent */
    final private ExecutorService tpe;
    
    /** The socket factory to use to create the Tunnel */
    final private MessageRoutingSocketFactorySPI socketFactory;

    /**
     * Create a routing agent
     * 
     * The router must be available when the constructor is called.
     * 
     * @param routerAddr
     *            Address of the router
     * @param routerPort
     *            TCP port on which the router listen
     * @param messageHandlerClass
     *            Class the will handled received message
     * @throws ProActiveException
     *             If the router cannot be contacted.
     */
    public AgentImpl(InetAddress routerAddr, int routerPort,
            Class<? extends RoutedMessageHandler> messageHandlerClass, MessageRoutingSocketFactorySPI socketFactory)
            throws ProActiveException {
        this(routerAddr, routerPort, messageHandlerClass, new ArrayList<Valve>(), socketFactory);
    }

    /**
     * Create a routing agent
     * 
     * The router must be available when the constructor is called.
     * 
     * @param routerAddr
     *            Address of the router
     * @param routerPort
     *            TCP port on which the router listen
     * @param messageHandlerClass
     *            Class the will handled received message
     * @param valves
     *            List of {@link Valve} to be applied to all incomming and
     *            outgoing messages.
     * @throws ProActiveException
     *             If the router cannot be contacted.
     */
    public AgentImpl(InetAddress routerAddr, int routerPort,
            Class<? extends RoutedMessageHandler> messageHandlerClass, List<Valve> valves,
            MessageRoutingSocketFactorySPI socketFactory) throws ProActiveException {
        super();
        this.routerAddr = routerAddr;
        this.routerPort = routerPort;
        this.valves = valves;
        this.failedTunnels = new LinkedList<Tunnel>();
        this.socketFactory = socketFactory;

        /* DO NOT USE A FIXED THREAD POOL
         * The entities which will use this thread pool
         * 	have this requirement:
         * 
         * 1) The ProActiveMessageHandler :
         * Each time a message arrives, it is handled by a task submitted to 
         * this executor service. Each task can a perform remote calls. If 
         * the number of workers is fixed it can lead to deadlock.
         * 
         * Reentrant calls is the most obvious case of deadlock. But the same 
         * issue can occur with remote calls.
         * 2) The DirectConnectionServer:
         * Using a fixed thread pool slows down the server. 
         */
        this.tpe = Executors.newCachedThreadPool();

        try {
            Constructor<? extends RoutedMessageHandler> mhConstructor;
            mhConstructor = messageHandlerClass.getConstructor(AgentInternal.class);
            this.messageHandler = mhConstructor.newInstance(this);
        } catch (Exception e) {
            throw new ProActiveException("Message routing agent failed to create the message handler", e);
        }

        // Avoid lazy connection to spot invalid host/port ASAP
        if (this.getTunnel() == null) {
            throw new ProActiveException("Failed to create the tunnel to " + routerAddr + ":" + routerPort);
        }

        // start the direct connections manager
        this.dcManager = new DirectConnectionManager(this);

        // Start the message receiver even if connection failed
        // Message reader will try to open the tunnel later
        MessageReader mr = new MessageReader(this);
        Thread mrThread = new Thread(mr);
        mrThread.setDaemon(true);
        mrThread.setName("Message routing: message reader for agent " + this.agentID);
        mrThread.start();
        this.incomingHandler = mr;

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = null;
        try {
            // Uniquely identify the MBeans and register them with the platform MBeanServer 
            name = new ObjectName("org.objectweb.proactive.extra.messagerouting:type=AgentImpl,name=" +
                this.agentID);
            mbs.registerMBean(this, name);
        } catch (Exception e) {
            logger.warn("Failed to register a JMX MBean for agent " + this.agentID);
        }
    }

    /**
     * Get the current tunnel
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
     * on the first call. If the agent cannot reconnect to the router, this.t is
     * set to null.
     * 
     * <b>This method must only be called by getTunnel</b>
     */
    private void __reconnectToRouter() {
        try {
            Socket s = socketFactory.createSocket(this.routerAddr.getHostAddress(), this.routerPort);
            Tunnel tunnel = new Tunnel(s);

            // start router handshake
            try {
                routerHandshake(tunnel);
            } catch (RouterHandshakeException e) {
                logger.error(
                        "Failed to reconnect to the router: the router handshake procedure failed. Reason: " +
                            e.getMessage(), e);
                tunnel.shutdown();
            }
            this.t = tunnel;
        } catch (IOException exception) {
            logger.debug("Failed to reconnect to the router", exception);
            this.t = null;
        }
    }

    /**
     * This is the initial handshake process between the {@link Agent} and the {@link Router}
     * <ul>
     * 	<li> The Agent will send a {@link MessageType#REGISTRATION_REQUEST} to the Router
     *  <li> On first connection, the {@link RegistrationMessage.Field#AGENT_ID} is set to -1 and the
     *  	{@link RegistrationMessage.Field#ROUTER_ID} field is set to zero. It is the responsibility of the router to fill them.</li>
     *  <li> The Router will reply with a {@link MessageType#REGISTRATION_REPLY} message</li>
     *  <li> On first connection, the Agent initializes its {@link RegistrationMessage.Field#AGENT_ID} and {@link RegistrationMessage.Field#ROUTER_ID}
     *  	fields according to the Router reply </li>
     *  <li> For subsequent reconnections, the Agent verifies that its {@link RegistrationMessage.Field#AGENT_ID} and {@link RegistrationMessage.Field#ROUTER_ID} fields
     *  	match the ones sent by the Router in the {@link MessageType#REGISTRATION_REPLY} message.</li>
     * </ul>
     * @throws IOException
     */
    private void routerHandshake(Tunnel tunnel) throws RouterHandshakeException, IOException {

        try {
            // if call for the first time then agentID is null
            RegistrationRequestMessage reg = new RegistrationRequestMessage(this.agentID, this.idGenerator
                    .getAndIncrement(), routerID);
            tunnel.write(reg.toByteArray());

            // Waiting the router response
            byte[] reply = tunnel.readMessage();
            Message replyMsg = Message.constructMessage(reply, 0);

            if (!(replyMsg instanceof RegistrationReplyMessage)) {
                if (replyMsg instanceof ErrorMessage) {
                    ErrorMessage em = (ErrorMessage) replyMsg;
                    if (em.getErrorType() == ErrorType.ERR_INVALID_ROUTER_ID) {
                        throw new RouterHandshakeException("The router has been restarted. Disconnecting...");
                    } else if (em.getErrorType() == ErrorType.ERR_MALFORMED_MESSAGE) {
                        throw new RouterHandshakeException(
                            "The router received a corrupted version of the original message.");
                    }
                } else {
                    throw new RouterHandshakeException("Invalid router response: expected a " +
                        MessageType.REGISTRATION_REPLY.toString() + " message but got " +
                        replyMsg.getType().toString() + " message");
                }
            }

            RegistrationReplyMessage rrm = (RegistrationReplyMessage) replyMsg;
            AgentID replyAgentID = rrm.getAgentID();
            if (this.agentID == null) {
                this.agentID = replyAgentID;
                logger.debug("Router assigned agentID=" + this.agentID + " to this client");
            } else {
                if (!this.agentID.equals(replyAgentID)) {
                    throw new RouterHandshakeException("Invalid router response: Local ID is " +
                        this.agentID + " but server told " + replyAgentID);
                }
            }

            if (this.routerID == 0) {
                this.routerID = rrm.getRouterID();
            } else if (this.routerID != rrm.getRouterID()) {
                throw new RouterHandshakeException("Invalid router response: previous router ID  was " +
                    this.agentID + " but server now advertises " + rrm.getRouterID());
            }
        } catch (MalformedMessageException e) {
            throw new RouterHandshakeException("Invalid router response: corrupted " +
                MessageType.REGISTRATION_REPLY.toString() + " message - " + e.getMessage());
        }

    }

    private class RouterHandshakeException extends Exception {

        public RouterHandshakeException() {
            super();
        }

        public RouterHandshakeException(String msg) {
            super(msg);
        }
    }

    /**
     * Reports a tunnel failure to the agent
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
    synchronized private void reportTunnelFailure(Tunnel brokenTunnel) {
        if (brokenTunnel == null)
            return;

        if (!this.failedTunnels.contains(brokenTunnel)) {
            this.failedTunnels.add(brokenTunnel);

            this.t.shutdown();
            this.t = null;
        }
    }

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
        Long requestID = idGenerator.getAndIncrement();
        DataRequestMessage msg = new DataRequestMessage(agentID, targetID, requestID, data);

        // try direct connection
        if (this.dcManager.isEnabled()) {
            try {
                if (this.dcManager.isConnected(targetID)) {
                    return this.dcManager.sendRequest(msg, oneWay);
                } else if (!this.dcManager.knows(targetID)) {
                    this.dcManager.seen(targetID);
                }
            } catch (MessageRoutingException e) {
                logger.warn("Could not send the message " + msg + " to the remote agent " + msg.getSender() +
                    " using a direct connection", e);
                // could not send it directly - try by the tunnel?
            }
        }

        return sendRoutingMessage(msg, oneWay);
    }

    public void sendReply(DataRequestMessage request, byte[] data) throws MessageRoutingException {
        DataReplyMessage reply = new DataReplyMessage(this.getAgentID(), request.getSender(), request
                .getMessageID(), data);

        // try direct connection
        if (this.dcManager.isEnabled()) {
            try {
                if (this.dcManager.isConnected(request.getSender())) {
                    this.dcManager.sendReply(reply);
                    return;
                } else if (!this.dcManager.knows(request.getSender())) {
                    this.dcManager.seen(request.getSender());
                }
            } catch (MessageRoutingException e) {
                logger.warn("Could not send the message " + request + " to the remote agent " +
                    request.getSender() + " using a direct connection", e);
                // could not send it directly - try by the tunnel?
            }
        }

        sendRoutingMessage(reply, true);
    }

    public byte[] sendRoutingMessage(Message msg, boolean oneWay) throws MessageRoutingException {

        byte[] response = null;
        if (oneWay) { // No response needed, just send it.
            internalSendMsg(msg);
        } else {
            long requestID = msg.getMessageID();
            AgentID targetID = getTargetID(msg);
            Patient mb = mailboxes.enter(targetID, requestID);
            internalSendMsg(msg);

            // block until the result arrives
            try {
                response = mb.waitForResponse(0);
            } catch (TimeoutException e) {
                throw new MessageRoutingException("Timeout reached " +
                    ProActiveLogger.getStackTraceAsString(e));
            }
        }

        return response;
    }

    // identifies a slot in the mailbox which will not be used by
    // any other DATA messages
    private static final AgentID UNUSED_MAILBOX_SLOT = new AgentID(-1);

    private AgentID getTargetID(Message msg) {
        switch (msg.getType()) {
            case DATA_REQUEST:
                return ((DataRequestMessage) msg).getRecipient();
            case DIRECT_CONNECTION_REQUEST:
                return UNUSED_MAILBOX_SLOT;
            default:
                break;
        }
        throw new UnsupportedOperationException("The agent currently blocks only for " +
            MessageType.DATA_REQUEST + " and " + MessageType.DIRECT_CONNECTION_REQUEST + " message replies ");
    }

    /**
     * Apply each valve to the message and write it into the tunnel
     * 
     * This method throws a {@link MessageRoutingException} if:
     * <ol>
     * <li>The tunnel fails and cannot be recreated</li>
     * <li>The tunnel fails, can be recreated but the second tunnel fails too</li>
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
                this.reportTunnelFailure(tunnel);
                throw new MessageRoutingException("Failed to send a message using the tunnel " + tunnel +
                    ProActiveLogger.getStackTraceAsString(e));

            }
        } else {
            throw new MessageRoutingException("Agent is not connected to the router");
        }
    }

    public IncomingMessageHandler getIncomingHandler() {
        return this.incomingHandler;
    }

    public ExecutorService getThreadPool() {
        return this.tpe;
    }

    /** Read incoming messages from the tunnel */
    public class MessageReader implements Runnable, IncomingMessageHandler {
        /** The local Agent */
        final AgentImpl agent;
        private final AtomicReference<Thread> runningThread;

        public MessageReader(AgentImpl agent) {
            this.agent = agent;
            this.runningThread = new AtomicReference<Thread>();
        }

        public void run() {
            this.runningThread.compareAndSet(null, Thread.currentThread());
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

        @SuppressWarnings("deprecation")
        public void stop() {
            // this is a hack. the message handler desperately needs
            // a clean shutdown code.
            // do NOT call this unless you know for sure that this Agent is no longer used
            if (this.runningThread.get() != null)
                this.runningThread.get().stop();
        }

        /**
         * Block until a message is received
         * 
         * Also in charge of handling tunnel failures
         * 
         * @return the received message
         */
        public Message readMessage() {
            while (true) {
                Tunnel tunnel = this.agent.t;
                try {
                    // Blocking call
                    byte[] msgBuf = tunnel.readMessage();
                    return Message.constructMessage(msgBuf, 0);
                } catch (MalformedMessageException e) {
                    // TODO : Send an ERR_ ?
                    logger.error("Dropping the message received from the router, reason:" + e.getMessage());
                } catch (IOException e) {
                    logger
                            .info(
                                    "PAMR Connection lost (while waiting for a message). A new connection will be established shortly",
                                    e);
                    // Create a new tunnel
                    tunnel = null;
                    do {
                        this.agent.reportTunnelFailure(this.agent.t);
                        tunnel = this.agent.getTunnel();

                        if (tunnel == null) {
                            logger
                                    .error("PAMR Router is unreachable. Will try to estalish a new tunnel in 10 seconds.");
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
                case DIRECT_CONNECTION_ACK:
                case DIRECT_CONNECTION_NACK:
                    handleDirectConnection(msg);
                    break;
                default:
                    // Bad message type. Log it.
                    logger.error("Invalid Message received, wrong type: " + msg);
            }
        }

        private void handleError(ErrorMessage error) {
            long messageId = error.getMessageID();
            switch (error.getErrorType()) {
                case ERR_DISCONNECTION_BROADCAST:
                    /*
                     * An agent disconnected. To avoid blocked thread we have to
                     * unlock all thread that are waiting a response from this agent
                     */
                    mailboxes.unlockDueToDisconnection(error.getSender());
                    // cleanup DC info for the disconnected client
                    if (dcManager.isEnabled())
                        dcManager.agentDisconnected(error.getSender());
                    break;
                case ERR_NOT_CONNECTED_RCPT:
                    /*
                     * The recipient of a given message is not connected to the
                     * router Unlock the sender
                     */
                    AgentID sender = error.getSender();

                    Patient mbox = mailboxes.remove(sender, messageId);
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
                case ERR_MALFORMED_MESSAGE:
                    // do we have the faulty AgentID?
                    AgentID faulty = error.getFaulty();
                    Patient patient;
                    if (faulty != null) {
                        patient = mailboxes.remove(faulty, messageId);
                    } else {
                        // harder without the faulty agent id
                        patient = mailboxes.unlockDueToCorruption(messageId);
                    }
                    if (patient == null) {
                        if (logger.isTraceEnabled()) {
                            logger
                                    .trace("The router got a corrupted version of message with ID " +
                                        messageId);
                        }
                    } else {
                        if (logger.isTraceEnabled()) {
                            logger.trace("Unlocked " + patient + " due to corruption of message with ID " +
                                messageId + " on the router side");
                        }

                        patient
                                .setAndUnlock(new MessageRoutingException("Message corruption on router side"));
                    }
                    break;
                default:
                    logger.warn("Unexpected error received by agent from the router: " + error);
                    break;
            }
        }

        private void handleDataReply(DataReplyMessage reply) {
            Patient mbox = mailboxes.remove(reply.getSender(), reply.getMessageID());
            if (mbox == null) {
                logger.error("Received reply for an unknown request: " + reply);
            } else {
                if (logger.isTraceEnabled()) {
                    logger.trace("Received reply: " + reply);
                }
                mbox.setAndUnlock(reply.getData());
            }
        }

        private void handleDataRequest(DataRequestMessage request) {
            messageHandler.pushMessage(request);
        }

        private void handleDirectConnection(Message msg) {

            Patient mbox = mailboxes.remove(UNUSED_MAILBOX_SLOT, msg.getMessageID());
            if (mbox == null) {
                logger.error(" Received reply for an unknown direct connection request: " + msg);
                return;
            } else {
                if (logger.isTraceEnabled()) {
                    logger.trace("Received direct connection reply: " + msg);
                }
            }

            switch (msg.getType()) {
                case DIRECT_CONNECTION_ACK:
                    mbox.setAndUnlock(msg.toByteArray());
                    break;
                case DIRECT_CONNECTION_NACK:
                    mbox.setAndUnlock(new MessageRoutingException(
                        "Router refused the direct connection request;" + " the message from the router is" +
                            msg));
                    break;
                default:
                    throw new IllegalArgumentException("Not a direct connection reply message:" + msg);
            }
        }

    }

    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@ MBean @@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */

    public String getLocalAddress() {
        String ret = "unknown";

        Tunnel t = this.getTunnel();
        if (t != null) {
            ret = t.getLocalAddress();
        }

        return ret;

    }

    public int getLocalPort() {
        int ret = -1;

        Tunnel t = this.getTunnel();
        if (t != null) {
            ret = t.getLocalPort();
        }

        return ret;
    }

    public String getRemoteAddress() {
        String ret = "unknown";

        Tunnel t = this.getTunnel();
        if (t != null) {
            ret = t.getRemoteAddress();
        }

        return ret;
    }

    public int getRemotePort() {
        int ret = -1;

        Tunnel t = this.getTunnel();
        if (t != null) {
            ret = t.getRemotePort();
        }

        return ret;
    }

    public long getLocalAgentID() {
        return this.agentID.getId();
    }

    public String[] getMailboxes() {
        return this.mailboxes.getBlockedCallers();

    }

}
