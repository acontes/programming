package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;

/** 
 * Abstract class for Monitoring Records.
 * The Log Store should handle these records.
 * 
 * @author cruz
 *
 */
public abstract class AbstractRecord implements Serializable {

	/** Type of the record */
	protected RecordType recordType;
	
	/** ID of the new request */
	protected ComponentRequestID requestID;
	
	public AbstractRecord() {
		
	}
	
	public AbstractRecord(RecordType rt, ComponentRequestID requestID) {
		this.recordType = rt;
		this.requestID = requestID;
	}
	
	public RecordType getRecordType() {
		return recordType;
	}
	
	public ComponentRequestID getRequestID() {
		return requestID;
	}
	
}
