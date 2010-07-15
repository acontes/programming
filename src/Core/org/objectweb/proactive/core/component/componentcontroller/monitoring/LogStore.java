package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.component.componentcontroller.AbstractPAComponentController;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;

/**
 * Log Storage component for the Monitoring Framework.
 * Contains a collections of LogRecord objects.
 * 
 * This implementation is oriented to Performance Records ... 
 * ... it includes the CallLog and the RequestLog as originally planned.
 * 
 * Another implementation could introduce another kind of Log.
 * An abstract log or record class would be useful, too.
 * 
 * @author cruz
 *
 */
public class LogStore extends AbstractPAComponentController implements LogHandler {

	private static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_MONITORING);

	/** Log for incoming requests */
    private Map<ComponentRequestID, RequestRecord> requestLog;
    
    /** Log for outgoing request */
    private Map<ComponentRequestID, CallRecord> callLog;

	
	public LogStore() {
		super();
	}
	
	public void init() {
		logger.debug("[Log Store] Initializing logs ...");
		// should some of these two HashMap's be synchronized?		
		requestLog = new HashMap<ComponentRequestID, RequestRecord>();
    	callLog = new HashMap<ComponentRequestID, CallRecord>();
	}

	@Override
	public BooleanWrapper exists(Object key, RecordType rt) {
		if(rt == RecordType.RequestRecord) {
			if(requestLog.containsKey(key)) {
				return new BooleanWrapper(true);
			}
		}
		if(rt == RecordType.CallRecord) {
			if(callLog.containsKey(key)) {
				return new BooleanWrapper(true);
			}
		}
		return new BooleanWrapper(false);
	}

	@Override
	public AbstractRecord fetch(Object key, RecordType rt) {
		if(rt == RecordType.RequestRecord) {
			return requestLog.get(key);	
		}
		else if(rt == RecordType.CallRecord) {
			return callLog.get(key);
		}
		else {
			logger.debug("ERROR. Fetch: Unrecognized RecordType");
		}
		return null;
	}
	
	// FIXME Should use the abstract class, but that gives a ClassCastException when passing it as a parameter, won't fix it now...
	public RequestRecord fetchRequestRecord(Object key) {
		return requestLog.get(key);
	}
	
	public CallRecord fetchCallRecord(Object key) {
		return callLog.get(key);
	}

	@Override
	public void insert(AbstractRecord record) {
		if(record.getRecordType() == RecordType.RequestRecord) {
			//logger.debug("INSERTING IN REQ LOG: ID: "+ record.getRequestID() + " -- " + ((RequestRecord)record).getCalledComponent() + "." + ((RequestRecord)record).getInterfaceName() + "." + ((RequestRecord)record).getMethodName() + " -- " + ((RequestRecord)record).getArrivalTime() + ", "+ ((RequestRecord)record).getServingStartTime() + ", "+ ((RequestRecord)record).getReplyTime());
			requestLog.put(record.getRequestID(), (RequestRecord) record);
		}
		else if(record.getRecordType() == RecordType.CallRecord) {
			//logger.debug("INSERTING IN CALL LOG: ID: "+ record.getRequestID() + " -- " + ((CallRecord)record).getCalledComponent() + "." + ((CallRecord)record).getInterfaceName() + "." + ((CallRecord)record).getMethodName() + " -- " + ((CallRecord)record).getSentTime() + ", "+ ((CallRecord)record).getReplyReceptionTime() );
			callLog.put(record.getRequestID(), (CallRecord) record);
		}
		else {
			logger.debug("ERROR. Insert: Unrecognized RecordType, ID:"+ record.getRequestID() + ", ");
		}
	}
	
	// the same from above... because HashMap.put() replaces old value!!
	@Override
	public void update(Object key, AbstractRecord record) {
		if(record.recordType == RecordType.RequestRecord) {
			requestLog.put(record.getRequestID(), (RequestRecord) record);
		}
		else if(record.recordType == RecordType.CallRecord) {
			callLog.put(record.getRequestID(), (CallRecord) record);
		}
		else {
			logger.debug("ERROR. Update: Unrecognized RecordType");
		}
	} 

	@Override
	public Map<ComponentRequestID, CallRecord> getCallLog() {
		
		Map<ComponentRequestID, CallRecord> callRecords = new HashMap<ComponentRequestID, CallRecord>(callLog.size());
		// copy all entries of the log
		callRecords.putAll(callLog);
		return callRecords;
	}

	@Override
	public Map<ComponentRequestID, RequestRecord> getRequestLog() {
		
		Map<ComponentRequestID, RequestRecord> requestRecords = new HashMap<ComponentRequestID, RequestRecord>(callLog.size());
		// copy all entries of the log
		requestRecords.putAll(requestLog);
		return requestRecords;
	}

	/**
	 * Returns a subset of all the entries in the Call Log with an specific parent ID
	 */
	@Override
	public Map<ComponentRequestID, CallRecord> getCallRecordsFromParent(
			ComponentRequestID id) {

		Map<ComponentRequestID, CallRecord> selectedRecords = new HashMap<ComponentRequestID, CallRecord>();
		CallRecord cr;
		
		// TODO Perform the query in a more efficient way
		for(ComponentRequestID crid: callLog.keySet()) {
			cr = callLog.get(crid);
			// put all the records that have 'id' as parent
			if(cr.getParentID().equals(id)) {
				selectedRecords.put(crid, cr);
			}
		}
		return selectedRecords;
	}
	
	/**
	 * Returns a subset of all the entries in the Request Log with the same root ID
	 */
	@Override
	public Map<ComponentRequestID, RequestRecord> getRequestRecordsFromRoot(
			ComponentRequestID rootID) {

		Map<ComponentRequestID, RequestRecord> selectedRecords = new HashMap<ComponentRequestID, RequestRecord>();
		RequestRecord rr;
		
		// TODO Perform the query in a more efficient way
		for(ComponentRequestID crid: requestLog.keySet()) {
			rr = requestLog.get(crid);
			// put all the records that have 'rootID' as root
			if(rr.getRootID().equals(rootID)) {
				selectedRecords.put(crid, rr);
			}
		}
		return selectedRecords;
	}

	@Override
	public void reset() {
		requestLog.clear();
		callLog.clear();
	}
	
	
	public List<ComponentRequestID> getListOfRequestIDs() {
		Set<ComponentRequestID> keyset = requestLog.keySet();
		List<ComponentRequestID> keylist = new ArrayList<ComponentRequestID>(keyset.size());
		keylist.addAll(keyset);
		//Collections.sort(keylist);
		return keylist;
	}
    
	public List<ComponentRequestID> getListOfCallIDs() {
		Set<ComponentRequestID> keyset = callLog.keySet();
		List<ComponentRequestID> keylist = new ArrayList<ComponentRequestID>(keyset.size());
		keylist.addAll(keyset);
		//Collections.sort(keylist);
		return keylist;
	}


	
}
