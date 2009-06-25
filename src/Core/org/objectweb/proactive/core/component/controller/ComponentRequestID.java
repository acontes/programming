package org.objectweb.proactive.core.component.controller;

/** 
 * Unique identifier for a request.
 * It's based on the sequenceNumber of the request, which is based
 * on bodyID hashcode + a sequenceID, which is included in the message.
 *  
 * @author cruz
 *
 */
public class ComponentRequestID {

	private long reqID;
	
	public ComponentRequestID(long reqID) {
		this.reqID = reqID;
	}
	
	public long getComponentRequestID() {
		return reqID;
	}
}
