package org.objectweb.proactive.examples.scheduler;

import org.objectweb.proactive.extra.scheduler.task.JavaTask;
import org.objectweb.proactive.extra.scheduler.task.TaskResult;

public class PropertyTask extends JavaTask {

	/**  */
	private static final long serialVersionUID = -2536751215944833218L;

	public Object execute(TaskResult... results) {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("------> The property user.property1 has value '"+System.getProperty("user.property1")+"'");
		return 0;
	}

}
