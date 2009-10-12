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
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionAdvertiseMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;
import org.objectweb.proactive.extra.messagerouting.router.Client;
import org.objectweb.proactive.extra.messagerouting.router.RouterImpl;


/** Asynchronous handler for {@link MessageType#DIRECT_CONNECTION_ADVERTISE}
 *
 *  All it does is to register the fact that the Client now supports Direct Connection
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class ProcessorDirectConnectionAdvertise extends Processor {

    private final Client dcClient;

    public ProcessorDirectConnectionAdvertise(ByteBuffer messageAsByteBuffer, RouterImpl router, Client client) {
        super(messageAsByteBuffer, router);
        this.dcClient = client;
    }

    @Override
    public void process() throws MalformedMessageException {

        DirectConnectionAdvertiseMessage msg = new DirectConnectionAdvertiseMessage(this.rawMessage.array(),
            0);

        InetSocketAddress endpoint = new InetSocketAddress(msg.getInetAddress(), msg.getPort());

        dcClient.setDirectConnectionEndpoint(endpoint);

        logger.info("Agent " + dcClient.getAgentId() + " advertised direct connection availability");
    }

}
