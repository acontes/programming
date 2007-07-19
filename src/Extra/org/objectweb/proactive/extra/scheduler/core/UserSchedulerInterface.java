package org.objectweb.proactive.extra.scheduler.core;

import java.io.Serializable;

import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.job.JobResult;
import org.objectweb.proactive.extra.scheduler.job.UserIdentification;

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
	 * Submit a new job to the scheduler with an authentification.
	 * A user can only managed their jobs.
	 * It will execute the tasks of the jobs as soon as resources are available.
	 * The job will be considered as finished once the marked 'final' task has finished,
	 * or when every tasks have finished.
	 * Thus, user could get the job result according to the final task result.
	 * 
	 * @param job the new job to submit.
	 * @param userId the user identification.
	 * @return the generated new job ID.
	 * @throws SchedulerException
	 */
	public JobId submit(Job job, UserIdentification userId) throws SchedulerException;
	

	/**
	 * Get the result for the given jobId.
	 * A user can only get HIS result back.
	 * 
	 * @param jobId the job on witch the result will be send
	 * @param userId the user identification.
	 * @return a job Result containing information about the result.
	 * @throws SchedulerException
	 */
	public JobResult getResult(JobId jobId, UserIdentification userId) throws SchedulerException;
	
	
	/**
	 * Listen for the tasks user log.
	 * A user can only listen to HIS jobs.
	 * 
	 * @param jobId the id of the job to listen to.
	 * @param hostname the hostname where to send the log.
	 * @param port the port number on which the log will be sent.
	 * @param userId the user identification.
	 * @throws SchedulerException.
	 */
	public void listenLog(JobId jobId, String hostname, int port, UserIdentification userId) throws SchedulerException;
	
	
	/**
	 * Add a scheduler event Listener. this listener provides method to notice of
	 * new comming job, started task, finished task, running job, finished job.
	 * 
	 * @param sel a SchedulerEventListener on which the scheduler will talk.
	 * @return the scheduler current state containing the different lists of jobs.
	 */
	public SchedulerState addSchedulerEventListener (SchedulerEventListener sel);
	
}
