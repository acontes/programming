/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.extra.messagerouting.client;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.util.Sleeper;
import org.objectweb.proactive.core.util.SweetCountDownLatch;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.messagerouting.client.dc.client.DirectConnection;
import org.objectweb.proactive.extra.messagerouting.client.dc.server.DirectConnectionServer;
import org.objectweb.proactive.extra.messagerouting.client.dc.server.DirectConnectionServerConfig;
import org.objectweb.proactive.extra.messagerouting.client.dc.server.DirectConnectionServerConfig.DirectConnectionDisabledException;
import org.objectweb.proactive.extra.messagerouting.client.dc.server.DirectConnectionServerConfig.MissingPortException;
import org.objectweb.proactive.extra.messagerouting.exceptions.MalformedMessageException;
import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DataReplyMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DataRequestMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionAdvertiseMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.ErrorMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;
import org.objectweb.proactive.extra.messagerouting.protocol.message.RegistrationMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.RegistrationReplyMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.RegistrationRequestMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.ErrorMessage.ErrorType;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;
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
public class AgentImpl implements Agent, AgentImplMBean {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_CLIENT);

    /** Address of the router */
    final private InetAddress routerAddr;
    /** Port of the router */
    final private int routerPort;

    /** Local AgentID, set after initialization. */
    private AgentID agentID = null;
    /** Remote router ID, set after initialization */
    private long routerID = 0;
    /** Request ID Generator **/
    private final AtomicLong requestIDGenerator;

    /** Senders waiting for a response */
    final private WaitingRoom mailboxes;

    /** Current tunnel, can be null */
    private Tunnel t = null;
    /** List of tunnel reported as failed */
    final private List<Tunnel> failedTunnels;

    /** Every received data message will be handled by this object */
    final private MessageHandler messageHandler;

    /** List of Valves that will process each message */
    final private List<Valve> valves;

    /** Manager of direct communications */
    final private DirectConnectionManager dcManager;

    /** the handler of incoming messages*/
    final private MessageReader incomingHandler;

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
            Class<? extends MessageHandler> messageHandlerClass) throws ProActiveException {
        this(routerAddr, routerPort, messageHandlerClass, new ArrayList<Valve>());
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
            Class<? extends MessageHandler> messageHandlerClass, List<Valve> valves)
            throws ProActiveException {
        this.routerAddr = routerAddr;
        this.routerPort = routerPort;
        this.valves = valves;
        this.mailboxes = new WaitingRoom();
        this.requestIDGenerator = new AtomicLong(0);
        this.failedTunnels = new LinkedList<Tunnel>();
        this.incomingHandler = new MessageReader(this);

        try {
            Constructor<? extends MessageHandler> mhConstructor;
            mhConstructor = messageHandlerClass.getConstructor(Agent.class);
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
        Thread mrThread = new Thread(this.incomingHandler);
        mrThread.setDaemon(true);
        mrThread.setName("Message routing: message reader for agent " + this.agentID);
        mrThread.start();

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
            Tunnel tunnel = new Tunnel(this.routerAddr, this.routerPort);

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
            RegistrationRequestMessage reg = new RegistrationRequestMessage(this.agentID, requestIDGenerator
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
        Long requestID = requestIDGenerator.getAndIncrement();
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

        byte[] response = null;
        if (oneWay) { // No response needed, just send it.
            internalSendMsg(msg);
        } else {

            Patient mb = mailboxes.enter(targetID, requestID);
            internalSendMsg(msg);

            // block until the result arrives
            try {
                response = mb.waitForResponse(0);
            } catch (TimeoutException e) {
                throw new MessageRoutingException("Timeout reached", e);
            }
        }

        return response;
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

        internalSendMsg(reply);
    }

    public void advertiseDirectConnection(InetAddress dcAddress, int dcPort) throws MessageRoutingException {
        Long messageID = requestIDGenerator.getAndIncrement();
        DirectConnectionAdvertiseMessage dcAdMessage = new DirectConnectionAdvertiseMessage(messageID,
            dcAddress, dcPort);
        // one-way send; no need to wait for reply
        internalSendMsg(dcAdMessage);
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
                throw new MessageRoutingException("Failed to send a message using the tunnel " + tunnel, e);

            }
        } else {
            throw new MessageRoutingException("Agent is not connected to the router");
        }
    }

    /** All the clients waiting for a response
     * 
     * Add thread sending a message is a "patient". It will wait in the waiting room
     * until the response is available.
     * 
     * Patient must be created by using
     * {@link WaitingRoom#enter(AgentID, long)} and not by calling its
     * constructor.
     * 
     * It allows to group mailboxes by recipient. When a remote client crash or
     * disconnect, the agent must unblock all the threads waiting for a response
     * from this remote client.
     */
    private class WaitingRoom {
        final private Map<AgentID, Map<Long, Patient>> byRemoteAgent;

        /** Must be hold for each addition or removal */
        final private Object lock = new Object();

        private WaitingRoom() {
            this.byRemoteAgent = new HashMap<AgentID, Map<Long, Patient>>();
        }

        /** Add a new patient into the waiting room
         * 
         * @param remoteAgentId
         *            Message recipient
         * @param messageId
         *            Message ID
         * @return a newly created mailbox
         */
        private Patient enter(AgentID remoteAgentId, long messageId) {
            Patient mb = new Patient(remoteAgentId, messageId);
            synchronized (this.lock) {
                Map<Long, Patient> byMessageId;
                byMessageId = this.byRemoteAgent.get(remoteAgentId);
                if (byMessageId == null) {
                    byMessageId = new HashMap<Long, Patient>();
                    this.byRemoteAgent.put(remoteAgentId, byMessageId);
                }
                byMessageId.put(messageId, mb);
            }

            return mb;
        }

        /**
         * Unblock all the threads waiting for a response from a given remote
         * agent
         * 
         * @param agentID
         *            the remote Agent ID
         */
        private void unlockDueToDisconnection(AgentID agentID) {
            synchronized (this.lock) {
                MessageRoutingException e = new MessageRoutingException("Remote agent disconnected");

                Map<Long, Patient> map = this.byRemoteAgent.get(agentID);
                if (map != null) {
                    for (Patient patient : map.values()) {
                        if (logger.isTraceEnabled()) {
                            logger.trace("Unlocked request " + patient.requestID + " because remote agent" +
                                patient.recipient + " disconnected");
                        }
                        patient.setAndUnlock(e);
                    }
                }
            }
        }

        /** Remove a patient on response arrival */
        private Patient remove(AgentID agentId, long messageId) {
            Patient patient = null;
            synchronized (this.lock) {
                Map<Long, Patient> map;
                map = this.byRemoteAgent.get(agentId);
                if (map != null) {
                    patient = map.remove(messageId);
                }
            }

            return patient;
        }

        private String[] getBlockedCallers() {
            List<String> ret = new LinkedList<String>();

            synchronized (this.lock) {
                for (AgentID recipient : this.byRemoteAgent.keySet()) {
                    Map<Long, Patient> m = this.byRemoteAgent.get(recipient);
                    for (Long messageId : m.keySet()) {
                        ret.add("recipient: " + recipient + " messageId: " + messageId);
                    }
                }
            }

            return ret.toArray(new String[0]);
        }
    }

    /** Allows threads to wait for a response */
    private class Patient {
        /** 0 when the response is available or an error has been received */
        final private SweetCountDownLatch latch;
        /** The response */
        volatile private byte[] response = null;
        /** Received exception */
        volatile private MessageRoutingException exception = null;

        /** message ID of the request */
        final private long requestID;
        /** Agent ID of recipient of the request */
        final private AgentID recipient;

        private Patient(AgentID agentId, long recipient) {
            this.latch = new SweetCountDownLatch(1);

            this.requestID = recipient;
            this.recipient = agentId;
        }

        /**
         * Wait until the response is available or an error is received
         * 
         * @param timeout
         *            Maximum amount of time to wait before throwing an
         *            exception in milliseconds. 0 means no timeout
         * @return the response
         * @throws MessageRoutingException
         *             If the request failed to be send or if the recipient
         *             disconnected before sending the response.
         * @throws TimeoutException
         *             If the timeout is reached
         */
        private byte[] waitForResponse(long timeout) throws MessageRoutingException, TimeoutException {

            if (timeout == 0) {
                this.latch.await();
            } else {
                boolean b = this.latch.await(timeout, TimeUnit.MILLISECONDS);

                if (!b) {
                    throw new TimeoutException("Timeout reached");
                }
            }

            if (exception != null) {
                throw exception;
            }

            return response;
        }

        /**
         * Set the response and unlock the waiting thread
         * 
         * @param response
         *            the response
         */
        private void setAndUnlock(byte[] response) {
            this.response = response;
            latch.countDown();
        }

        /**
         * Set the exception and unlock the waiting thread
         * 
         * @param exception
         *            received error
         */
        public void setAndUnlock(MessageRoutingException exception) {
            this.exception = exception;
            latch.countDown();
        }
    }

    public MessageReader getIncomingHandler() {
        return this.incomingHandler;
    }

    /** Read incoming messages from the tunnel */
    public class MessageReader implements Runnable {
        /** The local Agent */
        final AgentImpl agent;

        public MessageReader(AgentImpl agent) {
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
                default:
                    // Bad message type. Log it.
                    logger.error("Invalid Message received, wrong type: " + msg);
            }
        }

        private void handleError(ErrorMessage error) {
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
                    long messageId = error.getMessageID();

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

    }

    /**
     * Direct Connection communications manager.
     * Maintains the information related to the agents
     * to which we are directly connected.
     *
     * It sends messages using direct connections with the Remote Agents,
     * instead of forwarding the messages to the router
     *
     * A direct connection manager can hold the following
     *   information on a Remote Agent(RA):
     *   * the RA was contacted by the local agent at least once, by
     *   	sending a message to it. We call this a "seen" agent
     *   * a direct connection was established with the RA.
     *      We call this a "connected" agent
     *   * the RA was seen and we have tried to establish
     *   	a connection with it, but failed for whatever reason.
     *   	We call this a "known" agent
     *
     */
    class DirectConnectionManager {

        public final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_CLIENT_DC);
        /** the remote agents which were "seen" by the local agent */
        private final Set<AgentID> seenAgents;
        /** the remote agents which are "known" by the local agent*/
        private final Set<AgentID> knownAgents;
        /** the remote agents to which a direct connection has already been established */
        private final Map<AgentID, DirectConnection> connectedAgents;
        /** the server for processing incoming direct connection communications*/
        private final DirectConnectionServer dcServer;

        private final boolean disabled;
        private final boolean noServer;

        public DirectConnectionManager(AgentImpl localAgent) {
            this.connectedAgents = new HashMap<AgentID, DirectConnection>();
            this.seenAgents = new HashSet<AgentID>();
            this.knownAgents = new HashSet<AgentID>();

            // (try to) start the direct connection server
            DirectConnectionServer dcServer = null;
            boolean disabled = true;
            boolean noServer = true;
            try {
                DirectConnectionServerConfig config = new DirectConnectionServerConfig();
                disabled = false;
                // create the Server
                dcServer = new DirectConnectionServer(localAgent, config);
                // start its Thread
                Thread dcServerThread = new Thread(dcServer);
                dcServerThread.setDaemon(true);
                dcServerThread.setName("Direct Connection Server: main select thread");
                dcServerThread.start();
                // advertise to the Router
                dcServer.advertise(config);
                // kill on JVM exit
                Runtime.getRuntime().addShutdownHook(dcServer.getShutdownHook());
                noServer = false;
            } catch (UnknownHostException e) {
                logger
                        .error(
                                "Direct connection server is disabled, reason: Problem with the IP address used to bind the server ",
                                e);
                disabled = false;
            } catch (DirectConnectionDisabledException e) {
                logger.debug("Direct connection mode is disabled, reason:" + e.getMessage(), e);
            } catch (MissingPortException e) {
                logger.error("Direct connection server is disabled, reason:" + e.getMessage(), e);
                disabled = false;
            } catch (IOException e) {
                logger.error(
                        "Direct connection server is disabled, reason: Could not initialize the server: " +
                            e.getMessage(), e);
            } catch (MessageRoutingException e) {
                logger
                        .error(
                                "The Direct Connection could not be advertised to the Router, reason: " +
                                    e.getMessage() +
                                    ".As a consequence, this endpoint cannot be contacted for direct communication with other Agents.",
                                e);
                // shut down the server
                dcServer.stop();
            } finally {
                this.dcServer = dcServer;
                this.disabled = disabled;
                this.noServer = noServer;
            }

        }

        public boolean isEnabled() {
            return !disabled;
        }

        public boolean isConnected(AgentID remoteAgent) {
            if (remoteAgent == null)
                throw new IllegalArgumentException("remoteAgent must be non-null");
            return this.connectedAgents.containsKey(remoteAgent);
        }

        public DirectConnection getConnection(AgentID remoteAgent) {
            if (!isConnected(remoteAgent))
                throw new IllegalArgumentException("There is no direct connection with the remote agent " +
                    remoteAgent);

            return this.connectedAgents.get(remoteAgent);
        }

        public void agentDisconnected(AgentID disconnected) {
            // this agent has been disconnected. Remove any trace of him.
            if (!this.seenAgents.remove(disconnected))
                if (!this.knownAgents.remove(disconnected)) {
                    DirectConnection connection = this.connectedAgents.remove(disconnected);
                    if (connection != null)
                        try {
                            connection.close();
                        } catch (IOException e) {
                            ProActiveLogger.logEatedException(logger, "Error while closing " +
                                "the connection for the " + disconnected + " agent:", e);
                        }
                }
        }

        public boolean knows(AgentID remoteAgent) {
            return this.knownAgents.contains(remoteAgent);
        }

        public void seen(AgentID remoteAgent) {
            this.seenAgents.add(remoteAgent);
        }

        public byte[] sendRequest(DataRequestMessage msg, boolean oneWay) throws MessageRoutingException {
            AgentID remoteAgent = msg.getRecipient();
            // get the direct connection
            DirectConnection connection = getConnection(remoteAgent);

            byte[] response = null;
            if (oneWay) { // No response needed, just send it.
                sendRoutingProtocolMessage(msg, connection);
            } else {
                Patient mb = mailboxes.enter(remoteAgent, msg.getMessageID());
                sendRoutingProtocolMessage(msg, connection);

                // block until the result arrives
                try {
                    response = mb.waitForResponse(0);
                } catch (TimeoutException e) {
                    throw new MessageRoutingException("Timeout reached", e);
                }
            }

            return response;
        }

        public void sendReply(DataReplyMessage reply) throws MessageRoutingException {

            // get the direct connection
            AgentID remoteAgent = reply.getRecipient();
            DirectConnection connection = this.getConnection(remoteAgent);

            sendRoutingProtocolMessage(reply, connection);
        }

        // generic one-way send of a pamr protocol message
        private void sendRoutingProtocolMessage(Message message, DirectConnection connection)
                throws MessageRoutingException {
            byte[] msgBuf = message.toByteArray();
            try {
                connection.push(msgBuf);
                if (logger.isTraceEnabled()) {
                    logger.trace("Sent message " + message + " using the direct connection " + connection);
                }
            } catch (IOException e) {
                // Fail fast
                throw new MessageRoutingException("Failed to send a message using the direct connection " +
                    connection, e);
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
