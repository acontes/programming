package org.objectweb.proactive.extra.vfs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFileTransfer;
import org.objectweb.proactive.core.filetransfer.RemoteFile;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

public class Mapper {

	ArrayList agents = new ArrayList();
	
	
	public Mapper()
	{
		
	}
	
  public void AOdeployer(GCMVirtualNode testVNode) throws ActiveObjectCreationException, NodeException, IOException
  
  {

	Node node = testVNode.getANode();
	  
	while (node != null) {

		
		Mapper obj = (Mapper) PAActiveObject.newActive(Mapper.class.getName(),
				null, node.getNodeInformation().getURL());
		
		
		
		//agents.add(obj);
		
		String str = "/tmp/output/" + node.getNodeInformation().getName();  

		/*if ((new File(str)).exists())

		{
			System.out.println("This node's directory exists, making Active Object directory in this already existing node dir");
		}

		else

		{*/

			/*RemoteFile noderemotefile = PAFileTransfer.mkdirs(node,
					new File("/tmp/output/"
							+ node.getNodeInformation().getName()));
			System.out.println("This node's directory is created");*/
			
			
			

		// creating directory for the active object.

		System.out.println("Creating Active Object directory on Node:"+node.getNodeInformation().getName());
		
		File path = new File("/tmp/output/"
				+ node.getNodeInformation().getName().toString() + "/"
				+ Integer.toString(obj.hashCode()));

		RemoteFile objectremotefile = PAFileTransfer.mkdirs(node, path);
		
		System.out.println("Printing directories for this active object");
        //obj.getOutput();
        
		node = testVNode.getANode();
		

	//}

}
	
	
 }
  
  
  
  
  
public void Display()
{
 System.out.println("OutPut Directories are >>>");
 
 //for(int i=0;i<agents.size();i++)
 
 //System.out.println(Agent.this.getInfo());
}
	
	
	
	


}
