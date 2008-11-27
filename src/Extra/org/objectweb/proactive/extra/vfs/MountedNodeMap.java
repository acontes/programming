package org.objectweb.proactive.extra.vfs;

import org.objectweb.proactive.core.node.Node;

public class MountedNodeMap {
	
	
	private Node node;
	private String url;
	
	public MountedNodeMap(Node node2, String url2) {
		// TODO Auto-generated constructor stub
		this.node = node2;
		this.url = url2;
	}
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	

}
