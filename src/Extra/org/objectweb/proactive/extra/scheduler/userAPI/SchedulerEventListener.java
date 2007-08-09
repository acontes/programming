package org.objectweb.proactive.extra.scheduler.userAPI;

import java.io.Serializable;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobEvent;
import org.objectweb.proactive.extra.scheduler.job.JobId;
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
	 * Invoked when the scheduler has just been started.
	 */
	public void schedulerStartedEvent();
	
	
	/**
	 * Invoked when the scheduler has just been stopped.
	 */
	public void schedulerStoppedEvent();
	
	
	/**
	 * Invoked when the scheduler has just been paused.
	 * 
	 * @param event the scheduler informations about the status of every tasks.
	 * 		use <code>SchedulerEvent.update(Vector<<Job>>)</code> to update your job.
	 */
	public void schedulerPausedEvent();
	
	
	/**
	 * Invoked when the scheduler has received a paused immediate signal.
	 */
	public void schedulerImmediatePausedEvent();
	
	
	/**
	 * Invoked when the scheduler has just been resumed.
	 */
	public void schedulerResumedEvent();
	
	
	/**
	 * Invoked when the scheduler shutdown sequence is initialised.
	 */
	public void schedulerShuttingDownEvent();
	
	
	/**
	 * Invoked when the scheduler has just been shutdown.
	 * 
	 * @param job the new scheduled job.
	 */
	public void schedulerShutDownEvent();
	
	
	/**
	 * Invoked when the scheduler has just been killed.
	 * Scheduler is not reachable anymore.
	 */
	public void schedulerKilledEvent();
	
	
	/**
	 * Invoked when a job has been killed on the scheduler.
	 * 
	 * @param jobId the job to killed.
	 */
	public void jobKilledEvent (JobId jobId);

	
	/**
	 * Invoked when a job has been paused on the scheduler.
	 * 
	 * @param event the informations on the paused job.
	 */
	public void jobPausedEvent (JobEvent event);
	
	
	/**
	 * Invoked when a job has been resumed on the scheduler.
	 * 
	 * @param event the informations on the resumed job.
	 */
	public void jobResumedEvent (JobEvent event);
	
	
	/**
	 * Invoked when the scheduler has received a new job to schedule.
	 * 
	 * @param job the new scheduled job.
	 */
	public void newPendingJobEvent(Job job);
	
	/**
	 * Invoked when the scheduling of a job has just started.
	 * The description of the job is contained in the jobEvent given.
	 * Use Job.update(JobEvent) to update your job.
	 * 
	 * @param event the event describing the job concerned.
	 */
	public void pendingToRunningJobEvent(JobEvent event);
	
	/**
	 * Invoked when the scheduling of a job has just been terminated.
	 * The description of the job is contained in the jobEvent given.
	 * Use Job.update(JobEvent) to update your job.
	 * 
	 * @param event the event describing the job concerned.
	 */
	public void runningToFinishedJobEvent(JobEvent event);
	
	/**
	 * Invoked when the scheduler has removed a job due to result reclamation.
	 * The description of the job is contained in the jobEvent given.
	 * Use Job.update(JobEvent) to update your job.
	 * 
	 * @param event the event describing the job concerned.
	 */
	public void removeFinishedJobEvent(JobEvent event);
	
	
	/**
	 * Invoked when the scheduling of a task has just started.
	 * The description of the task is contained in the TaskEvent given.
	 * Use Job.update(TaskEvent) to update your job.
	 * 
	 * @param event the event describing the task concerned.
	 */
	public void pendingToRunningTaskEvent(TaskEvent event);
	
	/**
	 * Invoked when the scheduling of a task has just finished.
	 * The description of the task is contained in the TaskEvent given.
	 * Use Job.update(TaskEvent) to update your job.
	 * 
	 * @param event the event describing the task concerned.
	 */
	public void runningToFinishedTaskEvent(TaskEvent event);
	
	
	/**
	 * Invoked when the scheduler has changed the priority of a job.
	 * The description of the job is contained in the jobEvent given.
	 * Use Job.update(JobEvent) to update your job.
	 * 
	 * @param event the event describing the job concerned.
	 */
	public void changeJobPriorityEvent(JobEvent event);
	
}
