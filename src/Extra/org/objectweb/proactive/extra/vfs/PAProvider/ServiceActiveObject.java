package org.objectweb.proactive.extra.vfs.PAProvider;

import java.util.ArrayList;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;


public class ServiceActiveObject implements InitActive {
	
	PAFileProviderEngine PAEngine = new PAFileProviderEngine();
	/*public ServiceActiveObject()
	{
		ArrayList<Node> nodereferences= new ArrayList<Node>();
	}*/
	
	
	public void createServiceActiveObject(Node node)
	{
			
		try {
			//Node currentnode = NodeFactory.getNode(URL);
			Node currentnode = node;
			
			ServiceActiveObject SAO = (ServiceActiveObject)PAActiveObject.newActive(ServiceActiveObject.class.getName(), null, currentnode);
			PAEngine.ServiceAOMap.put(currentnode, SAO);
			
			
			
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActiveObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public void initActivity(Body body)
	{
		String URL = body.getNodeURL();
		Node node;
		try {
			node = NodeFactory.getNode(URL);
			Object[] AOs = this.getAssociatedAOs(node);
			
			
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	
		
		
	}
	
	public Object[] getAssociatedAOs(Node node)
	{
	
		try {
			Object[] ActiveObjects = node.getActiveObjects();
			
			return ActiveObjects;
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActiveObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	
	
}
