package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	String componentName;  // the component called (this component)
	String interfaceName;  // the interface called (a server interface of this component)
	String methodName;     // the method called

	ComponentRequestID current;  // id of the received request
	
	long sendTime;      // when the caller sent the request to this component
	long replyRecvTime; // when the caller received the response from this component
	long recvTime;      // when this component received the request
	long replySentTime; // when this component sent the reply
	
	List<ComponentRequestID> childrenID;            // the ID of all the children of this request
	Map<ComponentRequestID, PathItem> children; // the list of children of this request
	
	public PathItem(ComponentRequestID current, String c, String i, String m) {
		this.current = current;
		this.componentName = c;
		this.interfaceName = i;
		this.methodName = m;
		this.childrenID = new ArrayList<ComponentRequestID>();
		this.children = new HashMap<ComponentRequestID, PathItem>();
	}

	public String toString() {
		return "("+current+") "+componentName+"."+interfaceName+"."+methodName+": \t"+"client: "+ (replyRecvTime-sendTime) + "\t"+"server: "+ (replySentTime-recvTime);
	}
	
	public ComponentRequestID getID() {
		return current;
	}
	
	
	public void setSendTime(long t) {
		sendTime = t;
	}
	
	public void setReplyRecvTime(long t) {
		replyRecvTime = t;
	}

	public void setRecvTime(long t) {
		recvTime = t;
	}
	
	public void setReplySentTime(long t) {
		replySentTime = t;
	}
	
	public long getSendTime() {
		return sendTime;
	}
	public long getRecvTime() {
		return recvTime;
	}
	
	public long getReplySentTime() {
		return replySentTime;
	}
	
	public void addChildID(ComponentRequestID childID) {
		childrenID.add(childID);
	}
	
	public void addChild(ComponentRequestID childID, PathItem child) {
		children.put(childID, child);
	}
	
	public List<ComponentRequestID> getChildrenID() {
		return childrenID;
	}
	
	public Map<ComponentRequestID, PathItem> getChildren() {
		return children;
	}
	
	public void setChildren(Map<ComponentRequestID, PathItem> map) {
		children = map;
	}
	
	public void setChildrenID(List<ComponentRequestID> list) {
		childrenID = list;
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
		return (int)(this.sendTime - o.getSendTime());
		//return (int)(this.recvTime - o.getRecvTime());
	}
	
}
