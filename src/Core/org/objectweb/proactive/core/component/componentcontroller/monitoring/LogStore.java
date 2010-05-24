package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	/*
	// only for testing
	public void displayLogs() {
		String hostComponentName = null;
		if(hostComponent != null) {
			hostComponentName = hostComponent.getComponentParameters().getControllerDescription().getName();
		}
		System.out.println("===================== Component ["+ hostComponentName +"] ===============");
		System.out.println("===================== Call Log ===================================");
		//displayCallLog();
		System.out.println("===================== Request Log ================================");
		displayRequestLog();
		System.out.println("==================================================================");
		System.out.println();
	}
	
	public void displayCallLog() {
		String hostComponentName = null;
		if(hostComponent != null) {
			hostComponentName = hostComponent.getComponentParameters().getControllerDescription().getName();
		}
    	Iterator<ComponentRequestID> i = callLog.keySet().iterator();
    	ComponentRequestID crID;
    	CallRecord cs;
    	while(i.hasNext()) {
    		crID = i.next();
    		cs = callLog.get(crID);
    		System.out.println("[callLog:"+hostComponentName+"] Parent: "+ cs.getParentID() + " Current: "+ crID + 
    				" Call: "+ cs.getCalledComponent() + "." + cs.getInterfaceName()+"."+cs.getMethodName()+ 
    				" SentTime: " + cs.getSentTime() + " RealReplyReceivedTime: " + cs.getReplyReceptionTime() +
    				" WbN: " + (cs.getWbnStartTime()==0 ? 0 : (cs.getReplyReceptionTime() - cs.getWbnStartTime())) + 
    				" SRV: " + (cs.getReplyReceptionTime() - cs.getSentTime()));
    	}
    }

    public void displayRequestLog() {
    	String hostComponentName = null;
		if(hostComponent != null) {
			hostComponentName = hostComponent.getComponentParameters().getControllerDescription().getName();
		}
    	Iterator<ComponentRequestID> i = requestLog.keySet().iterator();
    	ComponentRequestID crID;
    	RequestRecord rs;
    	while(i.hasNext()) {
    		crID = i.next();
    		rs = requestLog.get(crID);
    		System.out.println("[reqsLog:"+hostComponentName+"] ID: "+ crID + " Sender: "+ rs.getCallerComponent() +
    				" Call: "+ rs.getCalledComponent() + "." + rs.getInterfaceName()+"."+rs.getMethodName() +
    				" Arr: " + rs.getArrivalTime() + " Serv: " + rs.getServingStartTime() + " Repl: " + rs.getReplyTime() +
    				" WQ: " + (rs.getServingStartTime()-rs.getArrivalTime()) + 
    				" SRV: " + (rs.getReplyTime()-rs.getServingStartTime()) +
    				" TOT: "+ (rs.getReplyTime() - rs.getArrivalTime()));
    	}
    }

*/
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
    
}
