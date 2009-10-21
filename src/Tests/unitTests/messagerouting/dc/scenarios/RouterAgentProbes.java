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

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extra.messagerouting.router.RouterImplMBean;


/**
 * Same as RouterAgent, but with probes for gathering
 * 	additional information:
 * 	- Router MBean
 * 	- MBean for the agent
 */
public class RouterAgentProbes extends RouterAgent {

    protected RouterImplMBean routerMBean;

    public RouterAgentProbes(boolean dcAgent) {
        super(dcAgent);
    }

    public RouterImplMBean getRouterMBean() {
        return routerMBean;
    }

    public void startInfrastructure() throws IOException, ProActiveException {
        super.startInfrastructure();
        this.routerMBean = (RouterImplMBean) router;

        // test if the DC_AD was taken into account
        if (this.agentIsDC) {
            Assert.assertTrue(this.routerMBean.supportsDirectConnections(this.agent.getAgentID().getId()));
        }
    }

}
