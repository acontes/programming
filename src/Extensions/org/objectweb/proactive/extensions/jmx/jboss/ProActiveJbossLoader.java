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

import java.io.IOException;
import java.lang.annotation.Inherited;
import java.net.URI;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jboss.system.ServiceMBeanSupport;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Job;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PADeployment;
import org.objectweb.proactive.core.Constants;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.remoteobject.RemoteObjectExposer;
import org.objectweb.proactive.core.remoteobject.rmi.RmiRemoteObjectFactory;
import org.objectweb.proactive.core.rmi.RegistryHelper;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeRemoteObjectAdapter;
import org.objectweb.proactive.core.runtime.RuntimeFactory;
import org.objectweb.proactive.core.util.URIBuilder;
import org.objectweb.proactive.extensions.jmx.jboss.tests.DeployerTest;
import org.objectweb.proactive.p2p.service.P2PService;
import org.objectweb.proactive.p2p.service.StartP2PService;
import org.objectweb.proactive.p2p.service.util.P2PConstants;

/**
 * This is the ProActive loader implemented as JBoss service. See
 * {@link ProActiveJbossLoaderMBean} for configuration information (according to
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
public class ProActiveJbossLoader extends ServiceMBeanSupport implements
		ProActiveJbossLoaderMBean {

	private ProActiveRuntimeImpl _proActiveRuntime;
	private P2PService _p2pService; 
	
	/**
	 * starts a ProActive runtime within JBoss AS
	 * @see org.objectweb.proactive.core.runtime.StartRuntime.run()
	 */
	private void startProActiveRuntime() {

		getLog().debug("Creating a new ProActiveRuntime...");
		_proActiveRuntime = ProActiveRuntimeImpl.getProActiveRuntime();
		_proActiveRuntime.setVMName(_vmName);
		getLog().debug("New PART created succesfully at URL:" + _proActiveRuntime.getMBean().getURL());
		
	}
	
	/**
	 * Starts a P2P Service within the application server
	 * @see org.objectweb.proactive.p2p.service.StartP2PService.start()
	 */
	private void startP2PService() throws NodeException, ActiveObjectCreationException {

		// start the ProActive runtime on this local node
		startProActiveRuntime();

		// get p2p params
		String acquisitionMethod = PAProperties.PA_P2P_ACQUISITION.getValue();
		String p2pPort = PAProperties.PA_P2P_PORT.getValue();
		String p2pNodeName = P2PConstants.P2P_NODE_NAME;
		getLog().debug("P2P acquisition method: " + acquisitionMethod + ";port number:" + p2pPort + ";p2p node name:" + p2pNodeName );

		int portNumer = Integer.parseInt(p2pPort);

		String url = null;
		try {
			// if no registry started on port p2pPort , now it's the time to start it!
			getOrCreateRegistry(portNumer);

			// create local node
			URI nodeURI = URIBuilder.buildURI("localhost", p2pNodeName,
					acquisitionMethod, portNumer );
			getLog().debug( "Creating a local node on nodeURI = " + nodeURI );
			url = _proActiveRuntime.createLocalNode( nodeURI.toString() , false , null, _vmName, 
					Job.DEFAULT_JOBID);
			getLog().debug( "Local node created!" );
		} catch (AlreadyBoundException e) {
			getLog().warn("This name " + p2pNodeName + " is already bound in the registry ", e);
		} catch (RemoteException e) {
			getLog().error( " I could neither locate nor create a rmiregistry " +
					"on localhost , port " + portNumer );
			getLog().error(" Without it, the P2PService active object cannot be started.");
			getLog().error( e.getMessage() , e );
		}

		// P2PService Active Object Creation
		_p2pService = (P2PService) PAActiveObject.newActive(
				P2PService.class.getName(), null, url);

	}
	
	private void getOrCreateRegistry(int portNumber) throws RemoteException {
		RegistryHelper registryHelper = new RegistryHelper();
		registryHelper.setRegistryPortNumber(portNumber);
		registryHelper.initializeRegistry();
	}
	
	private void stopP2PService() {
		// terminate the p2p Service AO
		PAActiveObject.terminateActiveObject(_p2pService, false);
	}

	/**
	 * stops the ProActiveRuntime started within the Application Server
	 */
	private void stopProActiveRuntime() {
		// JMX Notification
        if (_proActiveRuntime.getMBean() != null) {
            _proActiveRuntime.getMBean().sendNotification(NotificationType.runtimeDestroyed);
        }
	}
	private DeployerTest<Test> _deplTest;
	@Override
	protected void createService() throws Exception {
		log.info("Creating service " + serviceName.getCanonicalName());
		// configure ProActive separate logging
		configureLogging();
		
		// test
		/*_deplTest = new DeployerTest<Test>( "computingNode" , 
				"file:///user/fabratu/home/workspace/dd/deploy.xml" , 
					Test.class.getName() , log );*/
	}

	private void configureLogging() {
		// override the default log4j initialization phase		
		System.setProperty("log4j.defaultInitOverride", "true");
		System.setProperty("log4j.configuration", _log4jConfigFile ); 		
	 }
	
//	private void testProperty(String propName) {
//		String log4j = System.getProperty(propName);
//		if (log4j == null) {
//			getLog().info("No property " + propName );
//		}
//		else {
//			getLog().info( propName + " is :  " + log4j);
//		}
//	}

	@Override
	protected void startService() throws Exception {
		log.info("Starting the service " + serviceName.getCanonicalName());

		//startProActiveRuntime();
		//_deplTest.deploy();
		startP2PService();
	}

	@Override
	protected void stopService() throws Exception {
		log.info("Stopping the service " + serviceName.getCanonicalName());
		
		//stopProActiveRuntime();
		//_deplTest.undeploy();
		stopP2PService();
	}
	
	
	
	////////// 		MBean 	config 	params
	private String _vmName;
	private String _log4jConfigFile;

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
	public String getProActiveRuntimeURL() {
		return _proActiveRuntime.getMBean().getURL();
	}

		
}
