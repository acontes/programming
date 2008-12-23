package org.objectweb.proactive.extra.vfs_akapur;


import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.provider.ftp.FtpFileProvider;
import org.apache.commons.vfs.provider.http.HttpFileProvider;
import org.apache.commons.vfs.provider.local.DefaultLocalFileProvider;
import org.apache.commons.vfs.provider.sftp.SftpFileProvider;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.extra.vfs_akapur.PAProvider.PAFileProvider;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;


/**
 * This class creates references to the VFS and mounts to URIs to the respective mounting points
 * @author akapur
 *
 */


public class ProActiveVFS {
	
	
	private static Hashtable<Node,FileSystem> NodeVFSMap = new Hashtable<Node,FileSystem>();
	private static Hashtable<Node,FileSystem> NodelocalVFSMap = new Hashtable<Node,FileSystem>();
	public static final String ROOT_NAME = "vfs://root";          //declares the root for the virtual file system
	public static final String REMOTE_ROOT_NAME = "vfs://global";          //declares the root for the virtual file system
	
	private static FileNameMap localSpaceURI;                             //read write access
	private static FileNameMap inputSpaceURI;                             //read access
	private static FileNameMap localScratchSpaceURI;
	private static FileNameMap outputSpaceURI;                       //read write access
	private static ArrayList<String> RemoteoutputSpaceURI = new ArrayList<String>();
	private static DefaultFileSystemManager singletonFSM;// = new DefaultFileSystemManager();
	private static FileSystem singletonFS;
	private static FileSystem vfs;
	private static FileSystem remotevfs;
	public static CreateNode cn = new CreateNode();
	public static Node currentNode;
	
	
		
	public ProActiveVFS()
	{
      
	}
	
	/*
	 * The following methods can be used to integrate the DefaultFileSystemManager and VFS with runtime.
	 */
	
	/*static synchronized public DefaultFileSystemManager getDefaultFileSystemManager() {
		
		
		if(singletonFSM == null)
		{
			singletonFSM = new DefaultFileSystemManager();
			
			try {
				singletonFSM.addProvider("local", new DefaultLocalFileProvider());
			    singletonFSM.addProvider("ftp", new FtpFileProvider());
			    singletonFSM.addProvider("rmi", new PAFileProvider());
			    singletonFSM.addProvider("http", new HttpFileProvider());
			    	    
		       }
			
		      catch (FileSystemException e) 
		      {			
                e.printStackTrace();
		      }
		      
		      try {
		    	  
					singletonFSM.init();
					
				} catch (FileSystemException e) {
					
					e.printStackTrace();
				}
		
		}
				
		return singletonFSM;
		     	
	}
		
	
	
	public synchronized static DefaultFileSystemManager getDefaultFileSystemManager(Node node)
	{
		ProActiveRuntime runtime = node.getProActiveRuntime();
		
		DefaultFileSystemManager fsm = runtime.getDefaultFileSystemManager();
		
		return fsm;
	}
	*/
	
	/*static synchronized public FileSystem getFileSystem()  {
		
		if(singletonFS == null)
		{
			DefaultFileSystemManager fManager = ProActiveVFS.getDefaultFileSystemManager(currentNode);
			FileObject root = null;
			try {
				root = fManager.createVirtualFileSystem(ROOT_NAME);
			} catch (FileSystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			singletonFS = root.getFileSystem();
		}
		
		return singletonFS;
				
	}*/
	
	
	
	/*----------------------------For Local Mounting-------------------------------------------------------------*/
	
	
	public static void setDirectories(FileNameMap inputSpaceURI,FileNameMap localScratchSpaceURI , FileNameMap outputSpaceURI)//, FileNameMap RemoteoutputSpaceURI)
	{
		
		ProActiveVFS.inputSpaceURI = inputSpaceURI;
		ProActiveVFS.localScratchSpaceURI = localScratchSpaceURI;
		ProActiveVFS.outputSpaceURI = outputSpaceURI;
		
	}
		
		
	//Mounting Step for the directories locally
	
	public static void mountLocally(Node node) throws FileSystemException
	{
				
		 System.out.println("*******Local Mounting Initiated********  at " + node.getNodeInformation().getName());	
		 
		 DefaultFileSystemManager localFSM = new DefaultFileSystemManager();
		 
		 localFSM.addProvider("local", new DefaultLocalFileProvider());
		 //localFSM.addProvider("rmi", new PAFileProvider());
		 localFSM.addProvider("ftp", new FtpFileProvider());
		 localFSM.addProvider("http", new HttpFileProvider());
		 localFSM.addProvider("sftp", new SftpFileProvider());
		 
		 localFSM.init();
		
		//DefaultFileSystemManager localFSM = ProActiveVFS.getDefaultFileSystemManager(node);
		 
		 FileObject root = localFSM.createVirtualFileSystem(ROOT_NAME);
		 
		 FileSystem localVFS = root.getFileSystem();
		 NodelocalVFSMap.put(node, localVFS);
		 
		// DefaultFileSystemManager localFSM = ProActiveVFS.getDefaultFileSystemManager(node);
		 //FileSystem localVFS = ProActiveVFS.getFileSystem(node);
		 //FileObject root = localFSM.createVirtualFileSystem(ROOT_NAME);
		// FileSystem localVFS = root.getFileSystem();
		 		 		 
	     FileObject input = ProActiveVFS.mount(localFSM,localVFS,ProActiveVFS.inputSpaceURI);
	     FileObject localScratch = ProActiveVFS.mount(localFSM,localVFS,ProActiveVFS.localScratchSpaceURI);
		 FileObject output = ProActiveVFS.mount(localFSM,localVFS,ProActiveVFS.outputSpaceURI);
			          		   				  		
	}
	
	
private static FileObject mount(DefaultFileSystemManager resolver, FileSystem virtualFS, FileNameMap fileToMount) throws FileSystemException 

{
			
	return ProActiveVFS.mount(resolver, virtualFS, fileToMount.getRealURI(), fileToMount.getMountingPoint());
		
}
		
		
	
