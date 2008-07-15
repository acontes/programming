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
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.factory.InstantiationException;
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
import org.objectweb.proactive.core.node.Node;


/**
 *
 * @author Matthieu Morel
 *
 */
public class JacobiParrallel {

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

    public static volatile transient Component[][] components;

    private static ComponentType createSubMatrixType() {
        try {
            Component boot = Fractal.getBootstrapComponent();
            ProActiveTypeFactory typeFact = (ProActiveTypeFactory) Fractal.getTypeFactory(boot);
            ProActiveGenericFactory genericFact = (ProActiveGenericFactory) Fractal.getGenericFactory(boot);

            // component types: PrimitiveComputer, PrimitiveMaster, CompositeWrapper
            ComponentType subMatrixType = typeFact.createFcType(new InterfaceType[] {
                    typeFact.createFcItfType("main", Main.class.getName(), TypeFactory.SERVER,
                            TypeFactory.MANDATORY, ProActiveTypeFactory.MULTICAST_CARDINALITY),
                    typeFact.createFcItfType("sender", MulticastDataSender.class.getName(),
                            TypeFactory.CLIENT, TypeFactory.OPTIONAL,
                            ProActiveTypeFactory.MULTICAST_CARDINALITY),
                    typeFact.createFcItfType("sender-collection-", CollectionDataSender.class.getName(),
                            TypeFactory.CLIENT, TypeFactory.OPTIONAL,
                            ProActiveTypeFactory.COLLECTION_CARDINALITY),
                    typeFact.createFcItfType("receiver", GathercastDataReceiver.class.getName(),
                            TypeFactory.SERVER, TypeFactory.MANDATORY,
                            ProActiveTypeFactory.GATHER_CARDINALITY),
                    typeFact.createFcItfType("attribute-controller", SubMatrixAttributes.class.getName(),
                            TypeFactory.SERVER, TypeFactory.MANDATORY,
                            ProActiveTypeFactory.SINGLETON_CARDINALITY) });
            return subMatrixType;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        //        System.out.println("#0 free mem = " + Runtime.getRuntime().freeMemory());
        if (args.length < 5) {
            printUsage();
        }
        try {
            ProActiveDescriptor deploymentDescriptor = null;
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
            //            Factory f = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();
            //            Map context = new HashMap();
            //            context.put("deployment-descriptor", deploymentDescriptor);
            deploymentDescriptor.activateMappings();
            components = new Component[nbMatrixX][nbMatrixY];

            System.out.println("Jacobi.main()XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

            ExecutorService threadPool = Executors.newCachedThreadPool();
            ExceptionList eList = new ExceptionList();
            Node[] nodes = deploymentDescriptor.getVirtualNode("matrixNode").getNodes();
            int nodesIndex = 0;
            for (int x = 0; x < nbMatrixX; x++) {
                for (int y = 0; y < nbMatrixY; y++) {
                    CreateComponentTask createTask = new CreateComponentTask(eList, subMatrixWidth +
                            ";" + subMatrixHeight, x + ";" + y , nbMatrixX + ";" + nbMatrixY , args[1] , x, y, nodes[nodesIndex%nodes.length]);
                    nodesIndex++;
//                    if (x == 0 && y == 0) {
                        createTask.run();
//                    } else {
//                        threadPool.execute(createTask);
//                    }
                    //System.err.println("instantiate component x " + x + ", y " + y);
                    //                    components[x][y] = (Component) f.newComponent(
                    //                            "org.objectweb.proactive.examples.components.jacobi.SubMatrix(" + subMatrixWidth +
                    //                                ";" + subMatrixHeight + "," + x + ";" + y + "," + nbMatrixX + ";" +
                    //                                nbMatrixY + "," + args[1] + ")", context);
                }
            }
            //            threadPool.shutdown();
            //            while (threadPool.isTerminated() == false)
            //                Thread.sleep(50);
            //            System.out.println("JacobiParrallel.main() threadPool.isTerminated():" +
            //                threadPool.isTerminated());
            //            if (!eList.isEmpty()) {
            //                throw new Exception("Errors during components creation.");
            //            }

            // instantiate components
//            System.out.println("\nInstantiate components...");
//            for (int x = 0; x < nbMatrixX; x++) {
//                for (int y = 0; y < nbMatrixY; y++) {
//                    //System.err.println("instantiate component x " + x + ", y " + y);
//                    components[x][y] = (Component) f.newComponent(
//                            "org.objectweb.proactive.examples.components.jacobi.SubMatrix(" + subMatrixWidth +
//                                ";" + subMatrixHeight + "," + x + ";" + y + "," + nbMatrixX + ";" +
//                                nbMatrixY + "," + args[1] + ")", context);
//                }
//            }

            //        System.out.println("#1 free mem = " + Runtime.getRuntime().freeMemory());


            threadPool.shutdown();
            while (threadPool.isTerminated() == false)
                Thread.sleep(50);
            System.out.println("JacobiParrallel.main() threadPool.isTerminated():" +
                threadPool.isTerminated());
            if (!eList.isEmpty()) {
                throw new Exception("Errors during components creation.");
            }
            for (int x = 0; x < nbMatrixX; x++) {
                for (int y = 0; y < nbMatrixY; y++) {
              System.out.println(components[x][y]);
                }
            }
            threadPool = Executors.newCachedThreadPool();
            eList.clear();
            // bind components
            System.out.println("\nBind components...");
            for (int x = 0; x < nbMatrixX; x++) {
                for (int y = 0; y < nbMatrixY; y++) {
                    System.out.println("Jacobi.main() components[x][y]: " + components[x][y]);
                    BindComponentTask bindTask = new BindComponentTask(eList, x, y);
                    if (x == 0 && y == 0) {
                        bindTask.run();
                    } else {
                        threadPool.execute(bindTask);
                    }

                }
            }

            threadPool.shutdown();
            while (threadPool.isTerminated() == false)
                Thread.sleep(50);
            System.out.println("JacobiParrallel.main() threadPool.isTerminated():" +
                threadPool.isTerminated());
            if (!eList.isEmpty()) {
                throw new Exception("Errors during components binding.");
            }
            //            for (int x = 0; x < nbMatrixX; x++) {
            //                for (int y = 0; y < nbMatrixY; y++) {
            //                    System.out.println("Jacobi.main() components[x][y]: " + components[x][y]);
            //
            //                    //System.err.println("binding component x " + x + ", y " + y);
            //                    BindingController bc = Fractal.getBindingController(components[x][y]);
            //
            //                    // bindings are performed in the following order : NORTH, EAST, SOUTH, WEST
            //                    if (y != 0) {
            //                        if (useMulticast) {
            //                            bc.bindFc("sender", components[x][y - 1].getFcInterface("receiver"));
            //                        } else {
            //                            bc.bindFc("sender-collection-NORTH", components[x][y - 1]
            //                                    .getFcInterface("receiver"));
            //                        }
            //                    }
            //                    if (x != (nbMatrixX - 1)) {
            //                        if (useMulticast) {
            //                            bc.bindFc("sender", components[x + 1][y].getFcInterface("receiver"));
            //                        } else {
            //                            bc.bindFc("sender-collection-EAST", components[x + 1][y]
            //                                    .getFcInterface("receiver"));
            //                        }
            //                    }
            //                    if (y != (nbMatrixY - 1)) {
            //                        if (useMulticast) {
            //                            bc.bindFc("sender", components[x][y + 1].getFcInterface("receiver"));
            //                        } else {
            //                            bc.bindFc("sender-collection-SOUTH", components[x][y + 1]
            //                                    .getFcInterface("receiver"));
            //                        }
            //                    }
            //                    if (x != 0) {
            //                        if (useMulticast) {
            //                            bc.bindFc("sender", components[x - 1][y].getFcInterface("receiver"));
            //                        } else {
            //                            bc.bindFc("sender-collection-WEST", components[x - 1][y]
            //                                    .getFcInterface("receiver"));
            //                        }
            //                    }
            //                }
            //            }

            //        System.out.println("#2 free mem = " + Runtime.getRuntime().freeMemory());
            // start and launch components
            System.out.println("\nLaunch components...");
            for (int x = 0; x < nbMatrixX; x++) {
                for (int y = 0; y < nbMatrixY; y++) {
                    //System.err.println("GO component x " + x + ", y " + y);
                    ((Main) components[x][y].getFcInterface("main")).go();
                }
            }

            threadPool = Executors.newCachedThreadPool();
            eList.clear();
            // bind components
            System.out.println("\nStart components...");
            for (int x = 0; x < nbMatrixX; x++) {
                for (int y = 0; y < nbMatrixY; y++) {
                    System.out.println("Jacobi.main() components[x][y]: " + components[x][y]);
                    StartComponentTask bindTask = new StartComponentTask(eList, components[x][y]);
                    if (x == 0 && y == 0) {
                        bindTask.run();
                    } else {
                        threadPool.execute(bindTask);
                    }

                }
            }

            threadPool.shutdown();
            while (threadPool.isTerminated() == false)
                Thread.sleep(50);
            System.out.println("JacobiParrallel.main() threadPool.isTerminated():" +
                threadPool.isTerminated());
            if (!eList.isEmpty()) {
                throw new Exception("Errors during components start.");
            }

            //            //        System.out.println("#3 free mem = " + Runtime.getRuntime().freeMemory());
            //            System.out.println("\nStart components...");
            //            for (int x = 0; x < nbMatrixX; x++) {
            //                for (int y = 0; y < nbMatrixY; y++) {
            //                    //System.err.println("START component x " + x + ", y " + y);
            //                    Fractal.getLifeCycleController(components[x][y]).startFc();
            //                }
            //            }

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

    private static class CreateComponentTask implements Runnable {
        private transient ExceptionList list;
        private transient Node node;
        private transient int x;
        private transient int y;
        
        
        private SubMatrixAttributes attCtrl;
        private String dimensions;
        private String coordinates;
        private String nbIterations;
        private String globalDimensions; 


        private static ComponentType subMatrixType;
        private  ControllerDescription controllerDesc;
        private  ContentDescription contentDesc;
        private  Component boot;
        private ProActiveGenericFactory f;

        static {
//            try {
                subMatrixType = createSubMatrixType();
//
//                controllerDesc = new ControllerDescription("SubMatrix", Constants.PRIMITIVE);
//                contentDesc = new ContentDescription(SubMatrixComponent.class.getName());
//                
//                boot = Fractal.getBootstrapComponent();
//            } catch (Exception e) {
//                System.err.println("Error when creating component ADL factory!");
//                e.printStackTrace();
//            }
        }

        public CreateComponentTask(ExceptionList list, String dimensions, String coordinates, String globalDimensions, String nbIterations , int x, int y,
                Node node) {
            this.list = list;
            this.dimensions = dimensions;
            this.coordinates = coordinates;
            this.globalDimensions = globalDimensions;
            this.nbIterations = nbIterations;
            this.x = x;
            this.y = y;
            this.node = node;

            try {
//                subMatrixType = createSubMatrixType();

                controllerDesc = new ControllerDescription("SubMatrix", Constants.PRIMITIVE);
                contentDesc = new ContentDescription(SubMatrixComponent.class.getName());
                
                boot = Fractal.getBootstrapComponent();
                f = (ProActiveGenericFactory) Fractal.getGenericFactory(boot);
            } catch (NoSuchInterfaceException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("CreateComponentTask.CreateComponentTask() " + this.list+ " "+
            this.dimensions+" "+
            this.coordinates+" "+
            this.globalDimensions +" "+
            this.nbIterations +" "+
            this.x +" "+
            this.y +" "+
            this.node);
        }

        public void run() {
            System.out.println("CreateComponentTask.run()" + subMatrixType);
            try {
                
                components[x][y] = (Component) f.newFcInstance(subMatrixType, controllerDesc, contentDesc);
                attCtrl = (SubMatrixAttributes) components[x][y].getFcInterface("attribute-controller");

                attCtrl.setDimensions(dimensions);
                attCtrl.setCoordinates(coordinates);
                attCtrl.setGlobalDimensions(globalDimensions);
                attCtrl.setNbIterations(nbIterations);
            } catch (Exception e) {
                e.printStackTrace();
                list.addException(e);
            }
            System.out.println("CreateComponentTask.run() comps[" + x + "][" + y + "]: " + components[x][y] + attCtrl.getCoordinates());
        }
    }

    private static class CreateComponentTaskADL implements Runnable {
        private transient ExceptionList list;
        private transient String name;
        private transient Map context;
        private transient Component[][] comps;
        private transient int x;
        private transient int y;

        private static Factory f;

        static {
            try {
                f = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();
            } catch (ADLException e) {
                System.err.println("Error when creating component ADL factory!");
                e.printStackTrace();
            }
        }

        public CreateComponentTaskADL(ExceptionList list, String name, Map context, int x, int y,
                Component[][] comps) {
            this.list = list;
            this.name = name;
            this.context = context;
            this.x = x;
            this.y = y;
            this.comps = comps;
        }

        public void run() {
            System.out.println("CreateComponentTask.run()" + name);
            try {
                comps[x][y] = (Component) f.newComponent(name, context);
            } catch (ADLException e) {
                e.printStackTrace();
                list.addException(e);
            }
            System.out.println("CreateComponentTask.run() comps[x][y]: " + comps[x][y]);
        }
    }

    private static class BindComponentTask implements Runnable {
        private transient ExceptionList list;
        private transient int x;
        private transient int y;

        public BindComponentTask(ExceptionList list, int x, int y) {
            this.list = list;
            this.x = x;
            this.y = y;
        }

        public void run() {
            System.out.println("Jacobi.main() components[x][y]: " + components[x][y]);

            //System.err.println("binding component x " + x + ", y " + y);
            BindingController bc;
            try {
                bc = Fractal.getBindingController(components[x][y]);

                // bindings are performed in the following order : NORTH, EAST, SOUTH, WEST
                if (y != 0) {
                    if (useMulticast) {
                        bc.bindFc("sender", components[x][y - 1].getFcInterface("receiver"));
                    } else {
                        bc.bindFc("sender-collection-NORTH", components[x][y - 1].getFcInterface("receiver"));
                    }
                }
                if (x != (nbMatrixX - 1)) {
                    if (useMulticast) {
                        bc.bindFc("sender", components[x + 1][y].getFcInterface("receiver"));
                    } else {
                        bc.bindFc("sender-collection-EAST", components[x + 1][y].getFcInterface("receiver"));
                    }
                }
                if (y != (nbMatrixY - 1)) {
                    if (useMulticast) {
                        bc.bindFc("sender", components[x][y + 1].getFcInterface("receiver"));
                    } else {
                        bc.bindFc("sender-collection-SOUTH", components[x][y + 1].getFcInterface("receiver"));
                    }
                }
                if (x != 0) {
                    if (useMulticast) {
                        bc.bindFc("sender", components[x - 1][y].getFcInterface("receiver"));
                    } else {
                        bc.bindFc("sender-collection-WEST", components[x - 1][y].getFcInterface("receiver"));
                    }
                }
            } catch (NoSuchInterfaceException e) {
                e.printStackTrace();
                list.addException(e);
            } catch (IllegalBindingException e) {
                e.printStackTrace();
                list.addException(e);
            } catch (IllegalLifeCycleException e) {
                e.printStackTrace();
                list.addException(e);
            }
        }
    }

    private static class StartComponentTask implements Runnable {
        private transient ExceptionList list;
        private transient Component c;

        public StartComponentTask(ExceptionList list, Component c) {
            this.list = list;
            this.c = c;
        }

        public void run() {
            try {
                Fractal.getLifeCycleController(c).startFc();
            } catch (IllegalLifeCycleException e) {
                e.printStackTrace();
                list.addException(e);
            } catch (NoSuchInterfaceException e) {
                e.printStackTrace();
                list.addException(e);
            }
        }
    }

    private static class ExceptionList {
        private List<Exception> listEx;

        public ExceptionList() {
        }

        public boolean isEmpty() {
            return (listEx == null) ? true : listEx.isEmpty();
        }

        public boolean addException(Exception exception) {
            if (null == listEx) {
                listEx = new Vector<Exception>();
            }
            return listEx.add(exception);
        }

        public void clear() {
            if (null != listEx) {
                listEx.clear();
            }
            ;
        }
    }
}
