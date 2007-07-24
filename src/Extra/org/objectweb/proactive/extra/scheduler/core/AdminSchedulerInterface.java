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
	 * Pause the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public boolean pause();
	
	
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
}
