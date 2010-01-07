/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of
 * 						   Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
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
 * If needed, contact us to obtain a release under GPL Version 2.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.hpc.exchange;

import static junit.framework.Assert.assertTrue;

import org.objectweb.proactive.api.PAActiveObject;

import functionalTests.GCMFunctionalTestDefaultNodes;


public class TestComplexArray extends GCMFunctionalTestDefaultNodes {
    private B b1, b2, b3;

    public TestComplexArray() {
        super(2, 2);
    }

    @org.junit.Test
    public void action() throws Exception {
        b1 = PAActiveObject.newActive(B.class, new Object[] {}, super.getANode());
        b2 = PAActiveObject.newActive(B.class, new Object[] {}, super.getANode());
        b3 = PAActiveObject.newActive(B.class, new Object[] {}, super.getANode());

        b1.start(1, b1, b2, b3);
        b2.start(2, b1, b2, b3);
        b3.start(3, b1, b2, b3);
    }

    @org.junit.After
    public void after() throws Exception {
        double cs_b1_1 = b1.getChecksum1();
        double cs_b2_1 = b2.getChecksum1();

        double cs_b2_2 = b2.getChecksum2();
        double cs_b3_2 = b3.getChecksum2();

        assertTrue(cs_b1_1 == cs_b2_1);
        assertTrue(cs_b2_2 == cs_b3_2);
    }
}
