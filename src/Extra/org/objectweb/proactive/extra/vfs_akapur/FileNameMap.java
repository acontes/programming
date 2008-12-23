package org.objectweb.proactive.extra.vfs_akapur;

import java.util.ArrayList;

import org.objectweb.proactive.core.node.Node;

public class FileNameMap implements java.io.Serializable{
	
	private String realURI;
	private String mountingPoint;
	private ArrayList<Node> nodes;
	private ArrayList<String> remoteURLS;
	
	
	public FileNameMap(String realURI, String mountName) {
		this.realURI = realURI;
		this.mountingPoint = mountName;
	}
	
	public FileNameMap(ArrayList<Node> nodeList, ArrayList<String> urls)
	{
		this.nodes = nodeList;
		this.remoteURLS = urls;
	}

	public String getRealURI() {
		return realURI;
	}

	public String getMountingPoint() {
		return mountingPoint;
	}
	
	
}

