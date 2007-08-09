/**
 * 
 */
package org.objectweb.proactive.extra.scheduler.task;

import org.objectweb.proactive.extra.scheduler.job.LightTask;

/**
 * This class represents an elligible task for the policy.
 * It is a sort of tag class that will avoid user from giving non-eligible task to the scheduler.
 * In fact policy will handle LightTask and EligibleLightTask but
 * will only be allowed to send EligibleLightTask to the scheduler
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
	 * Same constructor as LightTask
	 * 
	 * @param td the taskDescriptor to shrink.
	 */
	public EligibleLightTask(TaskDescriptor td) {
		super(td);
	}

}
