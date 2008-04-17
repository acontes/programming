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

import java.net.URI;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

import org.jboss.system.ServiceMBeanSupport;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Job;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.rmi.RegistryHelper;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.util.URIBuilder;
import org.objectweb.proactive.extensions.jmx.jboss.tests.DeployerTest;
import org.objectweb.proactive.p2p.service.P2PService;
import org.objectweb.proactive.p2p.service.StartP2PService;
import org.objectweb.proactive.p2p.service.util.P2PConstants;

/**
 * This is the ProActive loader implemented as JBoss service. See
 * {@link ProActiveJbossLoaderP2PMBean} for configuration information (according to
 * JBoss service convention). This loader should be used for tight integration
 * with JBoss. Specifically, it integrates ProActive with JBoss's logger and
 * MBean server. This loader should be used with
 * <tt>[PROACTIVE_HOME/dist/jboss/jboss-service.xml</tt> file shipped with
 * ProActive. <b>Note</b>: JBoss is not shipped with ProActive. If you don't
 * have JBoss, you need to download it separately. See <a target=_blank
 * href="http://www.jboss.com">http://www.jboss.com</a> for more information.
 * 
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
public class ProActiveJbossLoaderP2P extends ServiceMBeanSupport implements
		ProActiveJbossLoaderP2PMBean {

	private P2PService _p2pService; 
	private static final String PART_PREFIX = "PA_JVM";
	
	private void startP2PService() throws ProActiveException {
		
		StartP2PService service;
		if( _peersListFile!=null )
			service = new StartP2PService(_peersListFile);
		else
			service = new StartP2PService();
		service.start();
		_p2pService = service.getP2PService();
		
	}
	
	private void stopP2PService() {
		// terminate the p2p Service AO
		PAActiveObject.terminateActiveObject(_p2pService, false);
	}


	@Override
	protected void createService() throws Exception {
		log.info("Creating service " + serviceName.getCanonicalName());
		// configure ProActive separate logging
		configureLogging();
		log.debug("peersListFile=" + _peersListFile + ";acq=" + _acquisitionMethod + ";port=" + _portNumber );
		
	}

	private void configureLogging() {
		// override the default log4j initialization phase		
		System.setProperty("log4j.defaultInitOverride", "true");
		System.setProperty("log4j.configuration", _log4jConfigFile );
		// set p2p properties
		if( _acquisitionMethod != null )
			System.setProperty("proactive.p2p.acq", _acquisitionMethod);
		if( _portNumber != 0 )
			System.setProperty("proactive.p2p.port" , ""+_portNumber);
		// the PART name
		if( _vmName != null)
			System.setProperty("proactive.runtime.name", PART_PREFIX + _vmName );
	 }
	
	@Override
	protected void startService() throws Exception {
		log.info("Starting the service " + serviceName.getCanonicalName());

		startP2PService();
	}

	@Override
	protected void stopService() throws Exception {
		log.info("Stopping the service " + serviceName.getCanonicalName());
		
		stopP2PService();
	}
	
	////////// 		MBean 	config 	params
	private String _vmName;
	private String _log4jConfigFile;
	private String _peersListFile;
	private String _acquisitionMethod;
	private int _portNumber;

	/**
    * {@inheritDoc}
    */	
	@Override
	public String getvmName() {
		return _vmName;
	}


	/**
    * {@inheritDoc}
    */
	@Override
	public void setvmName(String vmName) {
		_vmName = vmName;
		
	}

	/**
    * {@inheritDoc}
    */
	@Override
	public String getLog4jConfigFile() {
		return _log4jConfigFile;
	}

	/**
    * {@inheritDoc}
    */
	@Override
	public void setLog4jConfigFile(String configFile) {
		_log4jConfigFile = configFile;
	}

	/**
    * {@inheritDoc}
    */
	@Override
	public String getAcquisitionMethod() {
		return _acquisitionMethod;
	}

	/**
    * {@inheritDoc}
    */
	@Override
	public String getPeersListFile() {
		return _peersListFile; 
	}

	/**
    * {@inheritDoc}
    */
	@Override
	public int getPortNumber() {
		return _portNumber;
	}

	/**
    * {@inheritDoc}
    */
	@Override
	public void setAcquisitionMethod(String acquisitionMethod) {
		_acquisitionMethod = acquisitionMethod;
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public void setPeersListFile(String peersListFile) {
		_peersListFile = peersListFile;
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	public void setPortNumber(int portNumber) {
		_portNumber = portNumber;
	}
		
}
