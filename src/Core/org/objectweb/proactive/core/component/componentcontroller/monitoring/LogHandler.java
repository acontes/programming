package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;

/**
 * Interface for storing monitoring records in the Log Store
 * 
 * Very basic management of log entries.
 * 
 * @author cruz
 *
 */
public interface LogHandler {

	// init the logs store
	void init();
	
	// inserts new record in the store
	void insert(AbstractRecord record);
	
	// fetches an existing record in the store
	AbstractRecord fetch(Object key, RecordType rt);
	RequestRecord fetchRequestRecord(Object key);
	CallRecord fetchCallRecord(Object key);
	
	// queries the existence of a record in the store
	BooleanWrapper exists(Object key, RecordType rt);

	// updates an existing record
	void update(Object key, AbstractRecord record);
	
	// display Logs (only for testing)
	void displayLogs();
	
}
