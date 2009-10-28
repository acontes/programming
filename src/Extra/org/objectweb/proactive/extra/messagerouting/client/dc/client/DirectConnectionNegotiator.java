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
import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.Sleeper;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.messagerouting.client.AgentInternal;
import org.objectweb.proactive.extra.messagerouting.exceptions.MalformedMessageException;
import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionReplyACKMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionRequestMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;


/**
 * The DirectConnectionNegotiator is responsible for establishing
 * 	new outgoing direct connections to other remote agents  
 * 
 * It will send a {@link MessageType#DIRECT_CONNECTION_REQUEST} to
 *  the Router and, according to the reply it receives,
 *  will try to establish a direct connection to the remote agent
 * 
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class DirectConnectionNegotiator implements Runnable {

    private final static Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_CLIENT_DC);

    // the local agent
    private final AgentInternal localAgent;

    // the direct connections manager
    private final DirectConnectionManager dcManager;

    // the agent to which we are trying to establish direct connections
    private final AgentID remoteAgent;

    public DirectConnectionNegotiator(DirectConnectionManager dcMan, AgentInternal currentAgent,
            AgentID remoteAgent) {
        this.dcManager = dcMan;
        this.localAgent = currentAgent;
        this.remoteAgent = remoteAgent;
    }

    /*
     * Negotiate with the remote router for obtaining 
     * 	a direct connection to the given agent
     * Negotiating means sending a DC_REQ message to the router
     * 	and waiting for the router reply. If the router replies with
     *  DC_ACK then direct communication is allowed. 
     * In all other cases - problems while sending the message
     * 	to the router, router replies with DC_NACK,
     *  direct connection cannot be established with the
     *  remote agent - it is supposed that direct connection
     *  is not possible with the remote agent. In this case, 
     *  the agent is put in the known (black)list  
     */
    @Override
    public void run() {
        try {
            byte[] result = this.sendDCRequest(remoteAgent);
            DirectConnectionReplyACKMessage replyMsg = new DirectConnectionReplyACKMessage(result, 0);
            InetAddress inetAddr = replyMsg.getInetAddress();
            int port = replyMsg.getPort();
            DirectConnection connection = tryConnection(remoteAgent, inetAddr, port);
            this.dcManager.directConnectionAllowed(remoteAgent, connection);
            return;
        } catch (MessageRoutingException e) {
            logger.info("Direct connection refused for agent " + remoteAgent + " reason:" + e.getMessage());
            this.dcManager.directConnectionRefused(remoteAgent);
        } catch (MalformedMessageException e) {
            logger.info("Direct connection refused for agent " + remoteAgent + " reason:" + e.getMessage());
            this.dcManager.directConnectionRefused(remoteAgent);
        } catch (IOException e) {
            logger.info("All attempts to directly connect to remote router " + remoteAgent + " failed." +
                "Direct connection will be disallowed for agent " + remoteAgent);
            this.dcManager.directConnectionRefused(remoteAgent);
        }
    }

    private byte[] sendDCRequest(AgentID remoteAgent) throws MessageRoutingException {
        long reqId = this.localAgent.getIDGenerator().getAndIncrement();
        DirectConnectionRequestMessage dcReq = new DirectConnectionRequestMessage(reqId, this.localAgent
                .getAgentID(), remoteAgent);

        return this.localAgent.sendRoutingMessage(dcReq, false);
    }

    // try to establish a direct connection
    // it will try two times in a interval of 3s
    // if both attempts will fail, will throw an IOException
    private DirectConnection tryConnection(AgentID remoteAgent, InetAddress inetAddr, int port)
            throws IOException {
        try {
            return new DirectConnection(inetAddr, port);
        } catch (IOException e) {
            logger.warn("First attempt to establish a direct connection with agent " + remoteAgent +
                " failed, because " + e.getMessage());
            logger.debug("Stacktrace is:", e);
            logger.warn("Trying again in 3 seconds...");
            new Sleeper(3000).sleep();
            try {
                return new DirectConnection(inetAddr, port);
            } catch (IOException e1) {
                logger.warn("Second attempt to establish a direct connection with agent " + remoteAgent +
                    " failed, because " + e1.getMessage());
                logger.debug("Stacktrace is:", e1);
                logger.warn("Agent " + remoteAgent + " will be considered unreachable");
                throw e1;
            }
        }
    }

}