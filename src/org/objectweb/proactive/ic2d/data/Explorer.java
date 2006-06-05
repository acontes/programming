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
package org.objectweb.proactive.ic2d.data;

import java.io.IOException;
import java.util.List;

import javax.swing.DefaultListModel;

import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.ic2d.finder.HostRTFinder;
import org.objectweb.proactive.ic2d.finder.RMIHostRTFinder;
import org.objectweb.proactive.ic2d.logger.IC2DLoggers;


public class Explorer {
	
	private DefaultListModel skippedObjects;
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	public Explorer() {
		this.skippedObjects = new DefaultListModel();
	}
	
	//
	// -- PUBLIC METHODS -----------------------------------------------
	//
	
	public void exploreHost(HostObject host) {
		HostRTFinder runtimeFinder = initiateFinder(host.getProtocol());
		if (skippedObjects.contains(host)) {
			return;
		}
		List foundRuntimes = null;
		try {
			foundRuntimes = runtimeFinder.findPARuntimes(host);
		} catch (IOException e) {
			// if an IOException is thrown when connecting the registry, the host is removed
			skippedObjects.addElement(host);
			IC2DLoggers.getInstance().log(e);
		}
		if (foundRuntimes != null) {
			for (int idx = 0; idx < foundRuntimes.size(); ++idx) {
				ProActiveRuntime part = (ProActiveRuntime) foundRuntimes.get(idx);
				handleProActiveRuntime(part);
			}
		}
	}
	
	/**
	 * TODO comment
	 * @param protocol
	 * @return TODO, null if the protocol is undefined
	 */
	public HostRTFinder initiateFinder(int protocol) {
		switch(protocol) {
		case Protocol.RMI:
		case Protocol.RMISSH:
			return new RMIHostRTFinder(skippedObjects);
		default:
			return null; //TODO dangerous ???		
		}
	}
	
	//
	// -- PUBLIC METHODS -----------------------------------------------
	//
	
	private void handleProActiveRuntime(ProActiveRuntime pr) {
		
	}
	
}
