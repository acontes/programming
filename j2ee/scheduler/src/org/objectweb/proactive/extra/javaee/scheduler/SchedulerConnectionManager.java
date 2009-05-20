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
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.remoteobject.exception.UnknownProtocolException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.ow2.proactive.scheduler.common.SchedulerEvent;
import org.ow2.proactive.scheduler.common.exception.SchedulerException;
import org.ow2.proactive.scheduler.common.job.Job;
import org.ow2.proactive.scheduler.common.job.JobId;
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
	private boolean isConnected;
	private transient Logger logger;
	
	private transient SchedulerConnectionAO scm;
	private transient SchedulerListener jobFinishedListener=null;
	
	public SchedulerConnectionManager(String schedulerURL, String username, String password) 
		throws ActiveObjectCreationException, NodeException {
		
		this.schedulerURL = schedulerURL;
		this.username = username;
		this.password = password;
		this.isConnected = false;
		logger = ProActiveLogger.getLogger(Loggers.CONNECTOR);
		createAO();
	}
	
	private void createSchedulerListener() throws UnknownProtocolException, SchedulerException {
		jobFinishedListener = new SchedulerListener();
		SchedulerListener jobFinishedListenerRemoteRef = jobFinishedListener.createRemoteReference();
		scm.addSchedulerEventListener(jobFinishedListenerRemoteRef, SchedulerEvent.JOB_RUNNING_TO_FINISHED);
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
		if(!isConnected)
			throw new SchedulerException("Must be connected to the scheduler first!");
		try {
			jobFinishedListener.destroyRemoteReference();
		} catch (ProActiveException e) {
			logger.debug("Cannot destroy listener remote reference. Reason:", e);
		}
		scm.closeConnection();
		isConnected = false;
	}

	@Override
	public void connectToScheduler() throws SchedulerException, LoginException {
		if(isConnected)
			throw new SchedulerException("Already connected to the scheduler!");
		scm.connectToScheduler();
		try {
			createSchedulerListener();
		} catch (UnknownProtocolException e) {
			throw new SchedulerException("Cannot create the remote reference for the scheduler listener, because:",e);
		}
		isConnected = true;
	}
	
	@Override
	public JobId submitJob(Job job) throws SchedulerException {
		if(!isConnected)
			throw new SchedulerException("Must connect to the scheduler first!");
		try {
			JobId id = scm.submitJob(job);
			// register listener
			jobFinishedListener.startMonitoring(id);
			// ret id to user
			return id;
		} catch (SchedulerListenerException e) {
			throw new SchedulerException(e);
		}
	}
	
	private JobResult waitForJobResult(JobId id) throws SchedulerException {
		try {
			JobResult jResult = null;
			jobFinishedListener.waitJobFinished(id);
			jResult = scm.getJobResult(id);
			return jResult;
		} catch(InterruptedException e) {
			throw new SchedulerException("Cannot get the job result because we cannot wait for the job to finish! Reason:" , e);
		} catch (SchedulerListenerException e) {
			throw new SchedulerException(e);
		}
	}
	
	@Override
	public JobResult getJobResult(JobId id) throws SchedulerException {
		if(!isConnected)
			throw new SchedulerException("Must connect to the scheduler first!");
		JobResult ret = waitForJobResult(id);
		// stop monitoring this id
		jobFinishedListener.stopMonitoring(id);
		return ret;
	}
	
	@Override
	public boolean jobFinished(JobId job) {
		try {
			return jobFinishedListener.jobFinished(job);
		} catch (SchedulerListenerException e) {
			logger.warn(e.toString());
			return false;
		}
	}
	
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    	out.defaultWriteObject();
    	// terminate listener remote reference && close scheduler connection
		try {
			if(isConnected)
				closeConnection();
		}  catch (SchedulerException e) {
			logger.error("Error while closing the connection to the scheduler. Reason:", e);
		}
    	// kill active object
    	PAActiveObject.terminateActiveObject(scm, false);
    }
    
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    	in.defaultReadObject();
    	try {
			createAO();
			if(isConnected) {
				scm.connectToScheduler();
				createSchedulerListener();
			}
		} catch (ActiveObjectCreationException e) {
			logger.error("Error while restoring the state of scm: creation of the active object failed." , e);
		} catch (NodeException e) {
			logger.error("Error while restoring the state of scm: creation of the active object failed." , e);
		} catch (UnknownProtocolException e) {
			logger.error("Error while restoring the state of scm: creation of the scheduler listener failed." , e);
		} catch (SchedulerException e) {
			logger.error("Error while restoring the state of scm: creation of the scheduler listener failed." , e);
		} catch (LoginException e) {
			logger.error("Error while restoring the state of scm: connecting to the scheduler failed." , e);
		}
    }

}
