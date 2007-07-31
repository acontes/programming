package org.objectweb.proactive.extra.scheduler.core;


/**
 * Scheduler interface.
 * This interface represents what the AdminScheduler and the SchedulerFrontend should do.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 29, 2007
 * @since ProActive 3.2
 */
public interface AdminSchedulerInterface {
	

	
	/**
	 * Start the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public boolean start();
	
	
	/**
	 * Stop the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public boolean stop();
	
	
	/**
	 * Pause the scheduler by terminating running jobs.
	 * 
	 * @return true if success, false otherwise.
	 */
	public boolean pause();
	
	
	/**
	 * Pause the scheduler by terminating running tasks.
	 * 
	 * @return true if success, false otherwise.
	 */
	public boolean pauseImmediate();
	
	
	/**
	 * Resume the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public boolean resume();
	
	
	/**
	 * Shutdown the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public boolean shutdown();
	
	
	/**
	 * kill the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public boolean kill();
}
