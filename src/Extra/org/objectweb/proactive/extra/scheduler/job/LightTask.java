package org.objectweb.proactive.extra.scheduler.job;

import java.io.Serializable;
import java.util.Vector;

import org.objectweb.proactive.extra.scheduler.task.TaskDescriptor;
import org.objectweb.proactive.extra.scheduler.task.TaskId;

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
	/** number of parents remaining (initial value must be 0) */
	private int count = 0;
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
		this.id = td.getId();
		this.jobId = td.getJobId();
	}


	/**
	 * To get the children
	 * 
	 * @return the children
	 */
	public Vector<LightTask> getChildren() {
		if (children == null) return new Vector<LightTask>();
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
		if (parents == null) return new Vector<LightTask>();
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
	 * Return the number of parents remaining
	 * 
	 * @return the number of parents remaining.
	 */
	int getCount() {
		return count;
	}


	/**
	 * Set the number of parents remaining.
	 * 
	 * @param count the number of parents remaining.
	 */
	void setCount(int count) {
		this.count = count;
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
