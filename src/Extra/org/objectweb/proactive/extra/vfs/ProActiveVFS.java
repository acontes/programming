package org.objectweb.proactive.extra.vfs;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.impl.*;
import org.apache.commons.vfs.provider.ftp.FtpFileProvider;
import org.apache.commons.vfs.provider.http.HttpFileProvider;
import org.apache.commons.vfs.provider.local.DefaultLocalFileProvider;
import org.apache.commons.vfs.provider.sftp.SftpFileProvider;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extra.vfs.PAProvider.PAFileProvider;
import org.objectweb.proactive.extra.vfs.PAProvider.PAFileProviderEngine;




public class ProActiveVFS {
	
	/*private static DefaultFileSystemManager fsm;
	private static FileObject root;*/
	public static Hashtable<Node,FileSystem> NodeVFSMap = new Hashtable<Node,FileSystem>();
	//public static HashMap<Node,FileSystem> NodeVFSMap = new HashMap<Node,FileSystem>();
	public static final String ROOT_NAME = "vfs://root";          //declares the root for the virtual file system
	public static final String REMOTE_ROOT_NAME = "vfs://global";          //declares the root for the virtual file system
	
	private static FileNameMap localSpaceURI;                             //read write access
	private static FileNameMap inputSpaceURI;                             //read access
	private static FileNameMap LocaloutputSpaceURI;                       //read write access
	private static ArrayList<String> RemoteoutputSpaceURI = new ArrayList<String>();
	private static ArrayList<Node> nodes = new ArrayList<Node>();
	
	private static MountNameMap mountmap;
	
	private static FileSystem vfs;
	private static FileSystem remotevfs;
	private static MountedNodes mt = new MountedNodes();
	public static CreateNode cn = new CreateNode();
	private static DefaultFileSystemManager fsm = new DefaultFileSystemManager();
	
	public ProActiveVFS()
	{
        
	}
	
	
	/*----------------------------For Local Mounting--------------------------------------------*/
	
	
	public static void setDirectories(FileNameMap inputSpaceURI, FileNameMap LocaloutputSpaceURI)//, FileNameMap RemoteoutputSpaceURI)
	{
		//ProActiveVFS.localSpaceURI = localSpaceURI;
		ProActiveVFS.inputSpaceURI = inputSpaceURI;
		ProActiveVFS.LocaloutputSpaceURI = LocaloutputSpaceURI;
		//ProActiveVFS.RemoteoutputSpaceURI = RemoteoutputSpaceURI;
	}
	
	
	
	//Mounting Step for the directories locally
	
	public static FileSystem MountAll(Node node)
	{
		try {
		
		         //DefaultFileSystemManager fsm = new DefaultFileSystemManager();        //creating a default implementation of the filesystem
		           
		         //add providers to the filesystem
		         
			     ProActiveVFS.fsm.addProvider("local", new DefaultLocalFileProvider());
			     ProActiveVFS.fsm.addProvider("ftp", new FtpFileProvider());
			     ProActiveVFS.fsm.addProvider("http", new HttpFileProvider());
			     ProActiveVFS.fsm.addProvider("sftp", new SftpFileProvider());	
			     ProActiveVFS.fsm.addProvider("rmi", new PAFileProvider());
			     
			     
			     fsm.init();            //initialize the fsm
			     
			     //creating a virtual file system with the root    
			     
			     FileObject root = fsm.createVirtualFileSystem(ROOT_NAME);
			     System.out.println("Creating VFS with the root: " + root.getName());
			     System.out.println("Root created at:"+ root.getName());
			     
			     ProActiveVFS.vfs = root.getFileSystem();		     
			     		       
			     FileObject input = ProActiveVFS.mount(fsm, ProActiveVFS.vfs, ProActiveVFS.inputSpaceURI);
			     FileObject output1 = ProActiveVFS.mount(fsm, ProActiveVFS.vfs, ProActiveVFS.LocaloutputSpaceURI);
			     
			     
			     ProActiveVFS.NodeVFSMap.put(node, ProActiveVFS.vfs);                    //creating a node-vfs map
			     		     
			     return ProActiveVFS.vfs;
			     
		   } catch (FileSystemException e) {
			// TODO Auto-generated catch block
			  e.printStackTrace();
		}
		   
		   return null;
		
	}
	
	
private static FileObject mount(DefaultFileSystemManager resolver, FileSystem virtualFS, FileNameMap fileToMount) throws FileSystemException 

{
			
	return ProActiveVFS.mount(resolver, virtualFS, fileToMount.getRealURI(), fileToMount.getMountingPoint());
		
}
		
		
	
	// add a list a capabilities to be checked
	private static FileObject mount(DefaultFileSystemManager resolver, FileSystem virtualFs, String realURI, String mountingPoint) throws FileSystemException {
		FileObject fobj = resolver.resolveFile(realURI);
		FileSystem fsys = fobj.getFileSystem();
		
		// can check fs capability (e.g. read for input)
		System.out.println("Capability of the file system to read is:" + fsys.hasCapability(Capability.READ_CONTENT));
		System.out.println("Capability of the file system for junctions is:" + fsys.hasCapability(Capability.JUNCTIONS));
		
		
		System.out.println("[VFS] Space " + realURI + " is resolved");
		virtualFs.addJunction(mountingPoint, fobj);
		System.out.println("[VFS] Space " + fobj.getURL() + " is mounted on " + virtualFs.getRootName()+ mountingPoint);
		return fobj;
	}
	
	
	
	
	public static FileSystem getProActiveVFS()
	{
		
		return vfs;
	}
	
