/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
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
package nonregressiontest.descriptor.services.p2p;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.process.JVMProcessImpl;
import org.objectweb.proactive.core.process.AbstractExternalProcess.StandardOutputMessageLogger;

import testsuite.test.FunctionalTest;



/**
 * @author ProActiveTeam
 * @version 1.0 6 ao�t 2004
 * @since ProActive 2.0.1
 */
public class Test extends FunctionalTest {
    private static String P2P_XML_LOCATION_UNIX = Test.class.getResource(
            "/nonregressiontest/descriptor/services/p2p/TestP2P.xml").getPath();
    JVMProcessImpl process1;
    JVMProcessImpl process;
    Node[] nodeTab;
    ProActiveDescriptor pad;

    public Test() {
        super("P2P JVM acquisition in deployment descriptor",
            "Test service: P2P JVM acquisition in deployment descriptor");
    }

    /* (non-Javadoc)
     * @see testsuite.test.FunctionalTest#action()
     */
    public void action() throws Exception {
        process1 = new JVMProcessImpl(new StandardOutputMessageLogger());
        process1.setClassname(
            "org.objectweb.proactive.p2p.core.service.StartP2PService");
        process1.setParameters("rmi 2900");

        process = new JVMProcessImpl(new StandardOutputMessageLogger());
        process.setClassname(
            "org.objectweb.proactive.p2p.core.service.StartP2PService");
        process.setParameters("rmi 3000 -s //localhost:2900");

        process1.startProcess();
        Thread.sleep(5000);
        process.startProcess();
        Thread.sleep(7000);
         pad = ProActive.getProactiveDescriptor(P2P_XML_LOCATION_UNIX);
        pad.activateMappings();
        VirtualNode vn = pad.getVirtualNode("p2pvn");
        nodeTab = vn.getNodes();
    }

    /* (non-Javadoc)
     * @see testsuite.test.AbstractTest#initTest()
     */
    public void initTest() throws Exception {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see testsuite.test.AbstractTest#endTest()
     */
    public void endTest() throws Exception {
        pad.killall(false);
    }

    public boolean postConditions() throws Exception {
        return nodeTab.length == 3;
    }

    public static void main(String[] args) {
        Test test = new Test();

        try {
            test.action();
            System.out.println(test.postConditions());
            test.endTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
