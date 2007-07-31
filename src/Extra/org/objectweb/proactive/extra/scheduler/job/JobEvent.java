package org.objectweb.proactive.extra.scheduler.job;

import java.io.Serializable;
import java.util.HashMap;

import org.objectweb.proactive.extra.scheduler.task.Status;
import org.objectweb.proactive.extra.scheduler.task.TaskId;

/**
 * JobEvent provides some informations about a job.
 * These informations and only them are able to change,
 * that's what the scheduler will send to each listener.
 * To have the jog up to date, user must use Job.setJobInfo(JobEvent); .
 * This will automatically put the job up to date.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 25, 2007
 * @since ProActive 3.2
 */
public class JobEvent implements Serializable{
	
	/**  */
	private static final long serialVersionUID = -7426315610231893158L;
	/** job id */
	private JobId jobId = new JobId(0);
	/** job submitted time */
	private long submittedTime = -1;
	/** job started time */
	private long startTime = -1;
	/** job finished time */
	private long finishedTime = -1;
	/** job removed time (it means the user got back the result of the job) */
	private long removedTime = -1;
	/** total number of tasks */
	private int totalNumberOfTasks = 0;
	/** number of pending tasks */
	private int numberOfPendingTasks = 0;
	/** number of running tasks */
	private int numberOfRunningTasks = 0;
	/** number of finished tasks */
	private int numberOfFinishedTasks = 0;
	/** If this status is not null, it means the task have to change their status */
	private HashMap<TaskId,Status> taskStatusModify = null;
	
	
	/**
	 * To get the jobId
	 * 
	 * @return the jobId
	 */
	public JobId getJobId() {
		return jobId;
	}
	/**
	 * To set the jobId
	 * 
	 * @param jobId the jobId to set
	 */
	public void setJobId(JobId jobId) {
		this.jobId = jobId;
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
	 * To get the removedTime
	 * 
	 * @return the removedTime
	 */
	public long getRemovedTime() {
		return removedTime;
	}
	/**
	 * To set the removedTime
	 * 
	 * @param removedTime the removedTime to set
	 */
	public void setRemovedTime(long removedTime) {
		this.removedTime = removedTime;
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
	 * To get the submittedTime
	 * 
	 * @return the submittedTime
	 */
	public long getSubmittedTime() {
		return submittedTime;
	}
	/**
	 * To set the submittedTime
	 * 
	 * @param submittedTime the submittedTime to set
	 */
	public void setSubmittedTime(long submittedTime) {
		this.submittedTime = submittedTime;
	}
	/**
	 * To get the totalNumberOfTasks
	 * 
	 * @return the totalNumberOfTasks
	 */
	public int getTotalNumberOfTasks() {
		return totalNumberOfTasks;
	}
	/**
	 * To set the taskStatusModify
	 * 
	 * @param taskStatusModify the taskStatusModify to set
	 */
	public void setTaskStatusModify(HashMap<TaskId,Status> taskStatusModify) {
		this.taskStatusModify = taskStatusModify;
	}
	/**
	 * To get the taskStatusModify
	 * 
	 * @return the taskStatusModify
	 */
	public HashMap<TaskId,Status> getTaskStatusModify() {
		return taskStatusModify;
	}
	/**
	 * To get the numberOfFinishedTasks
	 * 
	 * @return the numberOfFinishedTasks
	 */
	public int getNumberOfFinishedTasks() {
		return numberOfFinishedTasks;
	}
	/**
	 * To set the numberOfFinishedTasks
	 * 
	 * @param numberOfFinishedTasks the numberOfFinishedTasks to set
	 */
	public void setNumberOfFinishedTasks(int numberOfFinishedTasks) {
		this.numberOfFinishedTasks = numberOfFinishedTasks;
	}
	/**
	 * To get the numberOfPendingTasks
	 * 
	 * @return the numberOfPendingTasks
	 */
	public int getNumberOfPendingTasks() {
		return numberOfPendingTasks;
	}
	/**
	 * To set the numberOfPendingTasks
	 * 
	 * @param numberOfPendingTasks the numberOfPendingTasks to set
	 */
	public void setNumberOfPendingTasks(int numberOfPendingTasks) {
		this.numberOfPendingTasks = numberOfPendingTasks;
	}
	/**
	 * To get the numberOfRunningTasks
	 * 
	 * @return the numberOfRunningTasks
	 */
	public int getNumberOfRunningTasks() {
		return numberOfRunningTasks;
	}
	/**
	 * To set the numberOfRunningTasks
	 * 
	 * @param numberOfRunningTasks the numberOfRunningTasks to set
	 */
	public void setNumberOfRunningTasks(int numberOfRunningTasks) {
		this.numberOfRunningTasks = numberOfRunningTasks;
	}
	/**
	 * To set the totalNumberOfTasks
	 * 
	 * @param totalNumberOfTasks the totalNumberOfTasks to set
	 */
	public void setTotalNumberOfTasks(int totalNumberOfTasks) {
		this.totalNumberOfTasks = totalNumberOfTasks;
	}
	
}