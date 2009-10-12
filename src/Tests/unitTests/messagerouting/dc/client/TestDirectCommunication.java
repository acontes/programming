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
package unitTests.messagerouting.dc.client;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.util.Sleeper;
import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;

import unitTests.UnitTests;
import unitTests.messagerouting.dc.Helpers;
import unitTests.messagerouting.dc.scenarios.AgentRouterAgentProbes;
import unitTests.messagerouting.dc.scenarios.AgentRouterAgentProbes.AgentState;


/**
 * Test whether direct communications between Agents
 * 	correctly occur
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class TestDirectCommunication extends UnitTests {

    private AgentRouterAgentProbes infrastructure;
    private static final long TIMEOUT = 1000;

    @Before
    public void before() throws IOException, ProActiveException {
        infrastructure = new AgentRouterAgentProbes();
        infrastructure.startInfrastructure();
    }

    // onw-way direct communication
    // from local agent to remote agent
    @Test
    public void testOneway() throws MessageRoutingException {
        Sleeper paSleeper = new Sleeper(TIMEOUT);
        AgentID remoteAgentID = infrastructure.getRemoteAgent().getAgentID();
        // the DC_AD message was sent; test if it was processed by the router
        paSleeper.sleep();
        Assert.assertTrue(infrastructure.routerMBean.supportsDirectConnections(remoteAgentID.getId()));
        // the remote agent is taken into account for DC at message transmission
        Assert.assertTrue(infrastructure.testRemoteAgentState(remoteAgentID, AgentState.NOT_SEEN));
        // send a DATA_REQ message with empty payload
        infrastructure.getLocalAgent().sendMsg(remoteAgentID, null, true); // send it oneway
        // at this point, the remoteAgentID should have been taken into account by the negotiator
        Assert.assertTrue(infrastructure.testRemoteAgentState(remoteAgentID, AgentState.SEEN_OR_CONNECTED));
        // after a while, a direct connection should have been established
        paSleeper.sleep();
        Assert.assertTrue(infrastructure.testRemoteAgentState(remoteAgentID, AgentState.CONNECTED));
        // now, sending a second DATA_REQ message should pass through the direct communication channel
        infrastructure.getLocalAgent().tunnelShutdown();
        Assert.assertTrue(infrastructure.getLocalAgent().onewaySend(null, infrastructure.getRemoteAgent(),
                TIMEOUT));
        // nothing should change in the Agent state
        Assert.assertTrue(infrastructure.testRemoteAgentState(remoteAgentID, AgentState.CONNECTED));
    }

    // bidirectional direct communication
    @Test
    public void testBidirectional() throws MessageRoutingException, IOException, ClassNotFoundException {
        Sleeper paSleeper = new Sleeper(TIMEOUT);
        AgentID localAgentID = infrastructure.getLocalAgent().getAgentID();
        AgentID remoteAgentID = infrastructure.getRemoteAgent().getAgentID();
        // the DC_AD message was sent; test if it was processed by the router
        paSleeper.sleep();
        Assert.assertTrue(infrastructure.routerMBean.supportsDirectConnections(remoteAgentID.getId()));
        Assert.assertTrue(infrastructure.routerMBean.supportsDirectConnections(localAgentID.getId()));
        // the remote agent is taken into account for DC only at message transmission
        Assert.assertTrue(infrastructure.testRemoteAgentState(remoteAgentID, AgentState.NOT_SEEN));
        Assert.assertTrue(infrastructure.testLocalAgentState(localAgentID, AgentState.NOT_SEEN));
        // send a DATA_REQ message with an actual payload
        byte[] data = Helpers.stringToByteArray(Helpers.REQUEST_PAYLOAD);
        byte[] reply = infrastructure.getLocalAgent().sendMsg(remoteAgentID, data, false);
        String replyStr = Helpers.byteArrayToString(reply);
        if (replyStr.equals(Helpers.REPLY_PAYLOAD)) {
            logger.info("Got reply:" + replyStr);
        } else {
            Assert.fail("Received invalid reply:" + replyStr);
        }
        // at this point, the remoteAgentID should have been taken into account by the negotiator
        Assert.assertTrue(infrastructure.testRemoteAgentState(remoteAgentID, AgentState.SEEN_OR_CONNECTED));
        paSleeper.sleep();
        Assert.assertTrue(infrastructure.testRemoteAgentState(remoteAgentID, AgentState.CONNECTED));
        // same for localAgentID
        Assert.assertTrue(infrastructure.testLocalAgentState(localAgentID, AgentState.SEEN_OR_CONNECTED));
        paSleeper.sleep();
        Assert.assertTrue(infrastructure.testLocalAgentState(localAgentID, AgentState.CONNECTED));
    }

    @After
    public void after() {
        infrastructure.stopInfrastructure();
    }
}
