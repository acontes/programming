package org.objectweb.proactive.extra.vfs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAFileTransfer;
import org.objectweb.proactive.core.filetransfer.RemoteFile;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extra.vfs.provider.PAFileProviderEngine;

public class Directories {
	
	PAFileProviderEngine engine = null;
/**
 * Creating and locally mounting node directories
 * @param n
 * @return
 */	
	public boolean makeAndMountNodeDirectories(Node node){

		RemoteFile a;
		try {
			// Creating node directory using PAFileTransfer
			a = PAFileTransfer.mkdirs(node, new File("/tmp/scratch/VFS_"+ node.getNodeInformation().getName()));
			if (a.exists()) System.out.println("Node directory created for Node ID : "+node.getNodeInformation().getName());
			
			// Setting data spaces to be used in local VFS
			
			ProActiveVFS.setInputSpace( 
					new FileNameMap("sftp://kqadir@cheypa.inria.fr/tmp/input/test" ,"input")
					);
			ProActiveVFS.setOutputSpace( 
					new FileNameMap("sftp://kqadir@cheypa.inria.fr/tmp/output/" ,"output")
					);
			
			// Mounting locally 
			ProActiveVFS.mountInitial(node);
			
			// Starting engine (ServiceAO)
			engine = new PAFileProviderEngine();
			engine.getPAFileProviderEngine(node);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return true;
	}
	
/**
 * Creating AO directories using PAFileTranfer
 * @param node
 * @param AOId
 * @return
 */
	public boolean makeAODirectories(Node node, int AOId){
		RemoteFile a;
		try {
			a = PAFileTransfer.mkdirs(node, new File("/tmp/scratch/VFS_"+ node.getNodeInformation().getName()+"/"+AOId));
			if (a.exists()) System.out.println("AO directory created in Node : "+node.getNodeInformation().getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
/**
 * 
 * @param n
 * @param AOId
 * @param data
 * @return
 */
	public boolean createDataFile(Node n, int AOId, String data) {
		RemoteFile a;
		try {
			a = PAFileTransfer.createFile(n, new File("/tmp/scratch/VFS_"+ n.getNodeInformation().getName()+"/"+AOId+"/Data.dat"));
			if (a.exists()) System.out.println("Test Data File Created Successfully Inside AO Directory ");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ActiveObjectCreationException e) {
			e.printStackTrace();
		} catch (NodeException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/*public boolean printDirectoryStructure(){
		boolean a = false;
		System.out.println("------------------------- Directory Structure -------------------------");
		File b = new File("/tmp/scratch/");
		for (String x : b.list()) 
		{
			//System.out.println(x);
			File c = new File("/tmp/scratch/"+x);
			for (String y : c.list()) 
			{
				System.out.println("/tmp/scratch/"+x+"/"+y);
				//a = new File("/tmp/output/"+x+"/"+y).delete();
				if(a) System.out.println("/tmp/scratch/"+x+"/"+y+" Deleted");				
			}
		}
		return a;
	}*/
}
