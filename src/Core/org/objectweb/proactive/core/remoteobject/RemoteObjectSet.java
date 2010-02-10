/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of
 *              Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
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
 * If needed, contact us to obtain a release under GPL Version 2
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.remoteobject;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;
import org.objectweb.proactive.core.util.URIBuilder;


public class RemoteObjectSet implements Serializable {

    private HashMap<URI, RemoteRemoteObject> rros;
    private HashSet<RemoteRemoteObject> unreliables;
    private RemoteRemoteObject _default;
    private static Method getURI;
    private String[] order = null;
    private String[] urls;
    private String forcedProtocol = null;

    static {
        try {
            getURI = InternalRemoteRemoteObject.class.getDeclaredMethod("getURI", new Class<?>[0]);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private RemoteObjectSet() {
        this.rros = new HashMap<URI, RemoteRemoteObject>();
        this.unreliables = new HashSet<RemoteRemoteObject>();
    }

    public RemoteObjectSet(RemoteRemoteObject rro) {
        this();
        try {
            this._default = rro;
            this.urls = getPARuntimeUrls(rro);
            this.updateOrder();
        } catch (NotReliableRemoteRemoteObjectException e) {
            this.unreliables.add(rro);
        }
    }

    public void forceProtocol(String protocol) {
        this.forcedProtocol = protocol;
    }

    public void add(RemoteRemoteObject rro) {
        // If an older same rro is present, it will be updated
        try {
            this.rros.put(getURI(rro), rro);
            // Update ?
        } catch (NotReliableRemoteRemoteObjectException e) {
            this.unreliables.add(rro);
        }
    }

    private Pair internalGet(int i) throws UnaccessibleRemoteRemoteObjectException,
            ArrayIndexOutOfBoundsException {
        String protocol = order[i];
        for (URI uri : rros.keySet()) {
            if (protocol.equalsIgnoreCase(uri.getScheme()))
                return new Pair(uri, rros.get(uri));
        }
        throw new UnaccessibleRemoteRemoteObjectException();
    }

    /**
     * Get the highest ordered (understand faster) RemoteRemoteObject in this runtime
     * among available one's 
     * 
     * @param i Index are used like in Arrays
     *          0 represent the first object
     *          
     * @throws UnaccessibleRemoteRemoteObjectException
     */
    public RemoteRemoteObject get(int i) throws UnaccessibleRemoteRemoteObjectException {
        if (forcedProtocol != null) {
            for (URI uri : rros.keySet()) {
                if (forcedProtocol.equalsIgnoreCase(uri.getScheme()))
                    return rros.get(uri);
            }
        }

        try {
            return internalGet(i).getRRO();
        } catch (ArrayIndexOutOfBoundsException e) {
            return _default;
        }
    }

    private URI getURI(RemoteRemoteObject rro) throws NotReliableRemoteRemoteObjectException {
        // Retrying the uri from here will ensure that accessing this rro
        // doesn't throw any exception, so it could be use quite safely
        // in the future.
        try {
            MethodCall mc = MethodCall.getMethodCall(getURI, new Object[0],
                    new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new InternalRemoteRemoteObjectRequest(mc);
            Reply rep = rro.receiveMessage(r);
            return (URI) rep.getResult().getResult();
        } catch (ProActiveException e) {
            throw new NotReliableRemoteRemoteObjectException();
        } catch (IOException e) {
            throw new NotReliableRemoteRemoteObjectException();
        } catch (ProActiveRuntimeException e) {
            throw new NotReliableRemoteRemoteObjectException();
        } catch (RenegotiateSessionException e) {
            e.printStackTrace();
            throw new NotReliableRemoteRemoteObjectException();
        }
    }

    private String[] getPARuntimeUrls(RemoteRemoteObject rro) throws NotReliableRemoteRemoteObjectException {
        try {
            Request r = new PARuntimeUrlsRequest();
            Reply rep = rro.receiveMessage(r);
            return (String[]) rep.getResult().getResult();
        } catch (ProActiveException e) {
            throw new NotReliableRemoteRemoteObjectException();
        } catch (IOException e) {
            throw new NotReliableRemoteRemoteObjectException();
        } catch (RenegotiateSessionException e) {
            e.printStackTrace();
            throw new NotReliableRemoteRemoteObjectException();
        }
    }

    private class Pair implements Serializable {
        private RemoteRemoteObject rro;
        private URI uri;

        Pair(URI uri, RemoteRemoteObject rro) {
            this.uri = uri;
            this.rro = rro;
        }

        RemoteRemoteObject getRRO() {
            return this.rro;
        }

        URI getURI() {
            return this.uri;
        }
    }

    private class NotReliableRemoteRemoteObjectException extends Exception {
    }

    public class UnaccessibleRemoteRemoteObjectException extends Exception {
    }

    public int size() {
        return this.rros.size();
    }

    public String getProtocol(int i) {
        try {
            return order[i];
        } catch (ArrayIndexOutOfBoundsException e) {
            return PAProperties.PA_COMMUNICATION_PROTOCOL.getValue();
        }
    }

    /**
     * Update the protocol order from the new ProActive Runtime
     * when the remote remote object is reified
     *
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.updateUnreliable();
        this.updateOrder();
    }

    private void updateUnreliable() {
        if (unreliables.size() != 0){
            new Thread(new CheckReliability()).start();
        }
    }

    private class CheckReliability implements Runnable {
        public void run() {
            for (RemoteRemoteObject rro : unreliables){
                add(rro);
            }
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void updateOrder() {
        order = ProActiveRuntimeImpl.getProActiveRuntime().getProtocolOrder(
                URIBuilder.getNameFromURI(urls[0]), urls);
    }
}
