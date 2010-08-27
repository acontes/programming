package org.objectweb.proactive.core.component.componentcontroller.sla;

public interface SLAService {

	void addSLO(String name, Object rule);
	
	void removeSLO(String name);
	
	void enableSLO(String name);
	
	void disableSLO(String name);
	
	
}
