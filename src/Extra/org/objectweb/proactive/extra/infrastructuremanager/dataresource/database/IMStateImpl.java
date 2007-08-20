/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
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
package org.objectweb.proactive.extra.infrastructuremanager.dataresource.database;

import java.util.List;

import org.objectweb.proactive.extra.infrastructuremanager.dataresource.IMState;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;

public class IMStateImpl implements IMState {
	
	private List<IMNode> free;
	private List<IMNode> busy;
	private List<IMNode> down;
	
	public IMStateImpl (List<IMNode> free, List<IMNode> busy, List<IMNode> down) {
		this.free = free;
		this.busy = busy;
		this.down = down;
	}

	public List<IMNode> getBusyNodes() {
		return free;
	}

	public List<IMNode> getDownNodes() {
		return busy;
	}

	public List<IMNode> getFreeNodes() {
		return down;
	}

}
