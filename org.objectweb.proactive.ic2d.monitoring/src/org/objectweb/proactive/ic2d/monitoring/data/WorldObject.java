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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Holder class for all monitored hosts and virtual nodes
 */
public class WorldObject extends AbstractDataObject {

	
	private static WorldObject instance;
	
	/**
	 * Contains all virtual nodes.
	 */
	private Map<String, VNObject> vnChildren;
	
	//
    // -- CONSTRUCTORS -----------------------------------------------
    //
    
	/**
	 * Create a new WorldObject
	 */
	private WorldObject() {
        super(null);
        vnChildren = new HashMap<String, VNObject>();
    }
	
	
    //
    // -- PUBLIC METHODS ---------------------------------------------
    //
	
	public static WorldObject getInstance() {
		if(instance == null)
			instance = new WorldObject();
		return instance;
	}
	
	@Override
	public String getKey() {
		// A WorldObject doesn't need a key because it is the only son of IC2DObject.
		return "WorldObject";
	}
	
	@Override
	public String getFullName(){
		return "WorldObject";
	}

	@Override
	public void explore() {
		List<AbstractDataObject> childrenList = new ArrayList<AbstractDataObject>(monitoredChildren.values());
		for(int i=0, size=childrenList.size(); i<size; i++)
			((HostObject)childrenList.get(i)).explore();
	}
	
	@Override
	public String getType() {
		return "world";
	}

	
	public List<AbstractDataObject> getVNChildren() {
		return new ArrayList<AbstractDataObject>(vnChildren.values());
	}
	
    //
    // -- PROTECTED METHODS -----------------------------------------------
    //
	
	/**
	 * Add a child to this object
	 * @param key 
	 * @param child
	 */
	@Override
	protected synchronized void putChild(AbstractDataObject child) {
		monitoredChildren.put(child.getKey(), child);
		setChanged();
		if(monitoredChildren.size() == 1)
			notifyObservers("putChild");
		notifyObservers();
	}
    
	
	/**
	 * Stop monitoring the host specified.
	 * @param host the host to stop monitoring
	 */
	protected void removeChild(HostObject host) {
		monitoredChildren.remove(host.getKey());
		setChanged();
		if(monitoredChildren.size() == 0)
			notifyObservers("removeChild");
		notifyObservers();
	}
	
	protected VNObject getVirtualNode(String name) {
		VNObject virtualNode = vnChildren.get(name);
		if(virtualNode == null){
			virtualNode = new VNObject(name);
			vnChildren.put(name, virtualNode);
			setChanged();
			notifyObservers(virtualNode);
		}
		return virtualNode;
	}
	
	@Override
	protected void alreadyMonitored() {/* Do nothing */}


	@Override
	protected void foundForTheFirstTime() {/* Do nothing */}
}