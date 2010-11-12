/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 *              Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
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
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.examples.components.sca.timeoutintent;

import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.IntentJoinPoint;


//@snippet-start component_scauserguide_4

public class TimeOutIntentHandler extends IntentHandler {
    private static final long DEFAULT_TIMEOUT = 5000;

    private long timeout = DEFAULT_TIMEOUT;

    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }

    public Object invoke(IntentJoinPoint ijp) throws Throwable {
        ProceedThread pt = new ProceedThread();
        pt.ijp = ijp;
        pt.start();

        try {
            pt.join(timeout);
            if (pt.isAlive()) {
                throw new Exception("Service Unavailable Timeout of " + timeout + " ms exceeded");
            }
        } catch (InterruptedException e) {
            System.err.println("call interrupted");
        }

        if (pt.throwable != null) {
            throw pt.throwable;
        }
        return pt.result;
    }
}

class ProceedThread extends Thread {
    IntentJoinPoint ijp = null;

    Object result = null;

    Throwable throwable = null;

    public void run() {
        try {
            result = ijp.proceed();
        } catch (Throwable e) {
            throwable = e;
        }
    }
}
//@snippet-end component_scauserguide_4