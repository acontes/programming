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

import java.io.Serializable;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.ow2.proactive.scheduler.common.exception.SchedulerException;
import org.ow2.proactive.scheduler.common.job.Job;
import org.ow2.proactive.scheduler.common.job.JobResult;

/** We need this because there must be a way for the EJBs
 * to lookup the scheduler service. We use the JNDI for this
 * Also, we do not want to block the service thread while waiting for the result
 * We only block on the caller thread.
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class SchedulerConnectionManager 
	implements SchedulerConnectionInterface, Serializable { 

	private String schedulerURL;
	private String username;
	private String password;
	private transient Logger logger;
	
	private transient SchedulerConnectionAO scm;
	
	public SchedulerConnectionManager(String schedulerURL, String username, String password) 
		throws ActiveObjectCreationException, NodeException {
		
		this.schedulerURL = schedulerURL;
		this.username = username;
		this.password = password;
		logger = ProActiveLogger.getLogger(Loggers.CONNECTOR);
		createAO();
	}
	
	private void createAO() throws ActiveObjectCreationException, NodeException {
		// create SCM active object
		Object[] params = new Object[] {
			schedulerURL,
			username,
			password
		};
		
		scm = (SchedulerConnectionAO)PAActiveObject.newActive(
				SchedulerConnectionAO.class.getName(), 
				params);
	}

	@Override
	public void closeConnection() throws SchedulerException {
		scm.closeConnection();
	}

	@Override
	public void connectToScheduler() throws SchedulerException, LoginException {
		scm.connectToScheduler();
	}

	@Override
	public JobResult submitJob(Job job) throws SchedulerException {
		return scm.submitJob(job);
	}
	
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    	out.defaultWriteObject();
    	PAActiveObject.terminateActiveObject(scm, false);
    }
    
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    	in.defaultReadObject();
    	try {
			createAO();
		} catch (ActiveObjectCreationException e) {
			logger.error("Error while restoring the state of scm:" , e);
		} catch (NodeException e) {
			logger.error("Error while restoring the state of scm:" , e);
		}
    }

}
