/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
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
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.masterworker.divisibletasks;

import functionalTests.FunctionalTest;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extensions.masterworker.interfaces.Master;
import org.objectweb.proactive.extensions.masterworker.ProActiveMaster;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.junit.Before;
import org.junit.After;
import static junit.framework.Assert.assertTrue;


/**
 * TestDivisibleTasksWithFT
 *
 * @author The ProActive Team
 */
public class TestDivisibleTasksWithFT extends FunctionalTest {
    private URL descriptor = TestDivisibleTasksWithFT.class.getResource("MasterWorkerFT.xml");
    private URL descriptor2 = TestDivisibleTasksWithFT.class.getResource("MasterWorkerFT2.xml");
    private Master<DaCSort, ArrayList<Integer>> master;
    private List<DaCSort> tasks;
    public static final int NB_ELEM = 20000;
    private GCMApplication pad;
    private GCMApplication pad2;
    private GCMVirtualNode vn1;
    private GCMVirtualNode vn2;

    Node submaster1;
    Collection<Node> nodes;
    Collection<Node> nodes1;
    Collection<Node> nodes2;

    @Before
    public void initTest() throws Exception {
        master = new ProActiveMaster<DaCSort, ArrayList<Integer>>();

        pad = PAGCMDeployment.loadApplicationDescriptor(descriptor);
        pad.startDeployment();
        vn1 = pad.getVirtualNode("VN1");

        pad2 = PAGCMDeployment.loadApplicationDescriptor(descriptor2);
        pad2.startDeployment();
        vn2 = pad2.getVirtualNode("VN2");
        vn1.waitReady();
        System.out.println("VN1 is ready");
        vn2.waitReady();
        System.out.println("VN2 is ready");

        nodes = new ArrayList<Node>();
        nodes1 = vn1.getCurrentNodes();
        submaster1 = nodes1.iterator().next();
        nodes1.remove(submaster1);
        nodes2 = vn2.getCurrentNodes();
        nodes.add(submaster1);
        master.addResources(nodes);
        master.addResources(nodes1);
        master.addResources(nodes2);

        master.setResultReceptionOrder(Master.SUBMISSION_ORDER);
        master.setInitialTaskFlooding(1);
        master.setPingPeriod(500);

        tasks = new ArrayList<DaCSort>();
        ArrayList<Integer> bigList = new ArrayList<Integer>();
        for (int i = 0; i < NB_ELEM; i++) {
            bigList.add((int) Math.round(Math.random() * NB_ELEM));
        }
        tasks.add(new DaCSort(bigList));
    }

    @org.junit.Test
    public void action() throws Exception {

        master.solve(tasks);

        try {
            Thread.sleep(1000);
            //System.out.println("\nkill submaster on host: " + submaster1.getVMInformation().getHostName());
            //submaster1.killAllActiveObjects();
            //        	Node worker = nodes1.iterator().next();
            //        	System.out.println("\nkill worker on host: " + worker.getVMInformation().getHostName());
            //        	worker.killAllActiveObjects();

            System.out.println("\nkill worker on host: " + submaster1.getVMInformation().getHostName());
            submaster1.killAllActiveObjects();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            Thread.sleep(40000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // pad2.kill();

        ArrayList<Integer> answer = master.waitOneResult();

        for (int i = 0; i < answer.size() - 1; i++) {
            assertTrue("List sorted", answer.get(i) <= answer.get(i + 1));
        }
        master.solve(tasks);
        Thread.sleep(2000);
        master.clear();

        master.solve(tasks);
        answer = master.waitOneResult();

        for (int i = 0; i < answer.size() - 1; i++) {
            assertTrue("List sorted", answer.get(i) <= answer.get(i + 1));
        }
    }

    @After
    public void endTest() throws Exception {
        master.terminate(true);
        //        pad.kill();
    }
}
