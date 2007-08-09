package org.objectweb.proactive.extra.scheduler.userAPI;

import java.io.Serializable;
import org.objectweb.proactive.extra.scheduler.core.Stats;
import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.job.JobPriority;
import org.objectweb.proactive.extra.scheduler.job.JobResult;

/**
 * Scheduler user interface.
 * This interface provides methods to managed the user task and jobs on the scheduler.
 * A user will only be able to managed his jobs and tasks, and also see the entire scheduling process.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 7, 2007
 * @since ProActive 3.2
 */
public interface UserSchedulerInterface extends Serializable{
	

	/**
	 * Submit a new job to the scheduler.
	 * A user can only managed their jobs.
	 * It will execute the tasks of the jobs as soon as resources are available.
	 * The job will be considered as finished once the marked 'final' task has finished,
	 * or when every tasks have finished.
	 * Thus, user could get the job result according to the final task result.
	 * 
	 * @param job the new job to submit.
	 * @return the generated new job ID.
	 * @throws SchedulerException
	 */
	public JobId submit(Job job) throws SchedulerException;
	

	/**
	 * Get the result for the given jobId.
	 * A user can only get HIS result back.
	 * 
	 * @param jobId the job on witch the result will be send
	 * @return a job Result containing information about the result.
	 * @throws SchedulerException
	 */
	public JobResult getResult(JobId jobId) throws SchedulerException;
	
	
	/**
	 * Listen for the tasks user log.
	 * A user can only listen to HIS jobs.
	 * 
	 * @param jobId the id of the job to listen to.
	 * @param hostname the hostname where to send the log.
	 * @param port the port number on which the log will be sent.
	 * @throws SchedulerException.
	 */
	public void listenLog(JobId jobId, String hostname, int port) throws SchedulerException;
	
	
	/**
	 * Add a scheduler event Listener. this listener provides method to notice of
	 * new comming job, started task, finished task, running job, finished job.
	 * 
	 * @param sel a SchedulerEventListener on which the scheduler will talk.
	 * @return the scheduler current state containing the different lists of jobs.
	 * @throws SchedulerException
	 */
	public SchedulerState addSchedulerEventListener (SchedulerEventListener sel) throws SchedulerException;
	
	
	/**
	 * Return the scheduler statistics as an hashMap.
	 * 
	 * @return the scheduler statistics as an hashMap.
	 * @throws SchedulerException
	 */
	public Stats getStats() throws SchedulerException;
	
	
	/**
	 * Disconnect properly the user from the scheduler.
	 * 
	 * @throws SchedulerException.
	 */
	public void disconnect() throws SchedulerException;
	
	
	/**
	 * kill the job represented by jobId.
	 * This method will kill every running tasks of this job, and remove it from the scheduler.
	 * The job won't be terminated, it won't have result.
	 * 
	 * @param jobId the job to kill.
	 * @return true if success, false if not.
	 * @throws SchedulerException (can be due to insufficient permission)
	 */
	public boolean kill (JobId jobId) throws SchedulerException;
	
	
	/**
	 * Pause the job represented by jobId.
	 * This method will finish every running tasks of this job, and then pause the job.
	 * The job will have to be resumed in order to finish.
	 * 
	 * @param jobId the job to pause.
	 * @return true if success, false if not.
	 * @throws SchedulerException (can be due to insufficient permission)
	 */
	public boolean pause (JobId jobId) throws SchedulerException;
	
	
	/**
	 * Resume the job represented by jobId.
	 * This method will restart every non-finished tasks of this job.
	 * 
	 * @param jobId the job to resume.
	 * @return true if success, false if not.
	 * @throws SchedulerException (can be due to insufficient permission)
	 */
	public boolean resume (JobId jobId) throws SchedulerException;
	
	
	/**
	 * Change the priority of the job represented by jobId.
	 * 
	 * @param jobId the job on whitch to change the priority.
	 * @throws SchedulerException (can be due to insufficient permission)
	 */
	public void changePriority (JobId jobId, JobPriority priority) throws SchedulerException;
}
