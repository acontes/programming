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
package org.objectweb.proactive.core.body.http;

import org.apache.log4j.Logger;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.exceptions.handler.Handler;
import org.objectweb.proactive.core.rmi.ClassServer;
import org.objectweb.proactive.core.runtime.http.RuntimeReply;
import org.objectweb.proactive.core.util.UrlBuilder;

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
import org.objectweb.proactive.ext.webservices.utils.HTTPUnexpectedException;
import org.objectweb.proactive.ext.webservices.utils.ProActiveXMLUtils;

import java.io.IOException;
import java.io.Serializable;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;


public class RemoteBodyAdapter implements UniversalBody, Serializable {
    /**
     * an Hashtable containing all the xmlhttp  adapters registered. They can be retrieved
     * thanks to the ProActive.lookupActive method
     */
    protected static transient Hashtable urnBodys = new Hashtable();
    private static Logger logger = Logger.getLogger("XML_HTTP");

    /**
     * The unique  ID of the body
     */
    protected UniqueID bodyID;

    /**
     * The url of the Runtime where the body is located
     */
    protected String url;

    /**
     * The port of the Runntime where the body is located
     */
    protected int port;

    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    public RemoteBodyAdapter() {
    }

    public RemoteBodyAdapter(UniversalBody body) throws ProActiveException {
        this.bodyID = body.getID();
        this.url = ClassServer.getUrl();
        this.port = ClassServer.getServerSocketPort();
        
        this.url=this.url+":"+this.port;  

    }
  
    //
    // -- PUBLIC METHODS -----------------------------------------------
    //

    /**
     * Registers an active object into the table of body.
     * @param obj the active object to register.
     * @param urn The urn of the body (in fact his url + his name)
     * @exception java.io.IOException if the remote body cannot be registered
     */
    public static void register(RemoteBodyAdapter paBody, String urn)
        throws java.io.IOException {
        urn = urn.substring(urn.lastIndexOf('/'));
        urnBodys.put(urn, paBody);

        if (logger.isInfoEnabled()) {
            logger.info("register object  at " + urn);
        }
    }

    /**
     * Unregisters an active object previously registered into the bodys table
     * @param url the urn under which the active object has been registered
     */
    public static void unregister(String urn) throws java.io.IOException {
        urnBodys.put(urn, null);
    }

