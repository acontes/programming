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
package org.objectweb.proactive.core.body.xmlhttp;

import org.apache.log4j.Logger;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.future.FuturePool;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.exceptions.handler.Handler;
import org.objectweb.proactive.core.rmi.ClassServer;
import org.objectweb.proactive.core.runtime.xmlhttp.RuntimeReply;
import org.objectweb.proactive.core.runtime.xmlhttp.RuntimeRequest;
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
    private static Logger logger =  Logger.getLogger("XML_HTTP");

    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    public RemoteBodyAdapter() {
    }

    public RemoteBodyAdapter(UniversalBody body) throws ProActiveException {
        this.bodyID = body.getID();
        this.url = ClassServer.getUrl();
        this.port = ClassServer.getServerSocketPort();
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
    public static void register(RemoteBodyAdapter paBody, String urn) {
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
    public static void unregister(String urn) {
        urnBodys.put(urn, null);
    }

    /**
     * Looks-up an active object previously registered in the bodys table .
     * @param urn the urn (in fact its url + name)  the remote Body is registered to
     * @return a UniversalBody
     */
    public static UniversalBody lookup(String urn) {
        String url;

        int port = ClassServer.DEFAULT_SERVER_BASE_PORT;

        url = urn;

        if (urn.lastIndexOf(":") > 4) {
            port = Integer.parseInt(urn.substring(urn.lastIndexOf(':'),
                        urn.lastIndexOf(':') + 5));
        }

        urn = urn.substring(urn.lastIndexOf('/'));

        XMLHTTPLookupMessage message = new XMLHTTPLookupMessage(urn);
        message = (XMLHTTPLookupMessage) ProActiveXMLUtils.sendMessage(url,
                port, message, ProActiveXMLUtils.MESSAGE);
        return (UniversalBody) message.processMessage();
    }

    // ------------------------------------------
    public boolean equals(Object o) {
        if (!(o instanceof RemoteBodyAdapter)) {
            return false;
        }
        String methodName = "equals";
        XMLHTTPMessage msg = new XMLHTTPMethodCallMessage("equals",
                new Object[] { o }, new Class[] { o.getClass() }, this.bodyID);
        return ((Boolean) sendRequest(msg)).booleanValue();
    }

    public int hashCode() {
        return 0;
    }

    //
    // -- implements UniversalBody -----------------------------------------------
    //
    public void receiveRequest(Request request)
        throws IOException, RenegotiateSessionException {
        logger.debug("Receive Request " + request.getMethodName());
        long start = System.currentTimeMillis();

        XMLHTTPRequest xmlReq = new XMLHTTPRequest(request, this.bodyID);

        String rep = (String) ProActiveXMLUtils.sendMessage(url, port, xmlReq,
                ProActiveXMLUtils.MESSAGE);
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("execution in " + time);
    }

    public void receiveReply(Reply reply) throws IOException {
        logger.debug("Receive Reply " + reply.getResult());
        XMLHTTPReply xmlReply = new XMLHTTPReply(reply, this.bodyID);
        String rep = (String) ProActiveXMLUtils.sendMessage(this.url,
                this.port, xmlReply, ProActiveXMLUtils.MESSAGE);
    }

    public String getURL() {
        return this.url;
    }

    public static synchronized UniversalBody getBodyFromUrn(String urn) {
        return (UniversalBody) urnBodys.get(urn);
    }

    public String getNodeURL() {
        String methodName = "getNodeURL";
        ArrayList paramsList = new ArrayList();

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        Object result = sendRequest(req);

        return (String) result;
    }

    public UniqueID getID() {
        return this.bodyID;
    }

    public void updateLocation(UniqueID id, UniversalBody body)
        throws IOException {
        String methodName = "updateLocation";
        ArrayList paramsList = new ArrayList();

        paramsList.add(id);
        paramsList.add(body);

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        sendRequest(req);
        System.out.println("end update Location");
    }

    public UniversalBody getRemoteAdapter() {
        return this;
    }

    public void enableAC() throws IOException {
        String methodName = "enableAC";
        ArrayList paramsList = new ArrayList();

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);
        sendRequest(req);
    }

    public void disableAC() throws IOException {
        String methodName = "disableAC";
        ArrayList paramsList = new ArrayList();

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        sendRequest(req);
    }

    public void setImmediateService(String methodName)
        throws IOException {
        String methodName_ = "setImmediateService";
        ArrayList paramsList = new ArrayList();

        paramsList.add(methodName);

        RuntimeRequest req = new RuntimeRequest(methodName_, paramsList,
                this.bodyID);

        sendRequest(req);
    }

    public void initiateSession(int type, UniversalBody body)
        throws IOException, CommunicationForbiddenException, 
            AuthenticationException, RenegotiateSessionException, 
            SecurityNotAvailableException {
        String methodName = "initiateSession";
        ArrayList paramsList = new ArrayList();

        paramsList.add(new Integer(type));
        paramsList.add(body);

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        sendRequest(req);
    }

    public void terminateSession(long sessionID)
        throws IOException, SecurityNotAvailableException {
        String methodName = "terminateSession";
        ArrayList paramsList = new ArrayList();

        paramsList.add(new Long(sessionID));

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        sendRequest(req);
    }

    public X509Certificate getCertificate()
        throws SecurityNotAvailableException, IOException {
        String methodName = "getCertificate";
        ArrayList paramsList = new ArrayList();

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        return (X509Certificate) sendRequest(req);
    }

    public Policy getPolicyFrom(X509Certificate certificate)
        throws SecurityNotAvailableException, IOException {
        String methodName = "getPolicyFrom";
        ArrayList paramsList = new ArrayList();

        paramsList.add(certificate);

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        return (Policy) sendRequest(req);
    }

    public long startNewSession(Communication policy)
        throws SecurityNotAvailableException, IOException, 
            RenegotiateSessionException {
        String methodName = "startNewSession";
        ArrayList paramsList = new ArrayList();

        paramsList.add(policy);

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        return ((Long) sendRequest(req)).longValue();
    }

    public ConfidentialityTicket negociateKeyReceiverSide(
        ConfidentialityTicket confidentialityTicket, long sessionID)
        throws SecurityNotAvailableException, KeyExchangeException, IOException {
        String methodName = "negociateKeyReceiverSide";
        ArrayList paramsList = new ArrayList();

        paramsList.add(confidentialityTicket);
        paramsList.add(new Long(sessionID));

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        return (ConfidentialityTicket) sendRequest(req);
    }

    public PublicKey getPublicKey()
        throws SecurityNotAvailableException, IOException {
        String methodName = "getPublicKey";
        ArrayList paramsList = new ArrayList();

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        return (PublicKey) sendRequest(req);
    }

    public byte[] randomValue(long sessionID, byte[] cl_rand)
        throws SecurityNotAvailableException, Exception {
        String methodName = "randomValue";
        ArrayList paramsList = new ArrayList();

        paramsList.add(new Long(sessionID));
        paramsList.add(cl_rand);

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        return (byte[]) sendRequest(req);
    }

    public byte[][] publicKeyExchange(long sessionID,
        UniversalBody distantBody, byte[] my_pub, byte[] my_cert,
        byte[] sig_code)
        throws SecurityNotAvailableException, Exception, 
            RenegotiateSessionException {
        String methodName = "publicKeyExchange";
        ArrayList paramsList = new ArrayList();

        paramsList.add(new Long(sessionID));
        paramsList.add(distantBody);
        paramsList.add(my_pub);
        paramsList.add(my_cert);
        paramsList.add(sig_code);

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        return (byte[][]) sendRequest(req);
    }

    public byte[][] secretKeyExchange(long sessionID, byte[] tmp, byte[] tmp1,
        byte[] tmp2, byte[] tmp3, byte[] tmp4)
        throws SecurityNotAvailableException, Exception, 
            RenegotiateSessionException {
        String methodName = "secretKeyExchange";
        ArrayList paramsList = new ArrayList();

        paramsList.add(new Long(sessionID));
        paramsList.add(tmp);
        paramsList.add(tmp1);
        paramsList.add(tmp2);
        paramsList.add(tmp3);
        paramsList.add(tmp4);

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        return (byte[][]) sendRequest(req);
    }

    public Communication getPolicyTo(String type, String from, String to)
        throws SecurityNotAvailableException, IOException {
        String methodName = "getPolicyTo";
        ArrayList paramsList = new ArrayList();

        paramsList.add(type);
        paramsList.add(from);
        paramsList.add(to);

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        return (Communication) sendRequest(req);
    }

    public SecurityContext getPolicy(SecurityContext securityContext)
        throws SecurityNotAvailableException, IOException {
        String methodName = "getPolicy";
        ArrayList paramsList = new ArrayList();

        paramsList.add(securityContext);

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);
        return (SecurityContext) sendRequest(req);
    }

    public String getVNName() throws SecurityNotAvailableException, IOException {
        String methodName = "getVNName";
        ArrayList paramsList = new ArrayList();

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        return (String) sendRequest(req);
    }

    public byte[] getCertificateEncoded()
        throws SecurityNotAvailableException, IOException {
        String methodName = "getCertificateEncoded";
        ArrayList paramsList = new ArrayList();

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        return (byte[]) sendRequest(req);
    }

    public ArrayList getEntities()
        throws SecurityNotAvailableException, IOException {
        String methodName = "getEntities";
        ArrayList paramsList = new ArrayList();

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        return (ArrayList) sendRequest(req);
    }

    public ProActiveSecurityManager getProActiveSecurityManager()
        throws SecurityNotAvailableException, IOException {
        String methodName = "getProActiveSecurityManager";
        ArrayList paramsList = new ArrayList();

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        return (ProActiveSecurityManager) sendRequest(req);
    }

    private Object sendRequest(RuntimeRequest req) {
        RuntimeReply reply = (RuntimeReply) ProActiveXMLUtils.sendMessage(this.url,
                this.port, req, ProActiveXMLUtils.RUNTIME_REQUEST);

        return reply.getReturnedObject();
    }

    private Object sendRequest(XMLHTTPMessage msg) {
        XMLHTTPMessage reply = (XMLHTTPMessage) ProActiveXMLUtils.sendMessage(this.url,
                this.port, msg, ProActiveXMLUtils.RUNTIME_REQUEST);

        return reply.processMessage();
    }

    public HashMap getHandlersLevel() {
        String methodName = "getHandlersLevel";
        ArrayList paramsList = new ArrayList();

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        return (HashMap) sendRequest(req);
    }

    public void setExceptionHandler(Class handler, Class exception)
        throws ProActiveException {
        String methodName = "setExceptionHandler";
        ArrayList paramsList = new ArrayList();

        paramsList.add(handler);
        paramsList.add(exception);

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        sendRequest(req);
    }

    public Handler unsetExceptionHandler(Class exception) {
        String methodName = "unsetExceptionHandler";
        ArrayList paramsList = new ArrayList();

        paramsList.add(exception);

        RuntimeRequest req = new RuntimeRequest(methodName, paramsList,
                this.bodyID);

        return (Handler) sendRequest(req);
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
