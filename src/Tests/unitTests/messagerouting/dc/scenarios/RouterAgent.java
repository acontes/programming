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

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.extra.messagerouting.client.ProActiveMessageHandler;
import org.objectweb.proactive.extra.messagerouting.router.Router;
import org.objectweb.proactive.extra.messagerouting.router.RouterConfig;

import unitTests.UnitTests;
import unitTests.messagerouting.dc.TestAgentImpl;
import unitTests.messagerouting.dc.client.TestAgentStartup;
import functionalTests.ft.Agent;


/**
 * A scenario which implies one Router and one Agent
 * It is assumed that {@link TestAgentStartup} passes.
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class RouterAgent extends UnitTests {

    protected Router router;
    protected TestAgentImpl agent;
    private boolean agentIsDC;

    protected static final int DC_PORT = 18989;

    public void startInfrastructure(boolean dcAgent) throws IOException, ProActiveException {
        this.agentIsDC = dcAgent;
        router = Router.createAndStart(new RouterConfig());
        try {
            PAProperties.PA_NET_ROUTER_DIRECT_CONNECTION.setValue(dcAgent);
            PAProperties.PA_NET_ROUTER_DC_PORT.setValue(DC_PORT);
            agent = new TestAgentImpl(router.getInetAddr(), router.getPort(), ProActiveMessageHandler.class);
        } catch (SecurityException e) {
            logger.error(e.getMessage(), e);
            Assert.fail(Agent.class.getName() +
                " implementation changed. This unit test should also be re-implemented.");
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            Assert.fail(Agent.class.getName() +
                " implementation changed. This unit test should also be re-implemented.");
        } catch (NoSuchFieldException e) {
            logger.error(e.getMessage(), e);
            Assert.fail(Agent.class.getName() +
                " implementation changed. This unit test should also be re-implemented.");
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
            Assert.fail(Agent.class.getName() +
                " implementation changed. This unit test should also be re-implemented.");
        }

    }

    public void stopInfrastructure() {
        if (agentIsDC)
            agent.getDCManager().stop();
        agent.getTheTunnel().shutdown();
        router.stop();
    }
}
