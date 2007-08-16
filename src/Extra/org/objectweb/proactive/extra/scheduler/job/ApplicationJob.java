/**
 * 
 */
package org.objectweb.proactive.extra.scheduler.job;

/**
 * Class ApplicationJob.
 * This is the definition of an application job.
 * The nodes use is under user responsability.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 7, 2007
 * @since ProActive 3.2
 */
public class ApplicationJob extends Job {

	/** Serial version UID */
	private static final long serialVersionUID = 5793055959400444968L;


	/**
	 * Create a new Application Job with the given parameters. It provides methods to add or
	 * remove tasks.
	 * 
	 * @param name the current job name.
	 * @param priority the priority of this job between 1 and 5.
	 * @param runtimeLimit the maximum execution time for this job given in millisecond.
	 * @param runUntilCancel true if the job has to run until its end or an user intervention.
	 * @param description a short description of the job and what it will do.
	 */
	public ApplicationJob(String name, JobPriority priority, long runtimeLimit, boolean runUntilCancel, String description) {
		super(name,priority,runtimeLimit,runUntilCancel,description);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.job.Job#getType()
	 */
	@Override
	public JobType getType() {
		return JobType.APPLI;
	}

}
