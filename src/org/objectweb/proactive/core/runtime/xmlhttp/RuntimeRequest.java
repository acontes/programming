/*
 * Created on May 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.objectweb.proactive.core.runtime.xmlhttp;

import org.apache.log4j.Logger;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.exceptions.handler.Handler;
import org.objectweb.proactive.core.mop.ConstructorCall;
import org.objectweb.proactive.core.mop.ConstructorCallExecutionFailedException;
import org.objectweb.proactive.core.process.UniversalProcess;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.ext.security.Communication;
import org.objectweb.proactive.ext.security.CommunicationForbiddenException;
import org.objectweb.proactive.ext.security.PolicyServer;
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

import java.lang.reflect.InvocationTargetException;

import java.security.cert.X509Certificate;

import java.util.ArrayList;


/**
 * @author vlegrand
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RuntimeRequest implements Serializable {
    private static Logger logger = Logger.getLogger("XML_HTTP");
    private String methodName;
    private ArrayList parameters = new ArrayList();
    private ArrayList paramsTypes;
    private UniqueID oaid;

    public RuntimeRequest(String methodName) {
        this.methodName = methodName;
    }

    public RuntimeRequest(String methodName, ArrayList parameters) {
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public RuntimeRequest(String methodName, ArrayList parameters,
        ArrayList paramsTypes) {
        this.methodName = methodName;
        this.parameters = parameters;
        this.paramsTypes = paramsTypes;
    }

    public RuntimeRequest(String methodName, ArrayList parameters, UniqueID oaid) {
        this.methodName = methodName;
        this.parameters = parameters;
        //  this.paramsTypes = paramsTypes;
        this.oaid = oaid;
    }

    public RuntimeReply process() throws ProActiveException {
        try {
            Object[] params = parameters.toArray();

            //                        Class[] classes = new Class[parameters.size()];
            //            
            //                       if (this.paramsTypes == null) {
            //                            //Remplissage du tableau des types:
            //                            for (int i = 0; i < parameters.size(); i++) {
            //                              classes[i] = parameters.get(i).getClass();
            //                            }
            //                        } else {
            //                        	Object[] tab = paramsTypes.toArray();
            //                            for (int i = 0; i < tab.length; i++) {
            //                               classes[i] = (Class) tab[i];
            //                           }
            //                        }
            Object result = null;

            if (this.oaid == null) {
                //                if (logger.isDebugEnabled()) {
                //                    logger.debug("-- > invoquation de la methode " +
                //                        methodName + " ( " + parameters +
                //                        ") sur le runtime :  " +
                //                        (ProActiveRuntimeImpl) ProActiveRuntimeImpl.getProActiveRuntime());
                //                }
                ProActiveRuntimeImpl runtime = (ProActiveRuntimeImpl) ProActiveRuntimeImpl.getProActiveRuntime();
                if (methodName.equals("createLocalNode")) {
                    String nodeName = (String) parameters.get(0);

                    //nodeName = nodeName.substring(nodeName.indexOf('/'));
                    result = runtime.createLocalNode(nodeName,
                            ((Boolean) parameters.get(1)).booleanValue(),
                            (PolicyServer) parameters.get(2),
                            (String) parameters.get(3),
                            (String) parameters.get(4));
                } else if (methodName.equals("killAllNodes")) {
                    runtime.killAllNodes();
                } else if (methodName.equals("killNode")) {
                    runtime.killNode((String) parameters.get(0));
                } else if (methodName.equals("createVM")) {
                    runtime.createVM((UniversalProcess) parameters.get(0));
                } else if (methodName.equals("getLocalNodeNames")) {
                    result = runtime.getLocalNodeNames();
                } else if (methodName.equals("getVMInformation")) {
                	
                    result = runtime.getVMInformation();
                    
                } else if (methodName.equals("register")) {
                		
                    runtime.register((ProActiveRuntime) parameters.get(0),
                        (String) parameters.get(1), (String) parameters.get(2),
                        (String) parameters.get(3), (String) parameters.get(4));
                } else if (methodName.equals("getProActiveRuntime")) {
                    result = ProActiveRuntimeImpl.getProActiveRuntime();
                } else if (methodName.equals("getProActiveRuntimes")) {
                
                    result = runtime.getProActiveRuntimes();
                } else if (methodName.equals("killRT")) {
                    runtime.killRT(((Boolean) parameters.get(0)).booleanValue());
                } else if (methodName.equals("getActiveObjects")) {
                    if (parameters.size() == 1) {
                        result = runtime.getActiveObjects((String) parameters.get(
                                    0));
                    } else {
                        result = runtime.getActiveObjects((String) parameters.get(
                                    0), (String) parameters.get(1));
                    }
                } else if (methodName.equals("getVirtualNode")) {
                    result = runtime.getVirtualNode((String) parameters.get(0));
                } else if (methodName.equals("registerVirtualNode")) {
                    runtime.registerVirtualNode((String) parameters.get(0),
                        ((Boolean) parameters.get(1)).booleanValue());
                } else if (methodName.equals("unregisterVirtualNode")) {
                    runtime.unregisterVirtualNode((String) parameters.get(0));
                } else if (methodName.equals("unregisterAllVirtualNodes")) {
                    runtime.unregisterAllVirtualNodes();
                } else if (methodName.equals("createBody")) {
                    String nodeName = (String) parameters.get(0);
                    System.out.println("Create Body  on " + nodeName);
                    result = runtime.createBody((String) parameters.get(0),
                            (ConstructorCall) parameters.get(1),
                            ((Boolean) parameters.get(2)).booleanValue());
                } else if (methodName.equals("receiveBody")) {
                    result = runtime.receiveBody((String) parameters.get(0),
                            (Body) parameters.get(1));
                } else if (methodName.equals("getPolicyServer")) {
                    result = runtime.getPolicyServer();
                } else if (methodName.equals("setProActiveSecurityManager")) {
                    runtime.setProActiveSecurityManager((ProActiveSecurityManager) parameters.get(
                            0));
                } else if (methodName.equals("getCreatorCertificate")) {
                    result = runtime.getCreatorCertificate();
                } else if (methodName.equals("getVNName")) {
                    result = runtime.getVNName((String) parameters.get(0));
                } else if (methodName.equals("setDefaultNodeVirtualNodeName")) {
                    runtime.setDefaultNodeVirtualNodeName((String) parameters.get(
                            0));
                } else if (methodName.equals("getNodePolicyServer")) {
                    result = runtime.getNodePolicyServer((String) parameters.get(
                                0));
                } else if (methodName.equals("enableSecurityIfNeeded")) {
                    runtime.enableSecurityIfNeeded();
                } else if (methodName.equals("getNodeCertificate")) {
                    result = runtime.getNodeCertificate((String) parameters.get(
                                0));
                } else if (methodName.equals("getEntities")) {
                    if (parameters.size() == 0) {
                        result = runtime.getEntities();
                    } else if ((parameters.size() == 1) &&
                            parameters.get(0) instanceof String) {
                        result = runtime.getEntities((String) parameters.get(0));
                    } else if ((parameters.size() == 2) &&
                            parameters.get(0) instanceof String) {
                        result = runtime.getEntities((UniversalBody) parameters.get(
                                    0));
                    }
                } else if (methodName.equals("getJobID")) {
                    if (parameters.size() == 0) {
                        result = runtime.getJobID();
                    } else {
                        result = runtime.getJobID((String) parameters.get(0));
                    }
                } else if (methodName.equals("getNodesNames")){
                	result = runtime.getNodesNames ();
                }

                //                    Method m = runtime.getClass().getMethod(methodName, classes);;
                //                    MethodCall mc = MethodCall.getMethodCall(m, parameters.toArray());
                //    result = mc.execute(runtime);                
                //                    result = runtime.getClass().getMethod(methodName, classes)
                //                                .invoke(runtime, params);
            } else {
                //Recuperation du body sur lequel on va faire appel
                Body body = ProActiveXMLUtils.getBody(this.oaid);

                //                if (logger.isDebugEnabled()) {
                //                    logger.debug("Invocation de " + methodName + " sur " +
                //                        body);
                //                }
                if (methodName.equals("equals")) {
                    result = new Boolean(body.equals(parameters.get(0)));
                } else if (methodName.equals("hashCode")) {
                    result = new Integer(body.hashCode());
                } else if (methodName.equals("getNodeURL")) {
                    result = body.getNodeURL();
                } else if (methodName.equals("updateLocation")) {
                    body.updateLocation((UniqueID) parameters.get(0),
                        (UniversalBody) parameters.get(1));
                } else if (methodName.equals("enableAC")) {
                    body.enableAC();
                } else if (methodName.equals("disableAC")) {
                    body.disableAC();
                } else if (methodName.equals("setImmediateService")) {
                    body.setImmediateService((String) parameters.get(0));
                } else if (methodName.equals("initiateSession")) {
                    body.initiateSession(((Integer) parameters.get(0)).intValue(),
                        (UniversalBody) parameters.get(1));
                } else if (methodName.equals("terminateSession")) {
                    body.terminateSession(((Long) parameters.get(0)).longValue());
                } else if (methodName.equals("getCertificate")) {
                    result = body.getCertificate();
                } else if (methodName.equals("getPolicyFrom")) {
                    result = body.getPolicyFrom((X509Certificate) parameters.get(
                                0));
                } else if (methodName.equals("startNewSession")) {
                    body.startNewSession((Communication) parameters.get(0));
                } else if (methodName.equals("negociateKeyReceiverSide")) {
                    body.negociateKeyReceiverSide((ConfidentialityTicket) parameters.get(
                            0), ((Long) parameters.get(1)).longValue());
                } else if (methodName.equals("getPublicKey")) {
                    result = body.getPublicKey();
                } else if (methodName.equals("randomValue")) {
                    result = body.randomValue(((Long) parameters.get(0)).longValue(),
                            (byte[]) parameters.get(1));
                } else if (methodName.equals("publicKeyExchange")) {
                    result = body.publicKeyExchange(((Long) parameters.get(0)).longValue(),
                            (UniversalBody) parameters.get(1),
                            (byte[]) parameters.get(2),
                            (byte[]) parameters.get(3),
                            (byte[]) parameters.get(4));
                } else if (methodName.equals("secretKeyExchange")) {
                    result = body.secretKeyExchange(((Long) parameters.get(0)).longValue(),
                            (byte[]) parameters.get(1),
                            (byte[]) parameters.get(2),
                            (byte[]) parameters.get(3),
                            (byte[]) parameters.get(4),
                            (byte[]) parameters.get(5));
                } else if (methodName.equals("getPolicyTo")) {
                    result = body.getPolicyTo((String) parameters.get(0),
                            (String) parameters.get(1),
                            (String) parameters.get(2));
                } else if (methodName.equals("getPolicy")) {
                    result = body.getPolicy((SecurityContext) parameters.get(0));
                } else if (methodName.equals("getVNName")) {
                    result = body.getVNName();
                } else if (methodName.equals("getCertificateEncoded")) {
                    result = body.getCertificateEncoded();
                } else if (methodName.equals("getEntities")) {
                    result = body.getCertificateEncoded();
                } else if (methodName.equals("getProActiveSecurityManager")) {
                    result = body.getProActiveSecurityManager();
                } else if (methodName.equals("getHandlersLevel")) {
                    result = body.getHandlersLevel();
                } else if (methodName.equals("setExceptionHandler")) {
                    body.setExceptionHandler((Handler) parameters.get(0),
                        (Class) parameters.get(1));
                } else if (methodName.equals("unsetExceptionHandler")) {
                    result = body.unsetExceptionHandler((Class) parameters.get(
                                0));
                }

                //                DOES NOT WORK FOR PRIMITIVE TYPES !!!!!!
                //                Method m = body.getClass().getMethod(methodName, classes);;
                //                MethodCall mc = MethodCall.getMethodCall(m, parameters.toArray());
                //result = mc.execute(body);      
                //                result = body.getClass().getMethod(methodName, classes).invoke(body,
                //                        parameters.toArray());
            }
            return new RuntimeReply(result);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ConstructorCallExecutionFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CommunicationForbiddenException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AuthenticationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityNotAvailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (KeyExchangeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public ArrayList getParameters() {
        return this.parameters;
    }
}
