package org.objectweb.proactive.extra.scheduler.task;

import java.io.Serializable;

import org.objectweb.proactive.extra.scheduler.job.JobEvent;
import org.objectweb.proactive.extra.scheduler.job.JobId;

/**
 * Informations about the task that is able to change.
 * These informations are in an other class in order to permit
 * the scheduler listener to send this class as event.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 25, 2007
 * @since ProActive 3.2
 */
public class TaskEvent implements Serializable{
	
	/** Serial version UID */
	private static final long serialVersionUID = -7625483185225564284L;
	/** global task id count */
	private static int globalCount = 0;
	/** id of the task */
	private TaskId taskID = new TaskId(globalCount++);
	/** informations about the job */
	private JobEvent jobEvent = null;
	/** task submitted time */
	private long submitTime = -1;
	/** task started time */
	private long startTime = -1;
	/** task finished time */
	private long finishedTime = -1;
	/** Current status of the task */
	private Status status = Status.SUBMITTED;
	/** name of the host where the task is executed */
	private String executionHostName;
	
	
	/**
	 * To get the jobEvent
	 * 
	 * @return the jobEvent
	 */
	public JobEvent getJobEvent() {
		return jobEvent;
	}
	/**
	 * To set the jobEvent
	 * 
	 * @param jobEvent the jobEvent to set
	 */
	public void setJobEvent(JobEvent jobEvent) {
		this.jobEvent = jobEvent;
	}
	/**
	 * To get the finishedTime
	 * 
	 * @return the finishedTime
	 */
	public long getFinishedTime() {
		return finishedTime;
	}
	/**
	 * To set the finishedTime
	 * 
	 * @param finishedTime the finishedTime to set
	 */
	public void setFinishedTime(long finishedTime) {
		this.finishedTime = finishedTime;
	}
	/**
	 * To get the jobId
	 * 
	 * @return the jobId
	 */
	public JobId getJobId() {
		if (jobEvent != null){
			return jobEvent.getJobId();
		}
		return null;
	}
	/**
	 * To set the jobId
	 * 
	 * @param jobId the jobId to set
	 */
	public void setJobId(JobId jobId) {
		if (jobEvent != null){
			jobEvent.setJobId(jobId);
		}
	}
	/**
	 * To get the startTime
	 * 
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}
	/**
	 * To set the startTime
	 * 
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	/**
	 * To get the taskID
	 * 
	 * @return the taskID
	 */
	public TaskId getTaskID() {
		return taskID;
	}
	/**
	 * To set the taskID
	 * 
	 * @param taskID the taskID to set
	 */
	public void setTaskID(TaskId taskID) {
		this.taskID = taskID;
	}
	/**
	 * To get the submitTime
	 * 
	 * @return the submitTime
	 */
	public long getSubmitTime() {
		return submitTime;
	}
	/**
	 * To set the submitTime
	 * 
	 * @param submitTime the submitTime to set
	 */
	public void setSubmitTime(long submitTime) {
		this.submitTime = submitTime;
	}
	/**
	 * To get the status
	 * 
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}
	/**
	 * To set the status
	 * 
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	/**
	 * To get the executionHostName
	 * 
	 * @return the executionHostName
	 */
	public String getExecutionHostName() {
		return executionHostName;
	}
	/**
	 * To set the executionHostName
	 * 
	 * @param executionHostName the executionHostName to set
	 */
	public void setExecutionHostName(String executionHostName) {
		this.executionHostName = executionHostName;
	}
	
	
}
