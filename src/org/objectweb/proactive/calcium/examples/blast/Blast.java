/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed, Concurrent
 * computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis Contact:
 * proactive-support@inria.fr
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Initial developer(s): The ProActive Team
 * http://www.inria.fr/oasis/ProActive/contacts.html Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.calcium.examples.blast;

import java.io.File;

import org.objectweb.proactive.calcium.Calcium;
import org.objectweb.proactive.calcium.MonoThreadedManager;
import org.objectweb.proactive.calcium.ResourceManager;
import org.objectweb.proactive.calcium.examples.nqueens.NQueens;
import org.objectweb.proactive.calcium.exceptions.ParameterException;
import org.objectweb.proactive.calcium.exceptions.PanicException;
import org.objectweb.proactive.calcium.interfaces.Skeleton;
import org.objectweb.proactive.calcium.proactive.ProActiveManager;
import org.objectweb.proactive.calcium.skeletons.*;
import org.objectweb.proactive.calcium.statistics.StatsGlobal;
import org.objectweb.proactive.calcium.statistics.StatsGlobalImpl;


public class Blast {
    Skeleton<BlastParameters> root;

    public Blast() {

        /*
         * Blast a database
         * 2.1 Format the database
         * 2.2 Format the database
         * 2.3 Blast the database
         * 2.4 Cleanup
         */
        Pipe<BlastParameters> blastPipe = new Pipe<BlastParameters>(new Seq<BlastParameters>(new ExceuteFormatDB()),
        						  new Seq<BlastParameters>(new ExecuteFormatQuery()),
                				  new Seq<BlastParameters>(new ExecuteBlast()),
                				  new Seq<BlastParameters>(new CleanBlast())
                				  );

        /* 1   Divide the database
         * 2   Blast the database with the query
         * 3   Conquer the query results
         */
        root = new DaC<BlastParameters>(new DivideDB(), 
        								new DivideDBCondition(),
        								blastPipe, 
        								new ConquerResults());
        			
    }

    public static void main(String[] args) {
        Blast blast = new Blast();
        blast.start(new BlastParameters(new File("/tmp/10seq"), new File("/tmp/10"), true));
    }

    private void start(BlastParameters parameters) {
        String descriptor = NQueens.class.getResource("LocalDescriptor.xml")
                                         .getPath();

        //descriptor="/home/mleyton/workspace/ProActive/descriptors/examples/SSH_SGE_Example.xml";
        ResourceManager manager = new MonoThreadedManager();
            //new MultiThreadedManager(8);
            //new ProActiveThreadedManager(descriptor, "local");
            new ProActiveManager(descriptor, "local");

        Calcium<BlastParameters> calcium = new Calcium<BlastParameters>(manager, root);
        calcium.inputParameter(parameters);
        calcium.eval();

		try {
			for(BlastParameters res = calcium.getResult(); 	res != null; res = calcium.getResult()){
				File outPutFile=res.getOutPutFile();
				System.out.println("Result in:"+outPutFile.getAbsolutePath()+ +outPutFile.length() + " [bytes]");
			}
		} catch (ParameterException e) {
			e.printStackTrace();
		} catch (PanicException e) {
			e.printStackTrace();
		}
        
        StatsGlobal stats = calcium.getStatsGlobal();
        System.out.println(stats);
    }
}
