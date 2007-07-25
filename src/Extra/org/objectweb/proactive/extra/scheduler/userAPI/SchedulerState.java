package org.objectweb.proactive.extra.scheduler.userAPI;

import java.io.Serializable;
import java.util.Vector;
import org.objectweb.proactive.extra.scheduler.job.Job;

/**
 * This class is a representation of the entire scheduler state.
 * It is represented by 3 lists of jobs.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 12, 2007
 * @since ProActive 3.2
 */
public final class SchedulerState implements Serializable {

	/** serial version UID */
	private static final long serialVersionUID = -7448663006621330188L;
	/** pending jobs */
	private Vector<Job> pendingJobs = new Vector<Job>();
	/** running jobs */
	private Vector<Job> runningJobs = new Vector<Job>();
	/** finished jobs */
	private Vector<Job> finishedJobs = new Vector<Job>();
	
	
	/**
	 * To get the finishedJobs
	 * 
	 * @return the finishedJobs
	 */
	public Vector<Job> getFinishedJobs() {
		return finishedJobs;
	}

	
	/**
	 * To set the finishedJobs
	 * 
	 * @param finishedJobs the finishedJobs to set
	 */
	public void setFinishedJobs(Vector<Job> finishedJobs) {
		this.finishedJobs = finishedJobs;
	}
	

	/**
	 * To get the pendingJobs
	 * 
	 * @return the pendingJobs
	 */
	public Vector<Job> getPendingJobs() {
		return pendingJobs;
	}

	
	/**
	 * To set the pendingJobs
	 * 
	 * @param pendingJobs the pendingJobs to set
	 */
	public void setPendingJobs(Vector<Job> pendingJobs) {
		this.pendingJobs = pendingJobs;
	}
	

	/**
	 * To get the runningJobs
	 * 
	 * @return the runningJobs
	 */
	public Vector<Job> getRunningJobs() {
		return runningJobs;
	}

	
	/**
	 * To set the runningJobs
	 * 
	 * @param runningJobs the runningJobs to set
	 */
	public void setRunningJobs(Vector<Job> runningJobs) {
		this.runningJobs = runningJobs;
	}
	
}
