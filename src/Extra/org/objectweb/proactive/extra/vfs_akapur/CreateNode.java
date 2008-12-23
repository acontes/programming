package org.objectweb.proactive.extra.vfs_akapur;


import java.io.IOException;
import java.util.ArrayList;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


/* This class should be used to create nodes and start the engine on the respective node 
 *  the class is not being used currently*/ 

public class CreateNode {
	
	public static ArrayList<Object> AgentList = new ArrayList<Object>();
	private static ArrayList<String> nodeURLs = new ArrayList<String>();
	
		
	/**
	 *This method receives a GCMVirtualNode and uses the getANode() method to get retrieve the node.
	 *While nodes available from the virtual node, it iterates through the nodes, creates the directories,
	 *mounts the directories on the local file systems of each node respectively. 
	 *Calls for Active Object creation for each node and also start the Engine(Service Active Object) for each node.
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
		
		CreateNode.nodeURLs.add(node.getNodeInformation().getURL());
		
		String input = "/user/akapur/home/Data.txt";
		String scratch = "/tmp/scratch/";                           //local directory
		String output = "/tmp/output/";
	 
		ProActiveDataSpace.createNodeDirectory(node, scratch);
				
		ProActiveVFS.setDirectories( 
		        new FileNameMap(input,"input"),
		        new FileNameMap(scratch + node.getNodeInformation().getName()+"/","scratch/"+ node.getNodeInformation().getName()+"/"),
		        new FileNameMap(output,"output/"));	
		
		ProActiveVFS.mountLocally(node);
		
	    System.out.println("Directories of Node:"+ node.getNodeInformation().getName() +" mounted");
		
	    MountedNodes.addNodeToList(node);            //adding node to mounted nodes' list
		
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
	
	   //PAFileProviderEngine.startPAFileProviderEngine(node);        
	
	   node = virtualnode.getANode(3000);         //get more nodes
		
    }
	
  }
	
	public static ArrayList<String> getCreatedNodesURL()
	{
		return nodeURLs;
	}
	
	public static void getInfo() 
	{
		for(String url : nodeURLs)
		{
			System.out.println("URL is : " + url);
			Node node = null;
			try {
				node = NodeFactory.getNode(url);
			} catch (NodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(node.getNodeInformation().getName());
		}
	}
	
}
	

