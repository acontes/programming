/**
 * 
 */
package org.objectweb.proactive.extra.scheduler.core;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.job.JobResult;

/**
 * Scheduler user interface.
 * This class provides method to managed jobs for a user.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 12, 2007
 * @since ProActive 3.2
 */
public class UserScheduler implements UserSchedulerInterface {
	
	/** serial version UID */
	private static final long serialVersionUID = 3319322779771815630L;
	/** Scheduler logger */
	public static Logger logger = ProActiveLogger.getLogger(Loggers.SCHEDULER);
	/** scheduler proxy as an active object */
	protected SchedulerFrontend schedulerFrontend;
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#getResult(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	public JobResult getResult(JobId jobId) throws SchedulerException {
		return schedulerFrontend.getResult(jobId);
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#submit(org.objectweb.proactive.extra.scheduler.job.Job)
	 */
	public JobId submit(Job job) throws SchedulerException {
		return schedulerFrontend.submit(job);
	}
	

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#listenLog(org.objectweb.proactive.extra.scheduler.job.JobId, java.lang.String, int)
	 */
	public void listenLog(JobId jobId, String hostname, int port) throws SchedulerException {
		schedulerFrontend.listenLog(jobId,hostname,port);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#addSchedulerEventListener(org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener)
	 */
	public SchedulerState addSchedulerEventListener (SchedulerEventListener sel) throws SchedulerException {
		return schedulerFrontend.addSchedulerEventListener(sel);
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#disconnect()
	 */
	public void disconnect() throws SchedulerException {
		schedulerFrontend.disconnect();
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#pause(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	public boolean pause(JobId jobId) {
		return schedulerFrontend.pause(jobId);
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#resume(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	public boolean resume(JobId jobId) {
		return schedulerFrontend.resume(jobId);
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#stop(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	public boolean stop(JobId jobId) {
		return schedulerFrontend.stop(jobId);
	}
	

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#getStats()
	 */
	public Stats getStats() throws SchedulerException {
		return schedulerFrontend.getStats();
	}
	
}
