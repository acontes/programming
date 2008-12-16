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
package org.objectweb.proactive.extra.forwardingv2.remoteobject;

import java.net.URI;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.remoteobject.AbstractRemoteObjectFactory;
import org.objectweb.proactive.core.remoteobject.InternalRemoteRemoteObject;
import org.objectweb.proactive.core.remoteobject.InternalRemoteRemoteObjectImpl;
import org.objectweb.proactive.core.remoteobject.RemoteObject;
import org.objectweb.proactive.core.remoteobject.RemoteObjectAdapter;
import org.objectweb.proactive.core.remoteobject.RemoteObjectFactory;
import org.objectweb.proactive.core.remoteobject.RemoteRemoteObject;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.client.AgentV2;
import org.objectweb.proactive.extra.forwardingv2.client.ForwardingAgentV2;
import org.objectweb.proactive.extra.forwardingv2.client.ProActiveMessageHandler;
import org.objectweb.proactive.extra.forwardingv2.remoteobject.message.MessageRoutingRegistryListRemoteObjectsMessage;
import org.objectweb.proactive.extra.forwardingv2.remoteobject.message.MessageRoutingRemoteObjectLookupMessage;
import org.objectweb.proactive.extra.forwardingv2.remoteobject.util.MessageRoutingRegistry;
import org.objectweb.proactive.extra.forwardingv2.remoteobject.util.MessageRoutingURIBuilder;
import org.objectweb.proactive.extra.forwardingv2.remoteobject.util.exceptions.MessageRoutingRemoteException;


public class MessageRoutingRemoteObjectFactory extends AbstractRemoteObjectFactory implements
        RemoteObjectFactory {
    final private AgentV2 agent;
    final private MessageRoutingRegistry registry;

    public MessageRoutingRemoteObjectFactory() {
        this.agent = new ForwardingAgentV2(ProActiveMessageHandler.class);
        this.registry = MessageRoutingRegistry.singleton;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.objectweb.proactive.core.remoteobject.RemoteObjectFactory#newRemoteObject(org.objectweb
     * .proactive.core.remoteobject.RemoteObject)
     */
    public RemoteRemoteObject newRemoteObject(InternalRemoteRemoteObject target) throws ProActiveException {
        try {
            return new MessageRoutingRemoteObjectImpl(target, null, agent);
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    /**
     * Registers an remote object into the registry
     * 
     * @param urn
     *            The urn of the body (in fact his url + his name)
     * @exception java.io.IOException
     *                if the remote body cannot be registered
     */

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.objectweb.proactive.core.remoteobject.RemoteObjectFactory#register(org.objectweb.proactive
     * .core.remoteobject.RemoteObject, java.net.URI, boolean)
     */
    public RemoteRemoteObject register(InternalRemoteRemoteObject ro, URI uri, boolean replacePrevious)
            throws ProActiveException {

        /* #@#@# FIXME */
        registry.bind(uri, ro);
        MessageRoutingRemoteObjectImpl rro = new MessageRoutingRemoteObjectImpl(ro, uri, agent);
        ProActiveLogger.getLogger(Loggers.REMOTEOBJECT)
                .debug("registering remote object  at endpoint " + uri);
        return rro;
    }

    /**
     * Unregisters an remote object previously registered into the bodies table
     * 
     * @param urn
     *            the urn under which the active object has been registered
     */
    public void unregister(URI uri) throws ProActiveException {
        registry.unbind(uri);
    }

    /**
     * Looks-up a remote object previously registered in the bodies table .
     * 
     * @param urn
     *            the urn (in fact its url + name) the remote Body is registered to
     * @return a UniversalBody
     */
    public RemoteObject lookup(URI uri) throws ProActiveException {
        MessageRoutingRemoteObjectLookupMessage message = new MessageRoutingRemoteObjectLookupMessage(uri,
            agent);
        try {
            message.send();
        } catch (MessageRoutingRemoteException e) {
            throw new ProActiveException(e);
        }
        RemoteRemoteObject result = message.getReturnedObject();

        if (result == null) {
            throw new ProActiveException("The uri " + uri + " is not bound to any known object");
        } else {
            return new RemoteObjectAdapter(result);
        }
    }

    /**
     * List all active object previously registered in the registry
     * 
     * @param url
     *            the url of the host to scan, typically //machine_name
     * @return a list of Strings, representing the registered names, and {} if no registry
     * @exception java.io.IOException
     *                if scanning reported some problem (registry not found, or malformed Url)
     */

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.core.body.BodyAdapterImpl#list(java.lang.String)
     */
    public URI[] list(URI uri) throws ProActiveException {
        MessageRoutingRegistryListRemoteObjectsMessage message = new MessageRoutingRegistryListRemoteObjectsMessage(
            uri, agent);

        try {
            message.send();
            return message.getReturnedObject();
        } catch (MessageRoutingRemoteException e) {
            throw new ProActiveException(e);
        }
    }

    public String getProtocolId() {
        return "pamr";
    }

    public void unexport(RemoteRemoteObject rro) throws ProActiveException {
        // see PROACTIVE-419
    }

    public int getPort() {
        return -1;
    }

    public URI generateURI(String objectName) {
        return MessageRoutingURIBuilder.create(agent.getAgentID(), objectName);
    }

    public InternalRemoteRemoteObject createRemoteObject(RemoteObject<?> remoteObject, String name)
            throws ProActiveException {

        URI uri = URI.create(this.getProtocolId() + ":/" + agent.getAgentID() + "/" + name);

        // register the object on the register
        InternalRemoteRemoteObject irro = new InternalRemoteRemoteObjectImpl(remoteObject, uri);
        RemoteRemoteObject rmo = register(irro, uri, true);
        irro.setRemoteRemoteObject(rmo);

        return irro;
    }
}
