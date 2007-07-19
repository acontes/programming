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
	private Map<String, Object> args = new HashMap<String, Object>();
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.task.TaskDescriptor#getTask()
	 */
	@Override
	public Task getTask() {
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

	public Map<String, Object> getArgs() {
		return args;
	}

	public void setArgs(Map<String, Object> args) {
		this.args = args;
	}
}
