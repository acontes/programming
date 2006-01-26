/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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
package org.objectweb.proactive.core.body.future;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.future.FutureEventProducer;
import org.objectweb.proactive.core.event.AbstractEventProducer;
import org.objectweb.proactive.core.event.FutureEvent;
import org.objectweb.proactive.core.event.FutureEventListener;
import org.objectweb.proactive.core.event.ProActiveEvent;
import org.objectweb.proactive.core.event.ProActiveListener;


public class FutureEventProducerImpl extends AbstractEventProducer
    implements FutureEventProducer, java.io.Serializable {
    public FutureEventProducerImpl() {
    }

    //
    // -- PUBLIC METHODS -----------------------------------------------
    //
    public void notifyListeners(UniqueID bodyID, UniqueID creatorID, int type) {
        if (hasListeners()) {
            notifyAllListeners(new FutureEvent(bodyID, creatorID, type));
        }
    }

    public void notifyOneListener(ProActiveListener listener,
        ProActiveEvent futureEvent) {
        switch (futureEvent.getType()) {
        case FutureEvent.WAIT_BY_NECESSITY:
            ((FutureEventListener) listener).waitingForFuture((FutureEvent) futureEvent);
            break;
        case FutureEvent.RECEIVED_FUTURE_RESULT:
            ((FutureEventListener) listener).receivedFutureResult((FutureEvent) futureEvent);
            break;
        }
    }

    //
    // -- implements FutureEventProducer -----------------------------------------------
    //
    public void addFutureEventListener(FutureEventListener listener) {
        addListener(listener);
    }

    public void removeFutureEventListener(FutureEventListener listener) {
        removeListener(listener);
    }
} // end inner class FutureEventProducer
