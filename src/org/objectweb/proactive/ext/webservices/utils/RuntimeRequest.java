/*
 * Created on May 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.objectweb.proactive.ext.webservices.utils;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.mop.ConstructorCall;
import org.objectweb.proactive.core.mop.ConstructorCallExecutionFailedException;
import org.objectweb.proactive.core.process.UniversalProcess;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.ext.security.PolicyServer;
import org.objectweb.proactive.ext.security.ProActiveSecurityManager;


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

    public RuntimeRequest(String methodName, ArrayList parameters,
        ArrayList paramsTypes, UniqueID oaid) {
        this.methodName = methodName;
        this.parameters = parameters;
        this.paramsTypes = paramsTypes;
        this.oaid = oaid;
    }

    public RuntimeReply process() throws ProActiveException {
        try {
            Object[] params = parameters.toArray();

                        Class[] classes = new Class[parameters.size()];
            
                       if (this.paramsTypes == null) {
                            //Remplissage du tableau des types:
                            for (int i = 0; i < parameters.size(); i++) {
                              classes[i] = parameters.get(i).getClass();
                            }
                        } else {
                        	Object[] tab = paramsTypes.toArray();
                            for (int i = 0; i < tab.length; i++) {
                               classes[i] = (Class) tab[i];
                           }
                        }
            Object result = null;

            if (this.oaid == null) {
                ProActiveRuntimeImpl runtime = (ProActiveRuntimeImpl) ProActiveRuntimeImpl.getProActiveRuntime();
                if (methodName.equals("createLocalNode")) {
                    result = runtime.createLocalNode((String) parameters.get(0),
                            ((Boolean) parameters.get(1)).booleanValue(),
                            (PolicyServer) parameters.get(2),
                            (String) parameters.get(3),
                            (String) parameters.get(4));
                } else if (methodName.equals("killAllNodes")) {
                	runtime.killAllNodes();
                } else if (methodName.equals("killNode")) {
                	runtime.killNode((String)parameters.get(0));
                } else if (methodName.equals("createVM")) {
                	runtime.createVM((UniversalProcess)parameters.get(0));
                } else if (methodName.equals("getLocalNodeNames")) {
                	result = runtime.getLocalNodeNames();
                } else if (methodName.equals("getVMInformation")) {
                	result = runtime.getVMInformation();
                } else if (methodName.equals("register")) {
                	result = runtime.getVNName((String)parameters.get(0));
                } else if (methodName.equals("getProActiveRuntime")) {
                	result = ProActiveRuntimeImpl.getProActiveRuntime();
                } else if(methodName.equals("getProActiveRuntimes")) {
                	result = runtime.getProActiveRuntimes();
                } else if (methodName.equals("killRT")) {
                	runtime.killRT(((Boolean)parameters.get(0)).booleanValue());
                } else if (methodName.equals("getActiveObjects")) {
                	if (parameters.size() == 1)
                		result = runtime.getActiveObjects((String)parameters.get(0));
                	else
                		result = runtime.getActiveObjects((String)parameters.get(0),(String)parameters.get(1));
                } else if (methodName.equals("getVirtualNode")) {
                		result = runtime.getVirtualNode((String)parameters.get(0));
                } else if (methodName.equals("registerVirtualNode")) {
                		runtime.registerVirtualNode((String)parameters.get(0), ((Boolean)parameters.get(1)).booleanValue());                			
                } else if (methodName.equals("unregisterVirtualNode")) {
                	runtime.unregisterVirtualNode((String)parameters.get(0));
                } else if (methodName.equals("unregisterAllVirtualNodes")) {
                	runtime.unregisterAllVirtualNodes();
					
                } else if (methodName.equals("createBody")) {
                	result = runtime.createBody((String)parameters.get(0), (ConstructorCall)parameters.get(1), ((Boolean)parameters.get(2)).booleanValue());
                } else if (methodName.equals("receiveBody")) {
                	result = runtime.receiveBody((String)parameters.get(0), (Body)parameters.get(1));
                } else if (methodName.equals("getPolicyServer")) {
                	result = runtime.getPolicyServer();
                } else if (methodName.equals("setProActiveSecurityManager")) {
                	 runtime.setProActiveSecurityManager((ProActiveSecurityManager)parameters.get(0));
                } else if (methodName.equals("getCreatorCertificate")) {
                	result = runtime.getCreatorCertificate();
                } else if (methodName.equals("getVNName")) {
                	result = runtime.getVNName((String)parameters.get(0));
                } else if (methodName.equals("setDefaultNodeVirtualNodeName")) {
                	runtime.setDefaultNodeVirtualNodeName((String)parameters.get(0));
                } else if (methodName.equals("getNodePolicyServer")) {
                	result = runtime.getNodePolicyServer((String)parameters.get(0)); 
                } else if (methodName.equals("enableSecurityIfNeeded")) {
                	runtime.enableSecurityIfNeeded();
                } else if (methodName.equals("getNodeCertificate")) {
                	result = runtime.getNodeCertificate((String)parameters.get(0));
                } else if (methodName.equals("getEntities")) {
                	if (parameters.size() == 0)
                		result = runtime.getEntities();
                	else if (parameters.size() == 1 && parameters.get(0) instanceof String)
                		result = runtime.getEntities((String)parameters.get(0));
                	else if (parameters.size() == 2 && parameters.get(0) instanceof String)
                		result = runtime.getEntities((UniversalBody)parameters.get(0));
                } else if (methodName.equals("getJobID")) {
                	if (parameters.size() == 0)
                		result = runtime.getJobID();
                	else result = runtime.getJobID((String)parameters.get(0));
                } 
                    //Recuperation de la Class Runtime
                    if (logger.isDebugEnabled()) {
                        logger.debug("-- > invoquation de la methode " +
                            methodName + " sur le runtime :  " +
                            (ProActiveRuntimeImpl) ProActiveRuntimeImpl.getProActiveRuntime());
                    }
                

//                result = runtime.getClass().getMethod(methodName, classes)
//                                .invoke(runtime, params);
            } else {
                //Recuperation du body sur lequel on va faire appel
                Body body = ProActiveXMLUtils.getBody(this.oaid);
                if (logger.isDebugEnabled()) {
                    logger.debug("Invocation de " + methodName + " sur " +
                        body);
                }
                result = body.getClass().getMethod(methodName, classes).invoke(body,
                        parameters.toArray());
            }
            return new RuntimeReply(result);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConstructorCallExecutionFailedException e) {
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
