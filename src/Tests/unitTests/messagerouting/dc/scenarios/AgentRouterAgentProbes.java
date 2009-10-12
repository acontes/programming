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
import org.objectweb.proactive.extra.messagerouting.client.AgentImplMBean;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.router.RouterImplMBean;


/**
 * Same as AgentRouterAgent, but with probes for gathering
 * 	additional information:
 * 	- Router MBean
 * 	- MBean for both agents
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class AgentRouterAgentProbes extends AgentRouterAgent {

    public RouterImplMBean routerMBean;
    public AgentImplMBean agentMBean;
    public AgentImplMBean r_agentMBean;

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
            this.agentMBean = (AgentImplMBean) this.agent;
            this.r_agentMBean = (AgentImplMBean) this.r_agent;
        } catch (ClassCastException e) {
            logger.error(e.getMessage(), e);
            Assert.fail(AgentImplMBean.class.getName() +
                " implementation changed. This unit test should also be re-implemented.");
        }
    }

    public boolean testRemoteAgentState(AgentID remoteAgent, AgentState expectedState) {
        List<String> seenList = Arrays.asList(this.agentMBean.getCandidateAgents());
        List<String> connectedList = Arrays.asList(this.agentMBean.getOutboundAgents());
        List<String> failedList = Arrays.asList(this.agentMBean.getFailedAgents());
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

    public boolean testLocalAgentState(AgentID localAgent, AgentState expectedState) {
        List<String> seenList = Arrays.asList(this.r_agentMBean.getCandidateAgents());
        List<String> connectedList = Arrays.asList(this.r_agentMBean.getOutboundAgents());
        List<String> failedList = Arrays.asList(this.r_agentMBean.getFailedAgents());
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

}
