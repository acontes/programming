package org.objectweb.proactive.extra.scheduler.core;

import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface;


/**
 * Scheduler interface.
 * This interface represents what the AdminScheduler and the SchedulerFrontend should do.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 29, 2007
 * @since ProActive 3.2
 */
public interface AdminSchedulerInterface extends UserSchedulerInterface {
	

	
	/**
	 * Start the scheduler.
	 * 
	 * @return true if success, false if not.
	 * @throws SchedulerException (can be due to insufficient permission)
	 */
	public boolean start() throws SchedulerException;
	
	
	/**
	 * Stop the scheduler.
	 * 
	 * @return true if success, false if not.
	 * @throws SchedulerException (can be due to insufficient permission)
	 */
	public boolean stop() throws SchedulerException;
	
	
	/**
	 * Pause the scheduler by terminating running jobs.
	 * 
	 * @return true if success, false if not.
	 * @throws SchedulerException (can be due to insufficient permission)
	 */
	public boolean pause() throws SchedulerException;
	
	
	/**
	 * Pause the scheduler by terminating running tasks.
	 * 
	 * @return true if success, false if not.
	 * @throws SchedulerException (can be due to insufficient permission)
	 */
	public boolean pauseImmediate() throws SchedulerException;
	
	
	/**
	 * Resume the scheduler.
	 * 
	 * @return true if success, false if not.
	 * @throws SchedulerException (can be due to insufficient permission)
	 */
	public boolean resume() throws SchedulerException;
	
	
	/**
	 * Shutdown the scheduler.
	 * 
	 * @return true if success, false if not.
	 * @throws SchedulerException (can be due to insufficient permission)
	 */
	public boolean shutdown() throws SchedulerException;
	
	
	/**
	 * kill the scheduler.
	 * 
	 * @return true if success, false if not.
	 * @throws SchedulerException (can be due to insufficient permission)
	 */
	public boolean kill() throws SchedulerException;
}
