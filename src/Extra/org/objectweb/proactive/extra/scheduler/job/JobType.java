package org.objectweb.proactive.extra.scheduler.job;

/**
 * 
 * 
 * @author ProActive Team
 * @version 1.0, Jun 11, 2007
 * @since ProActive 3.2
 */
public enum JobType implements java.io.Serializable {
	
	/** 
	 * Every tasks can communicate with each others.
	 * So they all have to be executed in same time.
	 */
	APPLI,
	/**
	 * Tasks can be executed one by one or all in same time but
	 * every task represents the same native or java task.
	 * Only the parameters given to the task will change. 
	 */
	PARAMETER_SWIPPING,
	/** 
	 * Tasks flow with dependences.
	 * Only the task that have their dependences finished
	 * can be executed.
	 */
	TASKSFLOW
	
	
}
