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
package org.objectweb.proactive.extra.messagerouting.remoteobject;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.remoteobject.AbstractRemoteObjectFactory;
import org.objectweb.proactive.core.remoteobject.InternalRemoteRemoteObject;
import org.objectweb.proactive.core.remoteobject.RemoteRemoteObject;
import org.objectweb.proactive.core.remoteobject.SynchronousReplyImpl;
import org.objectweb.proactive.core.remoteobject.exception.UnknownProtocolException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.messagerouting.client.Agent;
import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.messagerouting.remoteobject.message.MessageRoutingRemoteObjectRequest;


/**
 * 
 * @since ProActive 4.1.0
 */
@SuppressWarnings("serial")
public class MessageRoutingRemoteObject implements RemoteRemoteObject, Serializable {
    final static private Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_REMOTE_OBJECT);

    /** The URL of the RemoteObject */
    private URI remoteObjectURL;

    /** The local message routing agent 
     *
     * This field must NOT be used since it is set by the getAgent() method. Each time this
     * object is sent on a remote runtime, the local agent needs to be retrieved. Custom readObject()
     * is avoid by the use of a transient field and the getAgent() method.
     */
    private transient Agent agent;

    protected transient InternalRemoteRemoteObject remoteObject;

    public MessageRoutingRemoteObject(InternalRemoteRemoteObject remoteObject, URI remoteObjectURL,
            Agent agent) {
        this.remoteObject = remoteObject;
        this.remoteObjectURL = remoteObjectURL;
        this.agent = agent;
    }

    public Reply receiveMessage(Request message) throws ProActiveException {

        MessageRoutingRemoteObjectRequest req = new MessageRoutingRemoteObjectRequest(message,
            this.remoteObjectURL, getAgent());
        req.send();
        SynchronousReplyImpl rep = (SynchronousReplyImpl) req.getReturnedObject();
        return rep;
    }

    public void setURI(URI url) {
        this.remoteObjectURL = url;
    }

    public URI getURI() {
        return this.remoteObjectURL;
    }

    private Agent getAgent() {
        if (this.agent == null) {
            try {
                // FIXME: The factory cast is a hack but there is no clean way to do it
                MessageRoutingRemoteObjectFactory f;
                f = (MessageRoutingRemoteObjectFactory) AbstractRemoteObjectFactory
                        .getRemoteObjectFactory("pamr");
                this.agent = f.getAgent();
            } catch (UnknownProtocolException e) {
                logger.fatal("Failed to get the local message routing agent", e);
            }
        }

        return this.agent;
    }

}
