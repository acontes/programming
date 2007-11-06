/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.core.remoteobject.http;

import java.io.IOException;
import java.net.URI;
import java.security.AccessControlException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.remoteobject.InternalRemoteRemoteObject;
import org.objectweb.proactive.core.remoteobject.RemoteObject;
import org.objectweb.proactive.core.remoteobject.RemoteObjectAdapter;
import org.objectweb.proactive.core.remoteobject.RemoteRemoteObject;
import org.objectweb.proactive.core.remoteobject.SynchronousProxy;
import org.objectweb.proactive.core.remoteobject.SynchronousReplyImpl;
import org.objectweb.proactive.core.remoteobject.http.message.HTTPRemoteObjectRequest;
import org.objectweb.proactive.core.remoteobject.http.util.exceptions.HTTPRemoteException;
import org.objectweb.proactive.core.remoteobject.http.util.messages.HttpRemoteObjectRequest;
import org.objectweb.proactive.core.security.Communication;
import org.objectweb.proactive.core.security.PolicyServer;
import org.objectweb.proactive.core.security.ProActiveSecurityManager;
import org.objectweb.proactive.core.security.SecurityContext;
import org.objectweb.proactive.core.security.TypedCertificate;
import org.objectweb.proactive.core.security.crypto.KeyExchangeException;
import org.objectweb.proactive.core.security.crypto.SessionException;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;
import org.objectweb.proactive.core.security.exceptions.SecurityNotAvailableException;
import org.objectweb.proactive.core.security.securityentity.Entities;
import org.objectweb.proactive.core.security.securityentity.Entity;


public class HttpRemoteObjectImpl implements RemoteRemoteObject {
    private boolean isLocal;

    //    private String servletURL;
    private URI remoteObjectURL;
    protected Object stub;
    protected transient InternalRemoteRemoteObject remoteObject;

    public HttpRemoteObjectImpl(InternalRemoteRemoteObject remoteObject,
        URI remoteObjectURL) {
        //    	Thread.dumpStack();

        //        if (ProActiveConfiguration.getInstance().osgiServletEnabled()) {
        //            this.servletURL = ClassServerServlet.getUrl();
        //        } else {
        //            this.servletURL = ClassServer.getUrl();
        //        }
        //
        //        System.out.println("HttpRemoteObjectImpl.HttpRemoteObjectImpl() -------------- servlet "  + this.servletURL);
        //
        this.remoteObject = remoteObject;
        this.remoteObjectURL = remoteObjectURL;

        //        try {
        //            this.stub = remoteObject.getObjectProxy(this);
        //        } catch (ProActiveException e) {
        //            e.printStackTrace();
        //        }
    }

    public Reply receiveMessage(Request message)
        throws IOException, RenegotiateSessionException, ProActiveException {
        ArrayList<Object> paramsList = new ArrayList<Object>();
        paramsList.add(message);

        HTTPRemoteObjectRequest req = new HTTPRemoteObjectRequest(message,
                this.remoteObjectURL.toString());

        req.send();

        SynchronousReplyImpl rep = (SynchronousReplyImpl) req.getReturnedObject();
        return rep;
    }

    public TypedCertificate getCertificate()
        throws SecurityNotAvailableException, IOException {
        if (this.isLocal) {
            return this.remoteObject.getCertificate();
        }

        HttpRemoteObjectRequest br = new HttpRemoteObjectRequest("getCertificate",
                new ArrayList<Object>(), this.remoteObjectURL.toString());
        br.send();
        try {
            return (TypedCertificate) br.getReturnedObject();
        } catch (Exception e) {
            throw new HTTPRemoteException("Unexpected exception", e);
        }
    }

    // public byte[] getCertificateEncoded()
    //        throws SecurityNotAvailableException, IOException {
    //        if (isLocal) {
    //            return this.remoteObject.getCertificateEncoded();
    //        } else {
    //            HttpRemoteObjectRequest br = new HttpRemoteObjectRequest("getCertificateEncoded",
    //                    new ArrayList<Object>(), this.remoteObjectURL.toString());
    //            br.send();
    //            try {
    //                return (byte[]) br.getReturnedObject();
    //            } catch (Exception e) {
    //                throw new HTTPRemoteException("Unexpected exception", e);
    //            }
    //        }
    //    }
    public Entities getEntities()
        throws SecurityNotAvailableException, IOException {
        return this.remoteObject.getEntities();
    }

    public SecurityContext getPolicy(Entities local, Entities distant)
        throws SecurityNotAvailableException, IOException {
        return this.remoteObject.getPolicy(local, distant);
    }

