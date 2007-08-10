package org.objectweb.proactive.extra.scheduler.policy;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import org.objectweb.proactive.extra.scheduler.job.LightJob;
import org.objectweb.proactive.extra.scheduler.task.EligibleLightTask;

/**
 * Policy interface for the scheduler.
 * Must be implemented in order to be used as a policy in the scheduler core.
 * 
 * @author ProActive Team
 * @version 1.0, Jul 5, 2007
 * @since ProActive 3.2
 */
public interface PolicyInterface extends Serializable {

	
	/**
	 * Return the tasks that have to be scheduled.
	 * The tasks must be in the desired scheduling order.
	 * The first task to be schedule must be the first in the returned Vector.
	 * 
	 * @param jobs the list of light pending or running jobs. 
	 * @return a vector of every tasks that are ready to be schedule.
	 */
	Vector<EligibleLightTask> getOrderedTasks(List<LightJob> jobs);
	
}
