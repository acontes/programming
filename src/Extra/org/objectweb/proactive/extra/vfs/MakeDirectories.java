package org.objectweb.proactive.extra.vfs;

import java.io.*;

import org.objectweb.proactive.core.node.Node;

public class MakeDirectories {
	

	public void makeNodeDirectory(Node node)
	{
	
		//boolean dir = new File("/tmp/output/" + NodeID).mkdir();
		
		System.out.println("Node Directory is Created");
		
		
		
			
	}
	
	public void  makeActiveObjectDirectory(String NodeID, int ActiveObjectID)
	{   
		
		
		File path = new File("/tmp/output/" + NodeID + "/");
		
		boolean dir = new File(path,Integer.toString(ActiveObjectID)).mkdir();
		
		System.out.println("Active Object Directory is created");
		
	}
	
	

}
