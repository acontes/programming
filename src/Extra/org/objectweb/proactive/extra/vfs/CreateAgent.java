package org.objectweb.proactive.extra.vfs;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFileTransfer;
import org.objectweb.proactive.core.filetransfer.RemoteFile;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;

public class CreateAgent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static ArrayList<Object> agents = new ArrayList<Object>();
	
	
	
	public CreateAgent()
	{
		
	}
	
	/**
	 * 
	 * @param node : node on which the active objects are to be created
	 * @return : a list of active objects created
	 * @throws ActiveObjectCreationException
	 * @throws NodeException
	 * @throws IOException
	 */
	
	public static ArrayList<Object> createAgent(Node node) throws ActiveObjectCreationException, NodeException, IOException {

		
		
		for(int i=1;i<4;i++)          //creating 3 active objects for each node
		{	
			
		//creating active object	
		Agents obj = (Agents) PAActiveObject.newActive(Agents.class.getName(),null, node);
		     		
		CreateAgent.agents.add(obj);		
		
		String pathToNode = "/tmp/output/" + node.getNodeInformation().getName() +  "/" ;
		
		ProActiveDataSpace.createActiveObjectDirectory(node, pathToNode, obj.hashCode());
		
		
		}
				
	return CreateAgent.agents;

  }

	
	
}
