/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extra.scheduler.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import org.objectweb.proactive.extra.scheduler.task.Status;
import org.objectweb.proactive.extra.scheduler.task.TaskEvent;
import org.objectweb.proactive.extra.scheduler.task.TaskId;
import org.objectweb.proactive.extra.scheduler.task.descriptor.TaskDescriptor;
import org.objectweb.proactive.extra.scheduler.userAPI.JobState;

/**
 * Abstract class job.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 7, 2007
 * @since ProActive 3.2
 */
public abstract class Job implements Serializable, Comparable<Job> {

	public static final int SORT_BY_ID = 1;
	public static final int SORT_BY_NAME = 2;
	public static final int SORT_BY_PRIORITY = 3;
	public static final int SORT_BY_TYPE = 4;
	public static final int SORT_BY_DESCRIPTION = 5;
	public static final int SORT_BY_OWNER = 6;
	public static final int SORT_BY_STATE = 7;
	public static final int ASC_ORDER = 1;
	public static final int DESC_ORDER = 2;
	private static int currentSort = SORT_BY_ID;
	private static int currentOrder = ASC_ORDER;
	/** Serial version UID */
	private static final long serialVersionUID = 1565033147327965656L;
	private String owner = "";
	private String name = "";
	private long runtimeLimit = -1;
	private boolean RunUntilCancel = false;
	private String description = "";
	// TODO envParameters
	// TODO un moyen pour le user de mettre n'importe quelles données dans le job et la retrouver dans la police.
	// cela lui permettrai de moduler sa police en fonction de ces données
	protected HashMap<TaskId,TaskDescriptor> tasks = new HashMap<TaskId,TaskDescriptor>();
	/** Instances of the final task, important to know which results will be sent to user */
	protected Vector<TaskDescriptor> finalTasks = new Vector<TaskDescriptor>();
	/** informations about job execution */
	protected JobEvent jobInfo = new JobEvent();
	/** Light job for dependences management */
	private LightJob lightJob;

	/**
	 * ProActive empty constructor.
	 */
	public Job() {}

	/**
	 * Create a new Job with the given parameters. It provides methods to add or
	 * remove tasks.
	 * 
	 * @param name the current job name.
	 * @param priority the priority of this job between 1 and 5.
	 * @param runtimeLimit the maximum execution time for this job given in millisecond.
	 * @param runUntilCancel true if the job has to run until its end or an user intervention.
	 * @param description a short description of the job and what it will do.
	 */
	public Job(String name, JobPriority priority, long runtimeLimit, boolean runUntilCancel, String description) {
		super();
		this.name = name;
		this.jobInfo.setPriority(priority);
		this.runtimeLimit = runtimeLimit;
		this.RunUntilCancel = runUntilCancel;
		this.description = description;
	}

	
	/**
	 * Set the jobEvent contained in the TaskEvent to this job.
	 * 
	 * @param event a taskEvent containing a job event.
	 */
	public synchronized void update(TaskEvent event){
		jobInfo = event.getJobEvent();
		tasks.get(event.getTaskID()).update(event);
	}

	/**
	 * To set the jobInfo
	 * 
	 * @param jobInfo the jobInfo to set
	 */
	public synchronized void update(JobEvent jobInfo) {
		this.jobInfo = jobInfo;
		if (jobInfo.getTaskStatusModify() != null){
			for (TaskId id : tasks.keySet()){
				tasks.get(id).setStatus(jobInfo.getTaskStatusModify().get(id));
			}
		}
	}
	

	/**
	 * To get the jobInfo
	 * 
	 * @return the jobInfo
	 */
	public JobEvent getJobInfo() {
		return jobInfo;
	}
	
	/**
	 * Set the field to sort on.
	 * 
	 * @param sortBy
	 *            the field on which the sort will be made.
	 */
	public static void setSortingBy(int sortBy) {
		currentSort = sortBy;
	}

