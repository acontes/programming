package org.objectweb.proactive.extra.scheduler.userAPI;

/**
 * State of a job.
 * The differents job states are described below.
 * 
 * @author ProActive Team
 * @version 1.0, Aug 10, 2007
 * @since ProActive 3.2
 */
public enum JobState implements java.io.Serializable {
	
	/**
	 * The job is waiting to be scheduled.
	 */
	PENDING ("Pending"),
	/**
	 * The job is running. Actually at least one of its task has been scheduled.
	 */
	RUNNING ("Running"),
	/**
	 * The job has been launched but no task are currently running.
	 */
	STALLED ("Stalled"),
	/**
	 * The job is Rerunning, it means that one of its task has been rerunned.
	 */
	RERUNNING ("ReRunning"),
	/**
	 * The job is finished. Every tasks are finished.
	 */
	FINISHED ("Finished"),
	/**
	 * The job is paused waiting for user to resume it.
	 */
	PAUSED ("Paused"),
	/**
	 * The job has failed. One or more tasks have failed.
	 * There is no more rerun left for a task.
	 */
	FAILED ("Failed");
	
	
	/** The textual definition of the state */
	private String definition;
	
	/**
	 * Default constructor.
	 * @param def the textual definition of the state.
	 */
	JobState (String def){
		definition = def;
	}
	
	/**
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString(){
		return definition;
	}
	
}