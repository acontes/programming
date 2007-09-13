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
package org.objectweb.proactive.extra.scheduler.task.descriptor;

import org.objectweb.proactive.extra.scheduler.common.task.JavaTask;
import org.objectweb.proactive.extra.scheduler.common.task.Task;


/**
 * Description of a java task.
 * See also @see AbstractJavaTaskDescriptor
 * 
 * @author ProActive Team
 * @version 1.0, Jul 16, 2007
 * @since ProActive 3.2
 */
public class JavaTaskDescriptor extends AbstractJavaTaskDescriptor {

	/** Serial Version UID */
	private static final long serialVersionUID = -6946803819032140410L;
	/** the java task to launch */
	private JavaTask task;
	
	
	/**
	 * ProActive empty constructor
	 */
	public JavaTaskDescriptor() {}
	
	
	/**
	 * Create a new Java task descriptor using instantiated java task.
	 * 
	 * @param task the already instanciated java task.
	 */
	public JavaTaskDescriptor(JavaTask task) {
		this.task = task;
	}

	
	/**
	 * Create a new Java task descriptor using a specific Class.
	 * 
	 * @param taskClass the class instance of the class to instanciate.
	 */
	public JavaTaskDescriptor(Class<JavaTask> taskClass) {
		super(taskClass);
	}

	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.task.descriptor.TaskDescriptor#getTask()
	 */
	@Override
	public Task getTask() {
		if (task != null)
			return task;
		try {
			task = (JavaTask)taskClass.newInstance();
			try{
				task.init(args);
			} catch (Exception e){
				System.err.println("WARING : INIT has failed for task "+task.getClass().getSimpleName());
				e.printStackTrace();
			}
			return task;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Set the instanciated java task.
	 * 
	 * @param task the instanciated java task.
	 */
	public void setTask(JavaTask task){
		this.task = task;
	}
}
