/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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
package org.objectweb.proactive.core.body.jini;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.ext.security.Communication;
import org.objectweb.proactive.ext.security.CommunicationForbiddenException;
import org.objectweb.proactive.ext.security.Policy;
import org.objectweb.proactive.ext.security.ProActiveSecurityManager;
import org.objectweb.proactive.ext.security.SecurityContext;
import org.objectweb.proactive.ext.security.crypto.AuthenticationException;
import org.objectweb.proactive.ext.security.crypto.ConfidentialityTicket;
import org.objectweb.proactive.ext.security.crypto.KeyExchangeException;
import org.objectweb.proactive.ext.security.exceptions.RenegotiateSessionException;
import org.objectweb.proactive.ext.security.exceptions.SecurityNotAvailableException;

import java.io.IOException;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

import java.util.ArrayList;


/**
 * An object implementing this interface provides the minimum service a body offers
 * remotely. This interface is the glue with the JINI Remote interface that allow the
 * body to be accessed remotely.
 *
 * @author  ProActive Team
 * @version 1.0,  2001/10/23
 * @since   ProActive 0.9
 * @see java.rmi.Remote
 * @see org.objectweb.proactive.core.body.UniversalBody
 */
public interface JiniBody extends java.rmi.Remote {

    /**
     * Receives a request for later processing. The call to this method is non blocking
     * unless the body cannot temporary receive the request.
     * @param r the request to process
     * @exception java.io.IOException if the request cannot be accepted
     */
    public void receiveRequest(Request r)
        throws java.io.IOException, RenegotiateSessionException;

    /**
     * Receives a reply in response to a former request.
     * @param r the reply received
     * @exception java.io.IOException if the reply cannot be accepted
     */
    public void receiveReply(Reply r) throws java.io.IOException;

    /**
     * Returns the url of the node this body is associated to
     * The url of the node can change if the active object migrates
     * @return the url of the node this body is associated to
     * @exception java.rmi.RemoteException if an exception occured during the jini communication
     */
    public String getNodeURL() throws java.rmi.RemoteException;

    /**
     * Returns the UniqueID of this body
     * This identifier is unique accross all JVMs
     * @return the UniqueID of this body
     * @exception java.rmi.RemoteException if an exception occured during the jini communication
     */
    public UniqueID getID() throws java.rmi.RemoteException;

    public String getJobID() throws java.rmi.RemoteException;

    /**
     * Signals to this body that the body identified by id is now to a new
     * jini location. The body given in parameter is a new stub pointing
     * to this new location. This call is a way for a body to signal to his
     * peer that it has migrated to a new location
     * @param id the id of the body
     * @param body the stub to the new location
     * @exception java.io.IOException if an exception occured during the jini communication
     */
    public void updateLocation(UniqueID id, UniversalBody body)
        throws java.io.IOException;

    /**
     *  Enables automatic continuation mechanism for this body
     */
    public void enableAC() throws java.io.IOException;

    /**
     *  Disables automatic continuation mechanism for this body
     */
    public void disableAC() throws java.io.IOException;

    /**
     * For setting an immediate service for this body.
     * An immediate service is a method that will bw excecuted by the calling thread.
     */
    public void setImmediateService(String methodName)
        throws java.io.IOException;

    // SECURITY
    public void initiateSession(int type, UniversalBody body)
        throws java.io.IOException, CommunicationForbiddenException, 
            AuthenticationException, java.rmi.RemoteException, 
            RenegotiateSessionException, SecurityNotAvailableException;

    public void terminateSession(long sessionID)
        throws java.io.IOException, SecurityNotAvailableException;

    public X509Certificate getCertificate()
        throws java.io.IOException, SecurityNotAvailableException;

    public ProActiveSecurityManager getProActiveSecurityManager()
        throws java.io.IOException, SecurityNotAvailableException;

    public Policy getPolicyFrom(X509Certificate certificate)
        throws java.io.IOException, SecurityNotAvailableException;

    public long startNewSession(Communication policy)
        throws java.io.IOException, RenegotiateSessionException, 
            SecurityNotAvailableException;

    public ConfidentialityTicket negociateKeyReceiverSide(
        ConfidentialityTicket confidentialityTicket, long sessionID)
        throws java.io.IOException, KeyExchangeException, 
            SecurityNotAvailableException;

    public PublicKey getPublicKey()
        throws java.io.IOException, SecurityNotAvailableException;

    public byte[] randomValue(long sessionID, byte[] cl_rand)
        throws Exception, SecurityNotAvailableException;

    public byte[][] publicKeyExchange(long sessionID,
        UniversalBody distantBody, byte[] my_pub, byte[] my_cert,
        byte[] sig_code) throws Exception, SecurityNotAvailableException;

    public byte[][] secretKeyExchange(long sessionID, byte[] tmp, byte[] tmp1,
        byte[] tmp2, byte[] tmp3, byte[] tmp4)
        throws Exception, SecurityNotAvailableException;

    public Communication getPolicyTo(String type, String from, String to)
        throws java.io.IOException, SecurityNotAvailableException;

    /**
     * @return virtual node name where the object has been created
     */
    public String getVNName()
        throws java.io.IOException, SecurityNotAvailableException;

    /**
     * @return object certificate as byte array
     */
    public byte[] getCertificateEncoded()
        throws java.io.IOException, SecurityNotAvailableException;

    public SecurityContext getPolicy(SecurityContext securityContext)
        throws SecurityNotAvailableException, IOException;

    public ArrayList getEntities()
        throws SecurityNotAvailableException, IOException;
}
