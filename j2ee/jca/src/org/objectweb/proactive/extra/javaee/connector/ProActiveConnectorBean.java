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
package org.objectweb.proactive.extra.javaee.connector;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.remoteobject.AbstractRemoteObjectFactory;
import org.objectweb.proactive.core.util.log.Loggers;

/**
 * Some basic functionality for the Resource Adapter
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
public abstract class ProActiveConnectorBean {
	protected static final int DEFAULT_CODEBASE_PORT = 20260;
	protected Logger _raLogger;
	
	protected void configureRA(){
		// configure ProActive separate logging
		configureLogging();
		
		// configure communication protocol-specific parameters
		configureCommunication();
	}
	
	/**
	 * logging issues
	 */
	private void configureLogging() {
		// logging is configured via a separate RepositorySelector
		try {
			ContextRepositorySelector.init(this);
			_raLogger = Logger.getLogger(Loggers.CONNECTOR);
			_raLogger.debug("log4j initialised succesfully!");
		} catch (Exception e) {
			System.err.println("ERROR - Could not init log4j system. " +
					"Try to review your log4j configuration - in your AS, and also in " 
						+ ContextRepositorySelector.LOG4J_CONFIG_FILE);
			System.err.println("the error message is:" + e.getMessage());
			System.err.println("the stacktrace is:");
			e.printStackTrace();
		}
	}
	
	/**
	 * Setting specific communication parameters, 
	 * depending on the communication protocol used
	 * TODO are there any other protocols than RMI? :P
	 */
	private void configureCommunication(){
		
		try {
			_hostName = InetAddress.getLocalHost().getHostName();
		}
		catch(UnknownHostException e) {
			_raLogger.info("Could not obtain the hostname, going for the default , which is localhost");
			_hostName = "localhost";
		}
		finally {
			
			PAProperties.PA_NET_NOLOOPBACK.setValue(true);
			
			// protocol-dependent configuration
			String communicationProtocol = PAProperties.PA_RUNTIME_NAME.getValue();
			// if not set, assume communication protocol is RMI
			if( communicationProtocol == null || 
					(communicationProtocol != null && communicationProtocol.equals("rmi") ) ) {
				setRmiParams();
			}
			
		}
	}
	
	private void setRmiParams() {
		System.setProperty("java.rmi.server.hostname", _hostName);
	}
	
	////////// 	according to JSR the RA must be a JavaBean
	protected String _vmName;  //RW
	protected String _hostName;  //RO
	protected String _codeBaseUrl;  //RO
	
	public String getVmName() {
		return _vmName;
	}

	public void setVmName(String vmName) {
		_vmName = vmName;
	}

	public String getHostName() {
		return _hostName;
	}

	public String getCodebaseUrl() {
		return _codeBaseUrl;
	}
}
