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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.examples.userguide.cmagent.initialized;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.EndActive;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.core.util.wrapper.LongWrapper;
import org.objectweb.proactive.examples.userguide.cmagent.simple.CMAgent;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;


@ActiveObject
public class CMAgentInitialized extends CMAgent implements InitActive, RunActive, EndActive {
    private long lastRequestDuration;
    private long startTime;
    private long requestsServed = 0;

    //@snippet-start cma_init_full
    public void initActivity(Body body) {
        //TODO 1. Print start information
        System.out.println("### Started Active object " + body.getMBean().getName() + " on " +
            body.getMBean().getNodeUrl());

        //TODO 2. Record start time
        startTime = System.currentTimeMillis();
    }

    public void runActivity(Body body) {
        Service service = new Service(body);
        long currentRequestDuration = 0;
        while (body.isActive()) {
            //TODO 3. wait for a request
            service.waitForRequest(); // block until a request is received

            //TODO 4. Record time
            currentRequestDuration = System.currentTimeMillis();

            //TODO 5. Serve request
            service.serveOldest(); //server the requests in a FIFO manner

            //TODO 6. Calculate request duration
            currentRequestDuration = System.currentTimeMillis() - currentRequestDuration;

            // an intermediary variable is used so
            // when calling getLastRequestServeTime()
            // we get the first value before the last request
            // i.e when calling getLastRequestServeTime
            // the lastRequestDuration is update with the
            // value of the getLastRequestServeTime call
            // AFTER the previous calculated value has been returned
            lastRequestDuration = currentRequestDuration;

            //TODO 7. Increment the number of requests served
            requestsServed++;
        }
    }

    public void endActivity(Body body) {
        //TODO 8. Calculate the running time of the active object using the start time recorded in initActivity()
        long runningTime = System.currentTimeMillis() - startTime;

        //TODO 9. Print various stop information
        System.out.println("### You have killed the active object. The final" + " resting place is on " +
            body.getNodeURL() + "\n### It has faithfully served " + requestsServed + " requests " +
            "and has been an upstanding active object for " + runningTime + " ms ");
    }

    public LongWrapper getLastRequestServeTime() {
        //TODO 10. Use wrappers for primitive types so the calls are asynchronous
        return new LongWrapper(lastRequestDuration);
    }
    //@snippet-end cma_init_full

}
