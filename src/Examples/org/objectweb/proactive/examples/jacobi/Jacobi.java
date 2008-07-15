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
 */
package org.objectweb.proactive.examples.jacobi;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PADeployment;
import org.objectweb.proactive.api.PASPMD;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.node.NodeException;


public class Jacobi {

    /**
    * Number of columns of SubMatrix
    */
//    public static int nbMatrixY;

    /**
     * Number of lines of SubMatrix
     */
//    public static int nbMatrixX;
    
    
    public static int subMatrixWidth;
    public static int subMatrixHeight;
    public static int totalHeight = 1680;
    public static int totalWidth = 1680;

    /**
     * Max number of iterations
     */
    public static int nbIterations = 100;

    /**
     * Min diff to stop
     */
    public static final double MINDIFF = 0.001;

    /**
     * Default external border value
     */
    public static final double DEFAULT_BORDER_VALUE = 0;

    public static void main(String[] args) {
        if (args.length < 4) {
            printUsage();
        }
        ProActiveDescriptor proActiveDescriptor = null;
        String[] nodes = null;

        try {
            proActiveDescriptor = PADeployment.getProactiveDescriptor("file:" +
                    args[0]);
            if (args[1] != null) {
                nbIterations = Integer.parseInt(args[1]);
                System.out.println("RUNNING JACOBI WITH " + nbIterations +
                    " iterations");
            }
            proActiveDescriptor.activateMappings();
            VirtualNode vn = proActiveDescriptor.getVirtualNode("matrixNode");
            try {
                nodes = vn.getNodesURL();
            } catch (NodeException e) {
                System.err.println("** NodeException **");
            }

            int nbMatrixX = Integer.parseInt(args[2]);
            int nbMatrixY = Integer.parseInt(args[3]);

            if (args.length == 6) {
                totalWidth = Integer.parseInt(args[4]);
                totalHeight = Integer.parseInt(args[5]);
            } else {
                System.out.println("\n--- using default total width " +
                    totalWidth + " and total height " + totalHeight + " ---\n");
            }
            subMatrixWidth = totalWidth / nbMatrixX;
            subMatrixHeight = totalHeight / nbMatrixY;

            Object[][] params = new Object[nbMatrixX * nbMatrixY][];
            for (int i = 0; i < params.length; i++) {
                params[i] = new Object[6];
                params[i][0] = "SubMatrix" + i;
                params[i][1] = new Integer(subMatrixWidth);
                params[i][2] = new Integer(subMatrixHeight);
                params[i][3] = new Integer(nbMatrixX);
                params[i][4] = new Integer(nbMatrixY);
                params[i][5] = nbIterations;
            }

            System.out.println(
                "\n********************************************************");
            System.out.println(
                "Running group version of jacobi computation with the following parameters:");
            System.out.println("           total width = " + totalWidth);
            System.out.println("          total height = " + totalHeight);
            System.out.println("  number of iterations = " + args[1]);
            System.out.println("   number of submatrix = " +
                (nbMatrixX * nbMatrixY));
            System.out.println(
                "********************************************************\n");

            SubMatrix matrix = null;
            try {
                matrix = (SubMatrix) PASPMD.newSPMDGroup(SubMatrix.class.getName(),
                        params, nodes);
            } catch (ClassNotFoundException e) {
                System.err.println("** ClassNotFoundException **");
            } catch (ClassNotReifiableException e) {
                System.err.println("** ClassNotReifiableException **");
            } catch (ActiveObjectCreationException e) {
                System.err.println("** ActiveObjectCreationException **");
            } catch (NodeException e) {
                System.err.println("** NodeException **");
            }

            matrix.compute();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        System.out.println(
            "Usage : jacobi [deployment_descriptor] [nb_iterations] [nbMatrixX] [nbMatrixY] [totalWidth] [totalHeight]");
        System.out.println(
            "[deployment_descriptor] specifies the deployment infrastructure");
        System.out.println("[nb_iterations] specifies the number of iterations");
        System.out.println(
            "[nbMatrixX] [nbMatrixY] [totalWidth] [totalHeight] are number of sub matrix and dimensions of the global matrix");
        System.out.println(
            "As a deployment descriptor, you may want to try ../../../descriptors/Matrix.xml");

        System.exit(-1);
    }
}
