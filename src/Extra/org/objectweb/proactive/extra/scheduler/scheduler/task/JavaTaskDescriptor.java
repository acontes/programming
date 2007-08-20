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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @author ProActive Team
 * @version 1.0, Jul 16, 2007
 * @since ProActive 3.2
 */
public class JavaTaskDescriptor extends TaskDescriptor {

	private static final long serialVersionUID = 1340022492862249182L;
	private Class<Task> taskClass;
	private Task task;
	private Map<String, Object> args = new HashMap<String, Object>();
	
	
	/**
	 * ProActive empty constructor
	 */
	public JavaTaskDescriptor() {}
	
	
	/**
	 * @param task
	 */
	public JavaTaskDescriptor(Task task) {
		this.task = task;
	}

	/**
	 * @param taskClass
	 */
	public JavaTaskDescriptor(Class<Task> taskClass) {
		this.taskClass = taskClass;
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.task.TaskDescriptor#getTask()
	 */
	@Override
	public Task getTask() {
		if (task != null)
			return task;
		try {
			JavaTask task = (JavaTask)taskClass.newInstance();
			task.init(args);
			return task;
		} catch (Exception e) {
			return null;
		}
	}

	public Class<Task> getTaskClass() {
		return taskClass;
	}

	public void setTaskClass(Class<Task> taskClass) {
		this.taskClass = taskClass;
	}
	
	public void setTask(Task task){
		this.task = task;
	}

	public Map<String, Object> getArgs() {
		return args;
	}

	public void setArgs(Map<String, Object> args) {
		this.args = args;
	}
}
