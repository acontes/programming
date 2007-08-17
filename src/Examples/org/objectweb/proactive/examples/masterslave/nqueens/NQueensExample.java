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
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
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
package org.objectweb.proactive.examples.masterslave.nqueens;

import java.net.MalformedURLException;
import java.util.Vector;

import org.objectweb.proactive.examples.masterslave.AbstractExample;
import org.objectweb.proactive.examples.masterslave.nqueens.query.Query;
import org.objectweb.proactive.examples.masterslave.nqueens.query.QueryExtern;
import org.objectweb.proactive.examples.masterslave.nqueens.query.QueryGenerator;
import org.objectweb.proactive.examples.masterslave.util.Pair;
import org.objectweb.proactive.extra.masterslave.ProActiveMaster;
import org.objectweb.proactive.extra.masterslave.TaskAlreadySubmittedException;
import org.objectweb.proactive.extra.masterslave.TaskException;


/**
 * This examples calculates the Nqueen
 * @author fviale
 *
 */
public class NQueensExample extends AbstractExample {
    public static int nqueen_board_size;
    public static int nqueen_algorithm_depth;
    private ProActiveMaster<QueryExtern, Pair<Long, Long>> master;

    public ProActiveMaster getMaster() {
        return master;
    }

    public static void main(String[] args)
        throws MalformedURLException, TaskAlreadySubmittedException {
        NQueensExample instance = new NQueensExample();

        // Getting command line parameters
        instance.init(args, 2, " nqueen_board_size nqueen_algorithm_depth");

        // Creating the Master
        instance.master = new ProActiveMaster<QueryExtern, Pair<Long, Long>>();
        // Register shutdown process
        instance.registerHook();
        instance.master.addResources(instance.descriptor_url, instance.vn_name);

        System.out.println("Launching NQUEENS solutions finder for n = " +
            nqueen_board_size + " with a depth of " + nqueen_algorithm_depth);

        long sumResults = 0;
        long sumTime = 0;
        long begin = System.currentTimeMillis();

        // Generating the queries for the NQueens
        Vector<Query> unresolvedqueries = QueryGenerator.generateQueries(nqueen_board_size,
                nqueen_algorithm_depth);

        // Splitting Queries
        Vector<QueryExtern> toSolve = new Vector<QueryExtern>();
        while (!unresolvedqueries.isEmpty()) {
            Query query = unresolvedqueries.remove(0);
            Vector<Query> splitted = QueryGenerator.splitAQuery(query);
            if (!splitted.isEmpty()) {
                for (Query splitquery : splitted) {
                    toSolve.add(new QueryExtern(splitquery));
                }
            } else {
                toSolve.add(new QueryExtern(query));
            }
        }
        instance.master.solve(toSolve);

        // Print results on the fly
        while (!instance.master.isEmpty()) {
            try {
                Pair<Long, Long> res = instance.master.waitOneResult();
                sumResults += res.getFirst();
                sumTime += res.getSecond();
                System.out.println("Current nb of results : " + sumResults);
            } catch (TaskException e) {
                // Exception in the algorithm
                e.printStackTrace();
            }
        }

        // Calculation finished, printing summary and total number of solutions
        long end = System.currentTimeMillis();
        int nbslaves = instance.master.slavepoolSize();

        System.out.println("Total number of configurations found for n = " +
            nqueen_board_size + " and with " + nbslaves + " slaves : " +
            sumResults);
        System.out.println("Time needed with " + nbslaves + " slaves : " +
            ((end - begin) / 3600000) +
            String.format("h %1$tMm %1$tSs %1$tLms", end - begin));
        System.out.println("Total slaves calculation time : " +
            (sumTime / 3600000) +
            String.format("h %1$tMm %1$tSs %1$tLms", sumTime));

        System.exit(0);
    }

    @Override
    protected void init_specialized(String[] args) {
        nqueen_board_size = Integer.parseInt(args[2]);
        nqueen_algorithm_depth = Integer.parseInt(args[3]);
    }
}