	// add a list a capabilities to be checked
private static FileObject mount(DefaultFileSystemManager resolver, FileSystem virtualFs, String realURI, String mountingPoint) throws FileSystemException {
		FileObject fobj = resolver.resolveFile(realURI);
		FileSystem fsys = fobj.getFileSystem();
		
		// can check FileSystem capability (e.g. read for input)
		System.out.println("Capability of the file system to read is:" + fsys.hasCapability(Capability.READ_CONTENT));
		System.out.println("Capability of the file system for junctions is:" + fsys.hasCapability(Capability.JUNCTIONS));
				
		System.out.println("[VFS] Space " + realURI + " is resolved");
		virtualFs.addJunction(mountingPoint, fobj);
		System.out.println("[VFS] Space " + fobj.getURL() + " is mounted at " + virtualFs.getRootName()+ mountingPoint);
		return fobj;
	}
	
	
	
	
	/*public static FileSystem getProActiveVFS()
	{
		return vfs;
	}*/
	
	public static String getLocalWorkingDirectory()
	{
		return ProActiveVFS.localSpaceURI.getMountingPoint();
	}
	
	public static String getLocalOutPutDirectory()
	{
		return ProActiveVFS.outputSpaceURI.getMountingPoint();
	}

	
	/*-----------------------------Methods for Remote Mounting Begin from here------------------------------------*/
	
	
	/*
	 * setting the ProActive directories for remote nodes and calling the MountRemote method
	 * The URLs used here are constructed to lookup PAFileProviderEngines(PA Servers) running on the remote nodes
	 * incase of PAProvider being used for communication.
	 * A new instance of the DefaultFileSystemManager is created and a new virtual file system is created for each node
	 * Ideally, This should be corrected. This should be included with the runtime.
	 * Thus, there should be one DefaultFileSystemManager instance and one VFS, per runtime
	 */
	
	public static void setRemoteDirectories(Node currentNode,ArrayList<String> nodeURLs) throws FileSystemException, NodeException
	{ 	
		
		DefaultFileSystemManager remoteFSM = new DefaultFileSystemManager();
		remoteFSM.addProvider("local", new DefaultLocalFileProvider());
		remoteFSM.addProvider("rmi", new PAFileProvider());
		remoteFSM.addProvider("http", new HttpFileProvider());
		remoteFSM.addProvider("sftp", new SftpFileProvider());
		remoteFSM.addProvider("ftp", new FtpFileProvider());
		
		remoteFSM.init();
		
		FileObject remoteRoot = remoteFSM.createVirtualFileSystem(REMOTE_ROOT_NAME);
		
		FileSystem remoteVFS = remoteRoot.getFileSystem();
		
		NodeVFSMap.put(currentNode, remoteVFS);
		
		
		for(String url : nodeURLs)
		{
			Node node = NodeFactory.getNode(url);
			/*if(node.equals(currentNode))
			{
				System.out.println(currentNode.getNodeInformation().getName() + " Already Mounted******");
				continue;
			}
			
			else
			{*/
			 	
			 			
				String nodeName = node.getNodeInformation().getName();
				String urlToMount = MountedNodes.getPAURL(node);
				System.out.println("Now Mounting " + urlToMount + " at mounting point "+ nodeName);
				
				ProActiveVFS.RemoteoutputSpaceURI.add(urlToMount);
				
				//FileSystem currentVFS = ProActiveVFS.getVFS(currentNode);
				System.out.println("File System 1: " + remoteVFS);
				PAFileProvider.setCurrentNode(node);
				
				ProActiveVFS.mountremote(remoteFSM,remoteVFS,urlToMount,node);
				
				
			//} 
			
		}
		
	}	
	
	
	private static FileObject mountremote(DefaultFileSystemManager resolver, FileSystem virtualFS, String fileToMount, Node node) throws FileSystemException 
	   
	{	
		System.out.println("File System 2: " + virtualFS);
		return ProActiveVFS.mountremote(resolver, virtualFS, fileToMount, node.getNodeInformation().getName());	
	}

	
	private static FileObject mountremote(DefaultFileSystemManager resolver, FileSystem virtualFs, String realURI, String mountingPoint) throws FileSystemException {
				
		System.out.println("File System 3: " + virtualFs);
		FileObject fobj = resolver.resolveFile(realURI);
		//String url = "http://www.cse.buffalo.edu/~akkapur/";
		//FileObject fobj = resolver.resolveFile(url);
		System.out.println("Mounting Point is: " + mountingPoint);
		System.out.println("Mounting URI is: " + fobj);
		System.out.println("[VFS] Space " + realURI + " is resolved");
		virtualFs.addJunction("VFS_" + mountingPoint, fobj);	
		return fobj;
	}
	
		
	public static FileSystem getRemoteProActiveVFS()
	{
		
	return remotevfs;
	
	}
	
	//the following method will be used by the engine to get the file system for a specific node
	
	public static FileSystem getVFS(Node node)
	{

		return ProActiveVFS.NodeVFSMap.get(node);
	}
	
	public static FileSystem getLocalVFS(Node node)
	{
		return ProActiveVFS.NodelocalVFSMap.get(node);
	}
	
	
/*public void mountRemoteSpaces()
	
	{
	
	addRemoteSpaceURI(mt.getMountedNodesList());
	
	MountRemoteSpaceURIs();
	
	}
	*/
}
