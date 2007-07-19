package org.objectweb.proactive.extra.scheduler.task;

import java.io.Serializable;
import java.util.Vector;

import org.objectweb.proactive.extra.scheduler.job.JobId;

/**
 * This class represents a task for the policy.
 * The internal scheduler tasks are not sent to the policy.
 * Only a restricted number of properties on each tasks is sent to the policy.
 * 
 * @author ProActive Team
 * @version 1.0, Jul 9, 2007
 * @since ProActive 3.2
 */
public class LightTask implements Serializable {

	/** Serial version UID */
	private static final long serialVersionUID = -3597161883966090934L;
	/** Task id */
	private TaskId id;
	/** job id */
	private JobId jobId;
	/** list of parent tasks for this task (null if jobType!=TASK_FLOW) */
	private Vector<LightTask> parents;
	/** list of ordered children tasks for this task (null if jobType!=TASK_FLOW) */
	private Vector<LightTask> children;
	
	
	/**
	 * Get a new light task using a taskDescriptor.
	 * 
	 * @param td the taskDescriptor to shrink.
	 */
	public LightTask(TaskDescriptor td) {
		this.id = td.getId().clone();
		this.jobId = td.getJobId().clone();
	}
	
	/**
	 * Get a new light task using a taskId.
	 * 
	 * @param tid the id of a light task.
	 */
	public LightTask(TaskId tid) {
		this.id = tid;
	}


	/**
	 * To get the children
	 * 
	 * @return the children
	 */
	public Vector<LightTask> getChildren() {
		return children;
	}


	/**
	 * To get the id
	 * 
	 * @return the id
	 */
	public TaskId getId() {
		return id;
	}


	/**
	 * To get the parents
	 * 
	 * @return the parents
	 */
	public Vector<LightTask> getParents() {
		return parents;
	}


	/**
	 * To get the jobId
	 * 
	 * @return the jobId
	 */
	public JobId getJobId() {
		return jobId;
	}
	
	
	/**
	 * Add a parent to the list of parents dependence.
	 * 
	 * @param task the parent task to add.
	 */
	public void addParent(LightTask task){
		if (parents == null)
			parents = new Vector<LightTask>();
		parents.add(task);
	}
	
	
	/**
	 * Add a child to the list of children dependence.
	 * 
	 * @param task the child task to add.
	 */
	public void addChild(LightTask task){
		if (children == null)
			children = new Vector<LightTask>();
		children.add(task);
	}
	
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LightTask)
			return ((LightTask)obj).id.equals(id);
		return false;
	}

	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LightTask("+getId()+")";
	}
	
}
