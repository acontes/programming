package org.objectweb.proactive.core.jmx.mbean;

import javax.management.ObjectName;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.component.identity.ProActiveComponent;

public interface ComponentWrapperMBean extends BodyWrapperMBean {

	public String getComponentName();

	public boolean isComponent();

	public ProActiveComponent[] getSubComponents();
	
	public UniqueID getID();
	
	public ObjectName getObjectName();
	
	public UniqueID getParentUID();
	
	
}
