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

/**
 * Class representing the task result.
 * A task result can be an exception or an object that you have to cast into your own type.
 * Before getting the object it is recommended that you call the hadException() method.
 * It will tell you if an exception occured in the task that generate this result.
 * 
 * @author ProActive Team
 * @version 1.0, Aug 3, 2007
 * @since ProActive 3.2
 */
public class TaskResult implements Serializable {

	/** Serial Version UID */
	private static final long serialVersionUID = 2976276079143998102L;
	/** The task identification of the result */
	private TaskId id = null;
	/** The value of the result if no exception occured */
	private Object value = null;
	/** The exception throwed by the task */
	private Throwable exception = null;

	/** ProActive empty constructor. */
	public TaskResult(){}
	
	
	/**
	 * Return a new instance of task result represented by a task id and its result.
	 * 
	 * @param id the identification of the task that send this result.
	 * @param value the result of the task.
	 */
	public TaskResult(TaskId id, Object value) {
		this.id = id;
		this.value = value;
	}

	
	/**
	 * Return a new instance of task result represented by a task id and its exception.
	 * 
	 * @param id the identification of the task that send this result.
	 * @param exception the exception that occured in the task.
	 */
	public TaskResult(TaskId id, Throwable exception) {
		this.id = id;
		this.exception = exception;
	}

	
	/**
	 * To know if an exception has occured on this task.
	 * 
	 * @return true if an exception occured, false if not.
	 */
	public boolean hadException(){
		return exception != null;
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
	 * To get the exception
	 * 
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}
	
}
