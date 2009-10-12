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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;

import unitTests.UnitTests;
import unitTests.messagerouting.dc.scenarios.AgentRouterAgent;


/**
 * Both agents are started with DC disabled.
 *
 * The router replies with a DC_NACK message,
 * and this is covered by a functional test
 *
 *  This test covers the upper layer Agent primitives.
 *  If we use these to send a DC_REQ, we should receive
 *  a MessageRoutingException
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class TestDCRequestNACK extends UnitTests {

    private AgentRouterAgent infrastructure;

    @Before
    public void before() throws IOException, ProActiveException {
        infrastructure = new AgentRouterAgent(false, false);
        infrastructure.startInfrastructure();
    }

    @Test(expected = MessageRoutingException.class)
    public void testNACKUpperLayer() throws IOException, ProActiveException {
        infrastructure.getLocalAgent().sendDCRequest(infrastructure.getRemoteAgent().getAgentID());
    }

    @After
    public void after() {
        infrastructure.stopInfrastructure();
    }

}
