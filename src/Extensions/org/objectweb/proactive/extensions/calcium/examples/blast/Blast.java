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
package org.objectweb.proactive.extensions.calcium.examples.blast;

import java.io.File;

import org.objectweb.proactive.extensions.calcium.Calcium;
import org.objectweb.proactive.extensions.calcium.MultiThreadedManager;
import org.objectweb.proactive.extensions.calcium.ResourceManager;
import org.objectweb.proactive.extensions.calcium.Stream;
import org.objectweb.proactive.extensions.calcium.examples.nqueens.NQueens;
import org.objectweb.proactive.extensions.calcium.exceptions.MuscleException;
import org.objectweb.proactive.extensions.calcium.exceptions.PanicException;
import org.objectweb.proactive.extensions.calcium.futures.Future;
import org.objectweb.proactive.extensions.calcium.skeletons.DaC;
import org.objectweb.proactive.extensions.calcium.skeletons.Pipe;
import org.objectweb.proactive.extensions.calcium.skeletons.Seq;
import org.objectweb.proactive.extensions.calcium.skeletons.Skeleton;
import org.objectweb.proactive.extensions.calcium.statistics.StatsGlobal;


public class Blast {
    Skeleton<BlastParameters, BlastParameters> root;

    public Blast() {
        /*
         * Blast a database
         * 2.1 Format the database
         * 2.2 Format the database
         * 2.3 Blast the database
         * 2.4 Cleanup
         */
        Pipe<BlastParameters, BlastParameters> blastPipe = new Pipe<BlastParameters, BlastParameters>(new Seq<BlastParameters, BlastParameters>(
                    new ExecuteFormatDB()),
                new Seq<BlastParameters, BlastParameters>(new ExecuteFormatQuery()),
                new Seq<BlastParameters, BlastParameters>(new ExecuteBlast()),
                new Seq<BlastParameters, BlastParameters>(new CleanBlast()));

        /* 1   Divide the database
         * 2   Blast the database with the query
         * 3   Conquer the query results  */
        root = new DaC<BlastParameters, BlastParameters>(new DivideDB(),
                new DivideDBCondition(), blastPipe, new ConquerResults());
    }

    public static void main(String[] args) throws Exception {
        BlastParameters param = new BlastParameters(new File("/tmp/blast.query"),
                new File("/tmp/blast.db"), true, 100 * 1024);
        param.setRootParameter(true);
        Blast blast = new Blast();
        blast.start(param);
    }

    private void start(BlastParameters parameters)
        throws InterruptedException, PanicException {
        String descriptor = NQueens.class.getResource("LocalDescriptor.xml")
                                         .getPath();

        //descriptor="/home/mleyton/workspace/ProActive/descriptors/examples/SSH_SGE_Example.xml";
        ResourceManager manager = new MultiThreadedManager(8);

        //new MonoThreadedManager();
        //new ProActiveThreadedManager(descriptor, "local");
        //new ProActiveManager(descriptor, "local");
        Calcium calcium = new Calcium(manager);
        Stream<BlastParameters, BlastParameters> stream = calcium.newStream(root);
        Future<BlastParameters> future = stream.input(parameters);
        calcium.boot();

        try {
            BlastParameters res = future.get();
            File outPutFile = res.getOutPutFile();
            System.out.println("Result in:" + outPutFile.getAbsolutePath() +
                +outPutFile.length() + " [bytes]");
            System.out.println(future.getStats());
        } catch (MuscleException e) {
            e.printStackTrace();
        }

        StatsGlobal stats = calcium.getStatsGlobal();
        System.out.println(stats);
        calcium.shutdown();
    }
}
