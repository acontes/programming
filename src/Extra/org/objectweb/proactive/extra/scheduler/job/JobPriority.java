package org.objectweb.proactive.extra.scheduler.job;

/**
 * 
 * 
 * @author ProActive Team
 * @version 1.0, Jun 11, 2007
 * @since ProActive 3.2
 */
public enum JobPriority implements java.io.Serializable {
	
	/**  */
	LOWEST ("Lowest",1),
	/**  */
	BELOW_NORMAL ("Below Normal",2),
	/**  */
	NORMAL ("Normal",3),
	/**  */
	ABOVE_NORMAL ("Above Normal",4),
	/**  */
	HIGHEST ("Highest",5);
	
	private String name;
	private int priority;
	
	JobPriority (String name, int priority){
		this.name = name;
		this.priority = priority;
	}
	
	public String toString(){
		return name;
	}
	
	public int getPriority(){
		return priority;
	}
	
}
