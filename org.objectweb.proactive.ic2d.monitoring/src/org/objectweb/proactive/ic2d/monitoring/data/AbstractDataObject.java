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
	protected HashMap<String, AbstractDataObject> monitoredChildren;
	/** the object's children which are NOT monitored (HashMap<String, AbstractDataObject>) */
	protected HashMap<String, AbstractDataObject> skippedChildren;
	
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	/**
	 * Creates a new AbstractDataObject
	 * @param parent the object's parent
	 */
	protected AbstractDataObject(AbstractDataObject parent) {
		this(parent, null);
	}
	
	
	/**
	 * Creates a new AbstractDataObject
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
		this.monitoredChildren = new HashMap<String, AbstractDataObject>();
		this.skippedChildren = new HashMap<String, AbstractDataObject>();
	}
	
	//
	// -- PUBLICS METHODS -----------------------------------------------
	//
	
	public AbstractDataObject getChild(String key){
		return (AbstractDataObject) monitoredChildren.get(key);
	}
	
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
	 * Stop monitor this object
	 */
	public void stopMonitoring() {
		this.parent.monitoredChildren.remove(getKey());
		this.parent.skippedChildren.put(getKey(), this);
		setChanged();
		notifyObservers();
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
	 * Returns the type of the object.
	 * @return
	 */
	public abstract String getType();
	
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
	
	/**
	 * Explore the child
	 * @param child The child to explore
	 */
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
