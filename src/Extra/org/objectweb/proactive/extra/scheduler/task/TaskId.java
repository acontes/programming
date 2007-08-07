package org.objectweb.proactive.extra.scheduler.task;

import java.io.Serializable;


/**
 * Definition of a task identification.
 * For the moment, it is represented by an integer.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 29, 2007
 * @since ProActive 3.2
 */
public final class TaskId implements Comparable<TaskId>, Serializable {
	
	/** Serial version UID */
	private static final long serialVersionUID = -7367447876595953374L;
	/** task id */
	private int id = 0;

	
	/**
	 * Default constructor. Just set the id of the task.
	 * 
	 * @param id the task id to set.
	 */
	public TaskId(int id){
		this.id = id;
	}
	
	/**
	 * To get the id
	 * 
	 * @return the id
	 */
	public int value() {
		return id;
	}
	
	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(TaskId o) {
		return new Integer(id).compareTo(new Integer(o.id));
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o){
		if (o instanceof TaskId)
			return ((TaskId)o).id == id;
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return id;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ""+id;
	}
	
}
