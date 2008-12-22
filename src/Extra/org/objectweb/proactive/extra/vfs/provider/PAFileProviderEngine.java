package org.objectweb.proactive.extra.vfs.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.ProActiveInternalObject;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.vfs.MountedNodes;
import org.objectweb.proactive.extra.vfs.MountedNodesMap;
import org.objectweb.proactive.extra.vfs.ProActiveVFS;

public class PAFileProviderEngine implements ProActiveInternalObject, InitActive {
	protected static Logger logger = ProActiveLogger.getLogger(Loggers.VFS);
	private static PAFileProviderEngine singletonPAProviderEngine = null; //getPAFileProviderEngine();
	private String proto;
	private String host;
	private List<String> entries = new LinkedList<String>();
	private Node n = null;
	
	public PAFileProviderEngine() {
	
	}
	
	public synchronized PAFileProviderEngine getPAFileProviderEngine(Node n) {
		this.n = n;
		String scheme = "rmi";
		String host = n.getVMInformation().getHostName() ;
		int port = 1099;
		String url = scheme + "://" + host +":" + port + "/" + "VFS_"+n.getNodeInformation().getName();
		//String url = "rmi://" + n.getVMInformation().getHostName() + ":1099/VFSSERVER";
		//if (singletonPAProviderEngine == null) {
	            try {
	            	singletonPAProviderEngine = (PAFileProviderEngine) PAActiveObject.newActive(PAFileProviderEngine.class.getName(),null,n);
	            	/*if (singletonPAProviderEngine.proto.startsWith("rmi")) url = "rmi://localhost:1099/VFSSERVER"; 
	        		else if (singletonPAProviderEngine.proto.startsWith("http")) url = "http://localhost:1099/VFSSERVER";*/
	            	PAActiveObject.register(singletonPAProviderEngine,  url);
	            	
	            	//n = PAActiveObject.getActiveObjectNode(singletonPAProviderEngine);
	            	
	            	//System.out.println(n.getNodeInformation().getURL());
	            	System.out.println("***** VFS Server started at " + n.getVMInformation().getHostName() + " *****");
	            	
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        //}
	        return singletonPAProviderEngine;
	}
	
/*	public synchronized PAFileProviderEngine getPAFileProviderEngine(Node n) {
		
		ProActiveRuntime runtime = n.getProActiveRuntime();
		
		PAFileProviderEngine fpe = runtime.getPAVFSEngine();
		
		currentNode = n;
		
		return fpe;
		
	}*/
	
	public void initActivity(Body body) {
/*		proto = body.getNodeURL();
		try {
			currentNode = NodeFactory.getNode(body.getNodeURL());
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	/*
	 * get nodes from URI
	 */
	public Node[] getRemoteNodeReferences(String[] nodeURLs) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		try {
		for(String url:nodeURLs) {
			Node n = NodeFactory.getNode(url);
			nodes.add(n);
		}
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Node[])nodes.toArray();
	}
	 
	public PAFile[] getFiles(String relPath)
	   {	
		System.out.println("[VFS-PAFile-Engine] Get Files : " + relPath);
		List<PAFile> tmpResults = new LinkedList<PAFile>();
		File a = new File(relPath);
	if (a.list() != null) {
		//System.out.println(a.getAbsolutePath());
		for (String x : a.list()) 
		{
			File b = new File(relPath+"/"+x);
			//System.out.println("-----"+b.getName());
            PAFile tmp = new PAFile();
            tmp.setName(x);
            tmp.setLink(b.getAbsolutePath());
            tmpResults.add(tmp);
            
            if (b.list() != null) {
            	for (String y : b.list()) 
        		{
        			File c = new File(relPath+"/"+x+"/"+y);
        			//System.out.println("-----"+c.getName());
                    tmp = new PAFile();
                    tmp.setName(y);
                    tmp.setLink(c.getAbsolutePath());
                    tmpResults.add(tmp);
        			
        		}
            }
		}
	}
	else
		System.out.println("[VFS-PAFile-Engine] Get Files : ++++ Error in listing  ++++");

        return tmpResults.toArray(new PAFile[0]);
	 }
	 
	public InputStream readData(String url) {
		
		System.out.println("[VFS-PAFile-Engine] Read Data : " + url);
		byte [] bytes = null;
		InputStream is = null;
		
		try {
		File file = new File(url);
		is = new FileInputStream(file) ; 
	      
        /*// file size 
        long size = file.length(); 
        if  ( size  >  Integer.MAX_VALUE )   {  
        	System.out.println(" File is large");
          }  
      
        // Create the byte array to hold the data 
        bytes = new byte [(int)size] ; 
      
        // Read in the bytes 
        int offset = 0; 
        int numRead = 0; 
       
			while  ( offset  <  bytes.length &&  ( numRead=is.read ( bytes, offset, bytes.length-offset )) >= 0 )   {  
			     offset += numRead; 
			  }
      
         // Ensure all the bytes have been read in 
         if  ( offset  <  bytes.length )   {  
             throw new IOException ( "Could not completely read file "+file.getName (  )  ) ; 
          }  

         // Close the input stream and return bytes 
         is.close (  ) ; */
        } catch (IOException e) {
			e.printStackTrace();
		}
        
        return is; 
		//return "It will read 10 byets of";
	}
	
	public void getVFSObjects() {
		System.out.println(" It will return or print VFS Objects ");
	}

	public Object[] getAllAO(Node n) {
		try {
			return n.getActiveObjects();
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActiveObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void printPAVFS() {
		for(MountedNodesMap m:MountedNodes.getMountedNodeMap()) {
			System.out.println(" In printPAVFS "+m.getMountedUrl());
			printPAVFS(m.getMountedNode());
			}
	}
	
	public static void printPAVFS(Node n) {
		try {
			FileObject[] allFiles = null;

			allFiles = ProActiveVFS.getPAVFS(n).getRoot().findFiles(new AllFileSelector());

			System.out.println("===> Number of files in VFS : " + allFiles.length);

			for (FileObject f : allFiles){
				System.out.println("~>ls : " + f.getURL());
				/*FileObject[] childern = f.getChildren();
				for (FileObject c : childern) {
					System.out.println("~>ls : ------" + c.getURL());
				}*/
			}
		} catch (FileSystemException e) {
			e.printStackTrace();
		}
	}
	
	public void saveMountingMap() {
		System.out.println(" =========== Run time Mounting Map =============");
		//System.out.println("Real URI  :" + ProActiveVFS.getOutputURI().getRealURI() +"@"+this.hostName + " Virtual URI =" +ProActiveVFS.getOutputURI().getMountingPoint());
		
		try {
			File f = new File("/user/kqadir/home/MountingMap.xml");
			SAXBuilder builder = new SAXBuilder(true);
						
			Element root = new Element("FileMounts");
			Document doc = new Document(root);;
			doc.setRootElement(root);
			
			for(MountedNodesMap mn:MountedNodes.getMountedNodeMap()) {
				Element child1 = new Element("FileMount");
				child1.setAttribute("realURI", mn.getMountedUrl());
				child1.setAttribute("mountingPoint",mn.getMountedNode().getNodeInformation().getName());
				root.addContent(child1);
			}

			/*if(!f.exists())
				 { root = doc.getRootElement();
				doc = new Document(root); }
			else
				doc = builder.build(f);*/
		
		XMLOutputter outputter = new XMLOutputter();
		FileOutputStream o = new FileOutputStream(f);
		
		outputter.output(doc, o);
		outputter.output(doc, System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public boolean isConnectionOK() {
		return true;
	}

	public boolean makeDirectory(String relPath) {
		File a = new File(relPath);
		if (a.exists())
			return true;
		else
			return false;
	}
	
}
