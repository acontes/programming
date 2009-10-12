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
package functionalTests.messagerouting.router.blackbox;

import java.io.IOException;
import java.net.InetAddress;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.core.util.ProActiveRandom;
import org.objectweb.proactive.core.util.Sleeper;
import org.objectweb.proactive.extra.messagerouting.client.Tunnel;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionAdvertiseMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionRequestMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;


/**
 * Test how the router handles DC_REQ messages
 *
 * This test assumes(without checking) that TestConnection passes
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class TestDirectConnection extends TwoAgentsBlackBox {

    // send a DC_REQ with a random AgentID different of the already-registered one
    @Test
    public void testNACKRandom() throws IOException {
        long randomAgentId = nextIntNeq(agentID.getId(), r_agentID.getId());
        long randomMessageId = ProActiveRandom.nextPosLong();
        DirectConnectionRequestMessage dcReq = new DirectConnectionRequestMessage(randomMessageId, agentID,
            new AgentID(randomAgentId));
        this.tunnel.write(dcReq.toByteArray());
        byte[] response = this.tunnel.readMessage();
        Message reply = Message.constructMessage(response, 0);
        Assert.assertEquals(reply.getType(), MessageType.DIRECT_CONNECTION_NACK);
    }

    // send a DC_REQ for an unregistered agent
    @Test
    public void testNACKNotRegistered() throws IOException {
        long randomMessageId = ProActiveRandom.nextPosLong();
        DirectConnectionRequestMessage dcReq = new DirectConnectionRequestMessage(randomMessageId,
            this.agentID, this.r_agentID);
        this.tunnel.write(dcReq.toByteArray());
        byte[] response = this.tunnel.readMessage();
        Message reply = Message.constructMessage(response, 0);
        Assert.assertEquals(reply.getType(), MessageType.DIRECT_CONNECTION_NACK);
    }

    // if we register the agent, we should receive ACK
    @Test
    public void testACK() throws IOException {
        registerDC(r_tunnel);

        long randomMessageId = ProActiveRandom.nextPosLong();
        DirectConnectionRequestMessage dcReq = new DirectConnectionRequestMessage(randomMessageId,
            this.agentID, this.r_agentID);
        this.tunnel.write(dcReq.toByteArray());
        byte[] response = this.tunnel.readMessage();
        Message reply = Message.constructMessage(response, 0);
        Assert.assertEquals(reply.getType(), MessageType.DIRECT_CONNECTION_ACK);
    }

    private void registerDC(Tunnel t) throws IOException {
        long randomMessageId = ProActiveRandom.nextPosLong();
        DirectConnectionAdvertiseMessage dcAd = new DirectConnectionAdvertiseMessage(randomMessageId,
            InetAddress.getLocalHost(), ProActiveRandom.nextInt(65535));
        t.write(dcAd.toByteArray());
        // expect to succeed
        // wait a while, for the ad to be processed
        new Sleeper(1000).sleep();
    }

    private long nextIntNeq(long id, long id2) {
        long ret = id;
        while (ret == id || ret == id2) {
            ret = ProActiveRandom.nextPosLong();
        }
        return ret;
    }

}
