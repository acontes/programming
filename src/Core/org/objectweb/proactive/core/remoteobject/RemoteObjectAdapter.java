/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of 
 * 						   Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org
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
 * If needed, contact us to obtain a release under GPL Version 2. 
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_CONTRIBUTOR$$
 */
package org.objectweb.proactive.core.remoteobject;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.net.URI;
import java.security.AccessControlException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.future.MethodCallResult;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.remoteobject.RemoteObjectSet.UnaccessibleRemoteRemoteObjectException;
import org.objectweb.proactive.core.remoteobject.adapter.Adapter;
import org.objectweb.proactive.core.security.PolicyServer;
import org.objectweb.proactive.core.security.ProActiveSecurityManager;
import org.objectweb.proactive.core.security.SecurityContext;
import org.objectweb.proactive.core.security.SecurityEntity;
import org.objectweb.proactive.core.security.TypedCertificate;
import org.objectweb.proactive.core.security.crypto.KeyExchangeException;
import org.objectweb.proactive.core.security.crypto.SessionException;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;
import org.objectweb.proactive.core.security.exceptions.SecurityNotAvailableException;
import org.objectweb.proactive.core.security.securityentity.Entities;
import org.objectweb.proactive.core.security.securityentity.Entity;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * @author The ProActive Team The RemoteObjectAdapter is used to hide the fact
 *         that the remote object called is distant.
 */
public class RemoteObjectAdapter implements RemoteObject {
    static final Logger LOGGER_RO = ProActiveLogger.getLogger(Loggers.REMOTEOBJECT);

    protected RemoteObjectSet remoteObjectSet;

    /**
     * a stub on the object reified by the remote object
     */
    protected Object stub;

    /**
     * the URI where the remote remote is bound
     */
    protected URI uri;

    /**
     * Array of methods belonging to the RemoteObject class. These methods are
     * reified using the RemoteObjectRequest class
     */
    protected static Method[] methods;

    /**
     * Array of methods belonging to the SecurityEntity class These methods are
     * reified using the InternalRemoteRemoteObjectRequest class
     */
    protected static Method[] securityMethods;

    /**
     * Array of methods belonging to the InternalRemoteRemoteObject class. These
     * methods are reified using the InternalRemoteRemoteObjectRequest class
     */
    protected static Method[] internalRROMethods;

