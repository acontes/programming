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
package org.objectweb.proactive.extensions.jmx.jboss.p2p;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extensions.jmx.jboss.ProActiveJbossLoader;
import org.objectweb.proactive.extra.p2p.service.P2PService;
import org.objectweb.proactive.extra.p2p.service.StartP2PService;

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
public class ProActiveJbossLoaderP2P extends ProActiveJbossLoader implements
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
		// TODO how ???
		PAActiveObject.terminateActiveObject(_p2pService, false);
	}

	@Override
	protected void createService() throws Exception {
		super.createService();
		
		//specific P2P loader config
		configureP2P();
	}
		
	private void configureP2P() {
		// set p2p properties
		if( _acquisitionMethod != null )
			System.setProperty("proactive.p2p.acq", _acquisitionMethod);
		if( _portNumber != 0 )
			System.setProperty("proactive.p2p.port" , ""+_portNumber);
		// the PART name
		if( _vmName != null)
			System.setProperty("proactive.runtime.name", PART_PREFIX + _vmName );
		_jbossLogger.debug("peersListFile=" + _peersListFile + ";acq=" + _acquisitionMethod + ";port=" + _portNumber );
	}
	
	@Override
	protected void startService() throws Exception {
		_jbossLogger.info("Starting the service " + serviceName.getCanonicalName());

		startP2PService();
	}

	@Override
	protected void stopService() throws Exception {
		_jbossLogger.info("Stopping the service " + serviceName.getCanonicalName());
		
		stopP2PService();
	}
	
	////////// 		MBean 	config 	params
	private String _peersListFile;
	private String _acquisitionMethod;
	private int _portNumber;

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