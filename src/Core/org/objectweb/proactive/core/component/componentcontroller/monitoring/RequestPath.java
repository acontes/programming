package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.ArrayList;

/**
 * This class represent the path that a request follows through a series of components
 * 
 * @author cruz
 *
 */
public class RequestPath {

	/** The component request that this path is built for */
	ComponentRequestID requestID;
	
	/** The list of components/interfaces/methods traversed by this request */
	ArrayList<PathItem> path;
	
	public RequestPath() {
		
	}
	
	public void init() {
		path = new ArrayList<PathItem>();
	}
	
	public void add(PathItem newItem) {
		
	}
	
}
