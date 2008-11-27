package org.objectweb.proactive.extra.vfs;

import java.util.ArrayList;
import java.util.Iterator;

import org.objectweb.proactive.core.node.Node;

public class ReceiveInformation {

	public static void receive(Node currentnode,Node firstnode) {
		// TODO Auto-generated method stub
		
		MountedNodes mn = new MountedNodes();
		ArrayList <String>urls =  mn.getMountednodesUrl();
		System.out.println("Node URLs which "+ currentnode.getNodeInformation().getURL() + " knows are " ); 
				
				
				
			for(Iterator<String> it=urls.iterator();it.hasNext();)	
			{
				System.out.println(it.next());
			}
	}

}
