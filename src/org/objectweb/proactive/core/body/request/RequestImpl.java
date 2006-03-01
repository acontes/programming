/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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
package org.objectweb.proactive.core.body.request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.security.cert.X509Certificate;

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.future.FutureResult;
import org.objectweb.proactive.core.body.message.MessageImpl;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.reply.ReplyImpl;
import org.objectweb.proactive.core.exceptions.proxy.ProxyNonFunctionalException;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.MethodCallExecutionFailedException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.ext.security.ProActiveSecurity;
import org.objectweb.proactive.ext.security.ProActiveSecurityManager;
import org.objectweb.proactive.ext.security.crypto.Session;
import org.objectweb.proactive.ext.security.exceptions.RenegotiateSessionException;
import org.objectweb.proactive.ext.security.exceptions.SecurityNotAvailableException;

import sun.rmi.server.MarshalInputStream;


public class RequestImpl extends MessageImpl implements Request,
    java.io.Serializable {
    public static Logger logger = ProActiveLogger.getLogger(Loggers.REQUESTS);
    public static Logger loggerNFE = ProActiveLogger.getLogger(Loggers.NFE);
    protected MethodCall methodCall;
    protected boolean ciphered;

    /**
     * Indicates if the method has been sent through a forwarder
     */
    protected int sendCounter;

    /** transient because we deal with the serialization of this variable
       in a custom manner. see writeObject method*/
    protected transient UniversalBody sender;
    private transient ProActiveSecurityManager psm;
    private byte[][] methodCallCiphered;
    private byte[] methodCallCipheredSignature;
    public long sessionID;
    protected String codebase;
    private static Boolean enableStackTrace;
    private StackTraceElement[] stackTrace;

    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    public RequestImpl(MethodCall methodCall, UniversalBody sender,
        boolean isOneWay, long nextSequenceID) {
        super(sender.getID(), nextSequenceID, isOneWay, methodCall.getName());
        this.methodCall = methodCall;
        this.sender = sender;
        if (enableStackTrace == null) {

            /* First time */
            enableStackTrace = new Boolean(!"false".equals(System.getProperty(
                            "proactive.stack_trace")));
        }
        if (enableStackTrace.booleanValue()) {
            this.stackTrace = new Exception().getStackTrace();
        }
    }

    //
    // -- PUBLIC METHODS -----------------------------------------------
    //
    //
    // -- Implements Request -----------------------------------------------
    //
    public int send(UniversalBody destinationBody)
        throws java.io.IOException, RenegotiateSessionException {
        //System.out.println("RequestSender: sendRequest  " + methodName + " to destination");
        sendCounter++;
        return sendRequest(destinationBody);
    }

    public UniversalBody getSender() {
        return sender;
    }

    public Reply serve(Body targetBody) throws ServeException {
        if (logger.isDebugEnabled()) {
            logger.debug("Serving " + this.getMethodName());
        }
        FutureResult result = serveInternal(targetBody);
        if (logger.isDebugEnabled()) {
            logger.debug("result: " + result);
        }
        if (isOneWay) { // || (sender == null)) {
            return null;
        }
        result.augmentException(stackTrace);
        stackTrace = null;
        return createReply(targetBody, result);
    }

    public Reply serveAlternate(Body targetBody, ProxyNonFunctionalException nfe) {
        if (loggerNFE.isDebugEnabled()) {
            loggerNFE.debug("*** Serving an alternate version of " +
                this.getMethodName());
            if (nfe != null) {
                loggerNFE.debug("*** Result  " + nfe.getClass().getName());
            } else {
                loggerNFE.debug("*** Result null");
            }
        }
        if (isOneWay) { // || (sender == null)) {
            return null;
        }
        return createReply(targetBody, new FutureResult(null, null, nfe));
    }

    public boolean hasBeenForwarded() {
        return sendCounter > 1;
    }

    public void resetSendCounter() {
        this.sendCounter = 0;
    }

    public Object getParameter(int index) {
        return methodCall.getParameter(index);
    }

    public MethodCall getMethodCall() {
        return methodCall;
    }

    public void notifyReception(UniversalBody bodyReceiver)
        throws java.io.IOException {
        if (!hasBeenForwarded()) {
            return;
        }

        //System.out.println("the request has been forwarded times");
        //we know c.res is a remoteBody since the call has been forwarded
        //if it is null, this is a one way call
        if (sender != null) {
            sender.updateLocation(bodyReceiver.getID(),
                bodyReceiver.getRemoteAdapter());
        }
    }

    //
    // -- PROTECTED METHODS -----------------------------------------------
    //
    protected FutureResult serveInternal(Body targetBody)
        throws ServeException {
        Object result = null;
        Throwable exception = null;
        try {
            //loggerNFE.warn("CALL to " + targetBody);
            result = methodCall.execute(targetBody.getReifiedObject());
        } catch (MethodCallExecutionFailedException e) {
            // e.printStackTrace();
            throw new ServeException("serve method " +
                methodCall.getReifiedMethod().toString() + " failed", e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            exception = e.getTargetException();
            if (isOneWay) {
                throw new ServeException("serve method " +
                    methodCall.getReifiedMethod().toString() + " failed",
                    exception);
            }
        }

        return new FutureResult(result, exception, null);
    }

    protected Reply createReply(Body targetBody, FutureResult result) {
        ProActiveSecurityManager psm = null;
        try {
            psm = ((AbstractBody) ProActive.getBodyOnThis()).getProActiveSecurityManager();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (SecurityNotAvailableException e) {
            // do nothing
        }

        return new ReplyImpl(targetBody.getID(), sequenceNumber, methodName,
            result, psm);
    }

    public boolean crypt(ProActiveSecurityManager psm,
        UniversalBody destinationBody) throws RenegotiateSessionException {
        try {
            if (logger.isDebugEnabled()) {
                ProActiveLogger.getLogger(Loggers.SECURITY_REQUEST).debug(" sending request " +
                    methodCall.getName());
            }
            if (!ciphered && !hasBeenForwarded()) {
                sessionID = 0;

                if (sender == null) {
                    logger.warn("sender is null but why ?");
                }

                byte[] certE = destinationBody.getCertificateEncoded();
                X509Certificate cert = ProActiveSecurity.decodeCertificate(certE);
                sessionID = psm.getSessionIDTo(cert);
                if (sessionID != 0) {
                    methodCallCiphered = psm.encrypt(sessionID, methodCall,
                            Session.ACT_AS_CLIENT);
                    ciphered = true;
                    methodCall = null;
                    if (logger.isDebugEnabled()) {
                        ProActiveLogger.getLogger(Loggers.SECURITY_REQUEST)
                                       .debug("methodcallciphered " +
                            methodCallCiphered + ", ciphered " + ciphered +
                            ", methodCall " + methodCall);
                    }
                }
            }
        } catch (SecurityNotAvailableException e) {
            // do nothing
            //  e.printStackTrace();
            logger.debug("Request : security disabled");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    protected int sendRequest(UniversalBody destinationBody)
        throws java.io.IOException, RenegotiateSessionException {
        try {
            this.crypt(((AbstractBody) ProActive.getBodyOnThis()).getProActiveSecurityManager(),
                destinationBody);
        } catch (SecurityNotAvailableException e) {
            //TODO remove SecurityNotAvalaible e.printStackTrace();
        }

        int ftres = destinationBody.receiveRequest(this);

        if (logger.isDebugEnabled()) {
            logger.debug(" sending request finished");
        }

        return ftres;
    }

    // security issue
    public boolean isCiphered() {
        return ciphered;
    }

    public boolean decrypt(ProActiveSecurityManager psm)
        throws RenegotiateSessionException {
        //  String localCodeBase = null;
        //     if (ciphered) {
        ProActiveLogger.getLogger(Loggers.SECURITY_REQUEST).debug(" RequestImpl " +
            sessionID + " decrypt : methodcallciphered " + methodCallCiphered +
            ", ciphered " + ciphered + ", methodCall " + methodCall);

        if ((ciphered) && (psm != null)) {
            try {
                ProActiveLogger.getLogger(Loggers.SECURITY_REQUEST).debug("ReceiveRequest : this body is " +
                    psm.getCertificate().getSubjectDN() + " " +
                    psm.getCertificate().getPublicKey());
                byte[] decryptedMethodCall = psm.decrypt(sessionID,
                        methodCallCiphered, Session.ACT_AS_SERVER);

                //ProActiveLogger.getLogger("security.request").debug("ReceiveRequest :method call apres decryption : " +  ProActiveSecurityManager.displayByte(decryptedMethodCall));
                ByteArrayInputStream bin = new ByteArrayInputStream(decryptedMethodCall);
                MarshalInputStream in = new MarshalInputStream(bin);

                // ObjectInputStream in = new ObjectInputStream(bin);
                methodCall = (MethodCall) in.readObject();
                in.close();
                ciphered = false;

                //  logger.info("After decoding method call  seq id " +sequenceNumber + ":" + ciphered + ":" + sessionID + "  "+ methodCall + ":" +methodCallCiphered);
                return true;
            } catch (ClassNotFoundException e) {
                int index = e.toString().indexOf(':');
                String className = e.toString().substring(index).trim();
                className = className.substring(2);

                //			   		//		try {
                //  MOPClassLoader currentClassLoader =	org.objectweb.proactive.core.mop.MOPClassLoader.createMOPClassLoader();
                // this.getClass().getClassLoader().loadClass(className);
                //    currentClassLoader.loadClass(className);  
                this.decrypt(psm);

                //		} catch (ClassNotFoundException ex) {
                //		e.printStackTrace();
                //	}
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // hum something wrong during decryption, trying with a new session
                throw new RenegotiateSessionException("");
            }

            //    System.setProperty("java.rmi.server.codebase",localCodeBase);
        }

        return false;
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.body.request.Request#getSessionId()
     */
    public long getSessionId() {
        return sessionID;
    }

    //
    // -- PRIVATE METHODS FOR SERIALIZATION -----------------------------------------------
    //
    private void writeObject(java.io.ObjectOutputStream out)
        throws java.io.IOException {
        out.defaultWriteObject();
        out.writeObject(sender.getRemoteAdapter());
    }

    private void readObject(java.io.ObjectInputStream in)
        throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        sender = (UniversalBody) in.readObject(); // it is actually a UniversalBody
    }
}
