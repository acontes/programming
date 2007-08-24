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

import java.io.IOException;

import org.objectweb.proactive.extra.scheduler.task.NativeTask;
import org.objectweb.proactive.extra.scheduler.task.Task;
import org.objectweb.proactive.extra.scheduler.task.TaskResult;

/**
 * Description of a native task.
 * This task include the process
 * see also {@link TaskDescriptor}
 * 
 * @author ProActive Team
 * @version 1.0, Jun 29, 2007
 * @since ProActive 3.2
 */
public class NativeTaskDescriptor extends TaskDescriptor {

	/** Serial Version UID */
	private static final long serialVersionUID = 2587936204570926300L;
	/** Command line to execute */
	private String cmd;
	
	
	/**
	 * ProActive empty constructor.
	 */
	public NativeTaskDescriptor(){}
	
	
	/**
	 * Create a new native task descriptor with the given command line.
	 * 
	 * @param cmd the command line to execute
	 */
	public NativeTaskDescriptor(String cmd) {
		this.cmd = cmd;
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.task.descriptor.TaskDescriptor#getTask()
	 */
	@Override
	public Task getTask() {
		//create the new task that will launch the command on execute.
		NativeTask nativeTask = new NativeTask() {
			private static final long serialVersionUID = 0L;
			private Process process;

			/**
			 * @see org.objectweb.proactive.extra.scheduler.task.Task#execute(org.objectweb.proactive.extra.scheduler.task.TaskResult[])
			 */
			public Object execute(TaskResult... results) {
				try {
					process = Runtime.getRuntime().exec(cmd);
					return process.exitValue();
				} catch (IOException e) {
					return 255;
				}
			}
			
			/**
			 * @see org.objectweb.proactive.extra.scheduler.task.NativeTask#getProcess()
			 */
			public Process getProcess(){
				return process;
			}
		};
		return nativeTask;
	}
	
}