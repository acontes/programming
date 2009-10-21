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
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;

import unitTests.UnitTests;
import unitTests.messagerouting.dc.Helpers;
import unitTests.messagerouting.dc.scenarios.AgentRouterAgentProbes;
import unitTests.messagerouting.dc.scenarios.Infrastructure;
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

    @Before
    public void before() throws IOException, ProActiveException {
        infrastructure = new AgentRouterAgentProbes();
        infrastructure.startInfrastructure();
    }

    // onw-way direct communication
    // from local agent to remote agent
    @Test
    public void testOneway() throws MessageRoutingException {
        AgentID remoteAgentID = infrastructure.getRemoteAgent().getAgentID();
        // the remote agent is taken into account for DC at message transmission
        Assert.assertTrue(infrastructure.testRemoteAgentState(AgentState.NOT_SEEN));
        // send a DATA_REQ message with empty payload
        infrastructure.getLocalAgent().sendMsg(remoteAgentID, null, true); // send it oneway; cannot block
        // at this point, the remoteAgentID should have been taken into account by the negotiator
        Assert.assertTrue(infrastructure.testRemoteAgentState(AgentState.SEEN_OR_CONNECTED));
        // after a while, a direct connection should have been established
        infrastructure.sleep();
        Assert.assertTrue(infrastructure.testRemoteAgentState(AgentState.CONNECTED));
        // now, sending a second DATA_REQ message should pass through the direct communication channel
        Assert.assertTrue(
                "Could not inject a test tunnel for the local agent. Check the error log for details.",
                infrastructure.injectTestTunnel(infrastructure.getLocalAgent()));
        Assert.assertTrue(infrastructure.getLocalAgent().onewaySend(null, infrastructure.getRemoteAgent(),
                Infrastructure.TIMEOUT));
        // nothing should change in the Agent state
        Assert.assertTrue(infrastructure.testRemoteAgentState(AgentState.CONNECTED));
    }

    // bidirectional direct communication
    @Test
    public void testBidirectional() throws MessageRoutingException, IOException, ClassNotFoundException {
        // the remote agent is taken into account for DC only at message transmission
        Assert.assertTrue(infrastructure.testRemoteAgentState(AgentState.NOT_SEEN));
        Assert.assertTrue(infrastructure.testLocalAgentState(AgentState.NOT_SEEN));
        // send a DATA_REQ message with an actual payload
        // as this is a blocking operation, we'll do it in a separate thread
        asyncSendReq();

        Assert.assertTrue(infrastructure.testRemoteAgentState(AgentState.SEEN_OR_CONNECTED));
        Assert.assertTrue(infrastructure.testLocalAgentState(AgentState.SEEN_OR_CONNECTED));
        infrastructure.sleep();
        Assert.assertTrue(infrastructure.testRemoteAgentState(AgentState.CONNECTED));
        Assert.assertTrue(infrastructure.testLocalAgentState(AgentState.CONNECTED));

        // now, a DATA_REQ message should pass directly through the direct communication channels
        Assert.assertTrue(
                "Could not inject a test tunnel for the local agent. Check the error log for details.",
                infrastructure.injectTestTunnel(infrastructure.getLocalAgent()));
        Assert.assertTrue(
                "Could not inject a test tunnel for the remote agent. Check the error log for details.",
                infrastructure.injectTestTunnel(infrastructure.getRemoteAgent()));
        asyncSendReq();
        // nothing should change in the Agent state
        Assert.assertTrue(infrastructure.testRemoteAgentState(AgentState.CONNECTED));
        Assert.assertTrue(infrastructure.testLocalAgentState(AgentState.CONNECTED));

    }

    // remove the above line once the InterruptedException is thrown in SweetCountDownLatch
    @SuppressWarnings("deprecation")
    private void asyncSendReq() {
        MessageSender ms = new MessageSender(infrastructure);
        Thread senderThread = new Thread(ms);
        senderThread.start();
        long timeout = Infrastructure.TIMEOUT + Long.parseLong(Helpers.REQUEST_PAYLOAD);
        if (infrastructure.getLocalAgent().waitReply(infrastructure.getRemoteAgent(), timeout)) {
            // did the sender thread finish?
            if (ms.waitToFinish(Infrastructure.TIMEOUT)) {
                Assert.assertFalse("Attempt to send the message to the remote endpoint failed ", ms
                        .failedSend());
                Assert.assertTrue("Received unexpected reply", ms.gotExpectedReply());
            } else {
                // no other way; the InterruptedException was eaten in SweetCountDownLatch
                senderThread.stop();
                Assert.fail("Blocking operation: AgentImpl.sendMsg - did not finish in a timely manner.");
            }
        } else {
            // remote agent did not reply
            senderThread.stop();
            // was the message sent?
            Assert.assertFalse("Attempt to send the message to the remote endpoint failed ", ms.failedSend());
            // sent, but not replied to
            Assert.fail("Message was sent, but got no reply from the remote agent");
        }
    }

    public static class MessageSender implements Runnable {

        private final AgentRouterAgentProbes infrastructure;
        private volatile boolean failedToSend;
        private volatile boolean replyOK;

        public boolean failedSend() {
            return failedToSend;
        }

        public boolean gotExpectedReply() {
            return replyOK;
        }

        private volatile boolean finishedWork = false;

        public boolean waitToFinish(long timeout) {
            synchronized (this) {
                try {
                    if (!finishedWork) {
                        this.wait(timeout);
                    }
                } catch (InterruptedException e) {
                    // return
                }
            }

            return finishedWork;
        }

        public void notifyFinished() {
            synchronized (this) {
                this.finishedWork = true;
                this.notifyAll();
            }
        }

        public MessageSender(AgentRouterAgentProbes infr) {
            this.infrastructure = infr;
            this.failedToSend = false;
            this.replyOK = false;
            this.finishedWork = false;
        }

        @Override
        public void run() {
            try {
                AgentID remoteAgentID = infrastructure.getRemoteAgent().getAgentID();
                byte[] data = Helpers.stringToByteArray(Helpers.REQUEST_PAYLOAD);
                logger.info("Sending request:" + Helpers.REQUEST_PAYLOAD);
                byte[] reply = infrastructure.getLocalAgent().sendMsg(remoteAgentID, data, false);
                String replyStr = Helpers.byteArrayToString(reply);
                if (replyStr.equals(Helpers.REPLY_PAYLOAD)) {
                    logger.info("Got reply:" + replyStr);
                    replyOK = true;
                } else {
                    logger.info("Unexpected reply:" + replyStr);
                    replyOK = false;
                }
            } catch (MessageRoutingException e) {
                // only internalSendMsg cand fail here; means we were not able to send the message
                logger.error(e.getMessage(), e);
                this.failedToSend = true;
            }
            this.notifyFinished();
        }

    }

    @After
    public void after() {
        infrastructure.stopInfrastructure();
    }
}
