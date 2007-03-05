/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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
package org.objectweb.proactive.branchnbound.core;

import java.io.Serializable;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.branchnbound.user.BnBTask;
import org.objectweb.proactive.branchnbound.user.BnBWorker;


// TODO Javadoc
public abstract class BnBInternalTask<Value extends Comparable<Value>, Task extends BnBTask<Value>>
    implements InitActive, RunActive, Serializable {
    protected BnBWorker worker = null;

    /**
     * The no args construtor for activating the task.
     */
    public BnBInternalTask() {
        // empty
    }

    public BnBInternalTask(BnBWorker associatedWorker) {
        this.worker = associatedWorker;
    }

    //--------------------------------------------------------------------------
    // Internal stuff
    //--------------------------------------------------------------------------

    /**
     * <p>Do not override this method. </p>
     * <p>For internal only. Run <code>initLowerBound</code> and
     * <code>initUpperBound</code> methods at activation time.</p>
     * @see org.objectweb.proactive.InitActive#initActivity(org.objectweb.proactive.Body)
     */
    public void initActivity(Body body) {
        // TODO  this.initLowerBound();
        //TODO  this.initUpperBound();
    }

    /**
     * <p>Do not override this method. </p>
     * <p>For internal only. Serve only the <code>explore</code> method for one
     * time and stop the activity.</p>
     * @see org.objectweb.proactive.RunActive#runActivity(org.objectweb.proactive.Body)
     */
    public void runActivity(Body body) {
        Service service = new Service(body);

        service.blockingServeOldest("explore");
    }
}
