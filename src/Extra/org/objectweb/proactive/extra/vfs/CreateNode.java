package org.objectweb.proactive.extra.vfs;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;

import org.apache.commons.vfs.FileSystem;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFileTransfer;
import org.objectweb.proactive.core.filetransfer.RemoteFile;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeImpl;
import org.objectweb.proactive.extra.vfs.PAProvider.PAFileProviderEngine;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

public class CreateNode {
	
	//static CreateAgent ca = new CreateAgent();
	
	public static ArrayList<Object> AgentList = new ArrayList<Object>();
	

	
	
	/**
	 *
	 * @param vnode
	 * @throws NodeException
	 * @throws IOException
	 * @throws ActiveObjectCreationException
	 */
	  	
	
		
	public static void createNode(GCMVirtualNode vnode) throws NodeException, IOException, ActiveObjectCreationException
	
	{
	
	GCMVirtualNode virtualnode = vnode;
	
	Node node = virtualnode.getANode();
	
	while(node!=null)
	{
					
		String input = "/user/knirski/home/Demo/input/test.txt";
		String scratch = "/tmp/output/";
		String output = "";
	 
		ProActiveDataSpace.createNodeDirectory(node, scratch);
		
		ProActiveDataSpace.setNodeDirectory(input, scratch, node);
	
	    FileSystem currentFS = ProActiveVFS.MountAll(node);
	
	    System.out.println("Directories of Node:"+ node.getNodeInformation().getName() +" mounted");
		
	    MountedNodes.addNodeToList(node);            //adding node to mounted nodes' list
	    
	    MountedNodes.addRealURI(node.getNodeInformation().getURL());
		
	    for(Object agent: CreateAgent.createAgent(node))
	    	
	    {
		    CreateNode.AgentList.add(agent);
	    }
	
	    
	    
	/*
	 * Starting the PAServer at current node.
	 * This server should be started after mounting the local DataSpace to the VFS
	 * The server will be used by the PAProvider.
	 * The server is actually a Service Active Object
	 */
	
	   PAFileProviderEngine.startPAFileProviderEngine(node, currentFS);        
	
	   node = virtualnode.getANode(3000);         //get more nodes
	
	   
		
  }
	
	}
	

  }
	

