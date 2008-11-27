package org.objectweb.proactive.extra.vfs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

import org.apache.commons.vfs.FileObject;





public class Processing {

	
	public void processInput(File outputURI)
	{
		try{
			
			System.out.println("Control is processInput method");
			
			File inputfile = new File("/user/akapur/home/Demo/input/test");
			
			File outputfiledir = outputURI;
			
			File outputfile = new File(outputURI,"Data.txt");
			
			if(!outputfile.exists())
			{
				outputfile.createNewFile();
				System.out.println("Output file:" + outputfile.getPath());
			}
			
			//FileObject inputfile = ProActiveVFS.getProActiveVFS().resolveFile(inputURI);
			//outputfiledir.createNewFile();
		    
			/*FileInputStream fin = new FileInputStream(inputfile);
			FileOutputStream fout = new FileOutputStream(outputfile);
			
			int c;
			
			while((c = fin.read())!=-1)
			{
				fin.
				
				fout.write(c);
			}*/
			
			
	        //FileObject outputfile = ProActiveVFS.getRemoteProActiveVFS().resolveFile(outputURI);
	        
	        //String outputfilestring = (outputfile.toString()).concat("/Data.txt");
	        
	        FileReader in = new FileReader(inputfile);
	        
	        FileWriter out = new FileWriter(outputfile);
	        
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
	        writer.write("Hello this is the file for the active object");
	        //writer.write(s.next());
	        
	        }
	        
	        in.close();
	        out.close();
	        
	        
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	
}
