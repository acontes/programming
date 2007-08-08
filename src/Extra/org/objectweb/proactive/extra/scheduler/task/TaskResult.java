package org.objectweb.proactive.extra.scheduler.task;

import java.io.Serializable;


public class TaskResult implements Serializable {

	private static final long serialVersionUID = 2976276079143998102L;
	private TaskId id = null;
	private Object value = null;
	private Throwable exception = null;

	/** ProActive empty constructor. */
	public TaskResult(){}
	
	
	public TaskResult(TaskId id, Object value) {
		this.id = id;
		this.value = value;
	}

	
	public TaskResult(TaskId id, Throwable exception) {
		this.id = id;
		this.exception = exception;
	}

	/**
	 * To get the exception
	 * 
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}
	

	/**
	 * To get the id
	 * 
	 * @return the id
	 */
	public TaskId getTaskId() {
		return id;
	}
	

	/**
	 * To get the value
	 * 
	 * @return the value
	 */
	public Object value() {
		return value;
	}

	
	/**
	 * To know if an exception has occured on this task.
	 * 
	 * @return true if an exception occured, false if not.
	 */
	public boolean hadException(){
		return exception != null;
	}
}
