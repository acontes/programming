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
package org.objectweb.proactive.extra.messagerouting.router.processor;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.objectweb.proactive.extra.messagerouting.exceptions.MalformedMessageException;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionReplyACKMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionReplyMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionReplyNACKMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionRequestMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;
import org.objectweb.proactive.extra.messagerouting.router.Attachment;
import org.objectweb.proactive.extra.messagerouting.router.Client;
import org.objectweb.proactive.extra.messagerouting.router.RouterImpl;


/** Asynchronous handler for {@link MessageType#DIRECT_CONNECTION_REQUEST}
 *
 * The handler will reply with a {@link MessageType#DIRECT_CONNECTION_NACK}
 * in the following cases:
 * <ul>
 * <li>(possibly corrupted) direct connection messages containing
 * 	target agent IDs unknown to the router</li>
 * <li>the direct connection request is made for an agent which has not
 * 	yet advertised that it supports direct connections, using the
 * 	{@link MessageType#DIRECT_CONNECTION_ADVERTISE} message </li>
 * <li>the two agents which communicate using a direct communication link
 * 	must be on the same network segment.</li>
 * </ul>
 * There are two motivations for the last condition:
 * <ul>
 * 	<li>
 *  It makes no sense to have a direct connection between two endpoints
 *  which are not on the same network segment. It goes against the message router
 *  philosophy, which is to allow communications between remote objects behind a LAN.
 *  If a direct connection is possible between ANY two agents, then probably you don't
 *  want to use message routing at all!
 *  </li>
 *  <li>
 *  It tightens the security. It avoids for instance one possible attack
 *  in which an attacker located outside a LAN sends {@link MessageType#DIRECT_CONNECTION_REQUEST}
 *  messages with all possible {@link DirectConnectionRequestMessage.Field#REMOTE_AGENT_ID}.
 *  If the router replies with {@link MessageType#DIRECT_CONNECTION_ACK}, then the attacker gains
 *  information about the internal network topology by receiving all IP addresses for all
 *  the registered agents.
 *  </li>
 * </ul>
 *
 * Usually, the information about the network topology
 *  is available in a centralized manner - for instance, to the gateway administrator.
 *  This way, the administrator can also configure the router by providing relevant
 *  information about the network topology which will be used by the router
 *  in order to allow direct communication only between agents placed on the same network segment
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class ProcessorDirectConnectionRequest extends Processor {

    private final InetSocketAddress senderEndpoint;

    public ProcessorDirectConnectionRequest(ByteBuffer messageAsByteBuffer, RouterImpl router,
            Attachment attachment) {
        super(messageAsByteBuffer, router);
        this.senderEndpoint = attachment.getRemoteEndpoint();
    }

    @Override
    public void process() throws MalformedMessageException {

        try {
            DirectConnectionRequestMessage dcReq = new DirectConnectionRequestMessage(
                this.rawMessage.array(), 0);

            AgentID targetId = dcReq.getRemoteAgentID();
            AgentID senderId = dcReq.getAgentID();
            long messageId = dcReq.getMessageID();

            DirectConnectionReplyMessage reply;

            Client sender = this.router.getClient(senderId);
            if (sender == null)
                throw new MalformedMessageException("Malformed " + MessageType.DIRECT_CONNECTION_REQUEST +
                    " message " + dcReq + ":Invalid value in the " +
                    DirectConnectionRequestMessage.Field.AGENT_ID + " field: " +
                    " The router knows no agent with ID " + senderId);

            Client target = this.router.getClient(targetId);
            if (target == null) {
                // unknown remote endpoint
                logger.info("Got a " + MessageType.DIRECT_CONNECTION_REQUEST +
                    " message for an unknown agent: " + targetId);
                // NACK the sender
                reply = new DirectConnectionReplyNACKMessage(messageId);
            } else {
                if (!target.acceptsDirectConnections()) {
                    // agent known, but did not register for direct connections
                    logger.info("Agent " + targetId + " is known but has not sent a " +
                        MessageType.DIRECT_CONNECTION_ADVERTISE + " message to the router ");
                    reply = new DirectConnectionReplyNACKMessage(messageId);
                } else {
                    InetSocketAddress dcEndpoint = target.getDirectConnectionEndpoint();
                    if (sameNetworkSegment(dcEndpoint)) {
                        reply = new DirectConnectionReplyACKMessage(messageId, dcEndpoint.getAddress(),
                            dcEndpoint.getPort());
                    } else {
                        logger.info("Remote endpoint " + dcEndpoint +
                            " is not considered to be on the same network segment as the " + " client " +
                            sender + " that requested the direct connection to that remote endpoint ");
                        reply = new DirectConnectionReplyNACKMessage(messageId);
                    }
                }
            }

            if (logger.isDebugEnabled())
                logger.debug("Sending the direct connection reply " + reply + " to the client " + sender);

            sender.sendMessageOrCache(reply.toByteArray());
        } catch (MalformedMessageException e) {
            AgentID recipient;
            try {
                recipient = DirectConnectionRequestMessage.readAgentID(this.rawMessage.array(), 0);
            } catch (MalformedMessageException e1) {
                // don't know the sender
                recipient = null;
            }
            throw new MalformedMessageException(e, recipient);
        }

    }

    /**
     * Test if the remote Direct Connection endpoint is on the
     * 	same network segment as the Agent which sent the {@link MessageType#DIRECT_CONNECTION_REQUEST}
     *
     * @param dcEndpoint
     * @return
     */
    private boolean sameNetworkSegment(InetSocketAddress dcEndpoint) {
        if (this.senderEndpoint == null) {
            // unknown sender endpoint => assume ok
            return true;
        }

        // the information is available at the Router
        return this.router.isAllowed(this.senderEndpoint.getAddress(), dcEndpoint);
    }

}
