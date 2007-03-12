/*
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2006 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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


package org.objectweb.proactive.taskscheduler.policy;

import java.util.Vector;

import org.objectweb.proactive.taskscheduler.InternalTask;

/**
 * Must be implemented in order to be used as a policy in the scheduler
 *  
 *
 * @author walzouab
 *
 */

public interface GenericPolicy {
	
	
	/**
	 * //used for loggging, only implement if absouletely needed
	 * @param jobID
	 */
	
	public void finished(InternalTask task);

	

	/**
	 * gets the next available task and removes it from policy 
	 * @param maxumum tasks reqiured
	 * @return next task and null if not available
	 */
	public InternalTask getNextTask();

	/**
	 * 
	 * @return number of queued tasks
	 */
	public int getQueuedTasksNb();
	/**
	 * Inserts a set of  new tasks into the policy
	 * @param task to be inserted
	 */
	public void insert(Vector<InternalTask> t) ;
	
	/**
	 * inserts into the policy a task that started executing, but failed because of the node
	 * </br><b>It is up to the policy to decide what to do with failed tasks</b>
	 * @param t
	 */
	public void failed(InternalTask t);
	
	/**
	 * Deletes all tasks in the policy
	 * <b>Warning, make sure that failed tasks are flushed too</b>
	 *
	 */
	public void flush();
	
	/**
	 * returns the task with the specified ID
	 * <b>Warning, this function doesnt remo
	 * @param TaskID
	 * @return returns the task and null if it doesnt exist in queue 
	 */
	public InternalTask getTask(String TaskID);
	
	
	/**
	 * gets the task at the specified index and removes it 
	 * @param TaskID
	 * @return the ttask required and null if not available
	 */
	public InternalTask removeTask(String TaskID);
	
	/**
	 * returns a vector containing all queued taskID
	 * @return
	 */
	public Vector<String> getQueuedID();
	/**
	 * returns a vector containing all failed taskID
	 * @return
	 */
	public Vector<String> getFailedID();

}
