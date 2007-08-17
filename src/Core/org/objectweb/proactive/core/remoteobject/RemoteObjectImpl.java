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
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.core.remoteobject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.mop.ConstructionOfProxyObjectFailedException;
import org.objectweb.proactive.core.mop.ConstructionOfReifiedObjectFailedException;
import org.objectweb.proactive.core.mop.InvalidProxyClassException;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.MethodCallExecutionFailedException;
import org.objectweb.proactive.core.mop.ReifiedCastException;
import org.objectweb.proactive.core.security.Communication;
import org.objectweb.proactive.core.security.ProActiveSecurityManager;
import org.objectweb.proactive.core.security.SecurityContext;
import org.objectweb.proactive.core.security.crypto.KeyExchangeException;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;
import org.objectweb.proactive.core.security.exceptions.SecurityNotAvailableException;
import org.objectweb.proactive.core.security.securityentity.Entity;


/**
 *         Implementation of a remote object.
 *
 *
 */
public class RemoteObjectImpl implements RemoteObject, Serializable {
    protected Object target;
    protected String className;
    protected String proxyClassName;
    protected ProActiveSecurityManager psm;

    public RemoteObjectImpl(String className, Object target) {
        this.target = target;
        this.className = className;
        this.proxyClassName = "org.objectweb.proactive.core.remoteobject.SynchronousProxy";
    }

    public RemoteObjectImpl(String className, Object target,
        ProActiveSecurityManager psm) {
        this.target = target;
        this.className = className;
        this.psm = psm;
    }

    public Reply receiveMessage(Request message)
        throws RenegotiateSessionException {
        try {
            if (message.isCiphered() && (psm != null)) {
                message.decrypt(psm);
            }
            Object o = ((Request) message).getMethodCall().execute(target);
            return new SynchronousReplyImpl(o);
        } catch (MethodCallExecutionFailedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    // implements SecurityEntity ----------------------------------------------
    public X509Certificate getCertificate()
        throws SecurityNotAvailableException, IOException {
        if (psm != null) {
            return psm.getCertificate();
        }
        throw new SecurityNotAvailableException();
    }

    public byte[] getCertificateEncoded()
        throws SecurityNotAvailableException, IOException {
        if (psm != null) {
            return psm.getCertificateEncoded();
        }
        throw new SecurityNotAvailableException();
    }

    public ArrayList<Entity> getEntities()
        throws SecurityNotAvailableException, IOException {
        if (psm != null) {
            return psm.getEntities();
        }
        throw new SecurityNotAvailableException();
    }

    public SecurityContext getPolicy(SecurityContext securityContext)
        throws SecurityNotAvailableException, IOException {
        if (psm != null) {
            return psm.getPolicy(securityContext);
        }
        throw new SecurityNotAvailableException();
    }

    public PublicKey getPublicKey()
        throws SecurityNotAvailableException, IOException {
        if (psm != null) {
            return psm.getPublicKey();
        }
        throw new SecurityNotAvailableException();
    }

    public byte[][] publicKeyExchange(long sessionID, byte[] myPublicKey,
        byte[] myCertificate, byte[] signature)
        throws SecurityNotAvailableException, RenegotiateSessionException,
            KeyExchangeException, IOException {
        if (psm != null) {
            return psm.publicKeyExchange(sessionID, myPublicKey, myCertificate,
                signature);
        }
        throw new SecurityNotAvailableException();
    }

    public byte[] randomValue(long sessionID, byte[] clientRandomValue)
        throws SecurityNotAvailableException, RenegotiateSessionException,
            IOException {
        if (psm != null) {
            return psm.randomValue(sessionID, clientRandomValue);
        }
        throw new SecurityNotAvailableException();
    }

    public byte[][] secretKeyExchange(long sessionID, byte[] encodedAESKey,
        byte[] encodedIVParameters, byte[] encodedClientMacKey,
        byte[] encodedLockData, byte[] parametersSignature)
        throws SecurityNotAvailableException, RenegotiateSessionException,
            IOException {
        if (psm != null) {
            return psm.secretKeyExchange(sessionID, encodedAESKey,
                encodedIVParameters, encodedClientMacKey, encodedLockData,
                parametersSignature);
        }
        throw new SecurityNotAvailableException();
    }

    public long startNewSession(Communication policy)
        throws SecurityNotAvailableException, RenegotiateSessionException,
            IOException {
        if (psm != null) {
            return psm.startNewSession(policy);
        }
        throw new SecurityNotAvailableException();
    }

    public void terminateSession(long sessionID)
        throws SecurityNotAvailableException, IOException {
        if (psm != null) {
            psm.terminateSession(sessionID);
        }
        throw new SecurityNotAvailableException();
    }

    public Object getObjectProxy() throws ProActiveException {
        try {
            return MOP.newInstance(className, className, null, null,
                this.proxyClassName, new Object[] { null });
        } catch (ClassNotReifiableException e) {
            e.printStackTrace();
        } catch (ReifiedCastException e) {
            e.printStackTrace();
        } catch (InvalidProxyClassException e) {
            e.printStackTrace();
        } catch (ConstructionOfProxyObjectFailedException e) {
            e.printStackTrace();
        } catch (ConstructionOfReifiedObjectFailedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
