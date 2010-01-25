package org.objectweb.proactive.core.component.controller;

import org.objectweb.proactive.core.UniqueID;

/** 
 * Unique identifier for a request.
 * It's based on the sequenceNumber of the request, which is based
 * on bodyID hashcode + a sequenceID, which is included in the message.
 *  
 * @author cruz
 *
 */
public class ComponentRequestID {

	private Long reqID;
	
	public ComponentRequestID(long reqID) {
		this.reqID = reqID;
	}
	
	public long getComponentRequestID() {
		return reqID.longValue();
	}
	
	public String toString() {
		return ""+reqID.longValue();
	}
	
	/**
     * Overrides equals ...
     * @return true if and only if o is a ComponentRequestID with the same value that this ComponentRequestID
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ComponentRequestID) {
            return this.reqID.longValue() == ((ComponentRequestID) o).getComponentRequestID();
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return reqID.hashCode();
    }
    

}
