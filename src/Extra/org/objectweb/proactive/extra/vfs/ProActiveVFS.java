/**
 * 
 */
package org.objectweb.proactive.extra.vfs;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.provider.ftp.FtpFileProvider;
import org.apache.commons.vfs.provider.http.HttpFileProvider;
import org.apache.commons.vfs.provider.local.DefaultLocalFileProvider;
import org.apache.commons.vfs.provider.sftp.SftpFileProvider;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extra.vfs.provider.PAFileProvider;

/**
 * @author cdelbe
 *
 */
public class ProActiveVFS {

	//	declares the root for the virtual file system
	public static final String ROOT_NAME="vfs://";
	public static final String REMOTE_ROOT_NAME = "gvfs://";          
	static DefaultFileSystemManager fsm=null;
	
	private static FileNameMap localSpaceURI; // should be rw
	private static FileNameMap inputSpaceURI; // should be r
	private static FileNameMap outputSpaceURI; //should be rw
	private static ArrayList<FileNameMap> remoteSpaceURI = new ArrayList<FileNameMap>();

	private static FileSystem vfs;
	private static FileSystem remotevfs;
	private static HashMap<Node, FileSystem>nodeFSMap = new HashMap<Node, FileSystem>();
	private static HashMap<Node, FileSystem>nodeVFSMap = new HashMap<Node, FileSystem>();
	/**
	 * 
	 * @param localSpaceURI
	 * @param inputSpaceURI
	 * @param outputSpaceURI
	 * @param remoteSpaceURIs
	 */
	public static void setSpaces(FileNameMap inputSpaceURI, 
			FileNameMap outputSpaceURI ){
		//ProActiveVFS.localSpaceURI = localSpaceURI;
		ProActiveVFS.inputSpaceURI = inputSpaceURI;
		ProActiveVFS.outputSpaceURI = outputSpaceURI;
		//ProActiveVFS.remoteSpaceURIs[0] = outputSpaceURI;
	}
	
	/**
	 * 
	 * @param inputSpaceURI
	 */
	public static void setInputSpace(FileNameMap inputSpaceURI){
		ProActiveVFS.inputSpaceURI = inputSpaceURI;
	}
	
