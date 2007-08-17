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

import org.objectweb.proactive.extra.scheduler.job.JobId;

/**
 * Appli task Launcher.
 * 
 * @author ProActive Team
 * @version 1.0, Jul 10, 2007
 * @since ProActive 3.2
 */
public class AppliTaskLauncher extends TaskLauncher {

	/** Serial version UID */
	private static final long serialVersionUID = 4655938634771399458L;
	
	
	/**
	 * ProActive empty constructor.
	 */
	public AppliTaskLauncher() {}
	
	
	public AppliTaskLauncher(TaskId taskId, JobId jobId, String host, Integer port) {
		super(taskId, jobId, host, port);
	}
	
	
	/**
	 * Kill all launched nodes/tasks and terminate the launcher.
	 * 
	 * @see org.objectweb.proactive.extra.scheduler.task.TaskLauncher#terminate()
	 */
	@Override
	public void terminate(){
		//TODO détruire tout ce qui est lancé
		super.terminate();
	}
}
