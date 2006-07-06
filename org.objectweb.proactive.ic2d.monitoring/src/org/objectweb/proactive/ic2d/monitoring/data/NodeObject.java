package org.objectweb.proactive.ic2d.monitoring.data;

import java.rmi.AlreadyBoundException;
import java.util.List;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.ic2d.monitoring.spy.Spy;
import org.objectweb.proactive.ic2d.monitoring.spy.SpyEventListenerImpl;
import org.objectweb.proactive.ic2d.monitoring.spy.SpyListenerImpl;

public class NodeObject extends AbstractDataObject{

	/* The node name */
	private String key;
	/* A ProActive Node */
	protected Node node;
	/**  */
	protected Spy spy;
	private static String SPY_LISTENER_NODE_NAME = "SpyListenerNode";
	private static Node SPY_LISTENER_NODE;
	protected SpyListenerImpl activeSpyListener;
	
	static {
		String currentHost;
		try {
			currentHost = UrlBuilder.getHostNameorIP(java.net.InetAddress.getLocalHost());
		} catch (java.net.UnknownHostException e) {
			currentHost = "localhost";
		}
		//System.out.println("current host: "+currentHost);
		try {
			SPY_LISTENER_NODE = NodeFactory.createNode(UrlBuilder.buildUrlFromProperties(
					currentHost, SPY_LISTENER_NODE_NAME), true, null, null);
		} catch (NodeException e) {
			SPY_LISTENER_NODE = null;
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}
	}
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//

	public NodeObject(VMObject parent, Node node){
		super(parent, node.getNodeInformation().getName());
		System.out.println("NodeObject : constructor");
		this.node = node;
		this.key = node.getNodeInformation().getName();
		try {
			new SpyEventListenerImpl(this);
			SpyListenerImpl spyListener = new SpyListenerImpl(new SpyEventListenerImpl(this)/*spyEventListener*/);
			this.activeSpyListener = (SpyListenerImpl) ProActive.turnActive(spyListener,SPY_LISTENER_NODE);
			this.spy = (Spy) ProActive.newActive(Spy.class.getName(), new Object[] { activeSpyListener }, node);
		} 
		catch(NodeException e1) { 
			e1.printStackTrace();
		}
		catch(ActiveObjectCreationException e2) {
			e2.printStackTrace();
		}
	}
	//
	// -- PUBLIC METHOD -----------------------------------------------
	//
	/**
	 * Explores itself, in order to find all active objects known by this one
	 */
	public void explore(){
		System.out.println("NodeObject : explore()");
		VMObject parent = getTypedParent();
		List activeObjects = null;
		try {
			activeObjects = parent.getProActiveRuntime().getActiveObjects(this.key);
		} catch (ProActiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		handleActiveObjects(activeObjects);
	}
	public String getKey() {
		return this.key;
	}

	public String getFullName() {
		return this.key;
	}
	/**
	 * Returns the node's protocol
	 * @return The protocol used
	 */
	public String getProtocol() {
		return node.getNodeInformation().getProtocol();
	}

	public String toString() {
		return this.getKey();
	}

	public String getType() {
		return "node";
	}

	//
	// -- PROTECTED METHOD -----------------------------------------------
	//
	/**
	 * Get the typed parent
	 * @return the typed parent
	 */
	protected VMObject getTypedParent() {
		return (VMObject) parent;
	}
	protected void exploreChild(AbstractDataObject child) {
		if(skippedChildren.containsKey(child.getKey())){
			System.out.println("NodeObject : exploreChild");
			return;
		}
		else if (!monitoredChildren.containsKey(child.getKey()))
			this.putChild(child);
		else { //parent.monitoredChildren.containsKey(vm.getKey())
			AOObject.cancelCreation();
			child = (AbstractDataObject)monitoredChildren.get(child.getKey());
		}
		child.explore();
	}
	//
	// -- PRIVATE METHOD -----------------------------------------------
	//
	/**
	 * TODO
	 * @param activeObjects names' list of active objects containing in this NodeObject
	 */
	private void handleActiveObjects(List activeObjects){
		System.out.println("NodeObject : handleActiveobject");
		for (int i = 0, size = activeObjects.size(); i < size; ++i) {
			List aoWrapper = (List) activeObjects.get(i);
			UniversalBody ub = (UniversalBody)aoWrapper.get(0);
			String className = (String) aoWrapper.get(1);
			/* We don't monitor spies */
			if (className.equalsIgnoreCase(
			"org.objectweb.proactive.ic2d.spy.Spy")) {
				continue;
			}
			AOObject ao = new AOObject(this,className.substring(className.lastIndexOf(".")+1), ub.getID());
			exploreChild(ao);
		}
	}
}
