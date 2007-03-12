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

/**
 * This is a FIFO implementation of the Policy
 *
 * @author walzouab
 *
 */

package org.objectweb.proactive.taskscheduler.policy;

import java.util.LinkedList;
import java.util.Vector;

import org.objectweb.proactive.taskscheduler.InternalTask;

/**
 * An implementaion of the Generic Policy interface.
 * The default policy in the secheduler.
 * Has two queues one is the normal one, and the other is the failed which has a higher priority
 * @author walzouab
 *
 */
public class FIFOPolicy implements GenericPolicy {

	LinkedList<InternalTask> list;//normal queue
	LinkedList<InternalTask> failedList; //used in failed conditions


	public InternalTask getNextTask() {

		//failed tasks have a higher priority
		if(!failedList.isEmpty())
			return failedList.poll();
	
	
		return list.poll();
		
		
	}
	
	//gets how many elements in the list
	public int getQueuedTasksNb() {
		// TODO Auto-generated method stub
		return list.size()+failedList.size();
	}

	//append tasks to the queue
	public void insert(Vector<InternalTask> t) {
		list.addAll(t);

	}

	//intialization done here
	//creates a new list
	public FIFOPolicy() {
		list=new LinkedList<InternalTask>();
		failedList=new LinkedList<InternalTask>();
		

	}

	public void finished(InternalTask Task) {
		//		doesnt need to be implemented for FIFO
		
	}

	public void failed(InternalTask t) {
		//push the failed element to the begininng of the queue so that it is the next to be executed
		failedList.addFirst(t);
		
	}

	public void flush() {
		// TODO Auto-generated method stub
		this.failedList.clear();
		this.list.clear();
	}

	
		

	public InternalTask getTask(String TaskID) {
		
		for(int i=0;i<list.size();i++)
		{
			if(list.get(i).getTaskID().equals(TaskID))
				return list.get(i);
		}
		
		for(int i=0;i<failedList.size();i++)
		{
			if(failedList.get(i).getTaskID().equals(TaskID))
				return list.get(i);
		}
		
	
		return null;
	}



	public InternalTask removeTask(String TaskID) {
		for(int i=0;i<list.size();i++)
		{
			if(list.get(i).getTaskID().equals(TaskID))
				return list.remove(i);
		}
		
		for(int i=0;i<failedList.size();i++)
		{
			if(failedList.get(i).getTaskID().equals(TaskID))
				return list.remove(i);
		}
		
	
		return null;
	}

	public Vector<String> getFailedID() {
		Vector<String> failed=new Vector<String>();
		
		for (int i=0;i<failedList.size();i++)
		{
			failed.add(failedList.get(i).getTaskID());
		}
		
		
		return failed;
	}

	public Vector<String> getQueuedID() {
		Vector<String> queued=new Vector<String>();
		for (int i=0;i<list.size();i++)
		{
			queued.add(list.get(i).getTaskID());
		}
		return queued;
	}

}
