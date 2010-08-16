package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.List;
import java.util.Map;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;

/**
 * Interface for storing monitoring records in the Log Store
 * 
 * Very basic management of log entries.
 * 
 * @author cruz
 *
 */
public interface RecordHandler {

	// init the logs store
	void init();
	
	// inserts new record in the store
	void insert(AbstractRecord record);
	
	// fetches an existing record in the store
	AbstractRecord fetch(Object key, RecordType rt);
	IncomingRequestRecord fetchIncomingRequestRecord(Object key);
	OutgoingRequestRecord fetchOutgoingRequestRecord(Object key);
	
	// queries the existence of a record in the store
	BooleanWrapper exists(Object key, RecordType rt);

	// updates an existing record
	void update(Object key, AbstractRecord record);
	
	// test: obtain logs
	Map<ComponentRequestID, IncomingRequestRecord> getRequestLog();
	Map<ComponentRequestID, OutgoingRequestRecord> getCallLog();
	
	// obtain subset of entries
	Map<ComponentRequestID, OutgoingRequestRecord> getCallRecordsFromParent(ComponentRequestID id);
	Map<ComponentRequestID, IncomingRequestRecord> getRequestRecordsFromRoot(ComponentRequestID rootID);
	
	// clean the logs
	void reset();
	
	
	public List<ComponentRequestID> getListOfRequestIDs();
	public List<ComponentRequestID> getListOfCallIDs();
	
	// select an specific set records
	public List<IncomingRequestRecord> getIncomingRequestRecords(Condition<IncomingRequestRecord> condition);
	public List<OutgoingRequestRecord> geOutogoingRequestRecords(Condition<OutgoingRequestRecord> condition);
	
	public List<?> getIncomingRequestRecords(Condition<IncomingRequestRecord> condition, Transformation<IncomingRequestRecord,?> transformation);
	public List<?> geOutogoingRequestRecords(Condition<OutgoingRequestRecord> condition, Transformation<OutgoingRequestRecord,?> transformation);
	
}
