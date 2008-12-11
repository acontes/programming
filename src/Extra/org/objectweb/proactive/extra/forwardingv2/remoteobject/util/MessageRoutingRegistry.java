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
package org.objectweb.proactive.extra.forwardingv2.remoteobject.util;

import java.net.URI;
import java.util.HashMap;

import org.objectweb.proactive.core.remoteobject.InternalRemoteRemoteObject;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/* #@#@ Factorize MessageRoutingRegistry and HTTPRegistry, they are the same */
/**
 * An HTTP Registry that registers Bodies
 * @author The ProActive Team
 *
 */
public class MessageRoutingRegistry {
	public final static MessageRoutingRegistry singleton = new MessageRoutingRegistry();
	
    private HashMap<URI, InternalRemoteRemoteObject> rRemteObjectMap;
    
    private MessageRoutingRegistry() {
    	this.rRemteObjectMap = new HashMap<URI, InternalRemoteRemoteObject>();
    }

    /**
     * Binds a body  with a name
     * @param uri  the name of the body
     * @param body the body to be binded
     */
    public void bind(URI uri, InternalRemoteRemoteObject body) {
        ProActiveLogger.getLogger(Loggers.REMOTEOBJECT).debug("registering remote object at " + uri);
        rRemteObjectMap.put(uri, body);
    }

    /**
     * Unbinds a body from a  name
     * @param uri the name binded with a body
     */
    public void unbind(URI uri) {
        rRemteObjectMap.remove(uri);
    }

    /**
     * Gives all the names registered in this registry
     * @return the names list
     */
    public URI[] list() {
        URI[] list = new URI[rRemteObjectMap.size()];
        rRemteObjectMap.keySet().toArray(list);
        return list;
    }

    /**
     * Retrieves a body from a name
     * @param uri The name of the body to be retrieved
     * @return the binded body
     */
    public InternalRemoteRemoteObject lookup(URI uri) {
        return rRemteObjectMap.get(uri);
    }
}
