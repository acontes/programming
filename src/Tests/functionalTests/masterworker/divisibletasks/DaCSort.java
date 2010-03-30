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

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extensions.masterworker.ProActiveMaster;
import org.objectweb.proactive.extensions.masterworker.TaskException;
import org.objectweb.proactive.extensions.masterworker.interfaces.DivisibleTask;
import org.objectweb.proactive.extensions.masterworker.interfaces.Master;
import org.objectweb.proactive.extensions.masterworker.interfaces.SubMaster;
import org.objectweb.proactive.extensions.masterworker.interfaces.WorkerMemory;

import functionalTests.masterworker.basicordered.TestBasicOrdered;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;


/**
 * A merge-sort like task
 * splits the list into 2 sublists and merge results from subtasks
 *
  @author The ProActive Team
 */
public class DaCSort implements DivisibleTask<ArrayList<Integer>> {

    public static final int MIN_LIST_TO_SPLIT = 1000;

    private ArrayList<Integer> input;

    public DaCSort(ArrayList<Integer> input) {
        this.input = input;
    }

    public ArrayList<Integer> run(WorkerMemory memory, SubMaster master) throws Exception {
        ArrayList l1 = new ArrayList(input.subList(0, input.size() / 2));
        ArrayList l2 = new ArrayList(input.subList(input.size() / 2, input.size()));

        if (l1.size() < MIN_LIST_TO_SPLIT) {
            ArrayList<FinalSort> tasks = new ArrayList<FinalSort>();
            tasks.add(new FinalSort(l1));
            tasks.add(new FinalSort(l2));
            master.solve(tasks);
        } else {
            ArrayList<DaCSort> tasks = new ArrayList<DaCSort>();
            tasks.add(new DaCSort(l1));
            tasks.add(new DaCSort(l2));
            master.solve(tasks);
        }

        List<ArrayList<Integer>> results = master.waitAllResults();
        if (!master.isEmpty()) {
            throw new IllegalStateException("Master is not empty");
        }
        if (master.countAvailableResults() != 0) {
            throw new IllegalStateException("Master is not empty");
        }
        return merge(results.get(0), results.get(1));

    }

    private static ArrayList<Integer> merge(ArrayList<Integer> l1, ArrayList<Integer> l2) {
        // merging
        int size1 = l1.size();
        int size2 = l2.size();
        int i = 0;
        int j = 0;
        ArrayList<Integer> answer = new ArrayList<Integer>();

        while (i < size1 && j < size2) {

            if (l1.get(i) <= l2.get(j)) {
                answer.add(l1.get(i++));
            } else {
                answer.add(l2.get(j++));
            }

        }

        while (i < size1) {
            answer.add(l1.get(i++));
        }

        while (j < size2) {
            answer.add(l2.get(j++));
        }

        return answer;
    }

    public static void main(String[] args) {
        URL descriptor = TestBasicOrdered.class
                .getResource("/functionalTests/masterworker/TestMasterWorker.xml");
        Master<DaCSort, ArrayList<Integer>> master;
        List<DaCSort> tasks;
        int NB_ELEM = 10000;

        master = new ProActiveMaster<DaCSort, ArrayList<Integer>>();
        try {
            master.addResources(descriptor);
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        master.setResultReceptionOrder(Master.SUBMISSION_ORDER);

        tasks = new ArrayList<DaCSort>();
        ArrayList<Integer> bigList = new ArrayList<Integer>();
        for (int i = 0; i < NB_ELEM; i++) {
            bigList.add((int) Math.round(Math.random() * NB_ELEM));
        }
        tasks.add(new DaCSort(bigList));

        master.solve(tasks);

        ArrayList<Integer> answer = null;
        try {
            answer = master.waitOneResult();
        } catch (TaskException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.err.println("TestDivisibleTasks result is: " + answer.size());

        boolean sorted = true;
        for (int i = 0; i < answer.size() - 1 || !sorted; i++) {
            sorted = answer.get(i) <= answer.get(i + 1);
        }
        if (sorted) {
            System.out.println("TestDivisibleTasks : LIST IS SORTED");
        } else {
            System.err.println("TestDivisibleTasks : LIST IS NOT SORTED");
        }

        master.terminate(true);
        /*    	
         ArrayList<Integer> l1 = new ArrayList<Integer>();
         ArrayList<Integer> l2 = new ArrayList<Integer>();
         for (int i = 0; i < 100; i++) {
         l1.add((int) Math.round(Math.random() * 1000));
         l2.add((int) Math.round(Math.random() * 1000));
         }
         Collections.sort(l1);
         Collections.sort(l2);
         ArrayList<Integer> l3 = merge(l1, l2);
         for (int i = 0; i < l3.size() - 1; i++) {
         if (l3.get(i) > l3.get(i + 1)) {
         throw new IllegalStateException("List not sorted");
         }
         }
         */}

    public ArrayList<Integer> run(WorkerMemory memory) throws Exception {
        throw new UnsupportedOperationException();
    }
}
