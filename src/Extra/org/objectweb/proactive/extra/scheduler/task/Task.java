package org.objectweb.proactive.extra.scheduler.task;

import java.io.Serializable;


public interface Task extends Serializable {

	Object execute(TaskResult... results);
}
