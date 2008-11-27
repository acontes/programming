package org.objectweb.proactive.extra.vfs;

import java.io.File;
import java.io.IOException;

import org.objectweb.proactive.api.PAFileTransfer;
import org.objectweb.proactive.core.filetransfer.RemoteFile;
import org.objectweb.proactive.core.node.Node;

public class ProActiveDataSpace {
	
	/**
	 * 
	 * @param node : the node for which the directory is being created
	 * @param path : the physical path where the directory has to be created eg /tmp/output/
	 * @throws IOException 
	 */
	public static void createNodeDirectory(Node node, String path) throws IOException
	{
		
		System.out.println("CREATING NODE DIRECTORY");
		
		String nodeDirectory = path + node.getNodeInformation().getName();
		
		RemoteFile file = PAFileTransfer.mkdirs(node, new File(nodeDirectory));
	}
	
	/**
	 * 
	 * @param inputPath : absolute path where the input is located
	 * @param scratchPath : absolute path where the scratch data has to be put
	 * @param node : the node for which these directories are being set
	 */
	
	public static void setNodeDirectory(String inputPath, String scratchPath, Node node)
	{
		Node currentNode = node;
		String realInput = inputPath + "/";
		String realOutput = scratchPath + "/" + node.getNodeInformation().getName() + "/";          //todo..change to scratch
		
		ProActiveVFS.setDirectories( 
		        new FileNameMap(realInput,"input"), 
		        new FileNameMap(realOutput,"output/"+currentNode.getNodeInformation().getName()+"/"));
	}
	
	/**
	 * 
	 * @param node : the node where this active object is being created
	 * @param path : the absolute path where the node directory is located
	 * @param aoID : the HashCode of the Active Object
	 * @throws IOException
	 */
	  
	 
	
	public static void createActiveObjectDirectory(Node node,String path,Integer aoID) throws IOException
	{
		System.out.println("CREATING ACTIVE OBJECT DIRECTORY");
		
		File aoDir = new File(path + "/" + aoID );
		RemoteFile file = PAFileTransfer.mkdirs(node, aoDir);
		
	}

}
