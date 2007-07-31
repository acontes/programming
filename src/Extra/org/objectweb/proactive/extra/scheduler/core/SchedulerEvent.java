/**
 * 
 */
package org.objectweb.proactive.extra.scheduler.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobEvent;
import org.objectweb.proactive.extra.scheduler.job.JobId;

/**
 * This is the scheduler event.
 * It provides informations about the state of each tasks of each jobs.
 * 
 * @author ProActive Team
 * @version 1.0, Jul 27, 2007
 * @since ProActive 3.2
 */
public class SchedulerEvent implements Serializable {

	/** Serial version UID */
	private static final long serialVersionUID = -3277604983410141325L;
	/** Mapping between the job ident and the task status modify of each job. */
	private HashMap<JobId,JobEvent> events = new HashMap<JobId,JobEvent>();
	
	/**
	 * New instance of scheduler event will take the change to do on the tasks' job.
	 * 
	 * @param events the change to be done on every jobs.
	 */
	public SchedulerEvent(HashMap<JobId,JobEvent> events){
		this.events = events;
	}
	
	/**
	 * Update the state of every tasks in every given jobs. 
	 * 
	 * @param jobs the jobs to update.
	 */
	public void update (Vector<Job> jobs){
		for (Job job : jobs){
			if (events.containsKey(job.getId()))
				job.update(events.get(job.getId()));
		}
	}
	
}
