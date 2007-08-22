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
 * Abstract definition of a java task.
 * See also @see TaskDescriptor
 * 
 * @author ProActive Team
 * @version 1.0, Jul 16, 2007
 * @since ProActive 3.2
 */
public abstract class AbstractJavaTaskDescriptor extends TaskDescriptor {

	/** Serial Version UID  */
	private static final long serialVersionUID = 1340022492862249182L;
	/** Class instance of the class to instanciate. */
	protected Class<? extends Task> taskClass;
	/** Arguments of the task as a map */
	protected Map<String, Object> args = new HashMap<String, Object>();
	
	
	/**
	 * ProActive empty constructor
	 */
	public AbstractJavaTaskDescriptor() {}
	

	/**
	 * Create a new java task descriptor using the Class instance of the class to instanciate.
	 * 
	 * @param taskClass the Class instance of the class to instanciate.
	 */
	public AbstractJavaTaskDescriptor(Class<? extends Task> taskClass) {
		this.taskClass = taskClass;
	}


	/**
	 * Get the task Class instance.
	 * 
	 * @return the task Class instance.
	 */
	public Class<? extends Task> getTaskClass() {
		return taskClass;
	}
	

	/**
	 * Set the task Class instance.
	 * 
	 * @param taskClass the task Class instance.
	 */
	public void setTaskClass(Class<? extends Task> taskClass) {
		this.taskClass = taskClass;
	}

	
	/**
	 * Get the task arguments as a map.
	 * 
	 * @return the task arguments.
	 */
	public Map<String, Object> getArgs() {
		return args;
	}

	
	/**
	 * Set the task arguments as a map.
	 * 
	 * @param args the task arguments.
	 */
	public void setArgs(Map<String, Object> args) {
		this.args = args;
	}
}
