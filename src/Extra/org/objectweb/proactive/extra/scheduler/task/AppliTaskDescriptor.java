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

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;

/**
 * Description of an application java task.
 * See also @see AbstractJavaTaskDescriptor
 * 
 * @author ProActive Team
 * @version 1.0, Jul 16, 2007
 * @since ProActive 3.2
 */
public class AppliTaskDescriptor extends AbstractJavaTaskDescriptor {

	/** Serial Version UID */
	private static final long serialVersionUID = -6946803819032140410L;
	/** the java task to launch */
	private ApplicationTask task;
	
	
	/**
	 * ProActive empty constructor
	 */
	public AppliTaskDescriptor() {}
	
	
	/**
	 * Create a new Java application task descriptor using instantiated java task.
	 * 
	 * @param task the already instanciated java task.
	 */
	public AppliTaskDescriptor(ApplicationTask task) {
		this.task = task;
	}

	
	/**
	 * Create a new Java application task descriptor using a specific Class.
	 * 
	 * @param taskClass the class instance of the class to instanciate.
	 */
	public AppliTaskDescriptor(Class<Task> taskClass) {
		super(taskClass);
	}

	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.task.TaskDescriptor#getTask()
	 */
	@Override
	public Task getTask() {
		if (task != null)
			return task;
		try {
			task = (ApplicationTask)taskClass.newInstance();
			task.init(args);
			return task;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.task.TaskDescriptor#createLauncher(java.lang.String, int, org.objectweb.proactive.core.node.Node)
	 */
	@Override
	public TaskLauncher createLauncher(String host, int port, Node node) throws ActiveObjectCreationException, NodeException {
		AppliTaskLauncher launcher;
		if (getPreTask() == null){
			launcher = (AppliTaskLauncher)ProActive.newActive(AppliTaskLauncher.class.getName(), new Object[]{getId(),getJobId(), host, port}, node);
		} else {
			launcher = (AppliTaskLauncher)ProActive.newActive(AppliTaskLauncher.class.getName(), new Object[]{getId(),getJobId(),getPreTask(), host, port}, node);
		}
		setLauncher(launcher);
		return launcher;
	}
	
	
	/**
	 * Set the instanciated java application task.
	 * 
	 * @param task the instanciated java application task.
	 */
	public void setTask(ApplicationTask task){
		this.task = task;
	}


	/**
	 * @param numberOfNodesNeeded the numberOfNodesNeeded to set
	 */
	public void setNumberOfNodesNeeded(int numberOfNodesNeeded) {
		this.numberOfNodesNeeded = numberOfNodesNeeded;
	}
}
