package org.objectweb.proactive.ic2d.componentmonitoring.data;

import javax.management.ObjectName;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;

public class ComponentModel extends AbstractData<ComponentModel, ComponentModel> {

	public Logger logger = Logger.getLogger("ComponentModel");
	
	/**
	 * This ComponentModel connects to the ComponentWrapperMBean to get component-related info
	 */
	//private ComponentWrapperMBean proxyMBean;
	
	
	// Component data gathered from the mbean, for displaying in the Component View
	/**
	 * Name of the Component
	 */
	private String name;
	
	/**
	 * Hierarchy. TODO Use Fractal/GCM constants for this
	 */
	private String hierarchy; 
	
	/**
	 * Status. TODO Use Fractal/GCM constants for this
	 */
	private String status;
	
	
	
	public ComponentModel(ObjectName objectName) {
		super(objectName);
		logger.setLevel(Level.DEBUG);
		logger.debug("constructor");
		
	}
	
	
	
	
	

	@Override
	public void explore() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComponentModel getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}


}
