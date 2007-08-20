/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
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
	 * ProActive empty constructor
	 */
	public JobId(){}
	
	
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
