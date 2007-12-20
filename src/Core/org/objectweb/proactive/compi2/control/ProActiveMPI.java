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
package org.objectweb.proactive.compi2.control;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.compi2.MPISpmd;
import org.objectweb.proactive.compi2.control.controller.DGFractiveController;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.adl.FactoryFactory;
import org.objectweb.proactive.core.component.factory.ProActiveGenericFactory;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.ext.util.FutureList;
import org.objectweb.proactive.filetransfer.FileTransfer;
import org.objectweb.proactive.filetransfer.FileVector;


public class ProActiveMPI {
    
    /**
     * Deploy an array of MPI applications, wrapping native process within components
     * bound through their interfaces
     * @param spmdList The list of MPI applications to be deployed
     * @return a vector of results (return code of each MPI application)
     */
    public static Vector deploy(ArrayList spmdList) {
    	Component[] clusters = new Component[spmdList.size()];
    	FutureList readyToStartAcks = new FutureList();
    	
    	try {
    		Map<String, Object> context = new HashMap<String, Object>();

    		//create cluster composites on first node of each Vn (hopefully a frontent ;))
    		for(int i=0; i < spmdList.size(); i++){ 
    			MPISpmd spmd = (MPISpmd) spmdList.get(i);
    			VirtualNode vn = ((MPISpmd) spmdList.get(i)).getVn();
                Node[] allNodes = vn.getNodes();
                
                clusters[i] = newClusterInstance(allNodes[0], context);
                DGFractiveController fractiveController = (DGFractiveController) clusters[i].getFcInterface(DGConstants.DG_FRACTIVE_CONTROLLER);
                fractiveController.createInnerComponents(spmd);
    		}
    		
    		//bind collective interfaces  of cluster composites
    		for(int i=0; i < spmdList.size(); i++){
    			for(int j=0; j < spmdList.size(); j++){
    				if(i != j){
    					 Fractal.getBindingController(clusters[i]).bindFc("outMxNClientItf", clusters[j].getFcInterface("inMxNServerItf"));
    				}
    			}
    		}
    		
    		//start environment
    		for(int i=0; i < spmdList.size(); i++){
    			Fractal.getLifeCycleController(clusters[i]).startFc();
    			//readyToStartAcks.add(((DGController) clusters[i].getFcInterface(DGConstants.DG_FRACTIVE_CONTROLLER)).isReadyto);
    		}
    		

    	} catch (NodeException e) {
			e.printStackTrace();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		} catch (IllegalBindingException e) {
			e.printStackTrace();
		} catch (IllegalLifeCycleException e) {
			e.printStackTrace();
		}



    	return null;
    }

    private static Component newClusterInstance(Node node, Map<String, Object> context) {
    	Component cluster = null;

    	try {
    		
    		Component boot = Fractal.getBootstrapComponent();
    		ProActiveGenericFactory cf = Fractive.getGenericFactory(boot);
    		Factory f = FactoryFactory.getFactory();
    		ComponentType clusterType = (ComponentType) f.newComponentType(DGConstants.DG_CLUSTER_ADL, context);
    		ControllerDescription clusterController = new ControllerDescription("cluster", Constants.COMPOSITE, 
    				ProActiveMPI.class.getClass().getResource(DGConstants.DG_CLUSTER_CONTROLLER_CONFIG).getPath()); 
    		
    		cluster = cf.newFcInstance(clusterType, clusterController, null, node);

    	} catch (InstantiationException e) {
    		e.printStackTrace();
    	} catch (NoSuchInterfaceException e) {
    		e.printStackTrace();
    	} catch (ADLException e) {
    		e.printStackTrace();
    	}

    	return cluster;

    }

	/**
     * push native lib into nodes
     *
     * @param allNodes list of nodes to send the file
     * @param mpiSpmd definition of the MPISpmd deployment
     * @param hasNFS if true, lib is sent to fist node, otherwise, to all the nodes
     * @throws IOException throwed if the native file does not exist locally
     * @throws ProActiveException node could not be accessed
     */
    public static void pushNativeLib(Node[] allNodes, MPISpmd mpiSpmd, boolean hasNFS)
        throws IOException, ProActiveException {
        String remoteLibraryPath = mpiSpmd.getRemoteLibraryPath();

        ClassLoader cl = ProActiveMPI.class.getClassLoader();
        URL u = cl.getResource("org/objectweb/proactive/compi2/control/" + DGConstants.DEFAULT_LIBRARY_NAME);

        File remoteDest = new File(remoteLibraryPath + "/libProActiveMPIComm.so");
        File localSource = new File(u.getFile());

        FileVector filePushed = new FileVector();
        	
        if(hasNFS){
        	filePushed.add(FileTransfer.pushFile(allNodes[0], localSource, remoteDest));
        }else{
        	for (Node node: allNodes){
        	       filePushed.add(FileTransfer.pushFile(node, localSource, remoteDest));
        	}
        }
        filePushed.waitForAll();
    }
}
