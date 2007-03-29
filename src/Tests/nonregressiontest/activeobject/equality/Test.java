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
package nonregressiontest.activeobject.equality;

import nonregressiontest.descriptor.defaultnodes.TestNodes;
import nonregressiontest.group.A;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.group.ProActiveGroup;
import org.objectweb.proactive.core.util.UrlBuilder;

import testsuite.test.Assertions;
import testsuite.test.FunctionalTest;


/**
 * This test has to be run AFTER the tests of :
 * - active object creation
 * - lookupactive
 * - groups
 *
 * @author Matthieu Morel
 */
public class Test extends FunctionalTest {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1450115095761511338L;
	private A group1 = null;
    private A group2 = null;
    private A group3 = null;
    private A a1 = null;
    private A a2 = null;
    private nonregressiontest.activeobject.equality.RegisteredObject registeredA1 = null;
    private nonregressiontest.activeobject.equality.RegisteredObject registeredA2 = null;

    public Test() {
        super("comparisons with active objects using equals",
            "compares active objects, groups and standard objects using with the equals method");
    }

    @Override
	public void action() throws Exception {
        a1 = (A) ProActive.newActive(A.class.getName(), new Object[] { "a1" },
                TestNodes.getSameVMNode());
        a2 = (A) ProActive.newActive(A.class.getName(), new Object[] { "a2" },
                TestNodes.getLocalVMNode());

        group1 = (A) ProActiveGroup.newGroup(A.class.getName());
        group2 = (A) ProActiveGroup.newGroup(A.class.getName());
        group3 = (A) ProActiveGroup.newGroup(A.class.getName());

        registeredA1 = (nonregressiontest.activeobject.equality.RegisteredObject) ProActive.newActive(nonregressiontest.activeobject.equality.RegisteredObject.class.getName(),
                new Object[] { "toto" });
        registeredA1.register();
        registeredA2 = (nonregressiontest.activeobject.equality.RegisteredObject) ProActive.lookupActive(nonregressiontest.activeobject.equality.RegisteredObject.class.getName(),
                UrlBuilder.buildUrlFromProperties("localhost", "A"));
        
        ProActiveGroup.getGroup(group1).add(a1);
        ProActiveGroup.getGroup(group1).add(a2);

        ProActiveGroup.getGroup(group2).add(a1);
        ProActiveGroup.getGroup(group2).add(a2);

        ProActiveGroup.getGroup(group3).add(a1);
    }

    @Override
	public void initTest() throws Exception {
        // nothing to do
    }

    @Override
	public void endTest() throws Exception {
        // nothing to do
    }

    @Override
	public boolean postConditions() throws Exception {
        Assertions.assertEquals(group1, group2);
        Assertions.assertEquals(new Integer(group1.hashCode()),
            new Integer(group2.hashCode()));
        Assertions.assertNonEquals(group1, group3);
        Assertions.assertNonEquals(a1, "x");
        Assertions.assertNonEquals(a1, null);
        Assertions.assertNonEquals(group1, "x");
        Assertions.assertNonEquals(group1, null);
        Assertions.assertNonEquals(a1, group3);
        Assertions.assertNonEquals(group3, a1);
        Assertions.assertEquals(registeredA1, registeredA2);
        Assertions.assertEquals(new Integer(registeredA1.hashCode()),
            new Integer(registeredA2.hashCode()));
        return true;
    }
}
