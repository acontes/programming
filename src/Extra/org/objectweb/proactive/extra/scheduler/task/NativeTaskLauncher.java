/**
 * 
 */
package org.objectweb.proactive.extra.scheduler.task;

import org.objectweb.proactive.extra.scheduler.job.JobId;

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

	/**
	 * ProActive Empy Constructor
	 */
	public NativeTaskLauncher() {}

	
	public NativeTaskLauncher(TaskId taskId, JobId jobId, String host,
			Integer port) {
		super(taskId, jobId, host, port);
	}
	

	/**
	 * Kill all launched nodes/tasks and terminate the launcher.
	 * 
	 * @see org.objectweb.proactive.extra.scheduler.task.TaskLauncher#terminate()
	 */
	@Override
	public void terminate() {
		// TODO stopper le process natif
		super.terminate();
	}
}
