package org.objectweb.proactive.extra.vfs;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileFilter;
import org.apache.commons.vfs.FileFilterSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.objectweb.proactive.api.PAFileTransfer;
import org.objectweb.proactive.core.filetransfer.RemoteFile;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.examples.dynamicdispatch.nqueens.Out;

public class Processing {
	
	public void printChildrenPAVFS() {
		System.out.println(" Processing Input / Output ");
		for(MountedNodesMap a: MountedNodes.getMountedNodeMap()) {
			Node n = a.getMountedNode();
			FileSystem fs = ProActiveVFS.getRemoteVFS(n);
			try {
				FileObject root = fs.getRoot();
				FileObject [] children = root.findFiles(new AllFileSelector());
				//System.out.println("Children Length  -- " + root.getChildren().length);
				
				/*for (FileObject child : children) {
					System.out.println("Child -- " + child.getName());
				}*/
				
			} catch (FileSystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
		public void processData(Node node, int i) {

			System.out.println(" ============= Test Application ============ ");
			
			//FileSystem fs = ProActiveVFS.getPAVFS(n);
			try {
				/*FileObject root = fs.getRoot();
				//FileObject [] children = root.findFiles(new AllFileSelector());
				FileFilter ff = new FileFilter()
					{
					    public boolean accept(FileSelectInfo fileInfo)
					    {
					        FileObject fo = fileInfo.getFile();
					        return fo.getName().getBaseName().matches("/input");
					    }
					                
					};*/
				File scratch  = new File("/tmp/scratch/VFS_"+node.getNodeInformation().getName()+"/"+i+"/MyProcessing.dat");
				
				FileObject fo = ProActiveVFS.getPAVFS().resolveFile("/input");
				//System.out.println(" Exists : " +fo.exists() + " Readable?  "+fo.isReadable());		
				FileContent fc = fo.getContent();
				InputStream is = fc.getInputStream();
				char ch = 'a';
				String s = "";
				
				int read = 0;
				int nbRead = 0;
				while (read!=-1) {
					read = is.read();
					ch =  (char)read;
					if (ch == 'a') ch ='X';
					if (ch == 'b') ch ='Y';
					if (ch == 'c') ch ='Z';
					s = s+ch;
					//if (ch == '#' ) break;
					//if (ch == 'B') break;
					//System.out.println(ch);
					//System.out.println("[" + PAActiveObject.getBodyOnThis().getID().shortString() + "] " + nbRead++ + "th reads = " + read);
				}
				System.out.println(s);
				
				fo = ProActiveVFS.getPAVFS().resolveFile("/ut");
				OutputStream os = fo.getContent().getOutputStream();
				os.write(s.getBytes());
				os.flush();
				os.close();
				
				System.out.println(" ============= End Test Application ============ ");
				
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
}
