/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
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
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.ic2d.dgc.data;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.ActiveObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.WorldObject;


public class ObjectGraph {
    private static Map<UniqueID, ActiveObject> ActiveObjectByID = new HashMap<UniqueID, ActiveObject>();

    public static void addObject(ActiveObject ao) {
        ActiveObjectByID.put(ao.getUniqueID(), ao);
    }

    private static Collection<ActiveObject> bodyIDToActiveObject(Collection<UniqueID> ids) {
        Collection<ActiveObject> aos = new Vector<ActiveObject>();
        for (UniqueID id : ids) {
            ActiveObject ao = ActiveObjectByID.get(id);
            if (ao != null) {
                aos.add(ao);
            } else {
                System.out.println("Body " + id + " not found");
            }
        }
        return aos;
    }

    @SuppressWarnings("unchecked")
    public static Map<ActiveObject, Collection<ActiveObject>> getObjectGraph(WorldObject world) {
        Map<String, ActiveObject> activeObjects = world.getActiveObjects();

        Map<ActiveObject, Collection<ActiveObject>> res = new HashMap<ActiveObject, Collection<ActiveObject>>();
        for (ActiveObject ao : activeObjects.values()) {
            Collection<UniqueID> referencesID = null;
            try {
                referencesID = (Collection<UniqueID>) ao.getAttribute("ReferenceList");
                Collection<ActiveObject> referencesAO = bodyIDToActiveObject(referencesID);
                res.put(ao, referencesAO);
            } catch (InstanceNotFoundException e) {
                e.printStackTrace();
            } catch (MBeanException e) {
                e.printStackTrace();
            } catch (ReflectionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AttributeNotFoundException e) {
                e.printStackTrace();
            }
        }

        return res;
    }
}
