package org.objectweb.proactive.core.component.componentcontroller.sla;

public interface SLOStore {

	void init();
	
	void addSLO(String name, SLORule<?> rule);
	
	void removeSLO(String name);
	
	void enableSLO(String name);
	
	void disableSLO(String name);
	
	
	
}
