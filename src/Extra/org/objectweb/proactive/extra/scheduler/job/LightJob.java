package org.objectweb.proactive.extra.scheduler.job;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.objectweb.proactive.extra.scheduler.task.EligibleLightTask;
import org.objectweb.proactive.extra.scheduler.task.Status;
import org.objectweb.proactive.extra.scheduler.task.TaskDescriptor;
import org.objectweb.proactive.extra.scheduler.task.TaskId;


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
	private HashMap<TaskId,EligibleLightTask> eligibleTasks = new HashMap<TaskId,EligibleLightTask>();
	/** Job running tasks */
	private HashMap<TaskId,LightTask> runningTasks = new HashMap<TaskId,LightTask>();
	/** Job paused tasks */
	private HashMap<TaskId,LightTask> pausedTasks = new HashMap<TaskId,LightTask>();
	//TODO penser à mettre ici un champ pour connaitre le nb de tache total du job.
	
	/**
	 * Constructor of light job.
	 * Just make a mapping between some fields of the two type of job.
	 * 
	 * @param job the entire job to be lighted.
	 */
	public LightJob(Job job){
		id = job.getId();
		priority = job.getPriority();
		type = job.getType();
		if (type == JobType.TASKSFLOW) {
			//build dependence tree
			makeTree(job);
		} else {
			//every tasks are eligible
			for (TaskDescriptor td : job.getTasks()){
				if (td.getStatus() == Status.SUBMITTED){
					eligibleTasks.put(td.getId(),new EligibleLightTask(td));
				}
			}
		}
	}
	
	
	/**
	 * Make a dependences tree of the job's tasks according to the dependence list
	 * stored in taskDescriptor.
	 * This list represents the ordered TaskDescriptor list of its parent tasks.
	 */
	private void makeTree(Job job){
		HashMap<TaskDescriptor,LightTask> mem = new HashMap<TaskDescriptor, LightTask>();
		//create lightTask list
		for (TaskDescriptor td : job.getTasks()){
			//if this task is a first task, put it in eligible tasks list
			EligibleLightTask lt = new EligibleLightTask(td);
			if (!td.hasDependences()){
				eligibleTasks.put(td.getId(),lt);
			} 
			mem.put(td,lt);
		}
		//now for each taskDescriptor, set the parents and children list
		for (TaskDescriptor td : job.getTasks()){
			if (td.getDependences() != null){
				LightTask lightTask = mem.get(td);
				for(TaskDescriptor depends : td.getDependences()){
					lightTask.addParent(mem.get(depends));
				}
				lightTask.setCount(td.getDependences().size());
				for (LightTask lt : lightTask.getParents()){
					lt.addChild(lightTask);
				}
			}
		}
	}
	
	
	/**
	 * Delete this task from eligible task view and add it to running view.
	 * Visibility is package because user cannot use this method.
	 * 
	 * @param taskId the task that has just been started.
	 */
	void start(TaskId taskId){
		runningTasks.put(taskId,eligibleTasks.get(taskId));
		eligibleTasks.remove(taskId);
	}
	
	/**
	 * Update the eligible list of task and dependencies if necessary.
	 * This function considered that the taskId is in eligible task list.
	 * Visibility is package because user cannot use this method.
	 * 
	 * @param taskId the task to remove from running task.
	 */
	void terminate(TaskId taskId){
		if (type == JobType.TASKSFLOW){
			LightTask lt = runningTasks.get(taskId);
			for (LightTask task : lt.getChildren()){
				task.setCount(task.getCount()-1);
				if (task.getCount() == 0){
					eligibleTasks.put(task.getId(),(EligibleLightTask)task);
				}
			}
		}
		runningTasks.remove(taskId);
	}
	
	
	/**
	 * Update the list of eligible tasks according to the status of each task.
	 * This method is called only if user pause a job.
	 * 
	 * @param status the taskId with their current status.
	 */
	void update(HashMap<TaskId, Status> status){
		for (Entry<TaskId,Status> tid : status.entrySet()){
			if (tid.getValue() == Status.PAUSED_P || tid.getValue() == Status.PAUSED_S){
				LightTask lt = eligibleTasks.get(tid.getKey());
				if (lt != null){
					pausedTasks.put(tid.getKey(),eligibleTasks.get(tid.getKey()));
					eligibleTasks.remove(tid.getKey());
				}
			} else if (tid.getValue() == Status.PENDING || tid.getValue() == Status.SUBMITTED){
				EligibleLightTask lt = (EligibleLightTask)pausedTasks.get(tid.getKey());
				if (lt != null){
					eligibleTasks.put(tid.getKey(),lt);
					pausedTasks.remove(tid.getKey());
				}
			}
		}
	}
	
	
	/**
	 * Set the priority of this light Job.
	 * 
	 * @param priority the new priority.
	 */
	void setPriority(JobPriority priority) {
		this.priority = priority;
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
	public Collection<EligibleLightTask> getEligibleTasks() {
		return eligibleTasks.values();
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