    static {
        try {
            methods = new Method[20];
            methods[0] = RemoteObject.class.getDeclaredMethod("getObjectProxy", new Class<?>[0]);
            methods[1] = RemoteObject.class.getDeclaredMethod("getObjectProxy",
                    new Class<?>[] { RemoteRemoteObject.class });
            methods[2] = RemoteObject.class.getDeclaredMethod("getClassName", new Class<?>[0]);
            methods[3] = RemoteObject.class.getDeclaredMethod("getTargetClass", new Class<?>[0]);
            methods[4] = RemoteObject.class.getDeclaredMethod("getProxyName", new Class<?>[0]);
            methods[5] = RemoteObject.class.getDeclaredMethod("getAdapterClass", new Class<?>[0]);
            // methods[6] =
            // RemoteObject.class.getDeclaredMethod("getRemoteObjectProperties",
            // new Class<?>[0]);
            methods[7] = RemoteObject.class.getDeclaredMethod("getAdapter", new Class<?>[0]);

            securityMethods = new Method[20];
            securityMethods[0] = SecurityEntity.class.getDeclaredMethod("getCertificate", new Class<?>[0]);
            securityMethods[1] = SecurityEntity.class.getDeclaredMethod("startNewSession", new Class<?>[] {
                    long.class, SecurityContext.class, TypedCertificate.class });
            securityMethods[2] = SecurityEntity.class.getDeclaredMethod("getPublicKey", new Class<?>[0]);
            securityMethods[3] = SecurityEntity.class.getDeclaredMethod("publicKeyExchange", new Class<?>[] {
                    long.class, byte[].class });
            securityMethods[4] = SecurityEntity.class.getDeclaredMethod("secretKeyExchange", new Class<?>[] {
                    long.class, byte[].class, byte[].class, byte[].class, byte[].class, byte[].class });
            securityMethods[5] = SecurityEntity.class.getDeclaredMethod("getPolicy", new Class<?>[] {
                    Entities.class, Entities.class });
            securityMethods[7] = SecurityEntity.class.getDeclaredMethod("getEntities", new Class<?>[0]);
            securityMethods[8] = SecurityEntity.class.getDeclaredMethod("terminateSession",
                    new Class<?>[] { long.class });
            securityMethods[9] = SecurityEntity.class.getDeclaredMethod("randomValue", new Class<?>[] {
                    long.class, byte[].class });

            securityMethods[10] = SecurityEntity.class.getDeclaredMethod("getProActiveSecurityManager",
                    new Class<?>[] { Entity.class });
            securityMethods[11] = SecurityEntity.class.getDeclaredMethod("setProActiveSecurityManager",
                    new Class<?>[] { Entity.class, PolicyServer.class });

            internalRROMethods = new Method[20];
            internalRROMethods[0] = InternalRemoteRemoteObject.class.getDeclaredMethod("getObjectProxy",
                    new Class<?>[] {});
            internalRROMethods[2] = InternalRemoteRemoteObject.class.getDeclaredMethod("getURI",
                    new Class<?>[0]);
            internalRROMethods[3] = InternalRemoteRemoteObject.class.getDeclaredMethod("getRROs",
                    new Class<?>[0]);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public RemoteObjectAdapter() {
    }

    public RemoteObjectAdapter(RemoteRemoteObject ro) throws ProActiveException {
        this.remoteObjectSet = new RemoteObjectSet(ro);
        this.getActiveProtocols(ro);
    }

    public RemoteObjectAdapter(RemoteRemoteObject[] ros) throws ProActiveException {
        for (RemoteRemoteObject rro : ros) {
            this.remoteObjectSet.add(rro);
        }
    }

    public void AddRemoteRemoteObject(RemoteRemoteObject rro) throws ProActiveException {
        this.remoteObjectSet.add(rro);
    }

    public void forceProtocol(String protocol) {
        this.remoteObjectSet.forceProtocol(protocol);
    }

    public Reply receiveMessage(Request message) throws ProActiveException, RenegotiateSessionException,
            IOException {
        for (int i = 0; i < remoteObjectSet.size(); i++) {
            try {
                return remoteObjectSet.get(i).receiveMessage(message);
            } catch (UnaccessibleRemoteRemoteObjectException e) {
                continue;
            } catch (EOFException e) {
                LOGGER_RO.debug("EOFException while calling method " + message.getMethodName());
                return new SynchronousReplyImpl();
            } catch (ProActiveException e) {
                continue;
                //throw new IOException(e.getMessage());
            } catch (IOException e) {
                // Log for keeping a trace
                LOGGER_RO.warn("unable to contact remote object when calling " + message.getMethodName() +
                    "with protocol " + remoteObjectSet.getProtocol(i) +
                    ". Trying an other protocol if available. ", e);
                continue;
            }
        }
        // no one success
        return new SynchronousReplyImpl(new MethodCallResult(null, null));
    }

    // Implements SecurityEntity
    public TypedCertificate getCertificate() throws SecurityNotAvailableException, IOException {
        try {
            MethodCall mc = MethodCall.getMethodCall(securityMethods[0], new Object[0],
                    new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new InternalRemoteRemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

            return (TypedCertificate) reply.getResult().getResult();
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    // public byte[] getCertificateEncoded()
    // throws SecurityNotAvailableException, IOException {
    // return this.remoteObject.getCertificateEncoded();
    // }
    public Entities getEntities() throws SecurityNotAvailableException, IOException {
        try {
            MethodCall mc = MethodCall.getMethodCall(securityMethods[6], new Object[0],
                    new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new InternalRemoteRemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

            return (Entities) reply.getResult().getResult();
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public SecurityContext getPolicy(Entities local, Entities distant) throws SecurityNotAvailableException,
            IOException {
        try {
            MethodCall mc = MethodCall.getMethodCall(securityMethods[5], new Object[] { local, distant },
                    new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new InternalRemoteRemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

            return (SecurityContext) reply.getResult().getResult();
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public PublicKey getPublicKey() throws SecurityNotAvailableException, IOException {
        try {
            MethodCall mc = MethodCall.getMethodCall(securityMethods[2], new Object[0],
                    new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new InternalRemoteRemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

            return (PublicKey) reply.getResult().getResult();
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public byte[] publicKeyExchange(long sessionID, byte[] signature) throws SecurityNotAvailableException,
            RenegotiateSessionException, KeyExchangeException, IOException {
        try {
            MethodCall mc = MethodCall.getMethodCall(securityMethods[3],
                    new Object[] { sessionID, signature }, new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new InternalRemoteRemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

            return (byte[]) reply.getResult().getResult();
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public byte[] randomValue(long sessionID, byte[] clientRandomValue) throws SecurityNotAvailableException,
            RenegotiateSessionException, IOException {
        try {
            MethodCall mc = MethodCall.getMethodCall(securityMethods[9], new Object[] { sessionID,
                    clientRandomValue }, new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new InternalRemoteRemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

            return (byte[]) reply.getResult().getResult();
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public byte[][] secretKeyExchange(long sessionID, byte[] encodedAESKey, byte[] encodedIVParameters,
            byte[] encodedClientMacKey, byte[] encodedLockData, byte[] parametersSignature)
            throws SecurityNotAvailableException, RenegotiateSessionException, IOException {
        try {
            MethodCall mc = MethodCall.getMethodCall(securityMethods[4], new Object[] { sessionID,
                    encodedAESKey, encodedIVParameters, encodedClientMacKey, encodedLockData,
                    parametersSignature }, new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new InternalRemoteRemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

            return (byte[][]) reply.getResult().getResult();
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public long startNewSession(long distantSessionID, SecurityContext policy,
            TypedCertificate distantCertificate) throws SecurityNotAvailableException, IOException,
            SessionException {
        try {
            MethodCall mc = MethodCall.getMethodCall(securityMethods[1], new Object[] { distantSessionID,
                    policy, distantCertificate }, new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new InternalRemoteRemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

            return ((Long) reply.getResult().getResult()).longValue();
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public void terminateSession(long sessionID) throws SecurityNotAvailableException, IOException {
        try {
            MethodCall mc = MethodCall.getMethodCall(securityMethods[8], new Object[] { sessionID },
                    new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new InternalRemoteRemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // RemoteObjects
    public Object getObjectProxy() throws ProActiveException {
        if (this.stub == null) {
            // this.stub = this.remoteObject.getObjectProxy();
            try {
                MethodCall mc = MethodCall.getMethodCall(internalRROMethods[0], new Object[0],
                        new HashMap<TypeVariable<?>, Class<?>>());
                Request r = new InternalRemoteRemoteObjectRequest(mc);

                SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

                this.stub = reply.getResult().getResult();
            } catch (SecurityException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (ProActiveException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (RenegotiateSessionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return this.stub;
    }

    public Object getObjectProxy(RemoteRemoteObject rmo) throws ProActiveException {
        if (this.stub == null) {
            this.getObjectProxy();
        }
        return this.stub;
    }

    public String getClassName() {
        try {
            MethodCall mc = MethodCall.getMethodCall(methods[2], new Object[0],
                    new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new RemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

            return (String) reply.getResult().getResult();
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public String getProxyName() {
        try {
            MethodCall mc = MethodCall.getMethodCall(methods[4], new Object[0],
                    new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new RemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

            return (String) reply.getResult().getResult();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void getActiveProtocols(RemoteRemoteObject rro) {
        try {
            MethodCall mc = MethodCall.getMethodCall(internalRROMethods[3], new Object[0],
                    new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new InternalRemoteRemoteObjectRequest(mc);

            // Use the first created RRO for finding all possible others
            SynchronousReplyImpl reply = (SynchronousReplyImpl) rro.receiveMessage(r);

            ArrayList<RemoteRemoteObject> rros = (ArrayList<RemoteRemoteObject>) reply.getResult()
                    .getResult();
            for (RemoteRemoteObject ro : rros) {
                this.remoteObjectSet.add(ro);
            }
        } catch (ProActiveException pae) {
            // TODO Auto-generated catch block
            pae.printStackTrace();
        } catch (IOException ioe) {
            // TODO Auto-generated catch block
            ioe.printStackTrace();
        } catch (RenegotiateSessionException rse) {
            // TODO Auto-generated catch block
            rse.printStackTrace();
        }
    }

    @Override
    public int hashCode() {
        return this.remoteObjectSet.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // Self reference
        if (this == obj) {
            return true;
        }
        // Null reference
        if (obj == null) {
            return false;
        }
        // Class mismatch
        if (getClass() != obj.getClass()) {
            return false;
        }
        return this.hashCode() == ((RemoteObjectAdapter) obj).hashCode();
    }

    public Class<?> getTargetClass() {
        try {
            MethodCall mc = MethodCall.getMethodCall(methods[3], new Object[0],
                    new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new RemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

            return (Class<?>) reply.getResult().getResult();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public Class<?> getAdapterClass() {
        try {
            MethodCall mc = MethodCall.getMethodCall(methods[5], new Object[0],
                    new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new RemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

            return (Class<?>) reply.getResult().getResult();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public ProActiveSecurityManager getProActiveSecurityManager(Entity user)
            throws SecurityNotAvailableException, AccessControlException, IOException {
        try {
            MethodCall mc = MethodCall.getMethodCall(securityMethods[10], new Object[] { user },
                    new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new InternalRemoteRemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

            return (ProActiveSecurityManager) reply.getResult().getResult();
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void setProActiveSecurityManager(Entity user, PolicyServer policyServer)
            throws SecurityNotAvailableException, AccessControlException, IOException {
        try {
            MethodCall mc = MethodCall.getMethodCall(securityMethods[11],
                    new Object[] { user, policyServer }, new HashMap<TypeVariable<?>, Class<?>>());
            Request r = new InternalRemoteRemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return;
    }

    // TODO: write a public method which does't throw exception.
    public URI getURI() throws ProActiveException {
        try {
            MethodCall mc = MethodCall.getMethodCall(internalRROMethods[2], new Object[0],
                    new HashMap<TypeVariable<?>, Class<?>>());

            Request r = new InternalRemoteRemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

            return (URI) reply.getResult().getResult();
        } catch (IOException e) {
            throw new ProActiveException(e);
        } catch (RenegotiateSessionException e) {
            throw new ProActiveException(e);
        } catch (SecurityException e) {
            throw new ProActiveException(e);
        }
    }

    protected URI getURI(RemoteRemoteObject rro) throws ProActiveException {
        try {
            MethodCall mc = MethodCall.getMethodCall(internalRROMethods[2], new Object[0],
                    new HashMap<TypeVariable<?>, Class<?>>());

            Request r = new InternalRemoteRemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) rro.receiveMessage(r);

            return (URI) reply.getResult().getResult();
        } catch (IOException e) {
            throw new ProActiveException(e);
        } catch (RenegotiateSessionException e) {
            throw new ProActiveException(e);
        } catch (SecurityException e) {
            throw new ProActiveException(e);
        }
    }

    public RemoteObjectProperties getRemoteObjectProperties() {
        try {
            MethodCall mc = MethodCall.getMethodCall(methods[6], new Object[0],
                    new HashMap<TypeVariable<?>, Class<?>>());

            Request r = new InternalRemoteRemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);

            return (RemoteObjectProperties) reply.getResult().getResult();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Adapter getAdapter() {
        try {
            MethodCall mc = MethodCall.getMethodCall(methods[7], new Object[0],
                    new HashMap<TypeVariable<?>, Class<?>>());

            Request r = new InternalRemoteRemoteObjectRequest(mc);

            SynchronousReplyImpl reply = (SynchronousReplyImpl) this.receiveMessage(r);
            return (Adapter) reply.getResult().getResult();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
