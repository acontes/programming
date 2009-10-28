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
package org.objectweb.proactive.mpi.control;

import java.util.List;
import java.util.Vector;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.mpi.MPISpmd;


public class ProActiveMPI {
    private static ProActiveMPIManager manager;

    public static Vector<?> deploy(List<MPISpmd> spmdList) {
        if (manager == null) {
            // create manager
            try {
                manager = (ProActiveMPIManager) PAActiveObject.newActive(ProActiveMPIManager.class.getName(),
                        new Object[] {});
                manager.deploy(spmdList);
            } catch (ActiveObjectCreationException e) {
                e.printStackTrace();
            } catch (NodeException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalStateException(" ERROR: Application has already been deployed once !!!!!!!");
        }
        return null;
    }

    public static ProActiveMPIManager getManager() {
        return manager;
    }
}