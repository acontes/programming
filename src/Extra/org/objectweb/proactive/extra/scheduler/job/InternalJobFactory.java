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

import java.io.Serializable;
import org.objectweb.proactive.extra.scheduler.common.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.common.job.ApplicationJob;
import org.objectweb.proactive.extra.scheduler.common.job.Job;
import org.objectweb.proactive.extra.scheduler.common.job.TaskFlowJob;
import org.objectweb.proactive.extra.scheduler.common.task.ApplicationTask;
import org.objectweb.proactive.extra.scheduler.task.internal.InternalAbstractJavaTask;

/**
 * This is the factory to build Internal job with a job (user).
 * 
 * @author ProActive Team
 * @version 1.0, Sept 14, 2007
 * @since ProActive 3.2
 */
public class InternalJobFactory implements Serializable {

	/** Serial Version UID */
	private static final long serialVersionUID = -7017115222916960404L;
	
	
	/**
	 * Create a new internal job with the given job (user).
	 * 
	 * @param job the user job that will be used to create the internal job.
	 * @return the created internal job.
	 * @throws SchedulerException an exception if the factory cannot create the given job.
	 */
	public static InternalJob createJob(Job job) throws SchedulerException {
		switch (job.getType()){
			case PARAMETER_SWEEPING : throw new SchedulerException("The type of the given job is not yet implemented !");
			case APPLI : return createJob((ApplicationJob)job);
			case TASKSFLOW : return createJob((TaskFlowJob)job);
		}
		throw new SchedulerException("The type of the given job is unknown !");
	}
	
	
	private static InternalJob createJob(TaskFlowJob userJob) throws SchedulerException {
		InternalJob job = new InternalTaskFlowJob(
				userJob.getName(),
				userJob.getPriority(),
				userJob.getRuntimeLimit(),
				true,
				userJob.getDescription());
		
		return job;
	}
	
	
	/**
	 * Create an internalApplication job with the given Application job (user)
	 * 
	 * @param job the user job that will be used to create the internal job.
	 * @return the created internal job.
	 * @throws SchedulerException an exception if the factory cannot create the given job.
	 */
	private static InternalJob createJob(ApplicationJob userJob) throws SchedulerException {
		InternalApplicationJob job;
		ApplicationTask userTask = userJob.getTask();
		if (userTask != null)
			throw new SchedulerException("You must specify an application task !");
		if (userTask.getTaskClass() != null){
			job = new InternalApplicationJob(
					userJob.getName(),
					userJob.getPriority(),
					userJob.getRuntimeLimit(),
					true, /* not yet */
					userJob.getDescription(),
					userTask.getNumberOfNodesNeeded(),
					userTask.getTaskClass());
		} else if (userTask.getTaskInstance() != null) {
			job = new InternalApplicationJob(
					userJob.getName(),
					userJob.getPriority(),
					userJob.getRuntimeLimit(),
					true,/* not yet */
					userJob.getDescription(),
					userTask.getNumberOfNodesNeeded(),
					userTask.getTaskInstance());
		} else {
			throw new SchedulerException("You must specify your own executable application task to be launched (in the application task) !");
		}
		InternalAbstractJavaTask iajt = job.getTask();
		iajt.setDescription(userTask.getDescription());
		iajt.setName(userTask.getName());
		iajt.setPostTask(userTask.getPostTask());
		iajt.setPreTask(userTask.getPreTask());
		iajt.setRerunnable(userTask.getRerunnable());
		iajt.setRunTimeLimit(userTask.getRunTimeLimit());
		iajt.setVerifyingScript(userTask.getVerifyingScript());
		iajt.setArgs(userTask.getArguments());
		return job;
	}
	
}
