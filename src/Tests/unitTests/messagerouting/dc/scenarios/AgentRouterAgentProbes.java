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
package unitTests.messagerouting.dc.scenarios;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extra.messagerouting.client.dc.client.DirectConnectionManagerMBean;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.router.RouterImplMBean;

import unitTests.messagerouting.dc.TestAgentImpl;


/**
 * Same as AgentRouterAgent, but with probes for gathering
 * 	additional information:
 * 	- Router MBean
 * 	- MBean for the direct connection managers of both agents
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class AgentRouterAgentProbes extends AgentRouterAgent {

    public RouterImplMBean routerMBean;
    public DirectConnectionManagerMBean dcManagerMBean;
    public DirectConnectionManagerMBean r_dcManagerMBean;

    public AgentRouterAgentProbes() {
        super(true, true);
    }

    public AgentRouterAgentProbes(boolean b, boolean c) {
        super(b, c);
    }

    public void startInfrastructure() throws IOException, ProActiveException {
        super.startInfrastructure();

        // get the mbeans
        try {
            this.routerMBean = (RouterImplMBean) this.router;
        } catch (ClassCastException e) {
            logger.error(e.getMessage(), e);
            Assert.fail(RouterImplMBean.class.getName() +
                " implementation changed. This unit test should also be re-implemented.");
        }

        try {
            this.dcManagerMBean = (DirectConnectionManagerMBean) this.agent.getDCManager();
            this.r_dcManagerMBean = (DirectConnectionManagerMBean) this.r_agent.getDCManager();
        } catch (ClassCastException e) {
            logger.error(e.getMessage(), e);
            Assert.fail(DirectConnectionManagerMBean.class.getName() +
                " implementation changed. This unit test should also be re-implemented.");
        }

        // test the DC_ADs were taken into account
        if (this.localIsDC) {
            Assert.assertTrue(this.routerMBean.supportsDirectConnections(this.agent.getAgentID().getId()));
        }

        if (this.remoteIsDC) {
            Assert.assertTrue(this.routerMBean.supportsDirectConnections(this.r_agent.getAgentID().getId()));
        }
    }

    public boolean testRemoteAgentState(AgentState expectedState) {
        AgentID remoteAgent = this.r_agent.getAgentID();
        List<String> seenList = Arrays.asList(this.dcManagerMBean.getCandidateAgents());
        List<String> connectedList = Arrays.asList(this.dcManagerMBean.getOutboundAgents());
        List<String> failedList = Arrays.asList(this.dcManagerMBean.getFailedAgents());
        boolean expected;
        switch (expectedState) {
            case NOT_SEEN:
                expected = !seenList.contains(remoteAgent.toString()) &&
                    !connectedList.contains(remoteAgent.toString()) &&
                    !failedList.contains(remoteAgent.toString());
                break;
            case SEEN:
                expected = seenList.contains(remoteAgent.toString()) &&
                    !connectedList.contains(remoteAgent.toString()) &&
                    !failedList.contains(remoteAgent.toString());
                break;
            case SEEN_OR_CONNECTED:
                expected = seenList.contains(remoteAgent.toString()) ||
                    connectedList.contains(remoteAgent.toString()) &&
                    !failedList.contains(remoteAgent.toString());
                break;
            case CONNECTED:
                expected = !seenList.contains(remoteAgent.toString()) &&
                    connectedList.contains(remoteAgent.toString()) &&
                    !failedList.contains(remoteAgent.toString());
                break;
            default:
                expected = true;
                break;
        }
        return expected;
    }

    public static enum AgentState {
        NOT_SEEN, SEEN, SEEN_OR_CONNECTED, CONNECTED
    }

    public boolean testLocalAgentState(AgentState expectedState) {
        AgentID localAgent = this.agent.getAgentID();
        List<String> seenList = Arrays.asList(this.r_dcManagerMBean.getCandidateAgents());
        List<String> connectedList = Arrays.asList(this.r_dcManagerMBean.getOutboundAgents());
        List<String> failedList = Arrays.asList(this.r_dcManagerMBean.getFailedAgents());
        boolean expected;
        switch (expectedState) {
            case NOT_SEEN:
                expected = !seenList.contains(localAgent.toString()) &&
                    !connectedList.contains(localAgent.toString()) &&
                    !failedList.contains(localAgent.toString());
                break;
            case SEEN:
                expected = seenList.contains(localAgent.toString()) &&
                    !connectedList.contains(localAgent.toString()) &&
                    !failedList.contains(localAgent.toString());
                break;
            case SEEN_OR_CONNECTED:
                expected = seenList.contains(localAgent.toString()) ||
                    connectedList.contains(localAgent.toString()) &&
                    !failedList.contains(localAgent.toString());
                break;
            case CONNECTED:
                expected = !seenList.contains(localAgent.toString()) &&
                    connectedList.contains(localAgent.toString()) &&
                    !failedList.contains(localAgent.toString());
                break;
            default:
                expected = true;
                break;
        }
        return expected;
    }

    public boolean injectTestTunnel(TestAgentImpl agent) {
        logger.info("Replacing the tunnel of agent " + agent.getAgentID() + " with a test tunnel");
        try {
            agent.injectTestTunnel(this.router.getInetAddr(), this.router.getPort());
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

}