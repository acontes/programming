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

import java.io.PrintStream;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketAppender;
import org.objectweb.proactive.extra.logforwarder.LoggingOutputStream;
import org.objectweb.proactive.extra.scheduler.core.SchedulerCore;
import org.objectweb.proactive.extra.scheduler.exception.UserException;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.scripting.ScriptHandler;
import org.objectweb.proactive.extra.scheduler.scripting.ScriptLoader;
import org.objectweb.proactive.extra.scheduler.scripting.ScriptResult;

/**
 * Native Task Launcher.
 * 
 * @author ProActive Team
 * @version 1.0, Jul 10, 2007
 * @since ProActive 3.2
 */
public class NativeTaskLauncher extends TaskLauncher {

	/** Serial version UID */
	private static final long serialVersionUID = 8574369410634220047L;
	private Process process;

	/**
	 * ProActive Empy Constructor
	 */
	public NativeTaskLauncher() {}

	
	public NativeTaskLauncher(TaskId taskId, JobId jobId, String host, Integer port) {
		super(taskId, jobId, host, port);
	}
	

	/**
	 * Execute the user task as an active object.
	 * 
	 * @param core The scheduler core to be notify
	 * @param task the task to execute
	 * @param results the possible results from parent tasks.(if task flow)
	 * @return a task result representing the result of this task execution.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public TaskResult doTask(SchedulerCore core, Task task, TaskResult... results) {
		//handle loggers
       	Appender out = new SocketAppender(host,port);
       	// store stdout and err
       	PrintStream stdout = System.out;
        PrintStream stderr = System.err;
       	// create logger
       	Logger l = Logger.getLogger(SchedulerCore.LOGGER_PREFIX+jobId);
       	l.removeAllAppenders();
        l.addAppender(out);
        // redirect stdout and err
        System.setOut(new PrintStream(new LoggingOutputStream(l,Level.INFO), true));
        System.setErr(new PrintStream(new LoggingOutputStream(l,Level.ERROR), true));
		try {
			//launch pre script
			if (pre != null){
	        	ScriptHandler handler = ScriptLoader.createHandler(null);
	        	ScriptResult<Object> res = handler.handle(pre);
	        	if(res.errorOccured()){
	        		System.err.println("Error on pre-script occured : ");
	        		res.getException().printStackTrace();
	        		throw new UserException("PreTask script has failed on the current node");
	        	}
        	}
			//get process
			process = ((NativeTask)task).getProcess();
			//launch task
            TaskResult result = new TaskResult(taskId, task.execute(results));
            //return result
            return result;
		} catch (Exception ex) {
			return new TaskResult(taskId, ex);
		} finally {
			//Unhandle loggers
            LogManager.shutdown();
            System.setOut(stdout);
            System.setErr(stderr);
            //terminate the task
			core.terminate(taskId, jobId);	
		}
	}
	
	
	/**
	 * Kill all launched nodes/tasks and terminate the launcher.
	 * 
	 * @see org.objectweb.proactive.extra.scheduler.task.TaskLauncher#terminate()
	 */
	@Override
	public void terminate() {
		process.destroy();
		super.terminate();
	}
}