	/**
	 * 
	 * @param inputSpaceURI
	 */
	public static void setOutputSpace(FileNameMap outputSpaceURI){
		ProActiveVFS.outputSpaceURI = outputSpaceURI;
	}
	/**
	 * 
	 */
	public static void setRemoteSpaceURI(MountedNodesMap a)
	{ 	
		//for(MountedNodesMap b: MountedNodes.getMountedNodeMap()) {
		ProActiveVFS.remoteSpaceURI.add(new FileNameMap(a.getMountedUrl(),a.getMountedNode().getNodeInformation().getName() ));
		//}
	}
	/**
	 * @param n 
	 * 
	 */
	public static void mountInitial(Node n) {
		
		try {
			fsm = new DefaultFileSystemManager();
			fsm.addProvider("file", new DefaultLocalFileProvider());
			fsm.addProvider("sftp",new SftpFileProvider());
			fsm.addProvider("ftp",new FtpFileProvider()); 
			fsm.addProvider("http",new HttpFileProvider()); 
			fsm.init();
			
			FileObject root = fsm.createVirtualFileSystem(ProActiveVFS.ROOT_NAME);
			System.out.println("[VFS] VFS created with root name = " + root.getName());
			vfs = root.getFileSystem();
			System.out.println("Setting VFS Root = "+ProActiveVFS.vfs.getRoot());
			
			nodeVFSMap.put(n, vfs);
			
			/*System.out.println("FSM "+ProActiveVFS.fsm);
			System.out.println("VFS "+ProActiveVFS.vfs);*/
			
			FileObject input = ProActiveVFS.mount(fsm, vfs, ProActiveVFS.inputSpaceURI.getRealURI(),ProActiveVFS.inputSpaceURI.getMountingPoint());

			FileObject output = ProActiveVFS.mount(fsm, vfs, ProActiveVFS.outputSpaceURI.getRealURI(),ProActiveVFS.outputSpaceURI.getMountingPoint());
			
/*			for (FileNameMap uris : ProActiveVFS.remoteSpaceURIs) {
				ProActiveVFS.mount(fsm, vfs, uris.getRealURI(),uris.getMountingPoint());
			}*/
			
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void mountRemoteSpaceURI(MountedNodesMap a) {
		
		try {
			
			System.out.println(" I am "+ a.getMountedNode().getNodeInformation().getURL());
			
			DefaultFileSystemManager newfsm = new DefaultFileSystemManager();        //creating a default implementation of the filesystem
			
			//add providers to the filesystem
			PAFileProvider pfp = new PAFileProvider();
			newfsm.addProvider("rmi", pfp);

			//initialize the fsm
			newfsm.init();
		    
			//creating a virtual file system with the root			
			FileObject root = newfsm.createVirtualFileSystem(REMOTE_ROOT_NAME);
		    System.out.println("Creating VFS with the root: " + root.getName());
		    System.out.println("Root created at:"+ root.getName());

		    ProActiveVFS.remotevfs = root.getFileSystem();
		    System.out.println("VFS has root:"+ remotevfs.getRootName());
		    
		    nodeFSMap.put(a.getMountedNode(), remotevfs);
		    
			
			for(MountedNodesMap b: MountedNodes.getMountedNodeMap()) {
			pfp.setNode(b.getMountedNode());
			//mounting the local,input and output directories onto the virtual file system		     
		    FileObject remote = ProActiveVFS.mount(newfsm, ProActiveVFS.remotevfs, b.getMountedUrl(),b.getMountedNode().getNodeInformation().getName());
		    }
		     
		   } catch (FileSystemException e) {
			// TODO Auto-generated catch block
			  e.printStackTrace();
		   }
	}
	/*
	 * 
	 */
	// add a list a capabilities to be checked
	private static FileObject mount(FileSystemManager resolver, FileSystem virtualFs, String realURI, String mountingPoint) throws FileSystemException {
		FileObject fo = resolver.resolveFile(realURI);
		//System.out.println("PA FO is "+fo.getName());
		//FileSystem fs = fo.getFileSystem();
		// can check fs capability (e.g. read for input)
		//fs.hasCapability(Capability.READ_CONTENT);
		
		System.out.println("[VFS] Space " + realURI + " is resolved");
		virtualFs.addJunction(mountingPoint, fo);
		System.out.println("[VFS] Space " + fo.getURL() + " is mounted on " + virtualFs.getRootName()+mountingPoint);
	
/*		FileObject [] childern = fo.getChildren();
		System.out.println(" Childern of this folder / file " +  fo.getName().getURI() );
		for (int i=0;i<childern.length;i++) {
			System.out.println(childern[i].getName().getBaseName());
		}*/
		
		return fo;
	}
	
	/**
	 * 
	 * @return
	 */
	public static FileSystem getPAVFS() {
		//System.out.println("Return VFS ="+ProActiveVFS.vfs);
		return ProActiveVFS.vfs;
	}
	/**
	 * 
	 * @return
	 */
	public static FileSystem getPAVFS(Node n)
	{
		//System.out.println(" Return VFS "+ProActiveVFS.nodeFSMap.get(n));
		return ProActiveVFS.nodeVFSMap.get(n);
	}
	
	/**
	 * 
	 * @return
	 */
	public static FileSystem getRemoteVFS()
	{
		return ProActiveVFS.remotevfs;
	}
	/**
	 * 
	 * @return
	 */
	public static FileSystem getRemoteVFS(Node n)
	{
		//System.out.print(" Return Remote VFS "+ProActiveVFS.nodeFSMap.get(n));
		return ProActiveVFS.nodeFSMap.get(n);
	}
	/**
	 * 
	 * @return
	 */
	public static FileNameMap getLocalWorkingDirURI() {
		return ProActiveVFS.localSpaceURI;
	}
	/**
	 * 
	 * @return
	 */
	public static FileNameMap getInputDirURI() {
		return ProActiveVFS.inputSpaceURI;
	}
	/**
	 * 
	 * @return
	 */
	public static FileNameMap getOutputURI() {
		return ProActiveVFS.outputSpaceURI;
	}
	
/*	public synchronized static DefaultFileSystemManager getFileSystemManager(Node n) {
		
		ProActiveRuntime runtime = n.getProActiveRuntime();
		DefaultFileSystemManager fsm = runtime.getFileSystemManager();
		return fsm;
		
	}
	public static DefaultFileSystemManager getFileSystemManager() {
		if (ProActiveVFS.fsm == null)
		{
			try {
				fsm = new DefaultFileSystemManager();
				fsm.addProvider("file", new DefaultLocalFileProvider());
				fsm.addProvider("sftp",new SftpFileProvider());
				fsm.addProvider("ftp",new FtpFileProvider()); 
				fsm.addProvider("http",new HttpFileProvider()); 
				fsm.init();
			} catch (FileSystemException e) {
				e.printStackTrace();
			}
			
		}
		return ProActiveVFS.fsm;
	}
	
	public synchronized static FileSystem getFileSystem(Node n) {
		
		ProActiveRuntime runtime = n.getProActiveRuntime();
		FileSystem vfs = runtime.getFileSystem();
		return vfs;
		
	}
	public static FileSystem getFileSystem() {
		
		if (ProActiveVFS.vfs == null)
		{
		FileObject root;
		try {
			root = fsm.createVirtualFileSystem(ProActiveVFS.ROOT_NAME);
			System.out.println("[VFS] VFS created with root name = " + root.getName());
			FileSystem vfs1 = root.getFileSystem();
			System.out.println("Setting VFS Root = "+ProActiveVFS.vfs.getRoot());
			ProActiveVFS.vfs = vfs1;
			return vfs1;
		} catch (FileSystemException e) {
			e.printStackTrace();
		}
		}
		
		return ProActiveVFS.vfs;
	}*/
	
}
