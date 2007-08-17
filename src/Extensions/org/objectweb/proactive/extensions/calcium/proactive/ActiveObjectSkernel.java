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
package org.objectweb.proactive.extensions.calcium.proactive;

import java.io.Serializable;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestFilter;
import org.objectweb.proactive.extensions.calcium.Skernel;
import org.objectweb.proactive.extensions.calcium.Task;


public class ActiveObjectSkernel extends Skernel implements RunActive,
    Serializable {
    public ActiveObjectSkernel() {
    }

    public ActiveObjectSkernel(Skernel skernel) {
        super();

        while (skernel.getReadyQueueLength() > 0) {
            this.addReadyTask(skernel.getReadyTask(0));
        }
    }

    //Producer-Consumer
    public void runActivity(Body body) {
        Service service = new Service(body);

        while (true) {
            String allowedMethodNames = "getStats|addReadyTask|putProcessedTask|getReadyQueueLength|hasResults|isFinished|isPaniqued|getStatsGlobal";

            if (getReadyQueueLength() > 0) {
                allowedMethodNames += "getReadyTask|";
            }
            if (hasResults()) {
                allowedMethodNames += "getResult|";
            }

            service.blockingServeOldest(new RequestFilterOnAllowedMethods(
                    allowedMethodNames));
        }
    }

    @SuppressWarnings("unchecked")
    public Task<?> getReadyTask() {
        Task<?> task = super.getReadyTask(0);
        if (task == null) { //ProActive doesn't handle null
            task = new Task<Object>();
            task.setDummy();
            return task; //return dummy task
        }
        return task;
    }

    protected class RequestFilterOnAllowedMethods implements RequestFilter,
        java.io.Serializable {
        private String allowedMethodNames;

        public RequestFilterOnAllowedMethods(String allowedMethodNames) {
            this.allowedMethodNames = allowedMethodNames;
        }

        public boolean acceptRequest(Request request) {
            return allowedMethodNames.indexOf(request.getMethodName()) >= 0;
        }
    }
}
