/**
 * 
 */
package org.objectweb.proactive.extra.scheduler.userAPI;

/**
 * State of the scheduler.
 * The state and what you can do with the scheduler according to the current state
 * are best described below.
 * 
 * @author ProActive Team
 * @version 1.0, Jul 26, 2007
 * @since ProActive 3.2
 */
public enum State implements java.io.Serializable {
	
	/**
	 * The scheduler is running. Jobs can be submitted.
	 * Get the jobs results is possible.
	 * It can be paused, stopped or shutdown.
	 */
	STARTED ("Started"),
	/**
	 * The scheduler is stopped. Jobs cannot be sumitted anymore.
	 * It will terminate every submitted jobs.
	 * Get the jobs results is possible.
	 * It can be started or shutdown.
	 */
	STOPPED ("Stopped"),
	/**
	 * The scheduler is in immediate pause.
	 * It means that every running tasks will be terminated,
	 * but the running jobs will wait for the scheduler to resume.
	 * It can be resumed, stopped, paused or shutdown.
	 */
	PAUSED_IMMEDIATE ("Paused"),
	/**
	 * The scheduler is paused.
	 * It means that every running jobs will be terminated.
	 * It can be resumed, stopped, paused immediate or shutdown.
	 */
	PAUSED ("Paused"),
	/**
	 * The scheduler is shutting down,
	 * It will terminate all running jobs (during this time, get jobs results is possible),
	 * then it will serialize every remaining jobs results that still are in the finished queue.
	 * Finally, it will shutdown the scheduler.
	 */
	SHUTTING_DOWN ("Shutting down"),
	/**
	 * The scheduler has been killed, nothing can be done anymore.
	 * (Similar to Ctrl-C)
	 */
	KILLED ("Killed");
	
	private String definition;
	
	State (String def){
		definition = def;
	}
	
	public String getDefinition(){
		return definition;
	}
	
}