	/**
	 * Set the order for the next sort.
	 * 
	 * @param order
	 */
	public static void setSortingOrder(int order) {
		if (order == ASC_ORDER || order == DESC_ORDER) {
			currentOrder = order;
		} else {
			currentOrder = ASC_ORDER;
		}
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Job job) {
		switch (currentSort) {
		case SORT_BY_DESCRIPTION:
			return (currentOrder == ASC_ORDER) ? (description
					.compareTo(job.description)) : (job.description
					.compareTo(description));
		case SORT_BY_NAME:
			return (currentOrder == ASC_ORDER) ? (name.compareTo(job.name))
					: (job.name.compareTo(name));
		case SORT_BY_PRIORITY:
			return (currentOrder == ASC_ORDER) ?
					(jobInfo.getPriority().getPriority() - job.jobInfo.getPriority().getPriority())
					: (job.jobInfo.getPriority().getPriority() - jobInfo.getPriority().getPriority());
		case SORT_BY_TYPE:
			return (currentOrder == ASC_ORDER) ? (getType().compareTo(job.getType()))
					: (job.getType().compareTo(getType()));
		case SORT_BY_OWNER:
			return (currentOrder == ASC_ORDER) ? (owner.compareTo(job.owner))
					: (job.owner.compareTo(owner));
		case SORT_BY_STATE:
			return (currentOrder == ASC_ORDER) ? (jobInfo.getState().compareTo(job.jobInfo.getState()))
					: (job.jobInfo.getState().compareTo(jobInfo.getState()));
		default:
			return (currentOrder == ASC_ORDER) ? (getId().value() - job.getId().value())
					: (job.getId().value() - getId().value());
		}
	}

	/**
	 * Append a task to this job.
	 * 
	 * @param task
	 *            the task to add.
	 * @return true if the task has been correctly added to the job, false if
	 *         not.
	 */
	public boolean addTask(TaskDescriptor task) {
		task.setJobId(getId());
		if (task.isFinalTask())
			finalTasks.add(task);
		boolean result = (tasks.put(task.getId(),task) == null);
		if (result)
			jobInfo.setTotalNumberOfTasks(jobInfo.getTotalNumberOfTasks()+1);
		return result;
	}
	

	/**
	 * Start a new task will set some count and update dependencies if necessary.
	 * 
	 * @param td the task which has just been started.
	 * @param hostname the computer host name on which the task has been started.
	 */
	public void startTask(TaskDescriptor td, String hostName) {
		setNumberOfPendingTasks(getNumberOfPendingTask()-1);
		setNumberOfRunningTasks(getNumberOfRunningTask()+1);
		if (getState() == JobState.STALLED)
			setState(JobState.RUNNING);
		lightJob.start(td.getId());
		td.setStatus(Status.RUNNNING);
		td.setStartTime(System.currentTimeMillis());
		td.setExecutionHostName(hostName);
	}
	
	
	/**
	 * Terminate a task, change status, managing dependences
	 * 
	 * @param taskId the task to terminate.
	 * @return the taskDescriptor that has just been terminated.
	 */
	public TaskDescriptor terminateTask(TaskId taskId) {
		TaskDescriptor descriptor = tasks.get(taskId);
		descriptor.setFinishedTime(System.currentTimeMillis());
		descriptor.setStatus(Status.FINISHED);
		setNumberOfRunningTasks(getNumberOfRunningTask()-1);
		setNumberOfFinishedTasks(getNumberOfFinishedTask()+1);
		if (getState() == JobState.RUNNING && getNumberOfRunningTask() == 0)
			setState(JobState.STALLED);
		//terminate this task
		lightJob.terminate(taskId);
		//creating list of status
		HashMap<TaskId,Status> hts = new HashMap<TaskId, Status>();
		for (TaskDescriptor td : tasks.values()){
			hts.put(td.getId(), td.getStatus());
		}
		//updating light job for eligible task
		lightJob.update(hts);
		return descriptor;
	}
	

	/**
	 * Set all properties following a job submitting.
	 */
	public void submit() {
		setSubmittedTime(System.currentTimeMillis());
		setState(JobState.PENDING);
	}
	
	
	/**
	 * Set all properties in order to start the job.
	 * After this method and for better performances you may have to
	 * set the taskStatusModify to "null" : setTaskStatusModify(null);
	 */
	public void start(){
		setStartTime(System.currentTimeMillis());
		setNumberOfPendingTasks(getTotalNumberOfTasks());
		setNumberOfRunningTasks(0);
		setState(JobState.RUNNING);
		HashMap<TaskId,Status> status = new HashMap<TaskId,Status>();
		for (TaskDescriptor td : getTasks()){
			td.setStatus(Status.PENDING);
			status.put(td.getId(), Status.PENDING);
		}
		setTaskStatusModify(status);
	}
	
	
	/**
	 * Set all properties in order to terminate the job.
	 */
	public void terminate() {
		setState(JobState.FINISHED);
		setFinishedTime(System.currentTimeMillis());
	}
	

