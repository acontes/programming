/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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
 */
package org.objectweb.proactive.extra.messagerouting.client;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.objectweb.proactive.core.util.SweetCountDownLatch;
import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;


/** Allows threads to wait for a response
 * 
 *  Methods are made package-accessible by default; 
 *  only methods that are accessed from other packages are made public
 * */
public class Patient {
    /** 0 when the response is available or an error has been received */
    final private SweetCountDownLatch latch;
    /** The response */
    volatile private byte[] response = null;
    /** Received exception */
    volatile private MessageRoutingException exception = null;

    /** message ID of the request */
    final private long requestID;
    /** Agent ID of recipient of the request */
    final private AgentID recipient;

    Patient(AgentID agentId, long recipient) {
        this.latch = new SweetCountDownLatch(1);

        this.requestID = recipient;
        this.recipient = agentId;
    }

    /**
     * Wait until the response is available or an error is received
     * 
     * @param timeout
     *            Maximum amount of time to wait before throwing an
     *            exception in milliseconds. 0 means no timeout
     * @return the response
     * @throws MessageRoutingException
     *             If the request failed to be send or if the recipient
     *             disconnected before sending the response.
     * @throws TimeoutException
     *             If the timeout is reached
     */
    public byte[] waitForResponse(long timeout) throws MessageRoutingException, TimeoutException {

        if (timeout == 0) {
            this.latch.await();
        } else {
            boolean b = this.latch.await(timeout, TimeUnit.MILLISECONDS);

            if (!b) {
                throw new TimeoutException("Timeout reached");
            }
        }

        if (exception != null) {
            throw exception;
        }

        return response;
    }

    /**
     * Set the response and unlock the waiting thread
     * 
     * @param response
     *            the response
     */
    void setAndUnlock(byte[] response) {
        this.response = response;
        latch.countDown();
    }

    /**
     * Set the exception and unlock the waiting thread
     * 
     * @param exception
     *            received error
     */
    void setAndUnlock(MessageRoutingException exception) {
        this.exception = exception;
        latch.countDown();
    }

    long getRequestID() {
        return requestID;
    }

    AgentID getRecipient() {
        return recipient;
    }
}
