package org.objectweb.proactive.extra.vfs;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;

public class CreateAgent implements Serializable {

	/**
	 * 
	 */
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
		
		Directories mkdir = new Directories();
		Processing process = new Processing();
		
		for(int i=1;i<4;i++)          //creating 3 active objects for each node
		{	
			
		//creating active object
		Object[] params = new Object[] { node };
		Agent obj = (Agent) PAActiveObject.newActive(Agent.class.getName(),params,node);
		     		
		CreateAgent.agents.add(obj);		
		
		boolean ok = mkdir.makeAODirectories(node, obj.hashCode());
		
		ok = mkdir.createDataFile(node, obj.hashCode(), "Temporay Data File");
		
		//process.processData(node,obj.hashCode());
		
		//PAActiveObject.terminateActiveObject(obj, false);
		}
	return CreateAgent.agents;

  }
}
