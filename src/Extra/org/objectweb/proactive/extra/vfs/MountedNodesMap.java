package org.objectweb.proactive.extra.vfs;

import org.objectweb.proactive.core.node.Node;

public class MountedNodesMap implements java.io.Serializable{
	
	private Node mountedNode;
	private String mountedUrl;
	
	
	public MountedNodesMap(Node mountedNode, String mountedUrl) {
		this.mountedNode = mountedNode;
		this.mountedUrl = mountedUrl;
	}


	public Node getMountedNode() {
		return mountedNode;
	}


	public String getMountedUrl() {
		return mountedUrl;
	}

}

