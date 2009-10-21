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
package unitTests.messagerouting.dc.router;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extra.messagerouting.exceptions.MalformedMessageException;
import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionReplyACKMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;

import unitTests.UnitTests;
import unitTests.messagerouting.dc.scenarios.AgentRouterAgent;


/**
 * Both agents are started in DC mode.
 *
 * We should be able to directly connect
 * from the local agent to the remote agent
 * and also from the remote agent to the local agent
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class TestDCRequestBidirectionalACK extends UnitTests {

    private AgentRouterAgent infrastructure;

    @Before
    public void before() throws IOException, ProActiveException {
        infrastructure = new AgentRouterAgent(true, true);
        infrastructure.startInfrastructure();
    }

    @Test
    public void test() throws MessageRoutingException, MalformedMessageException, UnknownHostException {
        // local agent sends DC_REQ for remote agent
        byte[] response = infrastructure.getLocalAgent().sendDCRequest(
                infrastructure.getRemoteAgent().getAgentID());
        Message reply = Message.constructMessage(response, 0);
        Assert.assertEquals(reply.getType(), MessageType.DIRECT_CONNECTION_ACK);

        DirectConnectionReplyACKMessage replyAck = (DirectConnectionReplyACKMessage) reply;

        InetAddress replyAddr = replyAck.getInetAddress();
        int replyPort = replyAck.getPort();

        Assert.assertEquals(replyAddr, InetAddress.getLocalHost());

        // try to connect directly, it should be possible
        Assert.assertTrue(infrastructure.checkDCServerStarted(replyAddr, replyPort));

        // remote agent sends DC_REQ for connecting to local agent
        response = infrastructure.getRemoteAgent().sendDCRequest(infrastructure.getLocalAgent().getAgentID());
        reply = Message.constructMessage(response, 0);
        Assert.assertEquals(reply.getType(), MessageType.DIRECT_CONNECTION_ACK);

        replyAck = (DirectConnectionReplyACKMessage) reply;
        replyAddr = replyAck.getInetAddress();
        replyPort = replyAck.getPort();

        Assert.assertEquals(replyAddr, InetAddress.getLocalHost());

        // try to connect directly, it should be possible
        Assert.assertTrue(infrastructure.checkDCServerStarted(replyAddr, replyPort));
    }

    @After
    public void after() {
        infrastructure.stopInfrastructure();
    }

}
