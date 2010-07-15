package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
	
	public int getSize() {
		return path.size();
	}
	
	public RequestPath sort() {
		RequestPath result = new RequestPath();
		RequestPath temp = new RequestPath();
		int size = path.size();
		// the RequestPath has been constructed in a way that the last element inserted is the first of the path
		PathItem first = path.get(size-1);
		result.add(first);
		ComponentRequestID current = path.get(size-1).getCurrentID();
		
		while(result.getSize() < size) {
			// get all the requests received with the current ID (should be only one)
			for(PathItem pi: path) {
				if(pi.getCurrentID() == pi.getParentID() && pi.getCurrentID() == current) {
					result.add(pi);
				}
			}
			int n=0;
			// put all the child requests of this one
			for(PathItem pi:path) {
				if(pi.getCurrentID() != pi.getParentID() && pi.getCurrentID() == current) {
					temp.add(pi);
					n++;
				}
			}
			// sort the last n calls by startTime (they're done by the same component, so it should make sense)
			Collections.sort(temp.path);
			// and add them to the list
			result.getPath().addAll(temp.getPath());
		}
		
		
		return null;
	}
}
