package org.objectweb.proactive.extra.scheduler.job;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;
import org.objectweb.proactive.extra.scheduler.task.LightTask;
import org.objectweb.proactive.extra.scheduler.task.Status;
import org.objectweb.proactive.extra.scheduler.task.TaskDescriptor;


/**
 * This class represents a job for the policy.
 * The internal scheduler job is not sent to the policy.
 * Only a restricted number of properties on each jobs is sent to the policy.
 * 
 * @author ProActive Team
 * @version 1.0, Jul 6, 2007
 * @since ProActive 3.2
 */
public class LightJob implements Serializable, Comparable<LightJob> {
	
	/** Serial version UID */
	private static final long serialVersionUID = -2183608268194326422L;
	/** Job id */
	private JobId id;
	/** Job priority */
	private JobPriority priority;
	/** Job type */
	private JobType type;
	/** Job tasks to be able to be schedule */
	private Vector<LightTask> eligibleTasks = new Vector<LightTask>(); 
	
	
	private LightJob(){}
	
	
	/**
	 * Constructor of light job.
	 * Just make a mapping between some fields of the two type of job.
	 * 
	 * @param job the entire job to be lighted.
	 */
	public LightJob(Job job){
		id = job.getId().clone();
		priority = job.getPriority();
		type = job.getType();
		if (type == JobType.TASKSFLOW) {
			//build dependence tree
			makeTree(job);
		} else { // type is not TASKSFLOW
			//every tasks are eligible
			for (TaskDescriptor td : job.getTasks()){
				if (td.getStatus() == Status.SUBMITTED || td.getStatus() == Status.PENDING){
					eligibleTasks.add(new LightTask(td));
				}
			}
		}
	}
	
	
	/**
	 * Make a dependences tree of the job's tasks according to the dependence list
	 * stored in taskDescriptor.
	 * This list represents the ordered TaskDescriptor list of its parent tasks.
	 * 
	 */
	private void makeTree(Job job){
		HashMap<TaskDescriptor,LightTask> mem = new HashMap<TaskDescriptor, LightTask>();
		//create lightTask list
		for (TaskDescriptor td : job.getTasks()){
			mem.put(td,new LightTask(td));
			//if this task is a first task, put it in eligible tasks list
			if (!td.hasDependences())
				eligibleTasks.add(mem.get(td));
		}
		//now for each taskDescriptor, set the parents list
		for (TaskDescriptor td : job.getTasks()){
			for(TaskDescriptor depends : td.getDependences()){
				mem.get(td).addParent(mem.get(depends));
			}
		}
		//TODO make child dependence
	}

	
	/**
	 * Clone this job.
	 * Every modifications on the returned job won't have effect on the scheduler state.
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings("unchecked")
	public LightJob clone(){
		LightJob lj = new LightJob();
		lj.id = id.clone();
		lj.priority = priority;
		lj.type = type;
		lj.eligibleTasks = (Vector<LightTask>)eligibleTasks.clone();
		return lj;
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
	 * To get the priority
	 * 
	 * @return the priority
	 */
	public JobPriority getPriority() {
		return priority;
	}

	/**
	 * To get the tasks
	 * 
	 * @return the tasks
	 */
	public Vector<LightTask> getEligibleTasks() {
		return eligibleTasks;
	}

	/**
	 * To get the type
	 * 
	 * @return the type
	 */
	public JobType getType() {
		return type;
	}


	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(LightJob o) {
		return o.priority.compareTo(priority);
	}
	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LightJob("+getId()+")";
	}
	
}
