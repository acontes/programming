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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	/** the object's children (HashMap<String, AbstractDataObject>) */
	private HashMap children;
	
	/** true if this object has been destroyed, false otherwise */
	protected boolean isDestroyed;
	/** true if this object is alive, false otherwise */
	private boolean isAlive;
	
	
	
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
		this.children = new HashMap();
		
		this.isDestroyed = false;
		this.isAlive = true;
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
		children.toString();
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
	
	
	/**
	 * Returns an iterator over the object's children
	 * @return an iterator over the object's children
	 */
	public Iterator childrenIterator() {
		return children.values().iterator();
	}
	
	
	public List getChildren(){
		return new ArrayList(this.children.values());
	}
	
	/**
	 * Destroys this object
	 */
	public abstract void destroyObject();
	
	
	public void addPropertyChangeListener(PropertyChangeListener listener){
		/*listeners.addPropertyChangeListener(listener);*/
	}
	
	
	public void removePropertyChangeListener(PropertyChangeListener listener){
		/*listeners.removePropertyChangeListener(listener);*/
	}
	
	//
	// -- PROTECTED METHODS -----------------------------------------------
	//
	
	/**
	 * Add a child to this object
	 * @param key 
	 * @param child
	 */
	protected synchronized void putChild(String key, AbstractDataObject child) {
		if (isDestroyed) {
			return;
		}
		children.put(key, child);
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Remove a child
	 * @param key key of the child to remove
	 */
	protected synchronized AbstractDataObject removeChild(String key) {
		AbstractDataObject o;
		if (isDestroyed) {
			// we are in the Iterator to destroy all children object :
			// we don't want to remove from the collection not to
			// have an exception from the iterator
			o = (AbstractDataObject) children.get(key);
		} else {
			// we are asked to remove the child from elsewhere
			o = (AbstractDataObject) children.remove(key);
		}
		if (o == null) {
			return null;
		}
		
		o.destroy();
		return o;
	}
	
	
	/**
	 * Destroys this object.
	 */
	protected synchronized boolean destroy() {
		if (isDestroyed) {
			return false;
		}
		
		isDestroyed = true;
		destroyCollection(childrenIterator());
		children.clear();
		parent = null;
		return true;
	}
	
	
	/**
	 * TODO comment
	 * @param key
	 */
	protected synchronized AbstractDataObject getChild(String key) {
		return (AbstractDataObject) children.get(key);
	}
	
	
	/**
	 * Destroys all objects known by this object
	 * @param iterator an iterator over all objects to destroy
	 */
	protected synchronized void destroyCollection(Iterator iterator) {
		while (iterator.hasNext()) {
			AbstractDataObject o = (AbstractDataObject) iterator.next();
			o.destroyObject();
		}
	}
	
}
