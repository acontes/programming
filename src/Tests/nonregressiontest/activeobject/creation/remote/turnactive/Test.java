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
package nonregressiontest.activeobject.creation.remote.turnactive;

import nonregressiontest.activeobject.creation.A;
import nonregressiontest.descriptor.defaultnodes.TestNodes;

import org.objectweb.proactive.ProActive;

import testsuite.test.FunctionalTest;

public class Test extends FunctionalTest {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1029555907940725925L;
	A a;
    String name;
    String nodeUrl;
    String remoteHost;

    public Test() {
        super("remote turnActive", "Test turnActive method on a remote node");
    }

    /**
     * @see testsuite.test.FunctionalTest#action()
     */
    @Override
	public void action() throws Exception {
        a = new A("toto");
        a = (A) ProActive.turnActive(a, TestNodes.getRemoteVMNode());
        name = a.getName();
        nodeUrl = a.getNodeUrl();
    }

    @Override
	public boolean preConditions() throws Exception {
        remoteHost = TestNodes.getRemoteHostname();
        return (remoteHost != null);
    }

    /**
     * @see testsuite.test.AbstractTest#initTest()
     */
    @Override
	public void initTest() throws Exception {
    }

    /**
     * @see testsuite.test.AbstractTest#endTest()
     */
    @Override
	public void endTest() throws Exception {
    }

    @Override
	public boolean postConditions() throws Exception {
        return (name.equals("toto") /*&& (nodeUrl.indexOf(remoteHost) != -1)*/);
    }
}
