/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.group.barrier;

import static junit.framework.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.objectweb.proactive.api.PAGroup;
import org.objectweb.proactive.api.PASPMD;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeFactory;

import functionalTests.GCMFunctionalTestDefaultNodes;


/**
 * perform a barrier call on an SPMD group
 *
 * @author The ProActive Team
 */

public class TestBarrier extends GCMFunctionalTestDefaultNodes {
    private A spmdgroup = null;

    public TestBarrier() {
        super(2, 1);
    }

    @Before
    public void preConditions() throws Exception {
        //@snippet-start spmd_creation
        Object[][] params = { { "Agent0" }, { "Agent1" }, { "Agent2" } };
        Node[] nodes = { NodeFactory.getDefaultNode(), super.getANode(), super.getANode() };
        this.spmdgroup = (A) PASPMD.newSPMDGroup(A.class.getName(), params, nodes);
        //@snippet-end spmd_creation
        assertTrue(spmdgroup != null);
        assertTrue(PAGroup.size(spmdgroup) == 3);
    }

    @org.junit.Test
    public void action() throws Exception {
        this.spmdgroup.start();

        String errors = "";
        Iterator<A> it = PAGroup.getGroup(this.spmdgroup).iterator();
        while (it.hasNext()) {
            errors += ((A) it.next()).getErrors();
        }
        System.err.print(errors);
        assertTrue("".equals(errors));
    }
}
