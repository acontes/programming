/**
 * 
 */
package org.objectweb.proactive.extra.scheduler.task;

import org.objectweb.proactive.extra.scheduler.job.LightTask;

/**
 * This class represents an elligible task for the policy.
 * @see org.objectweb.proactive.extra.scheduler.job.LightTask
 * 
 * @author ProActive Team
 * @version 1.0, Jul 9, 2007
 * @since ProActive 3.2
 */
public class EligibleLightTask extends LightTask {

	/** Serial version UID */
	private static final long serialVersionUID = 8461969956605719440L;
	
	/**
	 * Get a new eligible light task using a taskDescriptor.
	 * 
	 * @param td the taskDescriptor to shrink.
	 */
	public EligibleLightTask(TaskDescriptor td) {
		super(td);
	}

}
