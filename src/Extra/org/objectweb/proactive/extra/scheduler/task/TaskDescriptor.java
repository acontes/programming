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
package org.objectweb.proactive.extra.scheduler.task;

import java.io.Serializable;
import java.util.ArrayList;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extra.scheduler.job.JobEvent;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.scripting.Script;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;


/**
 * Internal and global description of a task.
 * 
 * @author ProActive Team
 * @version 1.0, Jul 9, 2007
 * @since ProActive 3.2
 */
public abstract class TaskDescriptor implements Comparable<TaskDescriptor>, Serializable {

	
	public static final int SORT_BY_ID = 1;
	public static final int SORT_BY_NAME = 2;
	public static final int SORT_BY_STATUS = 3;
	public static final int SORT_BY_DESCRIPTION = 4;
	public static final int SORT_BY_RUN_TIME_LIMIT = 5;
	public static final int SORT_BY_RERUNNABLE = 6;
	public static final int SORT_BY_SUBMITTED_TIME = 7;
	public static final int SORT_BY_STARTED_TIME = 8;
	public static final int SORT_BY_FINISHED_TIME = 9;
	public static final int SORT_BY_HOST_NAME = 10;
	public static final int ASC_ORDER = 1;
	public static final int DESC_ORDER = 2;
	private static int currentSort = SORT_BY_ID;
	private static int currentOrder = ASC_ORDER;
	
	/** Number of nodes asked by the user. */
	protected int numberOfNodesNeeded = 1;
	
