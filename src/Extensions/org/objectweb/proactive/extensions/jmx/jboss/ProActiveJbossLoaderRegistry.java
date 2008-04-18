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

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;


public class ProActiveJbossLoaderRegistry extends ProActiveJbossLoader 
		implements ProActiveJbossLoaderRegistryMBean{
	
	private ProActiveRuntimeImpl _proActiveRuntime;
	private static final String PART_PREFIX = "PA_JVM";
	
	/**
	 * starts a ProActive runtime within JBoss AS
	 * @see org.objectweb.proactive.core.runtime.StartRuntime.run()
	 * @see org.objectweb.proactive.p2p.service.StartP2PService.start()
	 */
	private void startProActiveRuntime() throws NodeException, ActiveObjectCreationException {
		_proActiveRuntime = ProActiveRuntimeImpl.getProActiveRuntime();
		_jbossLogger.debug("New PART created succesfully at URL:" + _proActiveRuntime.getMBean().getURL());

	}
	
	private void configureRuntime() {
		// the PART name
		System.setProperty("proactive.runtime.name", PART_PREFIX + _vmName );
	}
	
	/**
	 * stops the ProActiveRuntime started within the Application Server
	 */
	private void stopProActiveRuntime() {
		// TODO how stop PART??
		// JMX Notification
        if (_proActiveRuntime.getMBean() != null) {
            _proActiveRuntime.getMBean().sendNotification(NotificationType.runtimeDestroyed);
        }
	}
	
	@Override
	protected void createService() throws Exception {
		super.createService();
		
		// specific RegistryLoader configuration
		configureRuntime();
	}
	
	
	@Override
	protected void startService() throws Exception {
		_jbossLogger.info("Starting the service " + serviceName.getCanonicalName());
		startProActiveRuntime();
		
	}

	@Override
	protected void stopService() throws Exception {
		_jbossLogger.info("Stopping the service " + serviceName.getCanonicalName());
		
		stopProActiveRuntime();
	}
	
	////////// 		MBean 	config 	params
	
	@Override
	public String getProActiveRuntimeURL() {
		return _proActiveRuntime.getURL().toString();
	}

	
}
