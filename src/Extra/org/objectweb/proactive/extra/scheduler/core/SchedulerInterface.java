package org.objectweb.proactive.extra.scheduler.core;

import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.job.JobResult;

/**
 * Scheduler interface.
 * This interface represents what the AdminScheduler and the SchedulerFrontend should do.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 29, 2007
 * @since ProActive 3.2
 */
public interface SchedulerInterface {
	
	
	/**
	 * Submit a new job to the scheduler.
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
	 * 
	 * @param jobId the job on witch the result will be send
	 * @return a job Result containing information about the result.
	 * @throws SchedulerException
	 */
	public JobResult getResult(JobId jobId) throws SchedulerException;
	
	
	/**
	 * Listen for the tasks user log.
	 * 
	 * @param jobId the id of the job to listen to.
	 * @param hostname the hostname where to send the log.
	 * @param port the port number on which the log will be sent.
	 */
	public void listenLog(JobId jobId, String hostname, int port);
	
}
