package org.objectweb.proactive.extra.scheduler.policy;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import org.objectweb.proactive.extra.scheduler.job.LightJob;
import org.objectweb.proactive.extra.scheduler.task.EligibleLightTask;

/**
 * 
 * 
 * @author ProActive Team
 * @version 1.0, Jul 5, 2007
 * @since ProActive 3.2
 */
public class PriorityPolicy implements PolicyInterface {

	/** Serial version UID */
	private static final long serialVersionUID = -5882465083001537486L;


	/**
	 * This method return the tasks using FIFO policy according to the jobs priorities.
	 * 
	 * @see org.objectweb.proactive.extra.scheduler.policy.PolicyInterface#getReadyTasks(java.util.List)
	 */
	public Vector<EligibleLightTask> getReadyTasks(List<LightJob> jobs) {
		Vector<EligibleLightTask> toReturn = new Vector<EligibleLightTask>();
		//sort jobs by priority
		Collections.sort(jobs);
		for (LightJob lj : jobs){
			toReturn.addAll(lj.getEligibleTasks());
		}
		return toReturn;
	}
	
	
}
