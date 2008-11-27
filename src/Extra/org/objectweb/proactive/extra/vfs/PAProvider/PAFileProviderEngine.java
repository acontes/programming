package org.objectweb.proactive.extra.vfs.PAProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.ProActiveInternalObject;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.extra.vfs.MountedNodeMap;
import org.objectweb.proactive.extra.vfs.MountedNodes;
import org.objectweb.proactive.extra.vfs.ProActiveVFS;

/*
 * This class is meant to be a service Active Object. It creates an engine on the node which calls it.
 */

public class PAFileProviderEngine 
implements ProActiveInternalObject, InitActive, RunActive

{
	
	
	static PAFileProviderEngine singletonPAFileProviderEngine; 
	private FileSystem fileSystem;
	static String protocol;
	String port;
	static int i = 1;
	static FileObject[] allfiles;
	Vector<FileSystem> fsPool;
	//private static Node currentNode;
	//private static FileSystem fs;
	
	
	public PAFileProviderEngine()
	{
		
	}
	
	public PAFileProviderEngine(FileSystem fs)
	{
		this.fileSystem = fs;
	}

	@Override
	public void initActivity(Body body) 
	{		
				 
		
	}
	
	@Override
	public void runActivity(Body body) {
		// TODO Auto-generated method stub
		
	}
	
	

		
	public static synchronized void startPAFileProviderEngine(Node node, FileSystem fs) throws NodeException
	{
	
		
		if(singletonPAFileProviderEngine == null)
		{
		
			try {
				
							
				PAFileProviderEngine singletonPAFileProviderEngine = (PAFileProviderEngine)PAActiveObject.newActive(PAFileProviderEngine.class.getName(), new Object[] {fs},node);
									
				String url =   "rmi" + "://" + node.getVMInformation().getHostName() + ":1099/" + node.getNodeInformation().getName();							
				
				PAActiveObject.register(singletonPAFileProviderEngine,url);	
				
				
			} catch (ActiveObjectCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}	
		
	}
	
	
	
	
	/**
	 * The local Engine will have the knowledge of the local VFS and consequently of the local file objects
	 * This method provides that knowledge
	 * @throws NodeException 
	 * @throws FileSystemException 
	 */
	
	
	
	
	
	public void getFileObjects() throws NodeException, FileSystemException
	{
		
		      
		 PAFileProviderEngine.allfiles =  this.fileSystem.getRoot().findFiles(new AllFileSelector());
			
			
			for(FileObject file: PAFileProviderEngine.allfiles)
			{
				System.out.println(file.toString());
			}
			
		}

	
		
	}	
		
		/*FileSystem fs;
		
		//Node currentNode = PAActiveObject.getActiveObjectNode(engine);
		
		Node currentNode = node;
		
		System.out.println("<<<<<<<<<<<<<<<<<<Current Node is: " + currentNode.getNodeInformation().getName());
		
		try {
			
			System.out.println("FileSystem is: ");
			
			System.out.println(ProActiveVFS.getVFS(currentNode));
			
			fs = ProActiveVFS.getVFS(currentNode);
			
			
			System.out.println("Getting the root>>>>>>>>>");
			
			PAFileProviderEngine.allfiles =  fs.getRoot().findFiles(new AllFileSelector());
			
			System.out.println("Printing Children");
			for(FileObject file: PAFileProviderEngine.allfiles)
			{
				System.out.println(file.toString());
			}
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	

