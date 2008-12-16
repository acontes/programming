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

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.remoteobject.InternalRemoteRemoteObject;
import org.objectweb.proactive.core.remoteobject.RemoteRemoteObject;
import org.objectweb.proactive.extra.forwardingv2.client.AgentV2;
import org.objectweb.proactive.extra.forwardingv2.remoteobject.MessageRoutingRemoteObjectFactory;
import org.objectweb.proactive.extra.forwardingv2.remoteobject.MessageRoutingRemoteObjectImpl;
import org.objectweb.proactive.extra.forwardingv2.remoteobject.util.MessageRoutingRegistry;


/**
 * This classes represents a HTTPMessage. When processed, this message performs a lookup thanks to the urn.
 * @author The ProActive Team
 * @see MessageRoutingMessage
 */
@SuppressWarnings("serial")
public class MessageRoutingRemoteObjectLookupMessage extends MessageRoutingMessage implements Serializable {
    private String urn;

    //Caller Side

    /**
     * Constructs an HTTP Message
     * @param urn The urn of the Object (it can be an active object or a runtime).
     */
    public MessageRoutingRemoteObjectLookupMessage(URI uri, AgentV2 agent) {
        super(uri, agent);
    }

    /**
     * Get the returned object.
     * @return the returned object
     */
    public RemoteRemoteObject getReturnedObject() {
        return (RemoteRemoteObject) this.returnedObject;
    }

    /**
     * Performs the lookup
     * @return The Object associated with the urn
     */
    @Override
    public Object processMessage() {
        if (this.urn != null) {
            InternalRemoteRemoteObject irro = MessageRoutingRegistry.singleton.lookup(uri);
            if (irro != null) {
                RemoteRemoteObject rro = null;
                try {
                    rro = new MessageRoutingRemoteObjectFactory().newRemoteObject(irro);
                    ((MessageRoutingRemoteObjectImpl) rro).setURI(uri);
                } catch (ProActiveException e) {
                    e.printStackTrace();
                }
                this.returnedObject = rro;
            }
        }
        return this.returnedObject;
    }
}
