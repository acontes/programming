package org.objectweb.proactive.extra.scheduler.job;

import java.io.Serializable;

/**
 * Definition of a job identification.
 * For the moment, it is represented by an integer.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 29, 2007
 * @since ProActive 3.2
 */
public final class JobId implements Comparable<JobId>, Serializable, Cloneable {
	
	/** Serial version UID */
	private static final long serialVersionUID = -7367447876595953374L;
	private int id = 0;

	
	/**
	 * Default Job id constructor
	 * 
	 * @param id the id to put in the jobId
	 */
	public JobId(int id){
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
	 * To set the id
	 * 
	 * @param id the id to set
	 */
	public void setValue(int id) {
		this.id = id;
	}
	
	
	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public JobId clone() {
		return new JobId(this.id);
	}


	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(JobId o) {
		return new Integer(id).compareTo(new Integer(o.id));
	}
	
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o){
		if (o instanceof JobId)
			return ((JobId)o).id == id;
		return false;
	}
	
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.id;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ""+id;
	}
	
}
