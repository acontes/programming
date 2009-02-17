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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.security.auth.login.LoginException;
import javax.transaction.xa.XAResource;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.javaee.connector.ContextRepositorySelector;
import org.ow2.proactive.scheduler.common.exception.SchedulerException;

/**
 * Resource adapter implementation for the Scheduler integration
 * This is needed so that we can have a singleton in the J2EE env
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class SchedulerResourceAdapter implements ResourceAdapter {

	/* (non-Javadoc)
	 * @see javax.resource.spi.ResourceAdapter#endpointActivation(javax.resource.spi.endpoint.MessageEndpointFactory, javax.resource.spi.ActivationSpec)
	 */
	@Override
	public void endpointActivation(MessageEndpointFactory arg0,
			ActivationSpec arg1) throws ResourceException {
		throw new NotSupportedException("Not implemented.");

	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ResourceAdapter#endpointDeactivation(javax.resource.spi.endpoint.MessageEndpointFactory, javax.resource.spi.ActivationSpec)
	 */
	@Override
	public void endpointDeactivation(MessageEndpointFactory arg0,
			ActivationSpec arg1) {
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ResourceAdapter#getXAResources(javax.resource.spi.ActivationSpec[])
	 */
	@Override
	public XAResource[] getXAResources(ActivationSpec[] arg0)
			throws ResourceException {
		throw new NotSupportedException("Not implemented.");
	}

	private Logger logger;
	private SchedulerConnectionManager scm;
	private InitialContext raCtx;
	
	// this method is guaranteed to be called only once per deployment 
	@Override
	public void start(BootstrapContext arg0)
			throws ResourceAdapterInternalException {
		// init logger
		try {
			ContextRepositorySelector.init(this);
		} catch (Exception e1) {
			System.err.println("ERROR - Could not init log4j system. " +
					"Try to review your log4j configuration - in your AS, and also in " 
						+ ContextRepositorySelector.LOG4J_CONFIG_FILE);
			throw new ResourceAdapterInternalException(e1);
		}
		logger = ProActiveLogger.getLogger(Loggers.CONNECTOR);
		logger.debug("log4j initialized succesfully");
		
		logger.debug("Creating the scheduler connection manager...");
			
		try {
			scm = new SchedulerConnectionManager(schedulerURL,username,password);
		} catch (ActiveObjectCreationException e) {
			logger.error("Error while creating the SchedulerConnectionManager active object:" , e);
			throw new ResourceAdapterInternalException(e);
		} catch (NodeException e) {
			logger.error("Error while creating the SchedulerConnectionManager active object:" , e);
			throw new ResourceAdapterInternalException(e);
		}
		
		logger.debug("Registering the scheduler connection manager to JNDI...");
		try {
			raCtx = new javax.naming.InitialContext();
			raCtx.bind(jndiName, scm);
		} catch (NamingException e1) {
			logger.error("Error while binding the scheduler connection manager to the name " + jndiName, e1);
			throw new ResourceAdapterInternalException(e1);
		} 
		
		logger.debug("Scheduler connection manager up and running!");
		
	}

	// this method is guaranteed to be called only once per deployment
	@Override
	public void stop() {
		logger.debug("closing the connection to the scheduler...");
		try {
			scm.closeConnection();
		} catch (SchedulerException e) {
			logger.error("Error while trying to close the connection to the scheduler at " + schedulerURL, e);
		}
		// unbind 
		try {
			raCtx.unbind(jndiName);
		} catch (NamingException e) {
			logger.error("Error while unbinding the scheduler connection manager to the name " + jndiName, e);
		}
	}
	
	/// RA config params
	private String schedulerURL;
	private String username;
	private String password;
	private String jndiName;
	
	public String getSchedulerURL() {
		return schedulerURL;
	}

	public void setSchedulerURL(String schedulerURL) {
		this.schedulerURL = schedulerURL;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getJndiName() {
		return jndiName;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(!(obj instanceof SchedulerResourceAdapter)){
			return false;
		}
		
		SchedulerResourceAdapter rhs = (SchedulerResourceAdapter)obj;

		return rhs.schedulerURL.equals(schedulerURL) && rhs.username.equals(username) && 
			rhs.password.equals(password) && rhs.jndiName.equals(jndiName);
		
	}

}
