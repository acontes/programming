/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.ic2d.monitoring.data;

import java.rmi.AlreadyBoundException;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.ic2d.console.Console;
import org.objectweb.proactive.ic2d.monitoring.Activator;
import org.objectweb.proactive.ic2d.monitoring.filters.FilterProcess;
import org.objectweb.proactive.ic2d.monitoring.spy.Spy;
import org.objectweb.proactive.ic2d.monitoring.spy.SpyEventListener;
import org.objectweb.proactive.ic2d.monitoring.spy.SpyEventListenerImpl;
import org.objectweb.proactive.ic2d.monitoring.spy.SpyListenerImpl;

public class NodeObject extends AbstractDataObject{

	/* The virtual node containing this node */
	private VNObject vnParent;

	/* The node name */
	private String key;
	/* A ProActive Node */
	private Node node;
	/**  */
	private Spy spy;
	private static String SPY_LISTENER_NODE_NAME = "SpyListenerNode";
	private static Node SPY_LISTENER_NODE;
	private SpyListenerImpl activeSpyListener;

	/** The node job ID */
	private String jobID;
	
	
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
		super(parent);
		Comparator<String> comparator = new AOObject.AOComparator();
		monitoredChildren = new TreeMap<String , AbstractDataObject>(comparator);
		this.node = node;
		this.key = node.getNodeInformation().getName();
		this.jobID = node.getNodeInformation().getJobID();
	}

	//
	// -- PUBLIC METHOD -----------------------------------------------
	//

	/**
	 * Explores itself, in order to find all active objects known by this one
	 */
	@Override
	public void explore(){
		VMObject parent = getTypedParent();
		List<List<Object>> activeObjects = null;
		try {
			activeObjects = parent.getProActiveRuntime().getActiveObjects(this.key);
		} catch (ProActiveException e) {
			notResponding();
			Console.getInstance(Activator.CONSOLE_NAME).debug(e);
		}
		handleActiveObjects(activeObjects);
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public String getFullName() {
		return "Node "+this.key;
	}
	/**
	 * Returns the node's protocol
	 * @return The protocol used
	 */
	public Protocol getProtocol() {
		// TODO Uses the parent's protocol or the node's protocol ?
		// return node.getNodeInformation().getProtocol();
		return ((HostObject)this.parent.parent).getProtocol();
	}

	@Override
	public String toString() {
		return this.getKey();
	}

	@Override
	public String getType() {
		return "node";
	}

	public Spy getSpy() {
		return this.spy;
	}

	public String getJobID() {
		return this.jobID;
	}
	
	@Override
	public synchronized AOObject findActiveObjectById(UniqueID id) {
		return (AOObject) monitoredChildren.get(id.toString());
	}


	public void setHighlight(boolean highlighted) {
		this.setChanged();
		if (highlighted)
			this.notifyObservers(State.HIGHLIGHTED);
		else
			this.notifyObservers(State.NOT_HIGHLIGHTED);
	}


	public VNObject getVNParent() {
		return vnParent;
	}

	public void setSpyUpdateFrequence(long updateFrequence) {
		spy.setUpdateFrequence(updateFrequence);
	}

	@Override
	public void stopMonitoring(boolean log) {
		destroySpy();
		super.stopMonitoring(log);
	}

	public void migrateTo(UniqueID activeObjectID, String nodeTargetURL) throws MigrationException {
		try {
			spy.migrateTo(activeObjectID, nodeTargetURL);
		} catch (MigrationException e) {
			throw e;
		} catch (Exception e) {
			throw new MigrationException("Problem contacting the Spy", e);
		}
	}

	//
	// -- PROTECTED METHOD -----------------------------------------------
	//

	/**
	 * Adds a spy in this node
	 */
	protected void addSpy(){
		try {
			SpyEventListener spyEventListener = new SpyEventListenerImpl(this);
			SpyListenerImpl spyListener = new SpyListenerImpl(/*new SpyEventListenerImpl(this)*/spyEventListener);
			this.activeSpyListener = (SpyListenerImpl) ProActive.turnActive(spyListener,SPY_LISTENER_NODE);
			this.spy = (Spy) ProActive.newActive(Spy.class.getName(), new Object[] { activeSpyListener }, node);
		} 
		catch(NodeException e) { 
			Console.getInstance(Activator.CONSOLE_NAME).logException(e);
		}
		catch(ActiveObjectCreationException e) {
			Console.getInstance(Activator.CONSOLE_NAME).logException(e);
		}
	}

	/**
	 * Destroys the spy of node.
	 */
	protected synchronized void destroySpy(){
		this.spy.terminate();
		this.activeSpyListener.terminate();
		this.spy = null;
		this.activeSpyListener = null;
	}

	@Override
	protected void foundForTheFirstTime() {
		Console.getInstance(Activator.CONSOLE_NAME).log("NodeObject created based on node "+key);
		this.addSpy();

		try {
			// Add a RequestQueueEventListener to the spy
			this.spy.sendEventsForAllActiveObjects();
		} catch (Exception e) {
			// TODO spy not responding
			this.notResponding();
			Console.getInstance(Activator.CONSOLE_NAME).debug(this.getFullName()+" not responding");
		}

		String vnName = null;
		try {
			vnName = getTypedParent().getRuntime().getVNName(node.getNodeInformation().getName());
		} catch (ProActiveException e) {
			Console.getInstance(Activator.CONSOLE_NAME).logException(e);
			e.printStackTrace();
		}
		if(vnName != null) {
			this.vnParent = getWorld().getVirtualNode(vnName);
			if(vnParent == null) {
				ProActiveRuntime pr = getTypedParent().getRuntime();
				String jobID = null;
				try {
					jobID = pr.getJobID(pr.getURL() + "/" + getKey());
				} catch (ProActiveException e) {
					// TODO
					Console.getInstance(Activator.CONSOLE_NAME).logException(e);
				}
				vnParent = new VNObject(vnName, jobID, getWorld());
			}
			if(FilterProcess.getInstance().filter(this))
				vnParent.skippedChildren.put(key, this);
			else {
				vnParent.putChild(this);
				this.setChanged();
				this.notifyObservers();
			}
		}
	}

	@Override
	protected void alreadyMonitored() {
		Console.getInstance(Activator.CONSOLE_NAME).log("NodeObject id="+key+" already monitored, ckeck for new active objects");

		try {
			// Add a RequestQueueEventListener to the spy
			this.spy.sendEventsForAllActiveObjects();
		} catch (Exception e) {
			// TODO spy not responding
			this.notResponding();
			/*Console.getInstance(Activator.CONSOLE_NAME).err("NodeObject.alreadyMonitored() -> not responding !!!!!!!!!!!!!!!!!!!!!!!!!!!");
			e.printStackTrace();*/
		}
	}

	//
	// -- PRIVATE METHOD -----------------------------------------------
	//

	/**
	 * Get the typed parent
	 * @return the typed parent
	 */
	private VMObject getTypedParent() {
		return (VMObject) parent;
	}

	/**
	 * TODO
	 * @param activeObjects names' list of active objects containing in this NodeObject
	 */
	private void handleActiveObjects(List<List<Object>> activeObjects){
		for (int i = 0, size = activeObjects.size(); i < size; ++i) {
			List<Object> aoWrapper = activeObjects.get(i);
			UniversalBody ub = (UniversalBody)aoWrapper.get(0);
			String className = (String) aoWrapper.get(1);
			AOObject ao = new AOObject(this,className.substring(className.lastIndexOf(".")+1), ub.getID(), ub.getJobID());
			if(FilterProcess.getInstance().filter(ao)) {
				AOObject.cancelCreation();
				return;
			}
			exploreChild(ao);
		}
	}
}
