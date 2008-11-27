package org.objectweb.proactive.extra.vfs;

import java.util.ArrayList;

import org.objectweb.proactive.core.node.Node;

public class SendInformation {

	public static void send(Node node, String url) {
		// TODO Auto-generated method stub
		MountedNodes mn = new MountedNodes();
		
		mn.getMountednodesUrl().add(url);
		
		System.out.println("URL " + url + " added to node " + node.getNodeInformation().getURL() + " list");
	}

	

}
