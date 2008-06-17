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
package org.objectweb.proactive.extensions.jee;

import java.lang.management.ManagementFactory;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

import org.objectweb.proactive.core.jmx.mbean.ProActiveRuntimeWrapperMBean;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.rmi.RegistryHelper;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;

/**
 * Resource adapter for the ProActive library
 * see jsr 112
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
public class ProActiveResourceAdapter extends ProActiveConnectorBean
	implements ResourceAdapter {

	private ProActiveRuntimeImpl _proActiveRuntime;
	private static final String PART_PREFIX = "PA_JVM";
	
	/* (non-Javadoc)
	 * @see javax.resource.spi.ResourceAdapter#endpointActivation(javax.resource.spi.endpoint.MessageEndpointFactory, javax.resource.spi.ActivationSpec)
	 */
	@Override
	public void endpointActivation(MessageEndpointFactory arg0,
			ActivationSpec arg1) throws ResourceException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ResourceAdapter#endpointDeactivation(javax.resource.spi.endpoint.MessageEndpointFactory, javax.resource.spi.ActivationSpec)
	 */
	@Override
	public void endpointDeactivation(MessageEndpointFactory arg0,
			ActivationSpec arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ResourceAdapter#getXAResources(javax.resource.spi.ActivationSpec[])
	 */
	@Override
	public XAResource[] getXAResources(ActivationSpec[] arg0)
			throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * This method will be called when a new ProActive ResourceAdapter
	 * will be bootstrapped into the application server. 
	 */
	@Override
	public void start(BootstrapContext arg0)
			throws ResourceAdapterInternalException {
		//configure first
		super.configureRA();
		
		// the PART name
		_raLogger.debug("Set the vmName to " + _vmName);
		System.setProperty("proactive.runtime.name", PART_PREFIX + _vmName );

		// actually create the runtime
		_proActiveRuntime = ProActiveRuntimeImpl.getProActiveRuntime();
		_raLogger.debug("New PART created succesfully at URL:" + _proActiveRuntime.getMBean().getURL());

	}

	/* 
	 * This method is called when the ProActive ResourceAdapter is
	 * undeployed from the application server
	 */
	@Override
	public void stop() {
		_raLogger.info("terminating Runtime " + _proActiveRuntime.getURL());
        
		// kill the nodes deployed up until now
		_proActiveRuntime.killAllNodes();
		
		// destroy the JMX MBean associated with this runtime
		destroyMBean();
		
		// unregister from the RMI Registry
		unregisterRuntime();

	}
	
	private void unregisterRuntime() {
		try {
			// interesting class, this Registry helper!
			RegistryHelper regHelper = new RegistryHelper();
			regHelper.initializeRegistry();
			Registry rmiRegistry = RegistryHelper.getRegistry();
			// unbind the name onto which PART is bound
			rmiRegistry.unbind(PART_PREFIX + _vmName);
		} catch ( RemoteException  e) {
			_raLogger.error( "Unable to contact the RMI registry that is (supposed to be) on the localhost." ); 
			_raLogger.error( e.getMessage() , e );
		} catch (NotBoundException e) {
			_raLogger.error( "The name " + PART_PREFIX + _vmName + " is not bound in the RMI Registry." );
			_raLogger.error( e.getMessage() , e );
		}
	}
	
	private void destroyMBean() {
		// Deal with the JMX MBeans
        ProActiveRuntimeWrapperMBean mbean = _proActiveRuntime.getMBean(); 
        if ( mbean != null) {
            mbean.sendNotification(NotificationType.runtimeDestroyed);

            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = mbean.getObjectName();
            if (mbs.isRegistered(objectName)) {
                try {
                    mbs.unregisterMBean(objectName);
                } catch (InstanceNotFoundException e) {
                    _raLogger.error("The MBean with the objectName " + objectName + " was not found", e);
                } catch (MBeanRegistrationException e) {
                    _raLogger.error("The MBean with the objectName " + objectName +
                        " can't be unregistered from the MBean server", e);
                }
            }
            mbean = null;
        }
	}
	
	/* 
	 * I need this to make sure that the AS will not create two separate instances of the RA
	 * For now, two RAs are considered to be equal if they create a PART with the same name
	 */
	@Override
	public boolean equals(Object obj) {
		ProActiveResourceAdapter raObj = (ProActiveResourceAdapter)obj;
		return _vmName.equals(raObj.getVmName());
	}

}
