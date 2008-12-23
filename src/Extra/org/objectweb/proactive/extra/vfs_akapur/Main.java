package org.objectweb.proactive.extra.vfs_akapur;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;


public class Main {

	public static void main(String args[]) throws IOException, ProActiveException {
		
		ArrayList<String> nodeURLs = new ArrayList<String>();
				
		System.out.println("Loading Descriptor...........................");
		GCMApplication pad;
		
		pad = PAGCMDeployment
						.loadApplicationDescriptor(new File(
								"/home/akapur/workspace/ProActivewithVFS/src/Extra/org/objectweb/proactive/extra/vfs/FileB.xml"));
			

		pad.startDeployment();

		GCMVirtualNode testVNode = pad.getVirtualNode("matrixNode");		
		
		ProActiveVFS proVFS = new ProActiveVFS();
		
		//CreateNode.createNode(testVNode);
		
		Node node = testVNode.getANode();	
		
		while(node!=null)
		{
			
			nodeURLs.add(node.getNodeInformation().getURL());
			
			
			
			String input = "sftp://akapur@cheypa.inria.fr/tmp/input/test.txt";
			//String input = "/user/akapur/home/Data.txt";
			String scratch = "/tmp/scratch/";                           //local directory
			String output = "/tmp/output/";
		 
			ProActiveDataSpace.createNodeDirectory(node, scratch);
					
			ProActiveVFS.setDirectories( 
			        new FileNameMap(input,"input"),
			        new FileNameMap(scratch + node.getNodeInformation().getName()+"/","scratch/"+ node.getNodeInformation().getName()+"/"),
			        new FileNameMap(output,"output/"));	
						
			ProActiveVFS.mountLocally(node);
			
		    System.out.println("Directories of Node:"+ node.getNodeInformation().getName() +" mounted");
			
		    MountedNodes.setPAURL(node);
		    		    
		    
		    //Creating Active Objects
		    for(Object agent: CreateAgent.createAgent(node))
		    	
		    {
			    CreateNode.AgentList.add(agent);
		    }
		
		          		
		   node = testVNode.getANode(5000);         //get more nodes
			
	    }
			  			
	    System.out.println("Now Remote Mounting.......");
	    	    	    	    
		for(String url : nodeURLs)
		{						
			
			System.out.println("Iterating on the node URL list");
			System.out.println(url);
			Node nodeRef = NodeFactory.getNode(url);		
			System.out.println("Testing node reference: " + nodeRef.getNodeInformation().getName());
			System.out.println("Testing node reference: " + nodeRef.getNodeInformation().getURL());
	  		System.out.println("Mounting Remote Space for: " + nodeRef.getNodeInformation().getName());		
			ProActiveVFS.setRemoteDirectories(nodeRef, nodeURLs);
		}
		
		for(String url : nodeURLs)
		{
			Node nodeRef = NodeFactory.getNode(url);
			
			
			System.out.println("File System View for Node: " + nodeRef.getNodeInformation().getName());			
			FileSystem fs = ProActiveVFS.getVFS(nodeRef);
			FileObject root = fs.getRoot();
			System.out.println("Got the Root");
		    //System.out.println(root.findFiles(new AllFileSelector()));
			System.out.println("Number of Children: " + root.getChildren().length);
			FileObject[] children = root.getChildren();
			
				for(FileObject child : children)
				{
					System.out.println("Child : " + child);
				}
				
					
			FileObject fobj = root.resolveFile("/tmp/scratch/VFS_"+nodeRef.getNodeInformation().getName());
			System.out.println("File Object: " + fobj.toString());
			
		}			

	}
	
}



