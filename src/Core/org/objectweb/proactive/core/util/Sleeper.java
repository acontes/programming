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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.util;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/** A helper class to sleep a given amount of time in one line
 *
 * Calling Thread.sleep() requires a few lines of code to handle InterruptedException.
 * This code is duplicated everywhere (or missing). This helper should reduce the number 
 * of poorly handled InterruptedException and duplicated code.
 * 
 * If an InterruptionException is thrown while sleeping, it is logged (debug level)
 */
public class Sleeper {
    static final Logger logger = ProActiveLogger.getLogger(Loggers.SLEEPER);
    private long duration;

    /**
     * @param duration the amount of milliseconds to sleep. If 0, {@link #sleep()} returns immediately.
     */
    public Sleeper(long duration) {
        this.duration = duration;
    }

    /** Sleep the predefined amount of time.
     * 
     * It is safe to call this method several times and from different threads.
     */
    public void sleep() {
        if (this.duration == 0) {
            // Avoid to sleep forever
            return;
        }

        TimeoutAccounter timeoutAccounter = TimeoutAccounter.getAccounter(this.duration);
        while (!timeoutAccounter.isTimeoutElapsed()) {
            try {
                Thread.sleep(timeoutAccounter.getRemainingTimeout());
            } catch (InterruptedException e) {
                ProActiveLogger.logEatedException(logger, e);
            }
        }
    }
}
