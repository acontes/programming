package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;

/**
 * This class represents an element in the path of a request.
 * It should, at least, include:
 * 		- Component Name
 * 		- Interface Name
 * 		- Method Name
 * Optionally, it can include statistics for each component 
 * 
 * @author cruz
 *
 */
public class PathItem implements Serializable, Comparable<PathItem> {

	String type;
	int level;
	ComponentRequestID parent;
	ComponentRequestID current;
	
	long startTime;
	long endTime;
	long serviceTime;
	
	String callerComponentName;
	String calledComponentName;
	String interfaceName;
	String methodName;
	
	public PathItem(ComponentRequestID parent, ComponentRequestID current, long startTime, String c1, String c2, String i, String m) {
		this.level = 0;
		this.parent = parent;
		this.current = current;
		this.startTime = startTime;
		this.callerComponentName = c1;
		this.calledComponentName = c2;
		this.interfaceName = i;
		this.methodName = m;
	}
	
	public PathItem(ComponentRequestID parent, ComponentRequestID current, long startTime, long endTime, long serviceTime, 
			String c1, String c2, String i, String m) {
		this.level = 0;
		this.parent = parent;
		this.current = current;
		this.startTime = startTime;
		this.endTime = endTime;
		this.serviceTime = serviceTime;
		this.callerComponentName = c1;
		this.calledComponentName = c2;
		this.interfaceName = i;
		this.methodName = m;
	}


	public String toString() {
		return startTime + ": "+ parent +"-->"+ current + ".\t"+ callerComponentName +" --> "+ calledComponentName + "." + interfaceName + "." + methodName + " ("+serviceTime+")";

	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public ComponentRequestID getParentID() {
		return parent;
	}
	
	public ComponentRequestID getCurrentID() {
		return current;
	}
	
	public void setLevel(int l) {
		this.level = l;
	}
	
	public int getLevel() {
		return level;
	}

	/**
	 * Partial order.
	 * It compares two pathItem supposed to come from the same Component, and determines which one "happened before".
	 * If pathItem from different components are compared the result is not guaranteed.
	 * @param o
	 * @return
	 */
	@Override
	public int compareTo(PathItem o) {

		return (int)(this.startTime - o.getStartTime());
	}
	
}
