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
import java.util.List;



/**
 * Holder class for all hosts
 */
public class WorldObject extends AbstractDataObject {

	
	private static WorldObject instance;
	
	//
    // -- CONSTRUCTORS -----------------------------------------------
    //
    
	/**
	 * Create a new WorldObject
	 */
	private WorldObject() {
        super(null);
    }
	
	
    //
    // -- PUBLIC METHODS ---------------------------------------------
    //
	
	public static WorldObject getInstance() {
		if(instance == null)
			instance = new WorldObject();
		return instance;
	}
	

	public String getKey() {
		// A WorldObject doesn't need a key because it is the only son of IC2DObject.
		return "WorldObject";
	}
	
	public String getFullName(){
		return "WorldObject";
	}

	public void explore() {
		List<AbstractDataObject> childrenList = new ArrayList<AbstractDataObject>(monitoredChildren.values());
		for(int i=0, size=childrenList.size(); i<size; i++)
			((HostObject)childrenList.get(i)).explore();
	}
	
	public String getType() {
		return "world";
	}
	
    //
    // -- PROTECTED METHODS -----------------------------------------------
    //
	
	/**
	 * Add a child to this object
	 * @param key 
	 * @param child
	 */
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
}