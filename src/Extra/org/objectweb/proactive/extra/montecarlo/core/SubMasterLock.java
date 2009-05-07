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
package org.objectweb.proactive.extra.montecarlo.core;

import java.io.Serializable;


/**
 * SubMasterLock
 *
 * @author The ProActive Team
 */
public class SubMasterLock implements Serializable {

    private boolean simulatorInUse = false;
    private boolean executorInUse = false;

    private final static String message = "Concurrent use of Executor and Simulator is not possible.";

    public SubMasterLock() {

    }

    void useSimulator() throws IllegalStateException {
        if (executorInUse) {
            throw new IllegalStateException(message);
        } else {
            simulatorInUse = true;
        }
    }

    void releaseSimulator() throws IllegalStateException {
        simulatorInUse = false;
    }

    void useExecutor() throws IllegalStateException {
        if (simulatorInUse) {
            throw new IllegalStateException(message);
        } else {
            executorInUse = true;
        }
    }

    void releaseExecutor() throws IllegalStateException {
        executorInUse = false;
    }

}
