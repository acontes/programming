package org.objectweb.proactive.ic2d.componentmonitoring.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;

public class ComponentModelHolder extends AbstractData<AbstractData<?, ?>, AbstractData<?, ?>> {

	private static String ObjectNameString = "org.objectweb.proactive.ic2d.componentmonitoring:type=ComponentHolder";
	private static String TypeString = "ComponentModel-Holder";
	/** 
	 * The list of all components included in this holder
	 */
	public Map<UniqueID, ComponentModel> components;

	/**
	 * Model name
	 */
	private String name = "";
	
	public ComponentModelHolder(String name) throws MalformedObjectNameException, NullPointerException {
		super(new ObjectName(ObjectNameString));
		this.name = name;
		this.components = new ConcurrentHashMap<UniqueID, ComponentModel>();
	}
	
	@Override
	public void explore() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getKey() {
		return ObjectNameString + ":" + name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public AbstractData<?, ?> getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		return TypeString;
	}
}
