package org.objectweb.proactive.extra.scheduler.job;

import java.io.Serializable;
import java.util.HashMap;
import org.objectweb.proactive.extra.scheduler.task.TaskResult;


/**
 * 
 * 
 * @author ProActive Team
 * @version 1.0, Jul 5, 2007
 * @since ProActive 3.2
 */
public class JobResult implements Serializable {

	/** Serial version UID */
	private static final long serialVersionUID = 6287355063616273677L;
	private JobId id = null;
	private HashMap<String,TaskResult> taskResults = null;

    
    /**
     * ProActive empty constructor
     * 
     */
    public JobResult() {}

    
    /**
     * Instanciate a new JobResult with a jobId and a result
     * 
     * @param id the jobId associated with this result
     * @param result the result associated with this result
     * @param 
     */
    public JobResult(JobId id){
    	this.id = id;
    }
    
	/**
	 * To get the id
	 * 
	 * @return the id
	 */
	public JobId getId() {
		return id;
	}
	
	
	/**
	 * Add a new task result to this job result.
	 * 
	 * @param taskName user define name (in XML) of the task.
	 * @param taskResult the corresponding result of the task.
	 */
	public void addTaskResult(String taskName, TaskResult taskResult){
		if (taskResults == null)
			taskResults = new HashMap<String, TaskResult>();
		taskResults.put(taskName,taskResult);
	}
	
	
	/**
	 * Return the task results of this job as a mapping between
	 * user task name (in XML jo description) and its task result.
	 * User that wants to get a specific result may get this map and ask for a specific mapping.
	 * 
	 * @return the task result as a map.
	 */
	public HashMap<String,TaskResult> getTaskResults(){
		return taskResults;
	}
	
}
