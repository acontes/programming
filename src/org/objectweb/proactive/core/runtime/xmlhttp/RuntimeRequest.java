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
import java.lang.reflect.Method;

import java.security.cert.X509Certificate;

import java.util.ArrayList;
import java.util.HashMap;
 
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

    private static HashMap hMapMethods;
    private static ProActiveRuntimeImpl runtime;
    
    static {
    	
    	// init the hashmap, that contains all the methods of  ProActiveRuntimeImpl 
        // in 'Object' (value) and the name of funtions in key 
        // (Warning two functions can t have the same name (for now)) 
        runtime = (ProActiveRuntimeImpl) ProActiveRuntimeImpl.getProActiveRuntime();
       	Method [] allmethods = runtime.getClass().getMethods();
       	int numberOfMethods = allmethods.length;
       	hMapMethods = new HashMap(numberOfMethods);
		for(int i=0;i<numberOfMethods;i++) {
			
			String methodname = allmethods[i].getName();
			if(hMapMethods.containsKey(methodname)) {
				
				Object obj = hMapMethods.get(methodname);
				
				if( ! ( obj instanceof ArrayList) ){
					ArrayList array = new ArrayList();
					array.add((Method)obj);
					array.add(allmethods[i]);
					hMapMethods.put(methodname,array);
				}
				else {
					((ArrayList)obj).add(allmethods[i]);
					hMapMethods.put(methodname,(ArrayList)obj);
				}
			} 
			else 
				hMapMethods.put(methodname,allmethods[i]);
		}     	
	
    	
    }
    
    public RuntimeRequest(String newmethodName) {
        this.methodName = newmethodName;
       	
    }
    
   
    public RuntimeRequest(String newmethodName, ArrayList newparameters) {
        this(newmethodName);
        this.parameters = newparameters;
    }

    public RuntimeRequest(String newmethodName, ArrayList newparameters,
        ArrayList mewparamsTypes) {
        this(newmethodName,newparameters);
        this.paramsTypes = mewparamsTypes;
    }

    public RuntimeRequest(String newmethodName, ArrayList newparameters, UniqueID newoaid) {
        this(newmethodName,newparameters);
        this.oaid = newoaid;
    }

    public RuntimeReply process() throws ProActiveException {
        try {
        		Object[] params = parameters.toArray();
        		Object result = null;

        		if (this.oaid == null) {
            
        			Method m = getProActiveRuntimeMethod(methodName,parameters);
        			result = m.invoke(runtime, parameters.toArray());
              
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
    
    public Method getProActiveRuntimeMethod(String methodsearch, ArrayList paramsearch){
    	  Object mret = hMapMethods.get(methodsearch);
    	  
    	  if(mret instanceof ArrayList) {
    	  	
    	  	ArrayList allSameMethod = (ArrayList)mret;
    	  	mret = null;
    	  	for(int i=0;i<allSameMethod.size();i++) {
    	  		
    	  		Method mtest = ((Method)allSameMethod.get(i));	
    	  		if( mtest.getParameterTypes().length == paramsearch.size() ) {
    	  			if(mret != null ) {
    					logger.error("----------------------------------------------------------------------------");
    					logger.error("----- ERROR : two functions in ProActiveRuntimeImpl can t have the same name");
    					logger.error("----- ERROR : and the same number of paramters ");
    					logger.error("----------------------------------------------------------------------------");
    	  				
    	  			}
    	  			else 
    	  				mret = mtest;
    	  		}
    	  	}
    	  	
    	  }
    
    	  return (Method)mret;	
    }
    
    
   
    
}
