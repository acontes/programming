/**
 * 
 */
package org.objectweb.proactive.extra.scheduler.core;

import java.io.Serializable;
import java.util.HashMap;

import org.objectweb.proactive.extra.scheduler.job.JobType;

/**
 * Stats class will be used to view some tips on the scheduler.
 * 
 * @author ProActive Team
 * @version 1.0, Jul 25, 2007
 * @since ProActive 3.2
 */
public class Stats implements Serializable {

	/** Serial version UID */
	private static final long serialVersionUID = 1986632393105294431L;
	/** Map of properties of the scheduler */
	private HashMap<String,Object> properties = new HashMap<String, Object>();
	
	
	/**
	 * ProActive Empty constructor
	 */
	public Stats(){}
	
	
	/**
	 * Set the start time of the scheduler.
	 */
	public void startTime(){
		String key = "Start Time";
		if (!properties.containsKey(key))
			properties.put(key, Tools.getFormattedDate(System.currentTimeMillis()));
	}
	
	/**
	 * Set the last stopped time of the scheduler.
	 */
	public void stopTime(){
		String key = "Last Stop Time";
		if (!properties.containsKey(key))
			properties.put(key, Tools.getFormattedDate(System.currentTimeMillis()));
	}
	
	
	/**
	 * Set the last paused time of the scheduler.
	 */
	public void pauseTime(){
		String key = "Last Pause Time";
		if (!properties.containsKey(key))
			properties.put(key, Tools.getFormattedDate(System.currentTimeMillis()));
	}
	
	
	/**
	 * Increase the number of submitted jobs.
	 * 
	 * @param type the job type of the submitted job.
	 */
	public void increaseSubmittedJobCount(JobType type){
		increaseProperty("Jobs Submitted",1);
		switch(type){
			case PARAMETER_SWIPPING : increasePSJobCount(); break;
			case APPLI : increasePAJobCount(); break;
			case TASKSFLOW : increaseTFJobCount(); break;
		}
	}
	
	/**
	 * Increase the number of finished jobs.
	 * 
	 * @param nbTasks the number of finished tasks for the job.
	 */
	public void increaseFinishedJobCount(int nbTasks){
		increaseProperty("Jobs finished",1);
		increaseTaskCount(nbTasks);
	}
	
	/**
	 * Increase the number of finished tasks.
	 * 
	 * @param inc the number of finished tasks
	 */
	private void increaseTaskCount(int inc){
		increaseProperty("Tasks finished",inc);
	}
	
	/**
	 * Increase the number of launched Appli jobs.
	 */
	private void increasePAJobCount(){
		increaseProperty("Appli jobs Submitted",1);
	}
	
	/**
	 * Increase the number of launched ParameterSwipping jobs.
	 */
	private void increasePSJobCount(){
		increaseProperty("ParameterSwipping jobs Submitted",1);
	}
	
	/**
	 * Increase the number of launched TaskFlow jobs.
	 */
	private void increaseTFJobCount(){
		increaseProperty("TaskFlow jobs Submitted",1);
	}
	
	/**
	 * Increase the count corresponding to the property name.
	 */
	private void increaseProperty(String propertyName, int inc){
		if (!properties.containsKey(propertyName))
			properties.put(propertyName, new Integer(0));
		else
			properties.put(propertyName, ((Integer)properties.get(propertyName))+inc);
	}

	/**
	 * To get the properties.
	 * 
	 * @return the properties
	 */
	public HashMap<String, Object> getProperties() {
		return properties;
	}
}
