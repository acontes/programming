/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.ic2d.finder;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeAdapterImpl;
import org.objectweb.proactive.core.runtime.RemoteProActiveRuntime;
import org.objectweb.proactive.ic2d.data.HostObject;
import org.objectweb.proactive.ic2d.data.VMObject;
import org.objectweb.proactive.ic2d.logger.IC2DLoggers;

public class RMIHostRTFinder extends HostRTFinder {

	
	//
    // -- CONSTRUCTORS -----------------------------------------------
    //
    public RMIHostRTFinder(DefaultListModel skippedObjects) {
        super(skippedObjects);
    }
	
	//
    // -- PUBLIC METHODS -----------------------------------------------
    //
	
	//
    // -- implements HostRTFinder -----------------------------------------------
    //
	public List findPARuntimes(HostObject host) throws IOException {
		IC2DLoggers.getInstance().log("Exploring " + host.getHostName() + " with RMI on port " + host.getPort());
		// Hook the registry
		Registry registry = LocateRegistry.getRegistry(host.getHostName(), 
				host.getPort());
		return findPARuntimes(registry, host);
	}

	//
    // -- PRIVATE METHODS -----------------------------------------------
    //
	
	private List findPARuntimes(Registry registry, HostObject host) throws IOException {
		// enumarate through the rmi binding on the registry
        IC2DLoggers.getInstance().log("Listing bindings for " + registry);
        String[] list = registry.list();
        List runtimeArray = new ArrayList();
        for (int idx = 0; idx < list.length; ++idx) {
            String id = list[idx];
            if (id.indexOf("PA_JVM") != -1) {
                ProActiveRuntime part = null;
                try {
                	RemoteProActiveRuntime r = (RemoteProActiveRuntime) registry.lookup(id);
                	part = new ProActiveRuntimeAdapterImpl(r);
                	runtimeArray.add(part);
                } catch (Exception e) {
                	// we build a jvmObject which won't be monitored
                	VMObject jvmObject = new VMObject(host);
                	if(! skippedObjects.contains(jvmObject)){
                        IC2DLoggers.getInstance().log(e.getMessage(), e, false);
                        skippedObjects.addElement(jvmObject);
                	}
                }
            }
        }
		return runtimeArray;
	}
}
