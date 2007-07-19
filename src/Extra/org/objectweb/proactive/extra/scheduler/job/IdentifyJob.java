package org.objectweb.proactive.extra.scheduler.job;

import java.io.Serializable;

/**
 * This class represented an authentificate job.
 * It is what the scheduler should be able to managed.
 * 
 * @author ProActive Team
 * @version 1.0, Jul 4, 2007
 * @since ProActive 3.2
 */
public class IdentifyJob implements Serializable {

	/** serial version UID */
	private static final long serialVersionUID = 9100796464303741891L;
	/** Job Identification */
	private JobId jobId;
	/** User identification (null if the job is admin) */
	private UserIdentification userIdentification;
	
	
	/**
	 * Identify job constructor with a given job and Identification.
	 * 
	 * @param jobId a job identification.
	 * @param userIdentification a user identification that should be able to identify the job user.
	 */
	public IdentifyJob(JobId jobId, UserIdentification userIdentification) {
		this.jobId = jobId;
		this.userIdentification = userIdentification;
	}
	

	/**
	 * Identify job constructor with a given job.
	 * It represents an admin job.
	 * 
	 * @param jobId a job description.
	 */
	public IdentifyJob(JobId jobId) {
		this(jobId,null);
	}
	

	/**
	 * To get the jobId
	 * 
	 * @return the jobId
	 */
	public JobId getJobId() {
		return jobId;
	}


	/**
	 * To get the userIdentification
	 * 
	 * @return the userIdentification
	 */
	public UserIdentification getUserIdentification() {
		return userIdentification;
	}
	
	
	/**
	 * Check if the given user identification can managed this job.
	 * 
	 * @param userId the user identification to check.
	 * @return true if userId has permission to managed this job.
	 */
	public boolean hasRight(UserIdentification userId){
		if (userIdentification == null)
			return false;
		return userIdentification.equals(userId);
	}
	
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return jobId.hashCode();
	}


	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IdentifyJob){
			return jobId.equals(((IdentifyJob)obj).jobId);
		}
		return false;
	}
	
}
