package org.objectweb.proactive.extra.vfs;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.io.*;

import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;



public class Agent implements Serializable, InitActive {
	
	private String name;
	private String nodeName;
	
	{
		try {
			 name = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public Agent()
	{
		
	}
	
	public Agent(StringWrapper obj)
	{
		this.nodeName = obj.toString();
	}
	
	//this method performs the initialization function for the active object
	public void initActivity(Body body)
	{
		
	//UUID id = UUID.
	
	//body.getNodeURL()
	}
	
	/*public void runActivity(Body body)
	{
		System.out.println("Node URL in run active" + body.getNodeURL());
	}*/
	
public static void getInfo(){ 
		
		//try {
			
			//System.out.println("Hostname:" + name);
			
			
			FileObject[] allFiles = null;
            
			
			System.out.println("Control in the getInfo method, now trying to get the files in the root");
			System.out.println("First let's get the root");
			System.out.println(ProActiveVFS.getProActiveVFS());
			
			/*allFiles = ProActiveVFS.getProActiveVFS().getRoot().findFiles(new AllFileSelector());

			System.out.println("===> Number of files in VFS : " + allFiles.length);

			for (FileObject f : allFiles){
				System.out.println("~>ls : " + f.getURL());
			}
*/
		/*} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
public void copyToLocalDir(String sourceURI, String destName)
{
	try {
		FileObject fo = ProActiveVFS.getProActiveVFS().resolveFile(sourceURI);
		FileObject foLocal = ProActiveVFS.getProActiveVFS().resolveFile(ProActiveVFS.getLocalWorkingDirectory()+"/"+ destName);
		foLocal.copyFrom(fo, new AllFileSelector());
	} catch (FileSystemException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public String getHostName()
{
	return name;
}

public void processInput(String inputURI, String outputURI)
{
	try{
		
		FileObject inputfile = ProActiveVFS.getProActiveVFS().resolveFile(inputURI);
		
		File f;
		f = new File("Data.txt");
		if(!f.exists())
		{
			f.createNewFile();
		}
		
		
        FileObject outputfile = ProActiveVFS.getProActiveVFS().resolveFile(outputURI);
        
        String outputfilestring = (outputfile.toString()).concat("/Data.txt");
        
        FileReader in = new FileReader(new File(inputfile.toString()));
        FileWriter out = new FileWriter(new File(outputfilestring));
        
        BufferedReader reader = new BufferedReader(in);
        BufferedWriter writer = new BufferedWriter(out);
        String line;
        while((line = reader.readLine()) != null)
        {
        	if(line.contains("dddd"))
        	{
        		line.replace('a', 'z');
        	}
        
        
        
        writer.write(line);
        
        }
        
        in.close();
        
        
        
	}catch(Exception e)
	{
		e.printStackTrace();
	}
}


public String getInputURL()
{
	try{
		
	FileObject File;
	File = ProActiveVFS.getProActiveVFS().getRoot().resolveFile("vfs://root/input");
	
	return File.toString();
	
	} catch(FileSystemException e)
	{
		e.printStackTrace();
	}
	
	return null;
}

public String getOutputURL()
{
	try{
	FileObject File;
	File = ProActiveVFS.getProActiveVFS().getRoot().resolveFile("vfs://root/output");
	return File.toString();
	
	} catch(FileSystemException e)
	{
		e.printStackTrace();
	}
	
	return null;
}
	

}
