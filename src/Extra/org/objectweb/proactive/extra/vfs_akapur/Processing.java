package org.objectweb.proactive.extra.vfs_akapur;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAFileTransfer;
import org.objectweb.proactive.core.filetransfer.RemoteFile;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;



/**
 * A class where some application dependent code can be introduced
 * @author akapur
 *
 */

public class Processing {

	
	/**
	 * 
	 * @param node
	 * @param scratchURI : this is the URI of the scratch directory of the AO where the processed text file needs to created.
	 */
	 public static void processInput(Node node,String scratchURI)
	 {
		 /*System.out.println("Processing............");
		 File inputFile = new File("/user/akapur/home/Demo/input/test");
		 File scratchFile = new File(scratchURI+"/","Data.txt");
		//File newscr = new File(scratchURI+ "/" + "Data.txt");
	    try {
			RemoteFile file = PAFileTransfer.createFile(node, scratchFile);
		} catch (ActiveObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		 
		 System.out.println("Processing Data...........");
		 
		 FileSystem fs = ProActiveVFS.getLocalVFS(node);
		 
		 FileSystemManager fsm = fs.getFileSystemManager();
		 
		 try {
			 
			FileObject inputFile = fsm.resolveFile("sftp://akapur@cheypa.inria.fr/tmp/input/test");
			FileObject scratch = fsm.resolveFile("/scratch/"+node.getNodeInformation().getName());
			System.out.println(scratch.exists());
			
			System.out.println(inputFile.isReadable());
			
			System.out.println(inputFile.exists());
			
			File scratchFile = new File(scratchURI+"/","Data.txt");
			
			RemoteFile outputFile = PAFileTransfer.createFile(node, scratchFile); 
			
			
			FileContent fc = inputFile.getContent();
			
			InputStream iStream = fc.getInputStream();
			FileOutputStream oStream = new FileOutputStream(scratchFile);
			int c;
			while((c = iStream.read())!= -1)
			{
				   
			       oStream.write(c);
			}
			
			
		} catch (FileSystemException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActiveObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	 }
	
	/*public static void processInput(String scratchURI)
	{
		try{
			
			System.out.println("Processing............");
			
			File inputFile = new File("/user/akapur/home/Demo/input/test");
			
			//File scratchFileDir = scratchURI;
			
			File scratchFile = new File(scratchURI,"Data.txt");
			
			if(!scratchFile.exists())
			{
				scratchFile.createNewFile();
				System.out.println("Intermediate Output file in Scratch at:" + scratchFile.getPath());
			}
			
			
			
			//FileObject inputfile = ProActiveVFS.getProActiveVFS().resolveFile(inputURI);
			//outputfiledir.createNewFile();
		    
			FileInputStream fin = new FileInputStream(inputfile);
			FileOutputStream fout = new FileOutputStream(outputfile);
			
			int c;
			
			while((c = fin.read())!=-1)
			{
				fin.
				
				fout.write(c);
			}
			
			
	        //FileObject outputfile = ProActiveVFS.getRemoteProActiveVFS().resolveFile(outputURI);
	        
	        //String outputfilestring = (outputfile.toString()).concat("/Data.txt");
	        
	        FileReader in = new FileReader(inputFile);
	        
	        FileWriter out = new FileWriter(scratchFile);
	        
	        BufferedReader reader = new BufferedReader(in);
	        BufferedWriter writer = new BufferedWriter(out);
	        char character;
	        int c;
	        String l;
	        Scanner s = null;
	        
	        s = new Scanner(reader);
	        
	        while(s.hasNext())
	        {
	        
	        String str = s.findInLine("aaa");
	        str.toUpperCase();
	        writer.write(str);
	        		
	        	
	        	
	        System.out.println("check check...readin data readind data");
	        //writer.write("Hello this is the file for the active object");
	        //writer.write(s.next());
	        
	        }
	        
	        in.close();
	        out.close();
	        
	        
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}*/

	
}
