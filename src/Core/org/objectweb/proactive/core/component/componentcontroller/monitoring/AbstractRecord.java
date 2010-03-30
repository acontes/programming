package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;

/** 
 * Abstract class for Monitoring Records.
 * The Log Store should handle this records.
 * 
 * @author cruz
 *
 */
public abstract class AbstractRecord implements Serializable {

	/** Type of the record, to know in which log to store it */
	protected RecordType recordType;
	
	/** ID of the received request */
	protected ComponentRequestID requestID;
	
	public AbstractRecord() {
		
	}
	public AbstractRecord(RecordType rt, ComponentRequestID requestID) {
		this.recordType = rt;
		this.requestID = requestID;
	}
	
}
