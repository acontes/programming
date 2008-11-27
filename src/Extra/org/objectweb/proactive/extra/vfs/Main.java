package org.objectweb.proactive.extra.vfs;

import java.io.File;
import java.io.IOException;


import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extra.vfs.PAProvider.PAClientFactory;
import org.objectweb.proactive.extra.vfs.PAProvider.PAFileProviderEngine;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


public class Main {

	public static void main(String args[]) throws IOException, ProActiveException {
		
		
		
		System.out.println("Loading Descriptor..........");
		GCMApplication pad;
		
		pad = PAGCMDeployment
						.loadApplicationDescriptor(new File(
								"/home/akapur/workspace/ProActive-Trunk/src/Extra/org/objectweb/proactive/extra/vfs/FileB.xml"));
			

		pad.startDeployment();

		GCMVirtualNode testVNode = pad.getVirtualNode("matrixNode");
	
		CreateNode.createNode(testVNode);	
			
		
		
			
		
			
			
			
			
		//ProActiveVFS.setRemoteDirectories();
		
		//ProActiveVFS.MountRemoteSpaceURIs();
		
		
		

		 
			
	
		

	}
	
}



