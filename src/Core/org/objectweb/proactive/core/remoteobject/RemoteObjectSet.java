package org.objectweb.proactive.core.remoteobject;

import java.io.IOException;
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
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;


public class RemoteObjectSet implements Serializable {

    private HashMap<URI, RemoteRemoteObject> rros;
    private HashSet<RemoteRemoteObject> unreliables;
    private RemoteRemoteObject _default;
    private static Method getURI;
    private Pair first;
    private String[] order = PAProperties.PA_COMMUNICATION_PROTOCOL_ORDER.getValue().split(";");

    static {
        try {
            getURI = InternalRemoteRemoteObject.class.getDeclaredMethod("getURI", new Class<?>[0]);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private RemoteObjectSet() {
        this.rros = new HashMap<URI, RemoteRemoteObject>();
    }

    public RemoteObjectSet(RemoteRemoteObject rro) {
        this();
        try {
            this.first = new Pair(getURI(rro), rro);
        } catch (NotReliableRemoteRemoteObjectException e) {
            this.unreliables.add(rro);
        }
        this._default = rro;
    }

    public void add(RemoteRemoteObject rro) {
        // If an older same rro is present, it will be updated
        try {
            this.rros.put(getURI(rro), rro);
            this.chooseFirst();
        } catch (NotReliableRemoteRemoteObjectException e) {
            this.unreliables.add(rro);
        }
    }

    public RemoteRemoteObject getFirst() {
        if (first == null)
            chooseFirst();
        return first.getRRO();
    }

    private Pair internalGet(int i) throws NoPriorityRemoteRemoteObjectException {
        // Search by order
        for (int j = i; j < order.length; j++) {
            String protocol = order[j];
            // Search the faster protocol in available one            
            for (URI uri : rros.keySet()) {
                if (protocol.equalsIgnoreCase(uri.getScheme()))
                    return new Pair(uri, rros.get(uri));
            }
        }
        throw new NoPriorityRemoteRemoteObjectException();
    }

    /**
     * Get the highest ordered (understand faster) RemoteRemoteObject in this runtime
     * among available one's 
     * 
     * @param i Index are used like in Arrays
     *          0 represent the first object
     *          
     * @return Never return null, if there is no order, or no available RemoteRemoteObject 
     * in the specified protocol order list, return the default protocol associated, RemoteRemoteObject 
     */
    public RemoteRemoteObject get(int i) {
        if (i > this.order.length)
            return _default;
        try {
            return internalGet(i).getRRO();
        } catch (NoPriorityRemoteRemoteObjectException e) {
            return _default;
        }
    }

    private void chooseFirst() {
        try {
            first = internalGet(0);
        } catch (NoPriorityRemoteRemoteObjectException e) {
            // No more change needed
        }
    }

    // Use only for debugging
    // Will be remove shortly
    public void printAll() {
        System.out.println("#####################################");
        System.out.println("Faster is : " + this.first.getURI());
        for (URI uri : rros.keySet()) {
            System.out.println(uri);
        }
        System.out.println("#####################################");
    }

    public void setOrder(String[] protocolOrder) {
        this.order = order;
        this.chooseFirst();
    }

    private URI getURI(RemoteRemoteObject rro) throws NotReliableRemoteRemoteObjectException {
        // Retrying the uri from here will ensure that accessing this rro
        //  doesn't throw any exception, so it could be use quite safely 
        // in the future.
        MethodCall mc = MethodCall.getMethodCall(getURI, new Object[0],
                new HashMap<TypeVariable<?>, Class<?>>());
        Request r = new InternalRemoteRemoteObjectRequest(mc);
        Reply rep = null;
        try {
            rep = rro.receiveMessage(r);
            // TODO Treat exception in a smarter way 
        } catch (ProActiveException e) {
            throw new NotReliableRemoteRemoteObjectException();
        } catch (IOException e) {
            throw new NotReliableRemoteRemoteObjectException();
        } catch (ProActiveRuntimeException e) {
            throw new NotReliableRemoteRemoteObjectException();
        } catch (RenegotiateSessionException e) {
            e.printStackTrace();
        }
        return (URI) rep.getResult().getResult();
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
        public NotReliableRemoteRemoteObjectException() {
            super();
        }
    }

    private class NoPriorityRemoteRemoteObjectException extends Exception {
        public NoPriorityRemoteRemoteObjectException() {
            super();
        }
    }

    public int size() {
        return this.rros.size();
    }

    //TODO remove debug only
    public URI getURI(int i) {
        try {
            return internalGet(i).getURI();
        } catch (NoPriorityRemoteRemoteObjectException e) {
            // ? 
        }
        return null;
    }
}
