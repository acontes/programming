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
package org.objectweb.proactive.core.body.ft.protocols.replay.servers;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.ft.internalmsg.Heartbeat;
import org.objectweb.proactive.core.body.ft.servers.FTServer;
import org.objectweb.proactive.core.body.ft.servers.faultdetection.FaultDetector;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * An implementation of the FaultDetector
 * @author The ProActive Team
 */
public class FaultDetectorReplay implements FaultDetector {
    //logger
    protected static Logger logger = ProActiveLogger.getLogger(Loggers.FAULT_TOLERANCE);

    // global server
    private FTServer server;

    // static heartbeat message
    private static final Heartbeat hbe = new Heartbeat();

    /**
     *
     */
    public FaultDetectorReplay(FTServer server) {
        this.server = server;
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.faultdetection.FaultDetector#isUnreachable(org.objectweb.proactive.core.body.UniversalBody)
     */
    public boolean isUnreachable(UniversalBody body) {
        Object res = null;
        try {
            res = body.receiveFTMessage(FaultDetectorReplay.hbe);
        } catch (Exception e) {
            // object is unreachable
            return true;
        }
        if (res.equals(FaultDetector.OK)) {
            // object is OK
            return false;
        } else {
            // object is dead
            return true;
        }
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.faultdetection.FaultDetector#startFailureDetector()
     */
    public void startFailureDetector() {
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.faultdetection.FaultDetector#suspendFailureDetector()
     */
    public void suspendFailureDetector() {
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.faultdetection.FaultDetector#stopFailureDetector()
     */
    public void stopFailureDetector() {
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.faultdetection.FaultDetector#forceDetection()
     */
    public void forceDetection() {
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.faultdetection.FaultDetector#initialize()
     */
    public void initialize() {
    }
}
