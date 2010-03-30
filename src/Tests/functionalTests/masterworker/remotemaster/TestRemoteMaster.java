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
package functionalTests.masterworker.remotemaster;

import functionalTests.FunctionalTest;
import functionalTests.masterworker.A;
import functionalTests.masterworker.basicordered.TestBasicOrdered;
import static junit.framework.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.objectweb.proactive.extensions.masterworker.ProActiveMaster;
import org.objectweb.proactive.extensions.masterworker.interfaces.Master;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Test Master/Worker ordering
 */
public class TestRemoteMaster extends FunctionalTest {
    private URL descriptor = TestBasicOrdered.class
            .getResource("/functionalTests/masterworker/RemoteMaster.xml");
    private Master<A, Integer> master;
    private List<A> tasks;
    //public static final int NB_TASKS = 30;
    public static final int NB_TASKS = 100;

    @org.junit.Test
    public void action() throws Exception {
        System.out.println(descriptor);
        tasks = new ArrayList<A>();
        for (int i = 0; i < NB_TASKS; i++) {
            //A t = new A(i, (NB_TASKS - i) * 100, false);
            A t = new A(i, (NB_TASKS - i), false);
            tasks.add(t);
        }

        master = new ProActiveMaster<A, Integer>(descriptor, "Master");
        // We use the same descriptor as resource, the Master VN should be ignored
        master.addResources(descriptor);
        master.setResultReceptionOrder(Master.SUBMISSION_ORDER);

        master.solve(tasks);

        // We stress the ordering heavily by calling multiple wait methods
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(master.waitOneResult());
        ids.addAll(master.waitKResults(5));
        ids.add(master.waitOneResult());
        ids.addAll(master.waitAllResults());

        // We check that the correct order is received
        Iterator<Integer> it = ids.iterator();
        int last = it.next();
        while (it.hasNext()) {
            int next = it.next();
            assertTrue("Results recieved in submission order", last < next);
            last = next;
        }
        System.out.println("Testing number of workers");
        //in this version, submaster is the worker of main master,
        //so there should be only one worker here
        assertTrue(master.workerpoolSize() == 1);
    }

    @Before
    public void initTest() throws Exception {

    }

    @After
    public void endTest() throws Exception {
        master.terminate(true);
    }
}
