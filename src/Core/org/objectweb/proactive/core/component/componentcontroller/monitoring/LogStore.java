package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.component.componentcontroller.AbstractProActiveComponentController;
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
public class LogStore extends AbstractProActiveComponentController implements LogHandler {

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
		if(rt == RecordType.CallRecord) {
			return callLog.get(key);
		}
		return null;
	}
	
	// FIXME Should use the abstract class, but that gives a ClassCastException when passing it as a parameter
	public RequestRecord fetchRequestRecord(Object key) {
		return requestLog.get(key);
	}
	public CallRecord fetchCallRecord(Object key) {
		return callLog.get(key);
	}

	@Override
	public void insert(AbstractRecord record) {
		if(record.recordType == RecordType.RequestRecord) {
			requestLog.put(record.requestID, (RequestRecord) record);
		}
		else if(record.recordType == RecordType.CallRecord) {
			callLog.put(record.requestID, (CallRecord) record);
		}
	}

	// the same from above... because HashMap.put() replaces old value!!
	@Override
	public void update(Object key, AbstractRecord record) {
		if(record.recordType == RecordType.RequestRecord) {
			requestLog.put(record.requestID, (RequestRecord) record);
		}
		else if(record.recordType == RecordType.CallRecord) {
			callLog.put(record.requestID, (CallRecord) record);
		}
	}    
    
}