	private String name;
	private String description;
	/** Parents list */
	private ArrayList<TaskDescriptor> dependences = null;
	private VerifyingScript verifyingScript;
	private Script<?> preTask;
	private Script<?> postTask;
	private long runTimeLimit;
	private int rerunnable;
	private boolean finalTask;
	private TaskLauncher launcher;
	private TaskEvent taskInfo = new TaskEvent();
    
    
	/**
	 * ProActive Empty constructor
	 */
	public TaskDescriptor(){}
	
	
	/**
	 * Return the user task represented by this task descriptor.
	 * 
	 * @return the user task represented by this task descriptor.
	 */
	public abstract Task getTask();
	
	
	/**
	 * Create the launcher for this taskDescriptor.
	 * 
	 * @param host the hostname on which to send the log.
	 * @param port the port on which to send the log.
	 * @param node the node on which to create the launcher.
	 * @return the created launcher as an activeObject.
	 */
	public TaskLauncher createLauncher(String host, int port, Node node) throws ActiveObjectCreationException,NodeException{
		TaskLauncher launcher;
		if (getPreTask() == null){
			launcher = (TaskLauncher)ProActive.newActive(TaskLauncher.class.getName(), new Object[]{getId(),getJobId(), host, port}, node);
		} else {
			launcher = (TaskLauncher)ProActive.newActive(TaskLauncher.class.getName(), new Object[]{getId(),getJobId(),getPreTask(), host, port}, node);
		}
		setLauncher(launcher);
		return launcher;
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
	public int compareTo(TaskDescriptor task) {
		switch (currentSort) {
		case SORT_BY_DESCRIPTION:
			return (currentOrder == ASC_ORDER)
					? (description.compareTo(task.description)) 
					: (task.description.compareTo(description));
		case SORT_BY_NAME:
			return (currentOrder == ASC_ORDER) 
					? (name.compareTo(task.name))
					: (task.name.compareTo(name));
		case SORT_BY_STATUS:
			return (currentOrder == ASC_ORDER)
					? (getStatus().compareTo(task.getStatus()))
					: (task.getStatus().compareTo(getStatus()));
		case SORT_BY_SUBMITTED_TIME:
			return (currentOrder == ASC_ORDER)
					? ((int)(getSubmitTime()-task.getSubmitTime()))
					: ((int)(task.getSubmitTime()-getSubmitTime()));
		case SORT_BY_STARTED_TIME:
			return (currentOrder == ASC_ORDER)
					? ((int)(getStartTime()-task.getStartTime()))
					: ((int)(task.getStartTime()-getStartTime()));
		case SORT_BY_FINISHED_TIME:
			return (currentOrder == ASC_ORDER)
					? ((int)(getFinishedTime()-task.getFinishedTime()))
					: ((int)(task.getFinishedTime()-getFinishedTime()));
		case SORT_BY_RERUNNABLE:
			return (currentOrder == ASC_ORDER)
					? (new Integer(getRerunnable()).compareTo(new Integer(task.getRerunnable())))
					: (new Integer(task.getRerunnable()).compareTo(new Integer(getRerunnable())));
		case SORT_BY_RUN_TIME_LIMIT:
			return (currentOrder == ASC_ORDER)
					? ((int)(getRunTimeLimit()-task.getRunTimeLimit()))
					: ((int)(task.getRunTimeLimit()-getRunTimeLimit()));
		case SORT_BY_HOST_NAME:
			return (currentOrder == ASC_ORDER)
					? (getExecutionHostName().compareTo(task.getExecutionHostName()))
					: (task.getExecutionHostName().compareTo(getExecutionHostName()));
		default:
			return (currentOrder == ASC_ORDER)
					? (getId().value() - task.getId().value())
					: (task.getId().value() - getId().value());
		}
	}
	

	/**
	 * @return the numberOfNodesNeeded
	 */
	public int getNumberOfNodesNeeded() {
		return numberOfNodesNeeded;
	}
	
	
	/**
	 * Add a dependence to the list of dependences for this taskDescriptor.
	 * The tasks in this list represents the tasks this tasks have to wait for before starting.
	 * 
	 * @param task a supertask of this task.
	 */
	public void addDependence(TaskDescriptor task){
		if (dependences == null)
			dependences = new ArrayList<TaskDescriptor>();
		dependences.add(task);
	}
	
	
	/**
	 * Return true if this task has dependencies.
	 * It means the first eligible tasks in case of TASK_FLOW job type.
	 * 
	 * @return true if this task has dependencies, false otherwise.
	 */
	public boolean hasDependences(){
		return dependences != null;
	}
	
	
	/**
	 * To get the taskInfo
	 * 
	 * @return the taskInfo
	 */
	public TaskEvent getTaskInfo() {
		return taskInfo;
	}

	/**
	 * To set the taskInfo
	 * 
	 * @param taskInfo the taskInfo to set
	 */
	public void update(TaskEvent taskInfo) {
		this.taskInfo = taskInfo;
	}


	/**
	 * To get the description of this task.
	 * 
	 * @return the description of this task.
	 */
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * To know if this task is the final one or not.
	 * 
	 * @return true if this task is the final task, false if not.
	 */
	public boolean isFinalTask() {
		return finalTask;
	}
	
	public void setFinalTask(boolean finalTask) {
		this.finalTask = finalTask;
	}

	/**
	 * To get the name of this task.
	 * 
	 * @return the name of this task.
	 */
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the number of possible rerun for this task.
	 * 
	 * @return the number of possible rerun for this task.
	 */
	public int getRerunnable() {
		return rerunnable;
	}
	
	/**
	 * Set the number of possible rerun for this task.
	 * 
	 * @param reRunnable the number of rerun possible for this task.
	 */
	public void setRerunnable(int reRunnable) {
		this.rerunnable = reRunnable;
	}

	/**
	 * To get the runTime limit of this task.
	 * It means the maximum amount of time (in millis) it is authorized to use.
	 * 
	 * @return the runTime limit of the task.
	 */
	public long getRunTimeLimit() {
		return runTimeLimit;
	}
	
	public void setRunTimeLimit(long runTimeLimit) {
		this.runTimeLimit = runTimeLimit;
	}

	public VerifyingScript getVerifyingScript() {
		return verifyingScript;
	}

	public void setVerifyingScript(VerifyingScript verifyingScript) {
		this.verifyingScript = verifyingScript;
	}

	
	/**
	 * To get the postTask
	 * 
	 * @return the postTask
	 */
	public Script<?> getPostTask() {
		return postTask;
	}

	
	/**
	 * To set the postTask
	 * 
	 * @param postTask the postTask to set
	 */
	public void setPostTask(Script<?> postTask) {
		this.postTask = postTask;
	}

	
	/**
	 * To get the preTask
	 * 
	 * @return the preTask
	 */
	public Script<?> getPreTask() {
		return preTask;
	}

	
	/**
	 * To set the preTask
	 * 
	 * @param preTask the preTask to set
	 */
	public void setPreTask(Script<?> preTask) {
		this.preTask = preTask;
	}

	
	/**
	 * To get the finishedTime
	 * 
	 * @return the finishedTime
	 */
	public long getFinishedTime() {
		return taskInfo.getFinishedTime();
	}
	
	
	/**
	 * To set the finishedTime
	 * 
	 * @param finishedTime the finishedTime to set
	 */
	public void setFinishedTime(long finishedTime) {
		taskInfo.setFinishedTime(finishedTime);
	}
	
	
	/**
	 * To get the jobID
	 * 
	 * @return the jobID
	 */
	public JobId getJobId() {
		return taskInfo.getJobId();
	}
	
	
	/**
	 * To set the jobId
	 * 
	 * @param id the jobId to set
	 */
	public void setJobId(JobId id) {
		taskInfo.setJobId(id);
	}
	
	
	/**
	 * To get the startTime
	 * 
	 * @return the startTime
	 */
	public long getStartTime() {
		return taskInfo.getStartTime();
	}
	
	
	/**
	 * To set the startTime
	 * 
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		taskInfo.setStartTime(startTime);
	}
	
	
	/**
	 * To get the submitTime
	 * 
	 * @return the submitTime
	 */
	public long getSubmitTime(){
		return taskInfo.getSubmitTime();
	}
	
	
	/**
	 * To set the submitTime
	 * 
	 * @param submitTime the submitTime to set
	 */
	public void setSubmitTime(long submitTime){
		taskInfo.setSubmitTime(submitTime);
	}
	
	
	/**
	 * To get the taskId
	 * 
	 * @return the taskID
	 */
	public TaskId getId() {
		return taskInfo.getTaskID();
	}
	
	
	/**
	 * To set the taskId
	 * 
	 * @param taskID the taskID to set
	 */
	public void setId(TaskId taskID) {
		taskInfo.setTaskID(taskID);
	}
	

	/**
	 * Set the job info to this task.
	 * 
	 * @param jobInfo a job info containing job id and others informations
	 */
	public void setJobInfo(JobEvent jobInfo) {
		taskInfo.setJobEvent(jobInfo);
	}

	
	/**
	 * To get the status
	 * 
	 * @return the status
	 */
	public Status getStatus() {
		return taskInfo.getStatus();
	}

	
	/**
	 * To set the status
	 * 
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		taskInfo.setStatus(status);
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
	public boolean equals(Object obj) {
		if (TaskDescriptor.class.isAssignableFrom(obj.getClass())){
			return ((TaskDescriptor)obj).getId().equals(getId());
		}
		return false;
	}


	/**
	 * To get the dependences
	 * 
	 * @return the dependences
	 */
	public ArrayList<TaskDescriptor> getDependences() {
		return dependences;
	}
	
	/**
	 * To get the executionHostName
	 * 
	 * @return the executionHostName
	 */
	public String getExecutionHostName() {
		return taskInfo.getExecutionHostName();
	}
	/**
	 * To set the executionHostName
	 * 
	 * @param executionHostName the executionHostName to set
	 */
	public void setExecutionHostName(String executionHostName) {
		taskInfo.setExecutionHostName(executionHostName);
	}
	


	/**
	 * To get the active object where this task is running.
	 * 
	 * @return the launcher
	 */
	public TaskLauncher getLauncher() {
		return launcher;
	}


	/**
	 * To set the active object where this task will run.
	 * 
	 * @param launcher the launcher to set
	 */
	public void setLauncher(TaskLauncher launcher) {
		this.launcher = launcher;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TaskDescriptor("+getId()+")";
	}
	
}
