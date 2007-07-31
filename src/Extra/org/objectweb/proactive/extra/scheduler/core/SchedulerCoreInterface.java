package org.objectweb.proactive.extra.scheduler.core;

import java.io.Serializable;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;

/**
 * Scheduler Core interface.
 * This interface represents what the schedulerCore should do.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 29, 2007
 * @since ProActive 3.2
 */
public interface SchedulerCoreInterface extends Serializable {
	
	/**
	 * Start the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public BooleanWrapper coreStart();
	
	
	/**
	 * Stop the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public BooleanWrapper coreStop();
	
	
	/**
	 * Pause the scheduler by terminating running jobs.
	 * 
	 * @return true if success, false otherwise.
	 */
	public BooleanWrapper corePause();
	
	
	/**
	 * Pause the scheduler by terminating running tasks.
	 * 
	 * @return true if success, false otherwise.
	 */
	public BooleanWrapper coreImmediatePause();
	
	
	/**
	 * Resume the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public BooleanWrapper coreResume();
	
	
	/**
	 * Shutdown the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public BooleanWrapper coreShutdown();
	
	
	/**
	 * Kill the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public BooleanWrapper coreKill();

}