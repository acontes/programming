package org.objectweb.proactive.extra.scheduler.core;

import java.io.Serializable;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobEvent;
import org.objectweb.proactive.extra.scheduler.task.TaskEvent;

/**
 * Class providing events that the scheduler is able to send using the described listener.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 12, 2007
 * @since ProActive 3.2
 */
public interface SchedulerEventListener extends Serializable {
	
	/**
	 * Invoked when the scheduler has received a new job to schedule.
	 * 
	 * @param job the new scheduled job.
	 */
	public void newPendingJobEvent(Job job);
	
	/**
	 * Invoked when the scheduling of a job has just started.
	 * The description of the job is contained in the jobEvent given.
	 * 
	 * @param event the event describing the job concerned.
	 */
	public void pendingToRunningJobEvent(JobEvent event);
	
	/**
	 * Invoked when the scheduling of a job has just been terminated.
	 * The description of the job is contained in the jobEvent given.
	 * 
	 * @param event the event describing the job concerned.
	 */
	public void runningToFinishedJobEvent(JobEvent event);
	
	/**
	 * Invoked when the scheduler has removed a job due to result reclamation.
	 * The description of the job is contained in the jobEvent given.
	 * 
	 * @param event the event describing the job concerned.
	 */
	public void removeFinishedJobEvent(JobEvent event);
	
	
	/**
	 * Invoked when the scheduling of a task has just started.
	 * The description of the task is contained in the TaskEvent given.
	 * 
	 * @param event the event describing the task concerned.
	 */
	public void pendingToRunningTaskEvent(TaskEvent event);
	
	/**
	 * Invoked when the scheduling of a task has just finished.
	 * The description of the task is contained in the TaskEvent given.
	 * 
	 * @param event the event describing the task concerned.
	 */
	public void runningToFinishedTaskEvent(TaskEvent event);
	
}
