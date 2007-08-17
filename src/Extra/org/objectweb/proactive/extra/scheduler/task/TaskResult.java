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
package org.objectweb.proactive.extra.scheduler.task;

import java.io.Serializable;


public class TaskResult implements Serializable {

	private static final long serialVersionUID = 2976276079143998102L;
	private TaskId id = null;
	private Object value = null;
	private Throwable exception = null;

	/** ProActive empty constructor. */
	public TaskResult(){}
	
	
	public TaskResult(TaskId id, Object value) {
		this.id = id;
		this.value = value;
	}

	
	public TaskResult(TaskId id, Throwable exception) {
		this.id = id;
		this.exception = exception;
	}

	/**
	 * To get the exception
	 * 
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}
	

	/**
	 * To get the id
	 * 
	 * @return the id
	 */
	public TaskId getTaskId() {
		return id;
	}
	

	/**
	 * To get the value
	 * 
	 * @return the value
	 */
	public Object value() {
		return value;
	}

	
	/**
	 * To know if an exception has occured on this task.
	 * 
	 * @return true if an exception occured, false if not.
	 */
	public boolean hadException(){
		return exception != null;
	}
}
