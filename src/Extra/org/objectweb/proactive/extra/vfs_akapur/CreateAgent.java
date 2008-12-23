package org.objectweb.proactive.extra.vfs_akapur;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;



import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;



/**
 * A class to create Active Objects
 * @author akapur
 *
 */


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
		CreateAgent obj = (CreateAgent) PAActiveObject.newActive(CreateAgent.class.getName(),null);
		     		
		CreateAgent.agents.add(obj);		
		
		String pathToNode = "/tmp/scratch/VFS_" + node.getNodeInformation().getName();
		
		int AO_ID = obj.hashCode();
		
		ProActiveDataSpace.createActiveObjectDirectory(node, pathToNode, AO_ID);
		
       // Processing.processInput(node,pathToNode + "/" + AO_ID);
		
		
		}
				
	return CreateAgent.agents;

  }

	
	
}
