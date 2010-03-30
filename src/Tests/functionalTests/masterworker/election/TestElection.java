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
package functionalTests.masterworker.election;

import functionalTests.FunctionalTest;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import functionalTests.masterworker.A;

import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.extensions.masterworker.interfaces.Master;
import org.objectweb.proactive.extensions.masterworker.core.AOSubMaster;
import org.objectweb.proactive.extensions.masterworker.core.AOSubWorkerManager;
import org.objectweb.proactive.extensions.masterworker.ProActiveMaster;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.junit.Before;
import org.junit.After;
import static junit.framework.Assert.assertTrue;


/**
 * TestElection
 *
 * @author The ProActive Team
 */
public class TestElection extends FunctionalTest {

    private URL descriptor = TestElection.class
            .getResource("/functionalTests/masterworker/TestMasterWorkerForElection.xml");

    private Master<A, Integer> master;
    private List<A> tasks;
    public static final int NB_TASKS = 1000;
    private GCMApplication pad;
    private GCMVirtualNode vn1;
    Collection<Node> nodes;

    @Before
    public void initTest() throws Exception {

        master = new ProActiveMaster<A, Integer>();

        pad = PAGCMDeployment.loadApplicationDescriptor(descriptor);
        pad.startDeployment();
        vn1 = pad.getVirtualNode("VN1");
        vn1.waitReady();
        System.out.println("VN1 is ready");
        nodes = vn1.getCurrentNodes();
        long numberOfNodes = vn1.getNbCurrentNodes();
        System.out.println("number of nodes: " + numberOfNodes);
        master.addResources(nodes);

        //ensure the deployment
        Thread.sleep(20000);

        master.setResultReceptionOrder(Master.SUBMISSION_ORDER);
        master.setInitialTaskFlooding(1);
        master.setPingPeriod(500);

        tasks = new ArrayList<A>();
        for (int i = 0; i < NB_TASKS; i++) {
            A t = new A(i, 200, false);
            tasks.add(t);
        }
    }

    @org.junit.Test
    public void action() throws Exception {

        master.solve(tasks);
        System.out.println("Waiting for one result");
        List<Integer> ids = master.waitKResults(1);

        System.out.println("Killing submaster");
        killOneSubMaster(0);

        int numberofResults = master.countAvailableResults();

        System.out.println("cur number: " + numberofResults);

        System.out.println("Waiting for the remaining results");
        List<Integer> ids2 = master.waitAllResults();
        ids.addAll(ids2);

        assertTrue("Only one worker left", master.workerpoolSize() == 1);
        assertTrue("all results back", ids.size() == NB_TASKS);

        Iterator<Integer> it = ids.iterator();
        int last = it.next();
        while (it.hasNext()) {
            int next = it.next();
            assertTrue("Results recieved in submission order", last < next);
            last = next;
        }

    }

    @After
    public void endTest() throws Exception {
        master.terminate(true);
        pad.kill();
    }

    private void killOneSubMaster(int phase) {
        Iterator<Node> itr = nodes.iterator();
        boolean foundFlag = false;

        while (itr.hasNext()) {

            Node cur = itr.next();
            Object[] obj = null;

            try {

                System.out.println(" ");
                obj = cur.getActiveObjects();

                String output = new String();
                output += "debug phase: " + phase + cur + " ";
                for (int i = 0; i < obj.length; i++)
                    output += obj[i];

                System.out.println(output);

                for (int i = 0; i < obj.length; i++)
                    if (obj[i] instanceof AOSubMaster || obj[i] instanceof AOSubWorkerManager) {
                        System.out.println("SubMaster found");

                        try {
                            ProActiveRuntime prt = cur.getProActiveRuntime();
                            prt.killRT(false);
                            itr.remove();
                        } catch (Exception e) {
                            System.out.println("kill exception");
                            e.printStackTrace();
                        }

                        //foundFlag = true;
                        //break;
                        return;
                    }
            } catch (Exception e) {
                System.out.println("getActiveObjects exception");
                e.printStackTrace();
            }

        }

        if (!foundFlag)
            System.out.println("SubMaster not found");
    }

}
