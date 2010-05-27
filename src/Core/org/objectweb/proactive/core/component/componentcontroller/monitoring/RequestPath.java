package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represent the path that a request follows through a series of components
 * 
 * @author cruz
 *
 */
public class RequestPath implements Serializable {

	/** The component request that this path is built for */
	ComponentRequestID requestID;
	
	/** The list of components/interfaces/methods traversed by this request */
	ArrayList<PathItem> path;
	
	public RequestPath() {
		path = new ArrayList<PathItem>();
	}
	
	public void init() {
		path = new ArrayList<PathItem>();
	}
	
	public void add(PathItem newItem) {
		path.add(newItem);
	}
	
	public ArrayList<PathItem> getPath() {
		return path;
	}
	
	public void add(RequestPath rp) {
		path.addAll(rp.getPath());
	}
	
	
}
