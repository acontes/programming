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
import java.net.InetAddress;
import java.net.Socket;

import junit.framework.Assert;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.extra.messagerouting.client.Agent;
import org.objectweb.proactive.extra.messagerouting.router.Router;
import org.objectweb.proactive.extra.messagerouting.router.RouterConfig;

import unitTests.messagerouting.dc.TestAgentImpl;


/**
 * A scenario which implies two agents and a router
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class AgentRouterAgent extends Infrastructure {

    protected Router router;
    protected TestAgentImpl agent;
    protected TestAgentImpl r_agent;

    protected boolean localIsDC = false;
    protected boolean remoteIsDC = false;

    public AgentRouterAgent(boolean localDC, boolean remoteDC) {
        super();
        this.localIsDC = localDC;
        this.remoteIsDC = remoteDC;
    }

    public TestAgentImpl getLocalAgent() {
        return agent;
    }

    public TestAgentImpl getRemoteAgent() {
        return r_agent;
    }

    public void startInfrastructure() throws IOException, ProActiveException {
        router = Router.createAndStart(new RouterConfig());
        try {
            PAProperties.PA_PAMR_DIRECT_CONNECTION.setValue(localIsDC);
            agent = new TestAgentImpl(router.getInetAddr(), router.getPort());
            PAProperties.PA_PAMR_DIRECT_CONNECTION.setValue(remoteIsDC);
            r_agent = new TestAgentImpl(router.getInetAddr(), router.getPort());
            // if any of the agents is DC
            if (localIsDC || remoteIsDC) {
                logger.info("Waiting for " + TIMEOUT + " ms, so that the router processes the DC_ADs");
                sleeper.sleep();
            }
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
        agent.shutdown();
        r_agent.shutdown();
        router.stop();
    }

    // try to connect to the DC server at the given address and port
    public boolean checkDCServerStarted(InetAddress inetAddress, int port) {
        try {
            Socket socket = new Socket(inetAddress, port);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
