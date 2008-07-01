package org.objectweb.proactive.core.jmx.mbean;

import javax.management.ObjectName;

import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.core.UniqueID;

public interface ComponentWrapperMBean extends BodyWrapperMBean {

	public String getComponentName();

	public boolean isComponent();

	public Component[] getSubComponents();
	
	public UniqueID getID();
	
	public ObjectName getObjectName();
	
	
}
