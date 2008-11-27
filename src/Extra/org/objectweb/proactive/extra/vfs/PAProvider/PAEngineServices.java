package org.objectweb.proactive.extra.vfs.PAProvider;

import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extra.vfs.MountedNodes;
import org.objectweb.proactive.extra.vfs.ProActiveVFS;

public class PAEngineServices {

	
	private static FileSystem filesys;
	private static Node currentNode;
	private static PAFileProviderEngine engine;
	private static FileObject[] allfiles;
	
	
	public static void setLocalFileSystem(Node node)
	{
	    PAEngineServices.currentNode = node; 
		filesys = ProActiveVFS.getVFS(currentNode);
		
	}
	
	public static void listFileObjects()
	{
		//PAEngineServices.engine = engine;
			
		System.out.println("Checking the node:>>>>>>>>>>>" + PAEngineServices.currentNode.getNodeInformation().getName());
		System.out.println("Checking the node:>>>>>>>>>>>" + PAEngineServices.currentNode.getNodeInformation().getURL());
		
		System.out.println("FileSystem is: " + filesys);
		try {
			System.out.println("Printing the root");
			System.out.println(filesys.getRoot());
			allfiles =  filesys.getRoot().findFiles(new AllFileSelector());
			for(FileObject file: allfiles)
			{
				System.out.println("Printing Children at the remote site");
				System.out.println(file.toString());
			}
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}
	
}
	
}
