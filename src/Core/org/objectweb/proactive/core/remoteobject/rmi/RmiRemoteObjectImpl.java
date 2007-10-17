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
package org.objectweb.proactive.core.remoteobject.rmi;

import java.io.IOException;
import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessControlException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.remoteobject.InternalRemoteRemoteObject;
import org.objectweb.proactive.core.remoteobject.RemoteObject;
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


/**
 * RMI implementation of the remote remote object interface
 *
 *
 */
public class RmiRemoteObjectImpl extends UnicastRemoteObject
    implements RmiRemoteObject {

    /**
    *
    */
    protected InternalRemoteRemoteObject internalrrObject;

    //    protected Object stub;
    //    protected URI uri;
    public RmiRemoteObjectImpl() throws java.rmi.RemoteException {
    }

    public RmiRemoteObjectImpl(InternalRemoteRemoteObject target)
        throws java.rmi.RemoteException {
        this.internalrrObject = target;
    }

    public RmiRemoteObjectImpl(InternalRemoteRemoteObject target,
        RMIServerSocketFactory sf, RMIClientSocketFactory cf)
        throws java.rmi.RemoteException {
        super(0, cf, sf);
        this.internalrrObject = target;
    }

    public Reply receiveMessage(Request message)
        throws RemoteException, RenegotiateSessionException, ProActiveException,
            IOException {
        if (message.isOneWay()) {
            this.internalrrObject.receiveMessage(message);
            return null;
        }

        return this.internalrrObject.receiveMessage(message);
    }

    public TypedCertificate getCertificate()
        throws SecurityNotAvailableException, IOException {
        return this.internalrrObject.getCertificate();
    }

//    public byte[] getCertificateEncoded()
//        throws SecurityNotAvailableException, IOException {
//        return this.remoteObject.getCertificateEncoded();
//    }


    public Entities getEntities()
        throws SecurityNotAvailableException, IOException {
        return this.internalrrObject.getEntities();
    }

    public SecurityContext getPolicy(Entities local, Entities distant)
        throws SecurityNotAvailableException, IOException {
        return this.internalrrObject.getPolicy(local, distant);
    }

    public PublicKey getPublicKey()
        throws SecurityNotAvailableException, IOException {
        return this.internalrrObject.getPublicKey();
    }

    public byte[] publicKeyExchange(long sessionID, byte[] signature)
        throws SecurityNotAvailableException, RenegotiateSessionException,
            KeyExchangeException, IOException {
        return this.internalrrObject.publicKeyExchange(sessionID, signature);
    }

    public byte[] randomValue(long sessionID, byte[] clientRandomValue)
        throws SecurityNotAvailableException, RenegotiateSessionException,
            IOException {
        return this.internalrrObject.randomValue(sessionID, clientRandomValue);
    }

    public byte[][] secretKeyExchange(long sessionID, byte[] encodedAESKey,
        byte[] encodedIVParameters, byte[] encodedClientMacKey,
        byte[] encodedLockData, byte[] parametersSignature)
        throws SecurityNotAvailableException, RenegotiateSessionException,
            IOException {
        return this.internalrrObject.secretKeyExchange(sessionID,
            encodedAESKey, encodedIVParameters, encodedClientMacKey,
            encodedLockData, parametersSignature);
    }

    public long startNewSession(long distantSessionID, SecurityContext policy,
			TypedCertificate distantCertificate) throws IOException,
			SessionException, SecurityNotAvailableException {
		return this.internalrrObject.startNewSession(distantSessionID, policy,
				distantCertificate);
	}

    public void terminateSession(long sessionID)
        throws SecurityNotAvailableException, IOException {
        this.internalrrObject.terminateSession(sessionID);
    }

    //    public Object getObjectProxy() throws ProActiveException, IOException {
    //        if (this.stub == null) {
    //            this.stub = this.internalrrObject.getObjectProxy(this);
    //
    //            //            if (stub instanceof Adapter) {
    //            //            	 ((StubObject) ((Adapter)this.stub).getAdapter()).setProxy(new SynchronousProxy(null, new Object[] { this } ));
    //            //            } else {
    //            //            ((StubObject) this.stub).setProxy(new SynchronousProxy(null, new Object[] { this } ));
    //            //            }
    //        }
    //        return this.stub;
    //    }

	public ProActiveSecurityManager getProActiveSecurityManager(Entity user)
			throws SecurityNotAvailableException, AccessControlException,
			IOException {
		return this.internalrrObject.getProActiveSecurityManager(user);
	}

	public void setProActiveSecurityManager(Entity user,
			PolicyServer policyServer) throws SecurityNotAvailableException,
			AccessControlException, IOException {
		this.internalrrObject.setProActiveSecurityManager(user, policyServer);
	}
}
