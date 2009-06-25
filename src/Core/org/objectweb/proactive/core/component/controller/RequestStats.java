package org.objectweb.proactive.core.component.controller;

/**
 * Stores the data and timestamps for a request.
 * 
 * @author cruz
 *
 */

public class RequestStats {

	private String callerComponent;
	private String interfaceName;
	private String methodName;
		
	private long arrivalTime;
	private long servingStartTime;
	private long replyTime;
	
	private boolean finished;
	
	public RequestStats(String callerComponent, String interfaceName, String methodName, long arrivalTime) {
		this.callerComponent = callerComponent;
		this.interfaceName = interfaceName;
		this.methodName = methodName;
		this.arrivalTime = arrivalTime;
		this.finished = false;
	}

	public long getServingStartTime() {
		return servingStartTime;
	}

	public void setServingStartTime(long servingStartTime) {
		this.servingStartTime = servingStartTime;
	}

	public long getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(long replyTime) {
		this.replyTime = replyTime;
	}

	public String getCallerComponent() {
		return callerComponent;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public long getArrivalTime() {
		return arrivalTime;
	}

	public boolean isFinished() {
		return finished;
	}
	
	
	
	
	
}
