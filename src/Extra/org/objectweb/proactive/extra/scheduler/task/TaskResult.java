package org.objectweb.proactive.extra.scheduler.task;

import java.io.Serializable;


public class TaskResult implements Serializable {

	private static final long serialVersionUID = 2976276079143998102L;
	private TaskId id = null;
	private Object value = null;
	private Throwable exception = null;

	/**
	 * ProActive empty constructor.
	 * 
	 */
	public TaskResult(){}
	
	public TaskResult(TaskId id){
		this.id = id;
	}
	
	public TaskResult(TaskId id, Object value) {
		this(id);
		this.value = value;
	}

	public TaskResult(TaskId id, Throwable exception) {
		this(id);
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
	 * To set the exception
	 * 
	 * @param exception the exception to set
	 */
	public void setException(Throwable exception) {
		this.exception = exception;
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
	 * To set the id
	 * 
	 * @param id the id to set
	 */
	public void setTaskId(TaskId id) {
		this.id = id;
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
	 * To set the value
	 * 
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
}
