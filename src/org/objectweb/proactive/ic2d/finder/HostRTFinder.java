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

import java.util.List;

import javax.swing.DefaultListModel;

import org.objectweb.proactive.ic2d.data.HostObject;

public abstract class HostRTFinder {
	
	protected DefaultListModel skippedObjects;
	
	//
    // -- CONSTRUCTORS -----------------------------------------------
    //
	
	public HostRTFinder(DefaultListModel skippedObjects) {
		this.skippedObjects = skippedObjects;
	}
	
	//
    // -- PUBLIC METHODS -----------------------------------------------
    //
	
	// List<ProActiveRuntime>
	public abstract List findPARuntimes(HostObject host) throws java.io.IOException;
	
}
