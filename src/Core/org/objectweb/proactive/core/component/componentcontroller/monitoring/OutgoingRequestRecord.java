package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores the data and timestamps related to an outgoing request (a call to another component).
 * 
 * @author cruz
 *
 */
public class OutgoingRequestRecord extends AbstractRecord implements Serializable {
	
	/** ID of the parent request */
	private ComponentRequestID parentID;
	
	private String calledComponent;
	private String interfaceName;
	private String methodName;
	
	/** The ID of the request that generated the sequence of calls to which this record belongs */
	private ComponentRequestID rootID;
	
	private long sentTime;
	private long replyReceptionTime;

	// maps to store WbN start and finish time
	// the key is the sequenceID
	private Map<Long, Long> wbnStartTime;
	private Map<Long, Long> wbnStopTime;
	
	private boolean finished;
	private boolean voidRequest;
	
	public OutgoingRequestRecord() {
	}

	public OutgoingRequestRecord(ComponentRequestID requestID, ComponentRequestID parentID, String calledComponent, String interfaceName, String methodName,
			long sentTime, boolean voidRequest, ComponentRequestID rootID) {
		super(RecordType.OutgoingRequestRecord, requestID);
		this.parentID = parentID;
		this.calledComponent = calledComponent;
		this.interfaceName = interfaceName;
		this.methodName = methodName;
		this.sentTime = sentTime;
		this.finished = false;
		this.voidRequest = voidRequest;
		this.wbnStartTime = new HashMap<Long, Long>();
		this.wbnStopTime = new HashMap<Long, Long>();
		this.rootID = rootID;
	}

	public long getSentTime() {
		return sentTime;
	}

	public void setSentTime(long sentTime) {
		this.sentTime = sentTime;
	}

	public long getReplyReceptionTime() {
		return replyReceptionTime;
	}

	public void setReplyReceptionTime(long replyReceptionTime) {
		this.replyReceptionTime = replyReceptionTime;
	}

	public Map<Long, Long> getWbnStartTime() {
		return wbnStartTime;
	}
	
	public Map<Long, Long> getWbnStopTime() {
		return wbnStopTime;
	}

	public void addWbnStartTime(long id, long wbnStartTime) {
		this.wbnStartTime.put(new Long(id), new Long(wbnStartTime));
	}
	
	public void addWbnStopTime(long id, long wbnStopTime) {
		this.wbnStopTime.put(new Long(id), new Long(wbnStopTime));
	}
	
	public ComponentRequestID getParentID() {
		return parentID;
	}

	public String getCalledComponent() {
		return calledComponent;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public boolean isFinished() {
		if(finished) return true;
		finished = (sentTime != 0 && replyReceptionTime != 0);
		return finished;
	}
	
	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean isVoidRequest() {
		return voidRequest;
	}
	
	public ComponentRequestID getRootID() {
		return rootID;
	}

}