	/**
	 * Paused every running and submitted tasks in this pending job.
	 * After this method and for better performances you may have to
	 * set the taskStatusModify to "null" : setTaskStatusModify(null);
	 */
	public boolean setPaused() {
		if (jobInfo.getState() == JobState.PAUSED)
			return false;
		jobInfo.setState(JobState.PAUSED);
		HashMap<TaskId,Status> hts = new HashMap<TaskId, Status>();
		for (TaskDescriptor td : tasks.values()){
			if (td.getStatus() != Status.FINISHED && td.getStatus() != Status.RUNNNING)
				td.setStatus(Status.PAUSED);
			hts.put(td.getId(), td.getStatus());
		}
		lightJob.update(hts);
		setTaskStatusModify(hts);
		return true;
	}

	/**
	 * State of every paused tasks becomes pending or submitted in this pending job.
	 * After this method and for better performances you may have to
	 * set the taskStatusModify to "null" : setTaskStatusModify(null);
	 */
	public boolean setUnPause() {
		if (jobInfo.getState() != JobState.PAUSED)
			return false;
		if (getNumberOfPendingTask()+getNumberOfRunningTask()+getNumberOfFinishedTask()==0)
			jobInfo.setState(JobState.PENDING);
		else if(getNumberOfRunningTask() == 0)
			jobInfo.setState(JobState.STALLED);
		else
			jobInfo.setState(JobState.RUNNING);
		HashMap<TaskId,Status> hts = new HashMap<TaskId, Status>();
		for (TaskDescriptor td : tasks.values()){
			if (jobInfo.getState() == JobState.PENDING){
				td.setStatus(Status.SUBMITTED);
			} else if (jobInfo.getState() == JobState.RUNNING || jobInfo.getState() == JobState.STALLED){
				if (td.getStatus() != Status.FINISHED && td.getStatus() != Status.RUNNNING)
					td.setStatus(Status.PENDING);
			}
			hts.put(td.getId(), td.getStatus());
		}
		lightJob.update(hts);
		setTaskStatusModify(hts);
		return true;
	}
	
	
	/**
	 * To get the description
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * To set the description
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * To get the id
	 * 
	 * @return the id
	 */
	public JobId getId() {
		return jobInfo.getJobId();
	}

	/**
	 * To get the name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * To get the priority
	 * 
	 * @return the priority
	 */
	public JobPriority getPriority() {
		return jobInfo.getPriority();
	}
	
	/**
	 * To set the priority.
	 */
	public void setPriority(JobPriority priority) {
		jobInfo.setPriority(priority);
		lightJob.setPriority(priority);
	}

	/**
	 * To get the runtimeLimit
	 * 
	 * @return the runtimeLimit
	 */
	public long getRuntimeLimit() {
		return runtimeLimit;
	}

	/**
	 * To get the runUntilCancel
	 * 
	 * @return the runUntilCancel
	 */
	public boolean isRunUntilCancel() {
		return RunUntilCancel;
	}

	/**
	 * To get the tasks as an arraylist.
	 * 
	 * @return the tasks
	 */
	public ArrayList<TaskDescriptor> getTasks() {
		return new ArrayList<TaskDescriptor>(tasks.values());
	}
	
	
	/**
	 * To get the tasks as a hash map.
	 * 
	 * @return the tasks
	 */
	public HashMap<TaskId,TaskDescriptor> getHMTasks() {
		return tasks;
	}

	/**
	 * To set the taskStatusModify
	 * 
	 * @param taskStatusModify the taskStatusModify to set
	 */
	public void setTaskStatusModify(HashMap<TaskId,Status> taskStatusModify) {
		jobInfo.setTaskStatusModify(taskStatusModify);
	}
	
	
	/**
	 * To get the type
	 * 
	 * @return the type
	 */
	public abstract JobType getType();

	/**
	 * To get the finalTask
	 * 
	 * @return the finalTask
	 */
	public Vector<TaskDescriptor> getFinalTasks() {
		return finalTasks;
	}

	/**
	 * To set the id
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(JobId id) {
		jobInfo.setJobId(id);
	}


	/**
	 * To get the numberOfFinishedTask
	 * 
	 * @return the numberOfFinishedTask
	 */
	public int getNumberOfFinishedTask() {
		return jobInfo.getNumberOfFinishedTasks();
	}

