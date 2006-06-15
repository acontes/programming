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
package org.objectweb.proactive.ic2d.monitoring.data;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeAdapterImpl;
import org.objectweb.proactive.core.runtime.RemoteProActiveRuntime;

public class RMIHostRTFinder implements HostRTFinder{
	
	//
	// -- PUBLIC METHODS -----------------------------------------------
	//
	
	public List FindPARuntime(HostObject host) throws RemoteException {
		/* Hook the registry */
		Registry registry = LocateRegistry.getRegistry(host.getHostName(),host.getPort());
		/* Gets a snapshot of the names bounds in the 'registry' */
		String[] names = registry.list();
		/* List of ProActive runtime */
		List runtimes = new ArrayList();
		
		/* Searchs all ProActve Runtimes */
		for (int i = 0; i < names.length; ++i) {
			String name = names[i];
			if (name.indexOf("PA_JVM") != -1) {
				
				RemoteProActiveRuntime remote = null;
				try {
					remote = (RemoteProActiveRuntime) registry.lookup(name);
				} catch (AccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				ProActiveRuntime proActiveRuntime = null;
				try {
					proActiveRuntime = new ProActiveRuntimeAdapterImpl(remote);
				} catch (ProActiveException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				runtimes.add(proActiveRuntime);
			}
		}
		return runtimes;
	}
}
