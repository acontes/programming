/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extra.scheduler.job;

/**
 * Class TaskFlowJob.
 * This is the definition of a tasks flow job.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 7, 2007
 * @since ProActive 3.2
 */
public class TaskFlowJob extends Job {

	/** Serial version UID */
	private static final long serialVersionUID = 5553430029462060936L;

	
	/**
	 * 
	 */
	public TaskFlowJob(){}
	
	
	/**
	 * Create a new Tasks Flow Job with the given parameters. It provides methods to add or
	 * remove tasks.
	 * 
	 * @param name the current job name.
	 * @param priority the priority of this job between 1 and 5.
	 * @param runtimeLimit the maximum execution time for this job given in millisecond.
	 * @param runUntilCancel true if the job has to run until its end or an user intervention.
	 * @param description a short description of the job and what it will do.
	 */
	public TaskFlowJob(String name, JobPriority priority, long runtimeLimit, boolean runUntilCancel, String description) {
		super(name,priority,runtimeLimit,runUntilCancel,description);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.job.Job#getType()
	 */
	@Override
	public JobType getType() {
		return JobType.TASKSFLOW;
	}

}
