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

import java.rmi.RemoteException;
import java.rmi.dgc.VMID;
import java.util.List;

import org.objectweb.proactive.core.runtime.ProActiveRuntime;

public class Explorer {
	
	/** The default exploration's depth */
	public final static int DefaultDepth = 3;
	
	/* Explores the host, in order to find his VMs */
	public void exploreHost(HostObject host){
		HostRTFinder runtimeFinder = HostRTFinderFactory.createHostRTFinder(host.getProtocol());
		List foundRuntimes = null;
		try {
			foundRuntimes = runtimeFinder.FindPARuntime(host);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(foundRuntimes != null){
			 for (int i = 0; i < foundRuntimes.size(); ++i) {
	                ProActiveRuntime proActiveRuntime = (ProActiveRuntime) foundRuntimes.get(i);
	                handleProActiveRuntime(host, proActiveRuntime, 1);
	            }
		}
	}
	
	public void handleProActiveRuntime(HostObject parent, ProActiveRuntime runtime, int depth){
		VMID vmid = runtime.getVMInformation().getVMID();
		VMObject vm = new VMObject(parent, vmid);
		//TODO Finish this method!!!
	}
	
}
