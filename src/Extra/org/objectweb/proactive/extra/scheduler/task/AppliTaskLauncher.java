/**
 * 
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
