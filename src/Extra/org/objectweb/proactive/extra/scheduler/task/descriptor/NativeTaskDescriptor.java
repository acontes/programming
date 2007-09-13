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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extra.scheduler.common.task.Task;
import org.objectweb.proactive.extra.scheduler.common.task.TaskResult;
import org.objectweb.proactive.extra.scheduler.task.NativeTask;
import org.objectweb.proactive.extra.scheduler.task.NativeTaskLauncher;
import org.objectweb.proactive.extra.scheduler.task.TaskLauncher;

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
			 * @see org.objectweb.proactive.extra.scheduler.common.task.Task#execute(org.objectweb.proactive.extra.scheduler.task.TaskResult[])
			 */
			public Object execute(TaskResult... results) {
				try {
					process = Runtime.getRuntime().exec(cmd);
					//TODO ask cdelbe for better solution.
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String str = null;
					while((str = reader.readLine()) != null){
						System.out.println(str);
					}
					process.waitFor();
					return process.exitValue();
				} catch (Exception e) {
					e.printStackTrace();
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
	

	/**
	 * @see org.objectweb.proactive.extra.scheduler.task.descriptor.TaskDescriptor#createLauncher(java.lang.String, int, org.objectweb.proactive.core.node.Node)
	 */
	@Override
	public TaskLauncher createLauncher(String host, int port, Node node) throws ActiveObjectCreationException, NodeException {
		NativeTaskLauncher launcher;
		if (getPreTask() == null){
			launcher = (NativeTaskLauncher)ProActive.newActive(NativeTaskLauncher.class.getName(), new Object[]{getId(),getJobId(), host, port}, node);
		} else {
			launcher = (NativeTaskLauncher)ProActive.newActive(NativeTaskLauncher.class.getName(), new Object[]{getId(),getJobId(),getPreTask(), host, port}, node);
		}
		setExecuterInformations(new ExecuterInformations(launcher,node));
		return launcher;
	}
	
}