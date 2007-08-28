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
package org.objectweb.proactive.compi.control;

import java.util.*;
import java.io.IOException;
import java.io.File;
import java.net.URL;

import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.adl.FactoryFactory;
import org.objectweb.proactive.core.component.factory.ProActiveGenericFactory;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.*;
import org.objectweb.proactive.filetransfer.FileVector;
import org.objectweb.proactive.filetransfer.FileTransfer;
import org.objectweb.proactive.compi.MPISpmd;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.fractal.adl.*;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.ProActiveException;


public class ProActiveMPI {
    public final static String DEFAULT_LIBRARY_NAME = "libProActiveMPIComm.so";

    /**
     * Deploy an array of MPI applications, wrapping native process within components
     * bound through their interfaces
     * @param spmdList The list of MPI applications to be deployed
     * @return a vector of results (return code of each MPI application)
     */
    public static Vector deploy(ArrayList spmdList) {
        Component[] manager = new Component[spmdList.size()];
        List<List<Component>> nodeCompListList = new ArrayList<List<Component>>();

        try {
            for (int i = 0; i < spmdList.size(); i++) {
                VirtualNode vn = ((MPISpmd) spmdList.get(i)).getVn();
                Node[] allNodes = vn.getNodes();
                pushNativeLib(allNodes, (MPISpmd) spmdList.get(i));


                Component boot = Fractal.getBootstrapComponent();
                ProActiveGenericFactory cf = Fractive.getGenericFactory(boot);
                Factory f = FactoryFactory.getFactory();
                Map<String, Object> context = new HashMap<String, Object>();

                /*create clusterItf component*/
                Object[] clusterParams = new Object[]{(MPISpmd) spmdList.get(i), allNodes.length, i, spmdList.size()};
                ComponentType clusterType = (ComponentType)f.newComponentType("org.objectweb.proactive.compi.control.adl.cluster", context);
                ControllerDescription clusterController = new ControllerDescription("Cluster", Constants.PRIMITIVE);
                ContentDescription clusterContent = new ContentDescription(ProActiveMPIClusterComp.class.getName(), clusterParams);

                manager[i] = cf.newFcInstance(clusterType, clusterController, clusterContent, allNodes[0]);

                /*create nodes component*/
                Object[] nodeParams = new Object[]{"ProActiveMPIComm", i};
                ComponentType nodeType = (ComponentType) f.newComponentType("org.objectweb.proactive.compi.control.adl.node", context);
                ControllerDescription nodeController = new ControllerDescription("Node", Constants.PRIMITIVE);
                ContentDescription nodeContent = new ContentDescription(ProActiveMPINodeComp.class.getName(), nodeParams);


                nodeCompListList.add(i, cf.newFcInstanceAsList(nodeType, nodeController, nodeContent, allNodes));

                /*do bindings*/
                for (int j = 0; j < nodeCompListList.get(i).size(); j++) {
                    Component nodeComp = nodeCompListList.get(i).get(j);
                    Fractal.getBindingController(nodeComp).bindFc("node2cluster", manager[i].getFcInterface("cluster"));
                    Fractal.getLifeCycleController(nodeComp).startFc();
                    Fractal.getBindingController(manager[i]).bindFc("cluster2node", nodeComp.getFcInterface("node"));
                }
            }

            for (int i = 0; i < spmdList.size(); i++) {
                for (int j = 0; j < spmdList.size(); j++) {
                    Fractal.getBindingController(manager[i]).bindFc("cluster2cluster", manager[j].getFcInterface("cluster"));
                }
                Fractal.getLifeCycleController(manager[i]).startFc();
            }

            for (int i = 0; i < spmdList.size(); i++) {
                ((ProActiveMPICluster) manager[i].getFcInterface("cluster")).createClusterProxy();
            }



           //  wait for termination (works, but must be changed)
           boolean finishNow = true;
            while(true){
                for (int i = 0; i < spmdList.size(); i++) {
                    if(!((ProActiveMPICluster) manager[i].getFcInterface("cluster")).isReadyToFinalize()){
                       finishNow = false;
                    }
                }
                if(finishNow){
                    for (Object aSpmdList : spmdList) {
                        ((MPISpmd) aSpmdList).getVn().killAll(false);
                    }
                    System.exit(0);
                }
                finishNow = true;
                Thread.sleep(1000);
            }

        } catch (org.objectweb.fractal.api.factory.InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchInterfaceException e) {
            e.printStackTrace();
        } catch (IllegalLifeCycleException e) {
            e.printStackTrace();
        } catch (ADLException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (IllegalBindingException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * push native lib into first node of a clusterItf (obs: requires NFS,
     * otherwise change it to push lib into the whole clusterItf)
     *
     * @param allNodes list of nodes to send the file
     * @param mpiSpmd definition of the MPISpmd deployment
     * @throws IOException throwed if the native file does not exist locally
     * @throws ProActiveException node could not be accessed
     */
    public static void pushNativeLib(Node[] allNodes, MPISpmd mpiSpmd) throws IOException, ProActiveException {
        String remoteLibraryPath = mpiSpmd.getRemoteLibraryPath();

        ClassLoader cl = ProActiveMPI.class.getClassLoader();
        URL u = cl.getResource(
                "org/objectweb/proactive/compi/control/" +
                        DEFAULT_LIBRARY_NAME);

        File remoteDest = new File(remoteLibraryPath +
                "/libProActiveMPIComm.so");
        File localSource = new File(u.getFile());

        FileVector filePushed = FileTransfer.pushFile(allNodes[0],
                localSource, remoteDest);
        filePushed.waitForAll();
    }


}
