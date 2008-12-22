package org.objectweb.proactive.extra.vfs;

import java.io.File;
import java.io.IOException;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		try {
			GCMApplication pad = PAGCMDeployment.loadApplicationDescriptor(new File("src/Extra/org/objectweb/proactive/extra/vfs/AD.xml"));
			pad.startDeployment();
			GCMVirtualNode vn = pad.getVirtualNode("myNode");
		
			Node n = vn.getANode();
			MountedNodes mn = new MountedNodes(); 
			Directories mkdir = new Directories();
			
			/*
			 * Iterating over nodes, mapped to virtual node 
			 */
			
			while(n!=null){

			mn.addNodetoMap(n);
			
			// Creating and localy mounting directory for the node 
			boolean ok = mkdir.makeAndMountNodeDirectories(n);
			
			// Creating ActiveObject
			CreateAgent.createAgent(n);
			
			n = vn.getANode(3000);
			}
			
			// Mounting remote data space for each node
			mn.setMountRemoteSpaceURI();
			
		} catch(ActiveObjectCreationException e) {
			e.printStackTrace();
		} catch (NodeException e) {
			e.printStackTrace();
		} catch (ProActiveException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}