	/**
	 * To get the finishedTime
	 * 
	 * @return the finishedTime
	 */
	public long getFinishedTime() {
		return jobInfo.getFinishedTime();
	}

	/**
	 * To set the finishedTime
	 * 
	 * @param finishedTime
	 *            the finishedTime to set
	 */
	public void setFinishedTime(long finishedTime) {
		jobInfo.setFinishedTime(finishedTime);
	}

	/**
	 * To get the numberOfPendingTask
	 * 
	 * @return the numberOfPendingTask
	 */
	public int getNumberOfPendingTask() {
		return jobInfo.getNumberOfPendingTasks();
	}

	/**
	 * To get the numberOfRunningTask
	 * 
	 * @return the numberOfRunningTask
	 */
	public int getNumberOfRunningTask() {
		return jobInfo.getNumberOfRunningTasks();
	}


	/**
	 * To get the startTime
	 * 
	 * @return the startTime
	 */
	public long getStartTime() {
		return jobInfo.getStartTime();
	}

	/**
	 * To set the startTime
	 * 
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(long startTime) {
		jobInfo.setStartTime(startTime);
	}

	/**
	 * To get the totalNumberOfTasks
	 * 
	 * @return the totalNumberOfTasks
	 */
	public int getTotalNumberOfTasks() {
		return jobInfo.getTotalNumberOfTasks();
	}


	/**
	 * Change the id of a task.
	 * 
	 * @param td the task descriptor from where to change the id.
	 * @param id the new id.
	 */
	public void setTaskId(TaskDescriptor td, TaskId id) {
		tasks.remove(td.getId());
		td.setId(id);
		tasks.put(id,td);
	}
	
	
	/**
	 * To get the removedTime
	 * 
	 * @return the removedTime
	 */
	public long getRemovedTime() {
		return jobInfo.getRemovedTime();
	}

	/**
	 * To get the runUntilCancel
	 * 
	 * @return the runUntilCancel
	 */
	public boolean getRunUntilCancel() {
		return RunUntilCancel;
	}

	/**
	 * To get the submittedTime
	 * 
	 * @return the submittedTime
	 */
	public long getSubmittedTime() {
		return jobInfo.getSubmittedTime();
	}

	/**
	 * To set the submittedTime
	 * 
	 * @param submittedTime
	 *            the submittedTime to set
	 */
	public void setSubmittedTime(long submittedTime) {
		jobInfo.setSubmittedTime(submittedTime);
	}

	/**
	 * To set the removedTime
	 * 
	 * @param removedTime
	 *            the removedTime to set
	 */
	public void setRemovedTime(long removedTime) {
		jobInfo.setRemovedTime(removedTime);
	}
	
	
	/**
	 * To set the numberOfFinishedTasks
	 * 
	 * @param numberOfFinishedTasks the numberOfFinishedTasks to set
	 */
	public void setNumberOfFinishedTasks(int numberOfFinishedTasks) {
		jobInfo.setNumberOfFinishedTasks(numberOfFinishedTasks);
	}

	/**
	 * To set the numberOfPendingTasks
	 * 
	 * @param numberOfPendingTasks the numberOfPendingTasks to set
	 */
	public void setNumberOfPendingTasks(int numberOfPendingTasks) {
		jobInfo.setNumberOfPendingTasks(numberOfPendingTasks);
	}

	/**
	 * To set the numberOfRunningTasks
	 * 
	 * @param numberOfRunningTasks the numberOfRunningTasks to set
	 */
	public void setNumberOfRunningTasks(int numberOfRunningTasks) {
		jobInfo.setNumberOfRunningTasks(numberOfRunningTasks);
	}
	

	/**
	 * To get the lightJob
	 * 
	 * @return the lightJob
	 */
	public LightJob getLightJob() {
		return lightJob;
	}

	
	/**
	 * To set the lightJob
	 * 
	 * @param lightJob the lightJob to set
	 */
	public void setLightJob(LightJob lightJob) {
		this.lightJob = lightJob;
	}
	
	
	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	/**
	 * To get the state of the job.
	 * 
	 * @return the state of the job.
	 */
	public JobState getState() {
		return jobInfo.getState();
	}
	
	/**
	 * @param state the state to set
	 */
	public void setState(JobState state) {
		jobInfo.setState(state);
	}

	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o){
		if (o instanceof Job)
			return getId().equals(((Job)o).getId());
		return false;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getId() + "]";
	}

}
