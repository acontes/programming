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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;


/**
 * Makes additional services which are present in the 
 * {@link Agent} implementation available within the 
 * message routing implementation 
 * 
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public abstract class AgentInternal implements Agent {

    /** Request ID Generator **/
    protected final AtomicLong idGenerator;

    /** Senders waiting for a response */
    protected final WaitingRoom mailboxes;

    public AgentInternal() {
        this.idGenerator = new AtomicLong(0);
        this.mailboxes = new WaitingRoom();
    }

    public abstract IncomingMessageHandler getIncomingHandler();

    /** generic method to send any
     * message routing protocol {@link Message}  
     */
    public abstract byte[] sendRoutingMessage(Message msg, boolean oneway) throws MessageRoutingException;

    public AtomicLong getIDGenerator() {
        return this.idGenerator;
    }

    public WaitingRoom getWaitingRoom() {
        return this.mailboxes;
    }

    /** Implementations might decide to use thread pools
     *   in order to implement agent-side message processing.
     *   In this case, there should be a single, per-agent
     *   thread pool which should be reused in the various 
     *   processing entities available on the agent side.
     * */
    public abstract ExecutorService getThreadPool();

}
