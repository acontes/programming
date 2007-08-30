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
package org.objectweb.proactive.examples.mpi.proactive_poisson3d_sliced;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.compi.MPI;
import org.objectweb.proactive.compi.MPISpmd;
import org.objectweb.proactive.compi.control.ProActiveMPI;


/**
 *  This example uses a simple mpi program which implements a simple Jacobi iteration for approximating
 *  the solution to a linear system of equations.
 */
public class Poisson3dSlicedWrappingComp {
    static public void main(String[] args) {
        Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

        if (args.length != 1) {
            logger.error("Usage: java " +
                Poisson3dSlicedWrapping.class.getName() + " <deployment file>");
            System.exit(0);
        }

        ProActiveConfiguration.load();
        // comment
        VirtualNode vnPoisson3d_1;
        VirtualNode vnPoisson3d_2;
        ProActiveDescriptor pad = null;

        try {
            pad = ProActive.getProactiveDescriptor("file:" + args[0]);

            ArrayList spmdList = new ArrayList<MPISpmd>();

            // gets virtual node 
            VirtualNode[] vns = pad.getVirtualNodes();
            for (int i = 0; i < vns.length; i++) {
                VirtualNode node = vns[i];
                node.activate();
                MPISpmd mpiSpmd_01 = MPI.newMPISpmd(node);
                System.out.println(mpiSpmd_01);
                spmdList.add(mpiSpmd_01);
            }
            //            vnPoisson3d_1 = pad.getVirtualNodes()
            //            getVirtualNode("poisson3d-1");
            //            vnPoisson3d_1.activate();
            //            vnPoisson3d_2 = pad.getVirtualNode("poisson3d-2");
            //            vnPoisson3d_2.activate();

            //            MPISpmd mpiSpmd_02 = MPI.newMPISpmd(vnPoisson3d_2);            
            //            System.out.println(mpiSpmd_01);
            //            System.out.println(mpiSpmd_02);
            //
            //            spmdList.add(mpiSpmd_01);
            //            spmdList.add(mpiSpmd_02);
            ProActiveMPI.deploy(spmdList);
            //            MPIResult res1 = mpiSpmd_01.();
            //            MPIResult res2 = mpiSpmd_02.startMPI();
            //            
            //            logger.info("[POISSON3D] Result value1 : " + res1.getReturnValue());
            //            logger.info("[POISSON3D] Result value2 : " + res2.getReturnValue());
            //            
            //            vnPoisson3d_1.killAll(false);
            //            vnPoisson3d_2.killAll(false);
            //            System.exit(0);
        } catch (ProActiveException e) {
            e.printStackTrace();
            logger.error("!!! Error when reading descriptor");
        }
    }
}
