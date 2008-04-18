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
package org.objectweb.proactive.extensions.jmx.jboss;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.jboss.system.ServiceMBeanSupport;
import org.objectweb.proactive.core.util.log.Loggers;

/**
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
public abstract class ProActiveJbossLoader extends ServiceMBeanSupport 
	implements ProActiveJbossLoaderMBean {

	protected static final int CODEBASE_PORT = 20260;
	protected static final Logger _jbossLogger = Logger.getLogger(Loggers.JBOSS);
	
	@Override
	protected void createService() throws Exception {
		_jbossLogger.info("Creating service " + serviceName.getCanonicalName());
		// configure ProActive separate logging
		configureLogging();
		
		// configure communication protocol-specific parameters
		configureCommunication();
	}
	
	private void configureLogging() {
		// override the default log4j initialization phase		
		System.setProperty("log4j.defaultInitOverride", "true");
		System.setProperty("log4j.configuration", _log4jConfigFile );

	 }
	
	private void configureCommunication() throws UnknownHostException {
		// TODO is this necessary ???
		_hostName = InetAddress.getLocalHost().getHostName();
		_codeBaseUrl = "http://" + _hostName + ":" + CODEBASE_PORT +"/";
		
		String communicationProtocol = System.getProperty("proactive.communication.protocol");
		// if not set, assume communication protocol is RMI
		if( communicationProtocol == null || 
				(communicationProtocol != null && communicationProtocol.equals("rmi") ) ) {
			
			System.setProperty("proactive.http.port", CODEBASE_PORT + "");
			System.setProperty("java.rmi.server.hostname", _hostName);
			
			System.setProperty("java.rmi.server.codebase", _codeBaseUrl );
			System.setProperty("proactive.net.nolocal", "true" );
			_jbossLogger.debug("Configuration info: hostName : " + _hostName + 
					"; codebase URL : " + _codeBaseUrl );
		}
		
	}
	
	////////// 		MBean 	config 	params
	protected String _vmName;
	protected String _log4jConfigFile;
	protected String _hostName;
	protected String _codeBaseUrl;
	
	@Override
	public String getLog4jConfigFile() {
		return _log4jConfigFile;
	}


	@Override
	public String getvmName() {
		return _vmName;
	}

	@Override
	public void setLog4jConfigFile(String configFile) {
		_log4jConfigFile = configFile;
	}

	@Override
	public void setvmName(String vmName) {
		_vmName = vmName;
	}

	@Override
	public String getHostName() {
		return _hostName;
	}

	@Override
	public String getCodebaseUrl() {
		return _codeBaseUrl;
	}

}
