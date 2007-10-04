/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package functionalTests.group.barrier;

import java.util.Iterator;

import org.junit.Before;
import org.objectweb.proactive.api.ProGroup;
import org.objectweb.proactive.core.group.spmd.ProSPMD;
import org.objectweb.proactive.core.node.Node;

import functionalTests.FunctionalTest;
import functionalTests.descriptor.defaultnodes.TestNodes;
import static junit.framework.Assert.assertTrue;

/**
 * perform a barrier call on an SPMD group
 *
 * @author Laurent Baduel
 */
public class Test extends FunctionalTest {
    private static final long serialVersionUID = 6929428940280564107L;
    private A spmdgroup = null;

    @Before
    public void preConditions() throws Exception {
        new TestNodes().action();

        Object[][] params = {
                { "Agent0" },
                { "Agent1" },
                { "Agent2" }
            };
        Node[] nodes = {
                TestNodes.getSameVMNode(), TestNodes.getLocalVMNode(),
                TestNodes.getRemoteVMNode()
            };
        this.spmdgroup = (A) ProSPMD.newSPMDGroup(A.class.getName(), params,
                nodes);

        assertTrue(spmdgroup != null);
        assertTrue(ProGroup.size(spmdgroup) == 3);
    }

    @org.junit.Test
    public void action() throws Exception {
        this.spmdgroup.start();

        String errors = "";
        Iterator it = ProGroup.getGroup(this.spmdgroup).iterator();
        while (it.hasNext()) {
            errors += ((A) it.next()).getErrors();
        }
        System.err.print(errors);
        assertTrue("".equals(errors));
    }
}
