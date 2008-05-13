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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;

/**
 * ProActive Registry JBoss Service implementation
 * This class registers a ProActive Runtime object in the RMI registry
 * The runtime can then be used later, by acquiring it using 
 * 	the RMIRegistryLookup service provided by the ProActive library
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
@Stateless
@Local ({ProActiveEjbLoaderRegistryInterface.class})
@LocalBinding (jndiBinding="ProActive/RegistryBean/Local")
@Remote ({ProActiveEjbLoaderRegistryInterface.class})
@RemoteBinding (jndiBinding="ProActive/RegistryBean/Remote")
public class ProActiveEjbLoaderRegistryBean extends ProActiveEjbLoader 
	implements	ProActiveEjbLoaderRegistryInterface {
	
	private ProActiveRuntimeImpl _proActiveRuntime;
	private static final String PART_PREFIX = "PA_JVM";
	
	private void configureRuntime() {
		// generic Loader configuration
		super.createService();
		// specific configuration for the Registry bean
		// set the PART name
		System.setProperty("proactive.runtime.name", PART_PREFIX + _vmName );
	}
	
	/**
	 * starts a ProActive runtime within JBoss AS
	 * @see org.objectweb.proactive.core.runtime.StartRuntime.run()
	 * @see org.objectweb.proactive.p2p.service.StartP2PService.start()
	 * @see org.objectweb.proactive.extensions.jmx.jboss.ProActiveJbossLoaderRegistry.startProActiveRuntime()
	 */
	private void startProActiveRuntime() {	
		_proActiveRuntime = ProActiveRuntimeImpl.getProActiveRuntime();
		_ejbLogger.debug("New PART created succesfully at URL:" + _proActiveRuntime.getMBean().getURL());
	}
	
	/**
	 * stops the ProActiveRuntime started within the Application Server
	 * TODO but, how???
	 * @see org.objectweb.proactive.extensions.jmx.jboss.ProActiveJbossLoaderRegistry.stopProActiveRuntime()
	 */
	private void stopProActiveRuntime() {
		// kill the nodes deployed until now
		_proActiveRuntime.killAllNodes();
		
		// TODO maybe unregister from RMI Registry?
		
		// JMX Notification
        if (_proActiveRuntime.getMBean() != null) {
            _proActiveRuntime.getMBean().sendNotification(NotificationType.runtimeDestroyed);
        }
	}

	@Override
	public void startService(){
		// first, some configuration
		configureRuntime();

		_ejbLogger.debug("Starting the ProActive service...");
		startProActiveRuntime();
	}
	
	@Override
	public void stopService() {
		_ejbLogger.debug("Stopping the ProActive service...");
		stopProActiveRuntime();
	}

	@Override
	public String getProActiveRuntimeURL() {
		return _proActiveRuntime.getURL().toString();	}

}
