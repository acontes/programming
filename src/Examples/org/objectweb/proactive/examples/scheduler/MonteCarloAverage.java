package org.objectweb.proactive.examples.scheduler;

import org.objectweb.proactive.extra.scheduler.task.JavaTask;
import org.objectweb.proactive.extra.scheduler.task.TaskResult;

public class MonteCarloAverage extends JavaTask {

	/** Serial version UID */
	private static final long serialVersionUID = -2762210298670871929L;

	public Object execute(TaskResult... results) {
		double avrg = 0;
		int count = 0;
		System.out.println("Parameters are : ");
		System.out.print("\t");
		for (TaskResult res : results){
			if (!res.hadException()){
				System.out.print(res.value()+" ");
				avrg += ((Double)(res.value())).doubleValue();
				count++;
			}
		}
		Double result = new Double(avrg/count);
		System.out.println("Average is : "+result);
		return result;
	}

}
