/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.examples.components.jacobi;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.api.PADeployment;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.factory.ProActiveGenericFactory;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactory;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.examples.components.userguide.primitive.PrimitiveComputer;


/**
 *
 * @author Matthieu Morel
 *
 */
public class Jacobi {

    /**
     * Number of columns of SubMatrix
     */
    public static int nbMatrixX;

    /**
     * Number of lines of SubMatrix
     */
    public static int nbMatrixY;
    public static int subMatrixWidth;
    public static int subMatrixHeight;
    public static int totalHeight = 1680;
    public static int totalWidth = 1680;

    /**
     * Max number of iterations
     */

    //    public static final int ITERATIONS = 500;
    /**
     * Min diff to stop
     */
    public static final double MINDIFF = 0.001;

    /**
     * Default external border value
     */
    public static final double DEFAULT_BORDER_VALUE = 0;
    public static boolean useMulticast = false;

    public static void main(String[] args) {
        //        System.out.println("#0 free mem = " + Runtime.getRuntime().freeMemory());
        if (args.length < 5) {
            printUsage();
        }
        try {
            ProActiveDescriptor deploymentDescriptor = null;
            String[] nodes = null;
            deploymentDescriptor = PADeployment.getProactiveDescriptor("file:" + args[0]);
            //            deploymentDescriptor.activateMappings();
            //            deploymentDescriptor.getVirtualNodes();

            org.objectweb.proactive.examples.jacobi.Jacobi.nbIterations = Integer.parseInt(args[1]);

            if ("multicast".equals(args[2])) {
                useMulticast = true;
            } else if ("collection".equals(args[2])) {
                useMulticast = false;
            } else {
                printUsage();
            }

            nbMatrixX = Integer.parseInt(args[3]);
            nbMatrixY = Integer.parseInt(args[4]);

            if (args.length == 7) {
                totalWidth = Integer.parseInt(args[5]);
                totalHeight = Integer.parseInt(args[6]);
            } else {
                System.out.println("\n--- using default total width " + totalWidth + " and total height " +
                    totalHeight + " ---\n");
            }
            subMatrixWidth = totalWidth / nbMatrixX;
            subMatrixHeight = totalHeight / nbMatrixY;

            System.out.println("\n********************************************************");
            System.out
                    .println("Running component version of jacobi computation with the following parameters:");
            System.out.println("           total width = " + totalWidth);
            System.out.println("          total height = " + totalHeight);
            System.out.println("  number of iterations = " + args[1]);
            System.out.println("   number of submatrix = " + (nbMatrixX * nbMatrixY));
            System.out.println("    data sending mode  = " + args[2]);
            System.out.println("********************************************************\n");

            //        if ((args.length==4) && args[3]!=null) {
            //        	// nb of nodes
            //        	System.out.println("Number of nodes asked on sophia : " + args[3]);
            //        	System.setProperty("SOPHIA_NODES", args[3]);
            //        }
            Factory f = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();
            Map context = new HashMap();
            context.put("deployment-descriptor", deploymentDescriptor);
            deploymentDescriptor.activateMappings();
            Component[][] components = new Component[nbMatrixX][nbMatrixY];
            
            // instantiate components
            System.out.println("\nInstantiate components...");
            for (int x = 0; x < nbMatrixX; x++) {
                for (int y = 0; y < nbMatrixY; y++) {
                    //System.err.println("instantiate component x " + x + ", y " + y);
                    System.out.println("Jacobi.main()" +
                        "org.objectweb.proactive.examples.components.jacobi.SubMatrix(" + subMatrixWidth +
                        ";" + subMatrixHeight + "," + x + ";" + y + "," + nbMatrixX + ";" + nbMatrixY + "," +
                        args[1] + ")");
                    components[x][y] = (Component) f.newComponent(
                            "org.objectweb.proactive.examples.components.jacobi.SubMatrix(" + subMatrixWidth +
                                ";" + subMatrixHeight + "," + x + ";" + y + "," + nbMatrixX + ";" +
                                nbMatrixY + "," + args[1] + ")", context);
                }
            }
            // org.objectweb.proactive.examples.components.jacobi.SubMatrix(840;840,0;0,2;2,10)
            // org.objectweb.proactive.examples.components.jacobi.SubMatrix(840;840,0;0,2;2,10)

            //        System.out.println("#1 free mem = " + Runtime.getRuntime().freeMemory());

            // bind components
            System.out.println("\nBind components...");
            for (int x = 0; x < nbMatrixX; x++) {
                for (int y = 0; y < nbMatrixY; y++) {
                    System.out.println("Jacobi.main() components[x][y]: " + components[x][y]);

                    //System.err.println("binding component x " + x + ", y " + y);
                    BindingController bc = Fractal.getBindingController(components[x][y]);

                    // bindings are performed in the following order : NORTH, EAST, SOUTH, WEST
                    if (y != 0) {
                        if (useMulticast) {
                            bc.bindFc("sender", components[x][y - 1].getFcInterface("receiver"));
                        } else {
                            bc.bindFc("sender-collection-NORTH", components[x][y - 1]
                                    .getFcInterface("receiver"));
                        }
                    }
                    if (x != (nbMatrixX - 1)) {
                        if (useMulticast) {
                            bc.bindFc("sender", components[x + 1][y].getFcInterface("receiver"));
                        } else {
                            bc.bindFc("sender-collection-EAST", components[x + 1][y]
                                    .getFcInterface("receiver"));
                        }
                    }
                    if (y != (nbMatrixY - 1)) {
                        if (useMulticast) {
                            bc.bindFc("sender", components[x][y + 1].getFcInterface("receiver"));
                        } else {
                            bc.bindFc("sender-collection-SOUTH", components[x][y + 1]
                                    .getFcInterface("receiver"));
                        }
                    }
                    if (x != 0) {
                        if (useMulticast) {
                            bc.bindFc("sender", components[x - 1][y].getFcInterface("receiver"));
                        } else {
                            bc.bindFc("sender-collection-WEST", components[x - 1][y]
                                    .getFcInterface("receiver"));
                        }
                    }
                }
            }

            //        System.out.println("#2 free mem = " + Runtime.getRuntime().freeMemory());
            // start and launch components
            System.out.println("\nLaunch components...");
            for (int x = 0; x < nbMatrixX; x++) {
                for (int y = 0; y < nbMatrixY; y++) {
                    //System.err.println("GO component x " + x + ", y " + y);
                    ((Main) components[x][y].getFcInterface("main")).go();
                }
            }

            //        System.out.println("#3 free mem = " + Runtime.getRuntime().freeMemory());
            System.out.println("\nStart components...");
            for (int x = 0; x < nbMatrixX; x++) {
                for (int y = 0; y < nbMatrixY; y++) {
                    //System.err.println("START component x " + x + ", y " + y);
                    Fractal.getLifeCycleController(components[x][y]).startFc();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("End of computation.");
        System.exit(0);
    }

    private static void printUsage() {
        System.out
                .println("Usage : jacobi [deployment_descriptor] [nb_iterations] [multicast/collection] [nbMatrixX] [nbMatrixY] [totalWidth] [totalHeight]");
        System.out.println("[deployment_descriptor] specifies the deployment infrastructure");
        System.out.println("[nb_iterations] specifies the number of iterations");
        System.out
                .println("[multicast/collection] specifies the communication mode for sending borders to neighbors (collection are individual invocations, multicast is a multicast invocation)");
        System.out
                .println("[nbMatrixX] [nbMatrixY] [totalWidth] [totalHeight] are number of sub matrix and dimensions of the global matrix");
        System.out.println("As a deployment descriptor, you may want to try ../../../descriptors/Matrix.xml");

        System.exit(-1);
    }
}
