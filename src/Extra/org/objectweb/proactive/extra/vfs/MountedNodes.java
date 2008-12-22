package org.objectweb.proactive.extra.vfs;

import java.util.ArrayList;

import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileFilter;
import org.apache.commons.vfs.FileFilterSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.objectweb.proactive.core.node.Node;

public class MountedNodes {
	
	private static ArrayList<MountedNodesMap> mountedNodeMap = new ArrayList<MountedNodesMap>();

	public void addNodetoMap(Node node)
	{
		//String url = "sftp://kqadir@"+node.getVMInformation().getHostName()+"/tmp/output/"+node.getNodeInformation().getName(); 
		//String url = node.getNodeInformation().getURL();
		//String url = "/tmp/output/"+node.getNodeInformation().getName();
		//String url = node.getNodeInformation().getDataSpaceInformation().getAbsolutePath();
		String scheme = node.getNodeInformation().getProtocol();
		String host = node.getVMInformation().getHostName() ;
		int port = 1099;
		String url = scheme + "://" + host +":" + port + "/" + "VFS_"+node.getNodeInformation().getName();
		mountedNodeMap.add(new MountedNodesMap (node, url) );
	}

	public static ArrayList<MountedNodesMap> getMountedNodeMap() {
		return mountedNodeMap;
	}
	
	public void printMountedNodes()
	{
	for(MountedNodesMap mn:mountedNodeMap) {
		System.out.println("Mounted Node :" + mn.getMountedNode().getNodeInformation().getName());
		System.out.println("Mounted Node URL :" + mn.getMountedUrl());
		}
	}
	
	public void setMountRemoteSpaceURI() {
		System.out.println("==================********* Remote Mounting ***********======================");
		for(MountedNodesMap a: MountedNodes.getMountedNodeMap()) {
			ProActiveVFS.mountRemoteSpaceURI(a);
		}
	}
	
}