	public static String getLocalWorkingDirectory()
	{
		return ProActiveVFS.localSpaceURI.getMountingPoint();
	}
	
	public static String getLocalOutPutDirectory()
	{
		return ProActiveVFS.LocaloutputSpaceURI.getMountingPoint();
	}

	
	/*-----------------------------Methods for Remote Mounting Begin from here------------------------------------*/
	
	
	/*
	 * setting the ProActive directories for remote nodes
	 * The URLs used here are constructed to lookup PAFileProviderEngines(PA Servers) running on the remote nodes
	 * incase of PAProvider being used for communication.
	 */
	
	public static void setRemoteDirectories()
	{ 	
		
		for(Node node: MountedNodes.getMountedNodesList())	
			for(String url: MountedNodes.getMountedNodesURLList())
		{	
			
		ProActiveVFS.RemoteoutputSpaceURI.add(url);
		ProActiveVFS.nodes.add(node);
				
		}
			
	}	
	
	
	
	public static void MountRemoteSpaceURIs() {
		
		try {
			
	         DefaultFileSystemManager newfsm = new DefaultFileSystemManager();        //creating a default implementation of the filesystem
	           
	         //add providers to the filesystem
	         
	         newfsm.addProvider("local", new DefaultLocalFileProvider());
	         newfsm.addProvider("ftp", new FtpFileProvider());
	         newfsm.addProvider("http", new HttpFileProvider());
		     newfsm.addProvider("sftp", new SftpFileProvider());
		     newfsm.addProvider("rmi", new PAFileProvider());
		     
	         newfsm.init();    //initialize the fsm
		     
		     //creating a virtual file system with the root  
		     
		     FileObject root = newfsm.createVirtualFileSystem(REMOTE_ROOT_NAME);
		     System.out.println("Creating the Global VFS with the root: " + root.getName());
		     System.out.println("Root created at:"+ root.getName());
		     
		     ProActiveVFS.remotevfs = root.getFileSystem();
		     		     
		     System.out.println("Now Mouting Remote URIs");
		     
		     FileObject remoteURLMounts = ProActiveVFS.mountremote(newfsm, ProActiveVFS.remotevfs,ProActiveVFS.RemoteoutputSpaceURI);   
		     
		     
	   } catch (FileSystemException e) {
		// TODO Auto-generated catch block
		  e.printStackTrace();
	}
		
}
	
	/*
	 * edited 14th Nov.... what i am doing here is... if the PAProvider has to be used then instead of making a FileObject of the realURI
	 * I pass this URI(get the node reference) to the engine and get the root of the VFS associated with the node 
	 */
	private static FileObject mountremote(DefaultFileSystemManager resolver, FileSystem virtualFs, String realURI, String mountingPoint) throws FileSystemException {
			
		
		FileObject fobj = resolver.resolveFile(realURI);
		System.out.println("[VFS] Space " + realURI + " is resolved");
		virtualFs.addJunction(mountingPoint, fobj);
		
		/*FileSystem fsys = fobj.getFileSystem();
		
		// can check fs capability (e.g. read for input)
		System.out.println("Capability of the file system to read is:" + fsys.hasCapability(Capability.READ_CONTENT));
		System.out.println("Capability of the file system for junctions is:" + fsys.hasCapability(Capability.JUNCTIONS));*/
		
		return fobj;
	}
	
	
	private static FileObject mountremote(DefaultFileSystemManager resolver, FileSystem virtualFS, ArrayList<String> fileToMount) {
		
		try {
			
			
			
		for(String url:fileToMount)
			for(Node node : ProActiveVFS.nodes)
			{
		{
			
			
		String realURI = url;
		System.out.println("Real URI is:" + realURI);
		System.out.println("Mounting:"+ realURI);
		
		//System.out.println("Global VFS, real usi is:"+realURI);
		
		if(realURI.contains("http"))
		{
			FileObject httpfile;
			
				httpfile = resolver.resolveFile(realURI);
			return ProActiveVFS.mountremote(resolver,virtualFS,httpfile.toString(),node.toString());
		}
		
		else if(realURI.contains("sftp"))
		{
			//FileObject sftpfile = resolver.resolveFile(realURI);
			 
			return ProActiveVFS.mountremote(resolver,virtualFS,realURI,node.getNodeInformation().getName());
		}
	         
		else
		{
			return ProActiveVFS.mountremote(resolver,virtualFS,realURI,node.toString());
		}
		
		
		
		}
		
		}
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	public static FileSystem getRemoteProActiveVFS()
	{
		
	return remotevfs;
	
	}
	
	//the following method will be used by the engine to get the file system for a specific node
	
	public static FileSystem getVFS(Node node)
	{
	
		return NodeVFSMap.get(node);
	}
	
	
/*public void mountRemoteSpaces()
	
	{
	
	addRemoteSpaceURI(mt.getMountedNodesList());
	
	MountRemoteSpaceURIs();
	
	}
	*/
}