    public PublicKey getPublicKey()
        throws SecurityNotAvailableException, IOException {
        return this.remoteObject.getPublicKey();
    }

    public byte[] publicKeyExchange(long sessionID, byte[] signature)
        throws SecurityNotAvailableException, RenegotiateSessionException,
            KeyExchangeException, IOException {
        return this.remoteObject.publicKeyExchange(sessionID, signature);
    }

    public byte[] randomValue(long sessionID, byte[] clientRandomValue)
        throws SecurityNotAvailableException, RenegotiateSessionException,
            IOException {
        return this.remoteObject.randomValue(sessionID, clientRandomValue);
    }

    public byte[][] secretKeyExchange(long sessionID, byte[] encodedAESKey,
        byte[] encodedIVParameters, byte[] encodedClientMacKey,
        byte[] encodedLockData, byte[] parametersSignature)
        throws SecurityNotAvailableException, RenegotiateSessionException,
            IOException {
        return this.remoteObject.secretKeyExchange(sessionID, encodedAESKey,
            encodedIVParameters, encodedClientMacKey, encodedLockData,
            parametersSignature);
    }

    public long startNewSession(long distantSessionID, SecurityContext policy,
        TypedCertificate distantCertificate)
        throws SecurityNotAvailableException, IOException, SessionException {
        return this.remoteObject.startNewSession(distantSessionID, policy,
            distantCertificate);
    }

    public void terminateSession(long sessionID)
        throws SecurityNotAvailableException, IOException {
        this.remoteObject.terminateSession(sessionID);
    }

    public Object getObjectProxy() throws ProActiveException, IOException {
        ((StubObject) stub).setProxy(new SynchronousProxy(null,
                new Object[] { this }));

        return stub;
    }

    public void setObjectProxy(Object stub)
        throws ProActiveException, IOException {
        this.stub = stub;
    }

    public void setURI(URI url) {
        this.remoteObjectURL = url;
    }

    public URI getURI() {
        return this.remoteObjectURL;
    }

    public RemoteObject getRemoteObject() throws ProActiveException {
        return new RemoteObjectAdapter(this);
    }

    public String getClassName() throws ProActiveException, IOException {
        if (isLocal) {
            return this.remoteObject.getRemoteObject().getClassName();
        } else {
            HttpRemoteObjectRequest br = new HttpRemoteObjectRequest("getClassName",
                    new ArrayList<Object>(), this.remoteObjectURL.toString());
            br.send();
            try {
                return (String) br.getReturnedObject();
            } catch (Exception e) {
                throw new HTTPRemoteException("Unexpected exception", e);
            }
        }
    }

    public String getProxyName() throws ProActiveException, IOException {
        if (isLocal) {
            return this.remoteObject.getRemoteObject().getProxyName();
        } else {
            HttpRemoteObjectRequest br = new HttpRemoteObjectRequest("getProxyName",
                    new ArrayList<Object>(), this.remoteObjectURL.toString());
            br.send();
            try {
                return (String) br.getReturnedObject();
            } catch (Exception e) {
                throw new HTTPRemoteException("Unexpected exception", e);
            }
        }
    }

    public Class<?> getTargetClass() throws ProActiveException, IOException {
        if (isLocal) {
            return this.remoteObject.getRemoteObject().getTargetClass();
        } else {
            HttpRemoteObjectRequest br = new HttpRemoteObjectRequest("getTargetClass",
                    new ArrayList<Object>(), this.remoteObjectURL.toString());
            br.send();
            try {
                return (Class<?>) br.getReturnedObject();
            } catch (Exception e) {
                throw new HTTPRemoteException("Unexpected exception", e);
            }
        }
    }

    public Class<?> getAdapterClass() throws ProActiveException, IOException {
        if (isLocal) {
            return this.remoteObject.getRemoteObject().getAdapterClass();
        } else {
            HttpRemoteObjectRequest br = new HttpRemoteObjectRequest("getAdapterClass",
                    new ArrayList<Object>(), this.remoteObjectURL.toString());
            br.send();
            try {
                return (Class<?>) br.getReturnedObject();
            } catch (Exception e) {
                throw new HTTPRemoteException("Unexpected exception", e);
            }
        }
    }

    public ProActiveSecurityManager getProActiveSecurityManager(Entity user)
        throws SecurityNotAvailableException, AccessControlException,
            IOException {
        return this.remoteObject.getProActiveSecurityManager(user);
    }

    public void setProActiveSecurityManager(Entity user,
        PolicyServer policyServer)
        throws SecurityNotAvailableException, AccessControlException,
            IOException {
        this.remoteObject.setProActiveSecurityManager(user, policyServer);
    }
}
