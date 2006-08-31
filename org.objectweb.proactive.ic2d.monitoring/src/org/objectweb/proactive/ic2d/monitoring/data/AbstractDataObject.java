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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.ic2d.console.Console;
import org.objectweb.proactive.ic2d.monitoring.Activator;
import org.objectweb.proactive.ic2d.monitoring.filters.FilterProcess;

/**
 * Holder class for the host data representation
 */
public abstract class AbstractDataObject extends Observable {

	/** the object's parent */
	protected AbstractDataObject parent;
	/** the object's children which are monitored (Map<String, AbstractDataObject>) */
	protected Map<String, AbstractDataObject> monitoredChildren;
	/** the object's children which are NOT monitored (HashMap<String, AbstractDataObject>) */
	protected Map<String, AbstractDataObject> skippedChildren;

	protected boolean isAlive;


	//
	// -- CONSTRUCTORS -----------------------------------------------
	//

	/**
	 * Creates a new AbstractDataObject
	 * @param parent the object's parent
	 */
	protected AbstractDataObject(AbstractDataObject parent) {
		this.isAlive = true;
		this.parent = parent;
		this.monitoredChildren = new HashMap<String, AbstractDataObject>();
		this.skippedChildren = new HashMap<String, AbstractDataObject>();
	}


	//
	// -- PUBLICS METHODS -----------------------------------------------
	//

	public AbstractDataObject getChild(String key){
		return monitoredChildren.get(key);
	}

	/**
	 * Returns the object's key. It is an unique identifier.
	 * @return the object's key
	 */
	public abstract String getKey();

	/**
	 * Returns the object's full name
	 * @return the object's full name
	 */
	public abstract String getFullName();

	/**
	 * Returns a string representing the object's name and children's names
	 */
	public String toString() {
		return this.getFullName();
		/*
		return "DataObject " + abstractDataObjectName + "\n" +
		monitoredChildren.toString();
		 */
	}

	/**
	 * Returns the object's parent
	 * @return the object's parent
	 */
	public AbstractDataObject getParent() {
		return parent;
	}


	/**
	 * Stop monitoring this object
	 * @param log Indicates if you want to log a message in the console.
	 * @param log Indicates if you want to log a message in the console.
	 */
	public void stopMonitoring(boolean log) {
		if(log)
			Console.getInstance(Activator.CONSOLE_NAME).log("Stop monitoring the " + getType() + " " + getFullName());
		this.parent.removeChild(this);
		setChanged();
		notifyObservers(State.NOT_MONITORED);
		AbstractDataObject[] children = monitoredChildren.values().toArray(new AbstractDataObject[]{});
		for(int i=0, size=children.length ; i<size ; i++)
			children[i].stopMonitoring(false);
	}

	/**
	 * Returns the list of monitored children
	 * @return The list of monitored children
	 */
	public List<AbstractDataObject> getMonitoredChildren() {
		return new ArrayList<AbstractDataObject>(monitoredChildren.values());
	}

	/**
	 * Explore the current object
	 */
	public abstract void explore();

	/**
	 * To know if this object is monitored
	 * @return true if it is monitored, false otherwise
	 */
	public boolean isMonitored(){
		if(parent == null)
			return true;
		else if(parent.monitoredChildren.get(getKey()) != null)
			return parent.isMonitored();
		else
			return false;
	}

	/**
	 * Returns the type of the object (ex : "ao" for AOObject).
	 * @return the type of the object.
	 */
	public abstract String getType();

	/**
	 * Find an active object.
	 * @param id The UniqueID of the active object
	 * @return The active object, or null.
	 */
	public synchronized AOObject findActiveObjectById(UniqueID id) {
		// We search in the monitored objects.
		Iterator<AbstractDataObject> iterator = monitoredChildren.values().iterator();
		while (iterator.hasNext()) {
			AbstractDataObject object = iterator.next();
			AOObject activeObject = object.findActiveObjectById(id);
			if (activeObject != null) {
				return activeObject;
			}
		}
		return null;
	}


	public void notResponding() {
		if(isAlive) {
			this.isAlive = false;
			List<AbstractDataObject> children = getMonitoredChildren();
			for(AbstractDataObject child : children){
				child.notResponding();
			}
			setChanged();
			notifyObservers(State.NOT_RESPONDING);
		}
	}


	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AbstractDataObject))
			return false;
		else {
			AbstractDataObject o = (AbstractDataObject)obj;
			return this.getKey().compareTo(o.getKey()) == 0;
		}
	}

	//
	// -- PROTECTED METHODS -----------------------------------------------
	//

	/**
	 * Add a child to this object.
	 * Warning : You musn't call this method, call filterAndPutChild.
	 * @param child the object to add
	 */
	protected synchronized void putChild(AbstractDataObject child) {
		monitoredChildren.put(child.getKey(), child);
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Remove a child to this object.
	 * @param child the object to remove
	 */
	protected synchronized void removeChild(AbstractDataObject child) {
		monitoredChildren.remove(child.getKey());
		this.parent.skippedChildren.put(getKey(), this);
		setChanged();
		notifyObservers();
	}

	/**
	 * Explore the child
	 * @param child The child to explore
	 */
	protected void exploreChild(AbstractDataObject child) {
		if(!child.isAlive)
			return;
		if(skippedChildren.containsKey(child.getKey()))
			return;


		if(monitoredChildren.containsKey(child.getKey())) {

			child = monitoredChildren.get(child.getKey());
			child.alreadyMonitored();
			child.explore();
		}
		else { // !skippedChildren.containsKey(child.getKey()) && !monitoredChildren.containsKey(child.getKey())
			if(FilterProcess.getInstance().filter(child)) {
				skippedChildren.put(child.getKey(), child);
			}
			else { //child is new and must be monitored
				// System.out.println("AbstractDataObject.exploreChild() ----------");
				putChild(child);
				child.foundForTheFirstTime();
				child.explore();
			}
		}
	}

	protected abstract void foundForTheFirstTime();

	protected abstract void alreadyMonitored();
}