    /**
     * Looks-up an active object previously registered in the bodys table .
     * @param urn the urn (in fact its url + name)  the remote Body is registered to
     * @return a UniversalBody
     */
    public static UniversalBody lookup(String urn) throws java.io.IOException {
        try {
        	
            String url;
            int port = ClassServer.DEFAULT_SERVER_BASE_PORT;
            url = urn;

            if (urn.lastIndexOf(":") > 4) {
                port = UrlBuilder.getPortFromUrl(urn);
//				port = Integer.parseInt(urn.substring(urn.lastIndexOf(':'),
  //                          urn.lastIndexOf(':') + 5));
            }

            urn = urn.substring(urn.lastIndexOf('/'));

            XMLHTTPLookupMessage message = new XMLHTTPLookupMessage(urn);
            message = (XMLHTTPLookupMessage) ProActiveXMLUtils.sendMessage(url,
                    port, message, ProActiveXMLUtils.MESSAGE);

            return (UniversalBody) message.processMessage();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    // ------------------------------------------
    public boolean equals(Object o) {
        if (!(o instanceof RemoteBodyAdapter)) {
            return false; 
        }

        RemoteBodyAdapter rba = (RemoteBodyAdapter) o;

        //String methodName = "equals";
        //XMLHTTPMessage msg = new XMLHTTPMethodCallMessage("equals",
        //        new Object[] { o }, new Class[] { o.getClass() }, this.bodyID);
        //return ((Boolean) sendRequest(msg)).booleanValue();
        return (url.equals(rba.url) && bodyID.equals(rba.bodyID) &&
        (port == rba.port));
    }

    /* If necessary, uncomment. The RMI version uses the default hashCode() */

    //public int hashCode() {
    //	// Receipe from http://deptinfo.unice.fr/~grin/messupports/java/HeritageTA6.pdf
    //    return (((((port + 17) * 37) + bodyID.hashCode() + 17) * 37) + url.hashCode() + 17) * 37;
    //}
    //
    // -- implements UniversalBody -----------------------------------------------
    //
    public void receiveRequest(Request request)
        throws IOException, RenegotiateSessionException {
        try {
            //logger.debug("Receive Request " + request.getMethodName());
            //long start = System.currentTimeMillis();
            XMLHTTPRequest xmlReq = new XMLHTTPRequest(request, this.bodyID);

            String rep = (String) ProActiveXMLUtils.sendMessage(url, port,
                    xmlReq, ProActiveXMLUtils.MESSAGE);

            //long end = System.currentTimeMillis();
            //long time = end - start;
            //System.out.println("execution in " + time);
        } catch (RenegotiateSessionException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public void receiveReply(Reply reply) throws IOException {
        try {
            logger.debug("Receive Reply " + reply.getResult());

            XMLHTTPReply xmlReply = new XMLHTTPReply(reply, this.bodyID);
            String rep = (String) ProActiveXMLUtils.sendMessage(this.url,
                    this.port, xmlReply, ProActiveXMLUtils.MESSAGE);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public String getURL() {
        return this.url/*+":"+this.port*/;
    }

    public static synchronized UniversalBody getBodyFromUrn(String urn) {
        return (UniversalBody) urnBodys.get(urn);
    }

    public String getNodeURL() {
        try {
            return (String) sendRequest(new BodyRequest("getNodeURL",
                    new ArrayList(), this.bodyID));
        } catch (Exception e) {
            return "cannot contact the body to get the nodeURL";
        }
    }

    public UniqueID getID() {
        return this.bodyID;
    }

    public void updateLocation(UniqueID id, UniversalBody body)
        throws IOException {
        try {
            ArrayList paramsList = new ArrayList();
            paramsList.add(id);
            paramsList.add(body);
            sendRequest(new BodyRequest("updateLocation", paramsList,
                    this.bodyID));

            //System.out.println("end update Location");
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public UniversalBody getRemoteAdapter() {
        return this;
    }

    public void enableAC() throws IOException {
        try {
            sendRequest(new BodyRequest("enableAC", new ArrayList(),
                    this.bodyID));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public void disableAC() throws IOException {
        try {
            sendRequest(new BodyRequest("disableAC", new ArrayList(),
                    this.bodyID));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public void setImmediateService(String methodName)
        throws IOException {
        try {
            ArrayList paramsList = new ArrayList();
            paramsList.add(methodName);
            sendRequest(new BodyRequest("setImmediateService", paramsList,
                    this.bodyID));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public void initiateSession(int type, UniversalBody body)
        throws IOException, CommunicationForbiddenException, 
            AuthenticationException, RenegotiateSessionException, 
            SecurityNotAvailableException {
        try {
            ArrayList paramsList = new ArrayList();
            paramsList.add(new Integer(type));
            paramsList.add(body);
            sendRequest(new BodyRequest("initiateSession", paramsList,
                    this.bodyID));
        } catch (CommunicationForbiddenException e) {
            throw e;
        } catch (AuthenticationException e) {
            throw e;
        } catch (RenegotiateSessionException e) {
            throw e;
        } catch (SecurityNotAvailableException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public void terminateSession(long sessionID)
        throws IOException, SecurityNotAvailableException {
        try {
            ArrayList paramsList = new ArrayList();
            paramsList.add(new Long(sessionID));
            sendRequest(new BodyRequest("terminateSession", paramsList,
                    this.bodyID));
        } catch (SecurityNotAvailableException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public X509Certificate getCertificate()
        throws SecurityNotAvailableException, IOException {
        try {
            return (X509Certificate) sendRequest(new BodyRequest(
                    "getCertificate", new ArrayList(), this.bodyID));
        } catch (SecurityNotAvailableException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public Policy getPolicyFrom(X509Certificate certificate)
        throws SecurityNotAvailableException, IOException {
        try {
            ArrayList paramsList = new ArrayList();
            paramsList.add(certificate);

            return (Policy) sendRequest(new BodyRequest("getPolicyFrom",
                    paramsList, this.bodyID));
        } catch (SecurityNotAvailableException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public long startNewSession(Communication policy)
        throws SecurityNotAvailableException, IOException, 
            RenegotiateSessionException {
        try {
            ArrayList paramsList = new ArrayList();
            paramsList.add(policy);

            return ((Long) sendRequest(new BodyRequest("startNewSession",
                    paramsList, this.bodyID))).longValue();
        } catch (RenegotiateSessionException e) {
            throw e;
        } catch (SecurityNotAvailableException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public ConfidentialityTicket negociateKeyReceiverSide(
        ConfidentialityTicket confidentialityTicket, long sessionID)
        throws SecurityNotAvailableException, KeyExchangeException, IOException {
        try {
            ArrayList paramsList = new ArrayList();
            paramsList.add(confidentialityTicket);
            paramsList.add(new Long(sessionID));

            return (ConfidentialityTicket) sendRequest(new BodyRequest(
                    "negociateKeyReceiverSide", paramsList, this.bodyID));
        } catch (KeyExchangeException e) {
            throw e;
        } catch (SecurityNotAvailableException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public PublicKey getPublicKey()
        throws SecurityNotAvailableException, IOException {
        try {
            return (PublicKey) sendRequest(new BodyRequest("getPublicKey",
                    new ArrayList(), this.bodyID));
        } catch (SecurityNotAvailableException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public byte[] randomValue(long sessionID, byte[] cl_rand)
        throws SecurityNotAvailableException, Exception {
        try {
            ArrayList paramsList = new ArrayList();
            paramsList.add(new Long(sessionID));
            paramsList.add(cl_rand);

            return (byte[]) sendRequest(new BodyRequest("randomValue",
                    paramsList, this.bodyID));
        } catch (SecurityNotAvailableException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public byte[][] publicKeyExchange(long sessionID,
        UniversalBody distantBody, byte[] my_pub, byte[] my_cert,
        byte[] sig_code) throws SecurityNotAvailableException, Exception {
        ArrayList paramsList = new ArrayList();
        paramsList.add(new Long(sessionID));
        paramsList.add(distantBody);
        paramsList.add(my_pub);
        paramsList.add(my_cert);
        paramsList.add(sig_code);

        return (byte[][]) sendRequest(new BodyRequest("publicKeyExchange",
                paramsList, this.bodyID));
    }

    public byte[][] secretKeyExchange(long sessionID, byte[] tmp, byte[] tmp1,
        byte[] tmp2, byte[] tmp3, byte[] tmp4)
        throws SecurityNotAvailableException, Exception {
        ArrayList paramsList = new ArrayList();
        paramsList.add(new Long(sessionID));
        paramsList.add(tmp);
        paramsList.add(tmp1);
        paramsList.add(tmp2);
        paramsList.add(tmp3);
        paramsList.add(tmp4);

        return (byte[][]) sendRequest(new BodyRequest("secretKeyExchange",
                paramsList, this.bodyID));
    }

    public Communication getPolicyTo(String type, String from, String to)
        throws SecurityNotAvailableException, IOException {
        try {
            ArrayList paramsList = new ArrayList();
            paramsList.add(type);
            paramsList.add(from);
            paramsList.add(to);

            return (Communication) sendRequest(new BodyRequest(
                    "getPolicyTo", paramsList, this.bodyID));
        } catch (SecurityNotAvailableException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public SecurityContext getPolicy(SecurityContext securityContext)
        throws SecurityNotAvailableException, IOException {
        try {
            ArrayList paramsList = new ArrayList();
            paramsList.add(securityContext);

            return (SecurityContext) sendRequest(new BodyRequest(
                    "getPolicy", paramsList, this.bodyID));
        } catch (SecurityNotAvailableException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public String getVNName() throws SecurityNotAvailableException, IOException {
        try {
            return (String) sendRequest(new BodyRequest("getVNName",
                    new ArrayList(), this.bodyID));
        } catch (SecurityNotAvailableException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public byte[] getCertificateEncoded()
        throws SecurityNotAvailableException, IOException {
        try {
            return (byte[]) sendRequest(new BodyRequest(
                    "getCertificateEncoded", new ArrayList(), this.bodyID));
        } catch (SecurityNotAvailableException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public ArrayList getEntities()
        throws SecurityNotAvailableException, IOException {
        try {
            return (ArrayList) sendRequest(new BodyRequest("getEntities",
                    new ArrayList(), this.bodyID));
        } catch (SecurityNotAvailableException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public ProActiveSecurityManager getProActiveSecurityManager()
        throws SecurityNotAvailableException, IOException {
        try {
            return (ProActiveSecurityManager) sendRequest(new BodyRequest(
                    "getProActiveSecurityManager", new ArrayList(), this.bodyID));
        } catch (SecurityNotAvailableException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    private Object sendRequest(BodyRequest req) throws Exception {
        RuntimeReply reply = (RuntimeReply) ProActiveXMLUtils.sendMessage(this.url,
                this.port, req, ProActiveXMLUtils.RUNTIME_REQUEST);

        return reply.getReturnedObject();
    }

//    private Object sendRequest(XMLHTTPMessage msg) throws Exception {
//        XMLHTTPMessage reply = (XMLHTTPMessage) ProActiveXMLUtils.sendMessage(this.url,
//                this.port, msg, ProActiveXMLUtils.RUNTIME_REQUEST);
//
//        return reply.processMessage();
//    }

    public HashMap getHandlersLevel() throws java.io.IOException {
        try {
            return (HashMap) sendRequest(new BodyRequest(
                    "getHandlersLevel", new ArrayList(), this.bodyID));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public void setExceptionHandler(Class handler, Class exception)
        throws IOException {
        try {
            ArrayList paramsList = new ArrayList();
            paramsList.add(handler);
            paramsList.add(exception);
            sendRequest(new BodyRequest("setExceptionHandler", paramsList,
                    this.bodyID));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    public Handler unsetExceptionHandler(Class exception)
        throws IOException {
        try {
            ArrayList paramsList = new ArrayList();
            paramsList.add(exception);

            return (Handler) sendRequest(new BodyRequest(
                    "unsetExceptionHandler", paramsList, this.bodyID));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new HTTPUnexpectedException("Unexpected exception", e);
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.body.UniversalBody#setExceptionHandler(org.objectweb.proactive.core.exceptions.handler.Handler, java.lang.Class)
     */
    public void setExceptionHandler(Handler handler, Class exception) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.Job#getJobID()
     */
    public String getJobID() {
        // TODO Auto-generated method stub
        return null;
    }

    // TODO TODO

    /**
     * Clear the local map of handlers
     */
    public void clearHandlersLevel() throws java.io.IOException {
    }

    /**
     * Get information about the handlerizable object
     * @return
     */
    public String getHandlerizableInfo() throws java.io.IOException {
        return "TODO";
    }
}
