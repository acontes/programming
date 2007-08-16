package org.objectweb.proactive.extra.scheduler.gui.data;

import org.objectweb.proactive.extra.scheduler.job.JobEvent;
import org.objectweb.proactive.extra.scheduler.job.JobId;

public interface EventJobsListener {

	/**
	 * Invoked when a job has been killed on the scheduler.
	 * 
	 * @param jobId the job to killed.
	 */
	public void killedEvent(JobId jobId);

	/**
	 * Invoked when a job has been paused on the scheduler.
	 * 
	 * @param event the informations on the paused job.
	 */
	public void pausedEvent(JobEvent event);

	/**
	 * Invoked when a job has been resumed on the scheduler.
	 * 
	 * @param event the informations on the resumed job.
	 */
	public void resumedEvent(JobEvent event);
}