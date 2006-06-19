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
import java.util.Observable;

/**
 * Holder class for the host data representation
 */
public abstract class AbstractDataObject extends Observable {

	
	/** the object's name */
	protected String abstractDataObjectName;
	
	/** the object's parent */
	protected AbstractDataObject parent;
	/** the object's children which are monitored (HashMap<String, AbstractDataObject>) */
	protected HashMap monitoredChildren;
	/** the object's children which are NOT monitored (HashMap<String, AbstractDataObject>) */
	protected HashMap skippedChildren;
	
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	/**
	 * TODO comment
	 * @param parent the object's parent
	 */
	protected AbstractDataObject(AbstractDataObject parent) {
		this(parent, null);
	}
	
	
	/**
	 * TODO comment
	 * @param parent the object's parent
	 * @param abstractDataObjectName the object's name
	 */
	protected AbstractDataObject(AbstractDataObject parent,
			String abstractDataObjectName) {
		
		//listeners = new PropertyChangeSupport(this);
		
		if (abstractDataObjectName == null) {
			this.abstractDataObjectName = this.getClass().getName();
		} else {
			this.abstractDataObjectName = abstractDataObjectName;
		}
		
		this.parent = parent;
		this.monitoredChildren = new HashMap();
		this.skippedChildren = new HashMap();
	}
	
	//
	// -- PUBLICS METHODS -----------------------------------------------
	//
	
	/**
	 * Returns the object's key. It is an unique identifier
	 * @return the object's key
	 */
	public abstract String getKey();
	
	/**
	 * Returns the object's full name
	 */
	public abstract String getFullName();
	
	/**
	 * Returns a string representing the object's name and children's names
	 */
	public String toString() {
		return "DataObject " + abstractDataObjectName + "\n" +
		monitoredChildren.toString();
	}
	
	/**
	 * Returns the object's parent
	 * @return the object's parent
	 */
	public AbstractDataObject getParent() {
		return parent;
	}
	
	
	/**
	 * Returns the top level parent
	 * @return the top level parent
	 */
	public AbstractDataObject getTopLevelParent() {
		if (parent == null) {
			return this;
		} else {
			return parent.getTopLevelParent();
		}
	}

	
	public void stopMonitoring() {
		this.parent.monitoredChildren.remove(getKey());
		this.parent.skippedChildren.put(getKey(), this);
		setChanged();
		notifyObservers();
	}
	
	public List getMonitoredChildren() {
		return new ArrayList(monitoredChildren.values());
	}
	
	public abstract void explore();
	
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
		notifyObservers();
	}
	
	
	protected void exploreChild(AbstractDataObject child) {
		if(skippedChildren.containsKey(child.getKey())){
			System.out.println("AbstractDataObject : exploreChild");
			return;
		}
		else if (!monitoredChildren.containsKey(child.getKey()))
			this.putChild(child);
		else //parent.monitoredChildren.containsKey(vm.getKey())
			child = (AbstractDataObject)monitoredChildren.get(child.getKey());
		child.explore();
	}
}
