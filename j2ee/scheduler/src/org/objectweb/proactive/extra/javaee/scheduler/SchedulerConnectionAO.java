/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extra.javaee.scheduler;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.extensions.annotation.ActiveObject;
import org.ow2.proactive.scheduler.common.exception.SchedulerException;
import org.ow2.proactive.scheduler.common.job.Job;
import org.ow2.proactive.scheduler.common.job.JobId;
import org.ow2.proactive.scheduler.common.job.JobResult;
import org.ow2.proactive.scheduler.common.scheduler.SchedulerAuthenticationInterface;
import org.ow2.proactive.scheduler.common.scheduler.SchedulerConnection;
import org.ow2.proactive.scheduler.common.scheduler.UserSchedulerInterface;

/**
 * Manages a connection to the Scheduler
 * Need an Active Object as all the requests 
 * should be served only from this thread
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
@ActiveObject
public class SchedulerConnectionAO implements SchedulerConnectionInterface{
	
	private String schedulerURL;
	private String username;
	private String password;
	
	private UserSchedulerInterface schedulerUI=null;
	private SchedulerAuthenticationInterface schedulerAuth=null;
	
	public SchedulerConnectionAO(){
		
	}
	
	public SchedulerConnectionAO(String schedulerURL, String username,
			String password) {
		super();
		this.schedulerURL = schedulerURL;
		this.username = username;
		this.password = password;
	}
	
	public void connectToScheduler() throws SchedulerException, LoginException {
		 schedulerAuth = SchedulerConnection.join(schedulerURL);
		 schedulerUI = schedulerAuth.logAsUser(username, password);
	}
	
	public JobResult submitJob(Job job) throws SchedulerException {
		JobId id = schedulerUI.submit(job);
	
		return schedulerUI.getJobResult(id);
	}
	
	public JobId submitJobNonBlocking(Job job) throws SchedulerException {
		return schedulerUI.submit(job);
	}
	
	public JobResult getJobResult(JobId id) throws SchedulerException {
		return schedulerUI.getJobResult(id);
	}
	
	public void closeConnection() throws SchedulerException {
		schedulerUI.disconnect();
	}
	
}
