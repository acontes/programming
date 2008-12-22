package org.objectweb.proactive.extra.vfs;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.core.node.Node;


/**
 * @author cdelbe
 *
 */
public class Agent implements java.io.Serializable, InitActive {
	private Node node;
	//private String nodeName;
	private String name ;
	{
		try {
			this.name = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Agent(){}
	
	public Agent(Node n){
		//System.out.println(" Passing Args = "+ob);
		this.node = n;
	}
	
	public void initActivity(Body body) {
		
		//System.out.print(" Node Name in Agent "+this.nodeName);
		/*ProActiveVFS.setSpaces( 
				new FileNameMap("http://cheypa:8080/tmp/output/","output"),
				new FileNameMap("/tmp/output/" ,"output"+ this.nodeName)
				);*/
		//ProActiveVFS.setInputSpace(new FileNameMap("http://cheypa:8080/ankush/","input"));
		//ProActiveVFS.setOutputSpace(new FileNameMap("/tmp/output","output"));
		//ProActiveVFS.mountAll(); 
	}
	
	public void copyFromTo(String sourceURI, String destinationURI){
		try {
			
			FileObject foSource = ProActiveVFS.getPAVFS(this.node).resolveFile(sourceURI);
			FileObject foDestination = ProActiveVFS.getPAVFS(this.node).resolveFile(destinationURI);
			foDestination.copyFrom(foSource, new AllFileSelector());
			if (foDestination.exists())
				System.out.println(" File Copied ");
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	public String readFile(String sourceURI, boolean randomWait) {
		String s ="";
		try {
			FileObject fo = ProActiveVFS.getRemoteVFS(node).resolveFile(sourceURI);
			FileContent fc = fo.getContent();
			InputStream is = fc.getInputStream();
			char ch = 'a';
			
			try {
				int read = 0;
				int nbRead = 0;
				while (read!=-1){
					read = is.read();
					Thread.sleep(randomWait?(long)(Math.random()*10):0);
					ch =  (char)read;
					s = s+ch;
					if (ch == '#' ) break;
					//System.out.println(ch);
					//System.out.println("[" + PAActiveObject.getBodyOnThis().getID().shortString() + "] " + nbRead++ + "th reads = " + read);
				}
				System.out.println(s);
			} catch (EOFException e) {
				System.out.println("END OF FILE");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			} catch (FileSystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return s;
	}
	
	public String toString() {
		 try {
			return  InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}return null;
	 }

}
