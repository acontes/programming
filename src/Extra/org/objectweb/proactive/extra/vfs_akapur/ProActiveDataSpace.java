package org.objectweb.proactive.extra.vfs_akapur;

import java.io.File;
import java.io.IOException;

import org.objectweb.proactive.api.PAFileTransfer;
import org.objectweb.proactive.core.filetransfer.RemoteFile;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extra.vfs_akapur.PAProvider.PAFileProviderEngine;


/**
 * A class to create data space directories for node and active objects
 * The engine is also being started in this class.
 * the engine creation method call needs to be relocated.. may be to the CreateNode class  
 * @author akapur
 *
 */

public class ProActiveDataSpace {
	
	/**
	 * 
	 * @param node : the node for which the directory is being created
	 * @param path : the physical path where the directory has to be created eg /tmp/output/
	 * @throws IOException 
	 * @throws NodeException 
	 */
	public static void createNodeDirectory(Node node, String path) throws IOException, NodeException
	{
		
		System.out.println("CREATING NODE DIRECTORY FOR NODE: " + node.getNodeInformation().getName());
		
		String nodeDirectory = path + "VFS_" + node.getNodeInformation().getName();
		
		RemoteFile file = PAFileTransfer.mkdirs(node, new File(nodeDirectory));
		
		
		/*
		 * Starting the PAServer at current node.
		 * This server should be started after mounting the local DataSpace to the VFS
		 * The server will be used by the PAProvider.
		 * The server is actually a Service Active Object
		 */
		
		
		new PAFileProviderEngine().startPAFileProviderEngine(node);
		
	}
	
	/**
	 * 
	 * @param inputPath : absolute path where the input is located
	 * @param scratchPath : absolute path where the scratch data has to be put
	 * @param node : the node for which these directories are being set
	 */
	
	/*public static void setNodeDirectory(String inputPath, String scratchPath, String outputPath,Node node)
	{
		Node currentNode = node;
		String realInput = inputPath + "/";
		String realScratch = scratchPath + "/" + node.getNodeInformation().getName() + "/";          //todo..change to scratch
		String realOutPut = outputPath + "/";
		
		ProActiveVFS.setDirectories( 
		        new FileNameMap(realInput,"input"), 
		        new FileNameMap(realScratch, "scratch/"+currentNode.getNodeInformation().getName()+"/"),
		        new FileNameMap(realOutPut,"output"));
	}*/
	
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
