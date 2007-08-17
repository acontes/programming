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
package org.objectweb.proactive.ic2d.dgc.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.ic2d.monitoring.data.AOObject;
import org.objectweb.proactive.ic2d.monitoring.data.AbstractDataObject;
import org.objectweb.proactive.ic2d.monitoring.data.NodeObject;
import org.objectweb.proactive.ic2d.monitoring.data.WorldObject;
import org.objectweb.proactive.ic2d.monitoring.spy.Spy;

public class ObjectGraph {
	private static Map<UniqueID, AOObject> AOObjectByID = new HashMap<UniqueID, AOObject>();

	public static void addObject(AOObject ao) {
		AOObjectByID.put(ao.getID(), ao);
	}
	
	private static Collection<AbstractDataObject> getAllChildren(Collection<AbstractDataObject> parents) {
		Collection<AbstractDataObject> res = new Vector<AbstractDataObject>();
		for (AbstractDataObject o : parents) {
			res.addAll(o.getMonitoredChildren());
		}
		return res;
	}
	
	private static Collection<AOObject> bodyIDToAOObject(Collection<UniqueID> ids) {
		Collection<AOObject> aos = new Vector<AOObject>();
		for (UniqueID id : ids) {
			AOObject ao = AOObjectByID.get(id);
			if (ao != null) {
				aos.add(ao);
			} else {
				System.out.println("Body " + id + " not found");
			}
		}
		return aos;
	}
	
	public static Map<AOObject, Collection<AOObject>> getObjectGraph(WorldObject world) {
		Collection<AbstractDataObject> hosts = world.getMonitoredChildren();
		Collection<AbstractDataObject> runtimes = getAllChildren(hosts);
		Collection<AbstractDataObject> nodes = getAllChildren(runtimes);
		
		Map<AOObject, Collection<AOObject>> res = new HashMap<AOObject, Collection<AOObject>>();
		for (AbstractDataObject o : nodes) {
			Spy spy = ((NodeObject) o).getSpy();
			Collection<AbstractDataObject> aos = o.getMonitoredChildren();
			for (AbstractDataObject oo : aos) {
				AOObject ao = (AOObject) oo;
				UniqueID bodyID = ((AOObject) oo).getID();
				Collection<UniqueID> referencesID = spy.getReferenceList(bodyID);
				Collection<AOObject> referencesAO = bodyIDToAOObject(referencesID);
				res.put(ao, referencesAO);
			}
		}
		return res;
	}
}
