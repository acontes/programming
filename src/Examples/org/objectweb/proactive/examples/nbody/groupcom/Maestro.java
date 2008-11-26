/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.examples.nbody.groupcom;

import java.io.Serializable;

import org.objectweb.proactive.api.PAGroup;
import org.objectweb.proactive.examples.nbody.common.Deployer;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;


/**
 * Synchronization of the computation of the Domains
 */
@ActiveObject
public class Maestro implements Serializable {
    private Domain domainGroup;
    private int nbFinished = 0;
    private int iter = 0;
    private int maxIter;
    private int size;
    private Deployer deployer;

    /**
     * Required by ProActive Active Objects
     */
    public Maestro() {
    }

    /**
     * Called by a Domain when computation is finished.
     * This method counts the answers, and restarts all Domains when all have finished.
     */
    public void notifyFinished() {
        nbFinished++;
        if (nbFinished == size) {
            iter++;
            if (iter == maxIter) {
                deployer.terminateAllAndShutdown(false);
                return;
            }
            nbFinished = 0;
            domainGroup.sendValueToNeighbours();
        }
    }
}
