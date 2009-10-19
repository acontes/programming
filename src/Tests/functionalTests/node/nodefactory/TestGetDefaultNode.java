/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package functionalTests.node.nodefactory;

import java.io.Serializable;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;

import functionalTests.GCMFunctionalTestDefaultNodes;


public class TestGetDefaultNode extends GCMFunctionalTestDefaultNodes {

    /* Checks that two runtime don't have the same default node URI.
     *
     */
    public TestGetDefaultNode() {
        super(1, 1);
    }

    @Test
    public void test() throws NodeException, ActiveObjectCreationException {
        Node dnode0;
        Node dnode1;

        dnode0 = NodeFactory.getDefaultNode();

        Node node = super.getANode();
        AO ao = PAActiveObject.newActive(AO.class, new Object[] {}, node);
        dnode1 = ao.getDefaultNode();

        String url0 = dnode0.getNodeInformation().getURL();
        String url1 = dnode1.getNodeInformation().getURL();
        Assert.assertFalse(url0.equals(url1));
    }

    static public class AO implements Serializable {

        public AO() {
        }

        public Node getDefaultNode() throws NodeException {
            return NodeFactory.getDefaultNode();
        }
    }
}
