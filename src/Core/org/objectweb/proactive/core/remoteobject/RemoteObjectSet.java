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
import java.rmi.dgc.VMID;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.remoteobject.exception.UnknownProtocolException;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;


public class RemoteObjectSet implements Serializable, Observer {

    private HashMap<URI, RemoteRemoteObject> rros;
    private HashSet<RemoteRemoteObject> unreliables;
    private RemoteRemoteObject _default;
    private static Method getURI;
    private String[] order = new String[] {};
    private String[] remoteRuntimeUrls;
    private String forcedProtocol = null;
    // Almost same as in term of speed UniqueID.getCurrentVMID() but more readable 
    private static VMID vmid = ProActiveRuntimeImpl.getProActiveRuntime().getVMInformation().getVMID();

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
            this.add(rro);
            this.remoteRuntimeUrls = getPARuntimeUrls(rro);
            this.updateOrder();
        } catch (RemoteRemoteObjectException e) {
            this.unreliables.add(rro);
        }
    }

    public void forceProtocol(String protocol) throws UnknownProtocolException {
        // Protocol factories can be added dynamically, so the only way to        
        // check a protocol existence is to check if it have a factory
        if (RemoteObjectProtocolFactoryRegistry.get(protocol) != null) {
            this.forcedProtocol = protocol;
        } else {
            throw new UnknownProtocolException();
        }
    }

    public void add(RemoteRemoteObject rro) {
        // If an older same rro is present, it will be updated
        try {
            this.rros.put(getURI(rro), rro);
            this.remoteRuntimeUrls = getPARuntimeUrls(rro);
            this.updateOrder();
        } catch (RemoteRemoteObjectException e) {
            this.unreliables.add(rro);
        }
    }

    private Pair internalGet(int i) throws RemoteRemoteObjectException, ArrayIndexOutOfBoundsException {
        String protocol = order[i];
        for (URI uri : rros.keySet()) {
            if (protocol.equalsIgnoreCase(uri.getScheme()))
                return new Pair(uri, rros.get(uri));
        }
        throw new RemoteRemoteObjectException(
            "RemoteObjectSet: can't found RemoteRemoteObject for protocol " + protocol);
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
    public RemoteRemoteObject get(int i) throws RemoteRemoteObjectException {
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

    /**
     * Send a non-functional internal request to get the URI of the RemoteRemoteObject
     */
    private URI getURI(RemoteRemoteObject rro) throws RemoteRemoteObjectException {
        try {
            MethodCall mc = MethodCall.getMethodCall(getURI, new Object[0],
                    new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new InternalRemoteRemoteObjectRequest(mc);
            Reply rep = rro.receiveMessage(r);
            return (URI) rep.getResult().getResult();
        } catch (ProActiveException e) {
            throw new RemoteRemoteObjectException(
                "RemoteObjectSet: can't access RemoteObject through " + rro, e);
        } catch (IOException e) {
            throw new RemoteRemoteObjectException(
                "RemoteObjectSet: can't access RemoteObject through " + rro, e);
        } catch (ProActiveRuntimeException e) {
            throw new RemoteRemoteObjectException(
                "RemoteObjectSet: can't access RemoteObject through " + rro, e);
        } catch (RenegotiateSessionException e) {
            e.printStackTrace();
            throw new RemoteRemoteObjectException(e);
        }
    }

    /**
     * Send a non-functional internal request to get the urls of all exposure of the remote ProActiveRuntime 
     * in order to know which protocols are exposed.
     */
    private String[] getPARuntimeUrls(RemoteRemoteObject rro) throws RemoteRemoteObjectException {
        try {
            Request r = new PARuntimeUrlsRequest();
            Reply rep = rro.receiveMessage(r);
            return (String[]) rep.getResult().getResult();
        } catch (ProActiveException e) {
            throw new RemoteRemoteObjectException("RemoteObjectSet: can't get ProActiveRuntime urls from " +
                rro, e);
        } catch (IOException e) {
            throw new RemoteRemoteObjectException("RemoteObjectSet: can't get ProActiveRuntime urls from " +
                rro, e);
        } catch (RenegotiateSessionException e) {
            e.printStackTrace();
            throw new RemoteRemoteObjectException(e);
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

    public class RemoteRemoteObjectException extends Exception {
        RemoteRemoteObjectException(Exception e) {
            super(e);
        }

        RemoteRemoteObjectException(String m) {
            super(m);
        }

        RemoteRemoteObjectException(String m, Exception e) {
            super(m, e);
        }
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
        VMID testLocal = ProActiveRuntimeImpl.getProActiveRuntime().getVMInformation().getVMID();
        if (!vmid.equals(testLocal)) {
            this.updateUnreliable();
            this.updateOrder();
        }
    }

    private void updateUnreliable() {
        if (unreliables.size() != 0) {
            new Thread(new CheckReliability()).start();
        }
    }

    private class CheckReliability implements Runnable {
        public void run() {
            for (RemoteRemoteObject rro : unreliables) {
                add(rro);
            }
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void updateOrder() {
        ProActiveRuntimeImpl.getProActiveRuntime().subscribeAsObserver(this, remoteRuntimeUrls);
    }

    /**
     * Notification from a BenchmarkMonitorThread created by the local ProActiveRuntime  
     */
    public void update(Observable o, Object arg) {
        order = (String[]) arg;
    }
}
