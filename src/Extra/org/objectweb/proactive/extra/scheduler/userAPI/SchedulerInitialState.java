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
package org.objectweb.proactive.extra.scheduler.userAPI;

import java.io.Serializable;
import java.util.Vector;
import org.objectweb.proactive.extra.scheduler.job.Job;

/**
 * This class is a representation of the entire scheduler state.
 * It is represented by 3 lists of jobs.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 12, 2007
 * @since ProActive 3.2
 */
public final class SchedulerInitialState implements Serializable {

	/** serial version UID */
	private static final long serialVersionUID = -7448663006621330188L;
	/** pending jobs */
	private Vector<Job> pendingJobs = new Vector<Job>();
	/** running jobs */
	private Vector<Job> runningJobs = new Vector<Job>();
	/** finished jobs */
	private Vector<Job> finishedJobs = new Vector<Job>();
	/** scheduler state */
	private SchedulerState state = SchedulerState.STOPPED;
	
	
	/**
	 * ProActive Empty constructor.
	 */
	public SchedulerInitialState(){}
	
	
	/**
	 * To get the finishedJobs
	 * 
	 * @return the finishedJobs
	 */
	public Vector<Job> getFinishedJobs() {
		return finishedJobs;
	}

	
	/**
	 * To set the finishedJobs
	 * 
	 * @param finishedJobs the finishedJobs to set
	 */
	public void setFinishedJobs(Vector<Job> finishedJobs) {
		this.finishedJobs = finishedJobs;
	}
	

	/**
	 * To get the pendingJobs
	 * 
	 * @return the pendingJobs
	 */
	public Vector<Job> getPendingJobs() {
		return pendingJobs;
	}

	
	/**
	 * To set the pendingJobs
	 * 
	 * @param pendingJobs the pendingJobs to set
	 */
	public void setPendingJobs(Vector<Job> pendingJobs) {
		this.pendingJobs = pendingJobs;
	}
	

	/**
	 * To get the runningJobs
	 * 
	 * @return the runningJobs
	 */
	public Vector<Job> getRunningJobs() {
		return runningJobs;
	}

	
	/**
	 * To set the runningJobs
	 * 
	 * @param runningJobs the runningJobs to set
	 */
	public void setRunningJobs(Vector<Job> runningJobs) {
		this.runningJobs = runningJobs;
	}


	/**
	 * @return the state
	 */
	public SchedulerState getState() {
		return state;
	}


	/**
	 * @param state the state to set
	 */
	public void setState(SchedulerState state) {
		this.state = state;
	}
	
}
