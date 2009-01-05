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
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extra.forwardingv2.remoteobject.message;

import java.io.Serializable;
import java.net.URI;

import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.remoteobject.InternalRemoteRemoteObject;
import org.objectweb.proactive.extra.forwardingv2.client.AgentV2;
import org.objectweb.proactive.extra.forwardingv2.remoteobject.util.MessageRoutingRegistry;


@SuppressWarnings("serial")
public class MessageRoutingRemoteObjectRequest extends MessageRoutingMessage implements Serializable {
    private Request request;

    /** Construct a request message
     * 
     * @param request the request to be send
     * @param uri the recipient (aka the remote object) of the request
     * @param agent the local agent to use to send this message
     */
    public MessageRoutingRemoteObjectRequest(Request request, URI uri, AgentV2 agent) {
        super(uri, agent);
        this.request = request;
        this.isOneWay = request.isOneWay();
    }

    /** Get the response of this request */
    // client side
    public Object getReturnedObject() {
        return this.returnedObject;
    }

    @Override
    // server side
    public Object processMessage() {
        if (logger.isTraceEnabled()) {
            logger.trace("Executing the request message " + this.request + " on " + uri);
        }

        try {
            InternalRemoteRemoteObject ro;
            ro = MessageRoutingRegistry.singleton.lookup(uri);
            return ro.receiveMessage(this.request);
        } catch (Exception e) {
            return e;
        }
    }
}
