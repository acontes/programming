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
package org.objectweb.proactive.extensions.ejb;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;

/**
 * Abstract class, grouping common functionality implemented by all the Loader beans
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
public abstract class ProActiveEjbLoader implements ProActiveEjbLoaderInterface {

	protected static final int CODEBASE_PORT = 20260;
	protected static final Logger _ejbLogger = Logger.getLogger(Loggers.JBOSS);
	
	
	/**
	 * Configure the ProActive Service
	 */
	protected void createService(){
		_ejbLogger.info( "Configuring the ProActive EJB Service...");
		// configure ProActive separate logging
		configureLogging();
		
		// configure communication protocol-specific parameters
		configureCommunication();
	}
	
	// hardcoded for now TODO
	protected static final String LOG4J_CONFIG = "file:/auto/sea/u/sea/0/user/fabratu/tools/jboss/jboss-4.2.2.GA/server/default/conf/jboss-log4j.xml";
	
	protected void configureLogging() {
		// override the default log4j initialization phase		
		System.setProperty("log4j.defaultInitOverride", "true");
		System.setProperty("log4j.configuration", _log4jConfigFile );

	 }
	
	/**
	 * Setting specific communication parameters, for the communication protocol used
	 */
	protected void configureCommunication(){
		// TODO is this necessary ???
		try {
			_hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			_ejbLogger.debug("Cannot get the host name of the host we are running in.");
			_ejbLogger.debug("Going for the <<default>> option -> localhost");
			_ejbLogger.debug("This was also the default behaviour if the parameters were not set.");
			_hostName = "localhost";
		}
		_codeBaseUrl = "http://" + _hostName + ":" + CODEBASE_PORT +"/";
		
		String communicationProtocol = System.getProperty("proactive.communication.protocol");
		// if not set, assume communication protocol is RMI
		if( communicationProtocol == null || 
				(communicationProtocol != null && communicationProtocol.equals("rmi") ) ) {
			
			System.setProperty("proactive.http.port", CODEBASE_PORT + "");
			System.setProperty("java.rmi.server.hostname", _hostName);
			
			System.setProperty("java.rmi.server.codebase", _codeBaseUrl );
			System.setProperty("proactive.net.nolocal", "true" );
			_ejbLogger.debug("Configuration info: hostName : " + _hostName + 
					"; codebase URL : " + _codeBaseUrl );
		}
		
	}
	
	////////// 		MBean 	config 	params
	protected String _vmName;
	protected String _hostName;
	protected String _codeBaseUrl;
	protected String _log4jConfigFile = LOG4J_CONFIG;
	
	@Override
	public String getLog4jConfigFile() {
		return _log4jConfigFile;
	}


	@Override
	public void setLog4jConfigFile(String configFile) {
		_log4jConfigFile = configFile;
	}
	

	@Override
	public String getvmName() {
		return _vmName;
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
