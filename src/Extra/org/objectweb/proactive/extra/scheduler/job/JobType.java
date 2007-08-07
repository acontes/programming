package org.objectweb.proactive.extra.scheduler.job;

/**
 * Type of the job.
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
	APPLI ("Job Application"),
	/**
	 * Tasks can be executed one by one or all in same time but
	 * every task represents the same native or java task.
	 * Only the parameters given to the task will change. 
	 */
	PARAMETER_SWIPPING ("Parameter Swipping"),
	/** 
	 * Tasks flow with dependences.
	 * Only the task that have their dependences finished
	 * can be executed.
	 */
	TASKSFLOW ("Tasks Flow");
	
	private String name;
	
	
	JobType (String name) {
		this.name = name;
	}
	
	
	@Override
    public String toString(){
    	return name;
    }
	
}
