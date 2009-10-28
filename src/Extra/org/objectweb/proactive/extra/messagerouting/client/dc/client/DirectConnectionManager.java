/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extra.messagerouting.client.dc.client;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.messagerouting.client.AgentInternal;
import org.objectweb.proactive.extra.messagerouting.client.Patient;
import org.objectweb.proactive.extra.messagerouting.client.WaitingRoom;
import org.objectweb.proactive.extra.messagerouting.client.dc.server.DirectConnectionServer;
import org.objectweb.proactive.extra.messagerouting.client.dc.server.DirectConnectionServerConfig;
import org.objectweb.proactive.extra.messagerouting.client.dc.server.DirectConnectionServerConfig.DirectConnectionDisabledException;
import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DataReplyMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DataRequestMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;


/**
 * Direct Connection communications manager.
 * Maintains the information related to the direct
 * 	connection support at the Agent level
 * 
 * The Manager aggregates the following entities
 * <ul>
 * <li>A {@link DirectConnectionServer} which listens for incoming 
 * 	direct connections coming from other remote agents 
 * </li>
 * <li>A {@link DirectConnectionNegotiator} which is responsible for establishing
 * 	new outgoing direct connections to other remote agents  
 * </li>
 * <li>Data structures containing state information
 * 	about outgoing direct connection attempts  
 * </li>
 * </ul>
 * 
 * Cleanup will be done using shutdown hooks. This is because there
 * 	is no other pointcut where the cleanup code can be called.
 * 
 * It sends messages using direct connections with the Remote Agents, 
 * instead of forwarding the messages to the router
 * 
 * A direct connection manager can hold the following 
 *   information on a Remote Agent(RA):
 *   <ul>
 *   <li>the RA was contacted by the local agent at least once, by
 *   	sending a message to it. We call this a "seen" agent</li>
 *   <li>a direct connection was established with the RA. 
 *      We call this a "connected" agent</li>
 *   <li>the RA was seen and we have tried to establish
 *   	a connection with it, but failed for whatever reason.
 *   	We call this a "known" agent</li>
 * 	</ul>
 * 
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class DirectConnectionManager implements DirectConnectionManagerMBean {

    public final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_CLIENT_DC);
    /** the remote agents which were "seen" by the local agent */
    private final Set<AgentID> seenAgents;
    /** the remote agents which are "known" by the local agent*/
    private final Set<AgentID> knownAgents;
    /** the remote agents to which a direct connection has already been established */
    private final Map<AgentID, DirectConnection> connectedAgents;
    /** the server for processing incoming direct connection communications*/
    private final DirectConnectionServer dcServer;
    /** the local agent */
    private final AgentInternal localAgent;

    private final boolean disabled;
    private final boolean noServer;

    public DirectConnectionManager(AgentInternal localAgent) {
        this.localAgent = localAgent;
        this.connectedAgents = Collections.synchronizedMap(new HashMap<AgentID, DirectConnection>());
        this.seenAgents = Collections.synchronizedSet(new HashSet<AgentID>());
        this.knownAgents = Collections.synchronizedSet(new HashSet<AgentID>());

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
        } catch (IllegalArgumentException e) {
            logger.error("Direct connection server is disabled, reason:" + e.getMessage(), e);
            disabled = false;
        } catch (IOException e) {
            logger.error("Direct connection server is disabled, reason: Could not initialize the server: " +
                e.getMessage(), e);
        } catch (MessageRoutingException e) {
            logger
                    .error(
                            "The Direct Connection could not be advertised to the Router, reason: " +
                                e.getMessage() +
                                ".As a consequence, this endpoint cannot be contacted for direct communication with other Agents.",
                            e);
            // shut down the server
            try {
                dcServer.stop();
            } catch (IllegalStateException exp) {
                // already stopped. good.
            }
        } finally {
            this.dcServer = dcServer;
            this.disabled = disabled;
            this.noServer = noServer;

            // MBean
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = null;
            try {
                // Uniquely identify the MBeans and register them with the platform MBeanServer 
                name = new ObjectName(
                    "org.objectweb.proactive.extra.messagerouting.client.dc.client:type=DirectConnectionManager,name=" +
                        localAgent.getAgentID());
                mbs.registerMBean(this, name);
            } catch (Exception e) {
                logger.warn("Failed to register a JMX MBean for the Direct Connection Manager of agent " +
                    localAgent.getAgentID());
            }
        }
    }

    // this method will be called if the router replied with a DC_ACK message
    // and we got the endpoint address of the seenAgent 
    void directConnectionAllowed(AgentID remoteAgent, DirectConnection connection) {
        synchronized (remoteAgent) {
            this.seenAgents.remove(remoteAgent);
            this.connectedAgents.put(remoteAgent, connection);
        }
    }

    // this method will be called after we tried to connect directly to the 
    // remote agent, but failed
    void directConnectionRefused(AgentID remoteAgent) {
        synchronized (remoteAgent) {
            this.seenAgents.remove(remoteAgent);
            this.knownAgents.add(remoteAgent);
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
        synchronized (connectedAgents) {
            // synchronized needed because we (potentially) make two successive ops on the map
            if (!isConnected(remoteAgent))
                throw new IllegalArgumentException("There is no direct connection with the remote agent " +
                    remoteAgent);

            return this.connectedAgents.get(remoteAgent);
        }
    }

    public void agentDisconnected(AgentID disconnected) {
        // this agent has been disconnected. Remove any trace of him.
        DirectConnection connection = null;
        synchronized (disconnected) {
            if (!this.seenAgents.remove(disconnected))
                if (!this.knownAgents.remove(disconnected)) {
                    connection = this.connectedAgents.remove(disconnected);
                }
        }
        // if there were a connection, close it
        if (connection != null)
            try {
                connection.close();
            } catch (IOException e) {
                ProActiveLogger.logEatedException(logger, "Error while closing " + "the connection for the " +
                    disconnected + " agent:", e);
            }
    }

    public boolean knows(AgentID remoteAgent) {
        synchronized (remoteAgent) {
            return this.knownAgents.contains(remoteAgent);
        }
    }

    public void seen(AgentID remoteAgent) {
        synchronized (remoteAgent) {
            if (!this.seenAgents.contains(remoteAgent)) {
                this.seenAgents.add(remoteAgent);
                this.localAgent.getThreadPool().execute(
                        new DirectConnectionNegotiator(this, this.localAgent, remoteAgent));
            }
        }
    }

    public byte[] sendRequest(DataRequestMessage msg, boolean oneWay) throws MessageRoutingException {
        AgentID remoteAgent = msg.getRecipient();
        // get the direct connection
        DirectConnection connection = getConnection(remoteAgent);

        byte[] response = null;
        if (oneWay) { // No response needed, just send it.
            sendRoutingProtocolMessage(msg, connection);
        } else {
            Patient mb = this.localAgent.getWaitingRoom().enter(remoteAgent, msg.getMessageID());
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

    // release all acquired resources
    public void stop() {
        // shut down the DC server
        if (!this.noServer) {
            try {
                dcServer.stop();
            } catch (IllegalStateException exp) {
                // already stopped. good.
            } finally {
                // also remove the shutdown hook
                try {
                    Runtime.getRuntime().removeShutdownHook(dcServer.getShutdownHook());
                } catch (IllegalStateException e) {
                    // JVM already stoppping. Leave it to die...
                }
            }
        }
    }

    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@ MBean @@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    public String[] getOutboundAgents() throws IllegalStateException {
        if (this.disabled)
            throw new IllegalStateException("This agent started with no direct connection support");

        AgentID agentArray[];
        synchronized (this.connectedAgents) {
            Set<AgentID> agentSet = this.connectedAgents.keySet();
            agentArray = agentSet.toArray(new AgentID[0]);
        }
        String[] ret = new String[agentArray.length];
        for (int i = 0; i < agentArray.length; i++) {
            ret[i] = agentArray[i].toString();
        }
        return ret;
    }

    public String[] getCandidateAgents() throws IllegalStateException {
        if (this.disabled)
            throw new IllegalStateException("This agent started with no direct connection support");

        AgentID agentArray[];
        synchronized (this.seenAgents) {
            agentArray = seenAgents.toArray(new AgentID[0]);
        }
        String[] ret = new String[agentArray.length];
        for (int i = 0; i < agentArray.length; i++) {
            ret[i] = agentArray[i].toString();
        }
        return ret;
    }

    public String[] getFailedAgents() throws IllegalStateException {
        if (this.disabled)
            throw new IllegalStateException("This agent started with no direct connection support");

        AgentID agentArray[];
        synchronized (this.knownAgents) {
            agentArray = knownAgents.toArray(new AgentID[0]);
        }
        String[] ret = new String[agentArray.length];
        for (int i = 0; i < agentArray.length; i++) {
            ret[i] = agentArray[i].toString();
        }
        return ret;
    }

    public boolean isServerStarted() {
        return !this.noServer;
    }

}