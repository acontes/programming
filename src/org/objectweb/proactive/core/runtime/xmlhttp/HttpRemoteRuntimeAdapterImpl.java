/*
 * Created on 26 juin 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.objectweb.proactive.core.runtime.xmlhttp;

import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.mop.ConstructorCall;
import org.objectweb.proactive.core.process.UniversalProcess;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.runtime.VMInformation;
import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.ext.security.PolicyServer;
import org.objectweb.proactive.ext.security.ProActiveSecurityManager;
import org.objectweb.proactive.ext.security.SecurityContext;
import org.objectweb.proactive.ext.security.exceptions.SecurityNotAvailableException;
import org.objectweb.proactive.ext.webservices.utils.ProActiveXMLUtils;

public class HttpRemoteRuntimeAdapterImpl implements HttpRuntimeStrategyAdapter  {
  

	private HttpRuntimeAdapter runtimeadapter;
	
	private static transient Logger logger = Logger.getLogger("XML_HTTP");
	

    /**
     *
     * @param url
     */
    public HttpRemoteRuntimeAdapterImpl(HttpRuntimeAdapter newruntimeadapter,String url) {
        logger.debug("URL de l'adapter = " + url);

        runtimeadapter = newruntimeadapter;
        
        // this.remoteProActiveRuntime = createRemoteProActiveRuntime();
        runtimeadapter.url = url;

        if (!runtimeadapter.url.startsWith("http:")) {
        	runtimeadapter.url = "http:" + runtimeadapter.url;
        }

        int index = runtimeadapter.url.lastIndexOf(':');

        if (index > 4) {
        	runtimeadapter.port = Integer.parseInt(runtimeadapter.url.substring(index + 1, index +
                        5));
        	runtimeadapter.url = runtimeadapter.url.substring(0, index);
        }

        index = runtimeadapter.url.lastIndexOf('/');

        if (index > 6) {
        	runtimeadapter.url = runtimeadapter.url.substring(0, index);
        }
        logger.debug("New Remote XML Adapter : " + runtimeadapter.url + " port = " + runtimeadapter.port);
    }

    //
    // -- Implements ProActiveRuntime -----------------------------------------------
    //
    public String createLocalNode(String nodeName,
        boolean replacePreviousBinding, PolicyServer ps, String vname,
        String jobId) {
        try {
            String methodName = "createLocalNode";

            //          first we build a well-formed url
            String nodeURL = null;
            try {
                nodeURL = runtimeadapter.buildNodeURL(nodeName);
            } catch (UnknownHostException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            //then take the name of the node
            String name = UrlBuilder.getNameFromUrl(nodeURL);

            ArrayList paramsList = new ArrayList();

            paramsList.add(name);
            paramsList.add(new Boolean(replacePreviousBinding));
            paramsList.add(ps);
            paramsList.add(vname);
            paramsList.add(jobId);

            RuntimeRequest req = new RuntimeRequest(methodName, paramsList);

            Object result = sendRequest(req);

            return nodeURL;
        } catch (ProActiveException e) {
            e.printStackTrace();
        }

        //      }
        return null;
    }

  

    /**
     *
     * @param req
     * @return
     * @throws ProActiveException
     */
    private Object sendRequest(RuntimeRequest req) throws ProActiveException {
        logger.debug("Send request to : " + runtimeadapter.url + ":" + runtimeadapter.port);
        if (req.getMethodName() == null) {
            throw new ProActiveException("Requete nulle");
        }


        RuntimeReply reply = (RuntimeReply) ProActiveXMLUtils.sendMessage(runtimeadapter.url,
        		runtimeadapter.port, req, ProActiveXMLUtils.RUNTIME_REQUEST);

        if (reply != null) {
            return reply.getReturnedObject();
        }

        return null;
    }

    public void killAllNodes() throws ProActiveException {
    
        String methodName = "killAllNodes";
        RuntimeRequest req = new RuntimeRequest(methodName);
        Object o = sendRequest(req);

        return;
    }

    public void killNode(String nodeName) throws ProActiveException {
      
        String methodName = "killNode";
        ArrayList params = new ArrayList();
        params.add(nodeName);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        Object o = sendRequest(req);
    }

    public void createVM(UniversalProcess remoteProcess)
        throws ProActiveException {
     
        String methodName = "createVM";
        ArrayList params = new ArrayList();
        params.add(remoteProcess);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        Object o = sendRequest(req);
    }

    public String[] getLocalNodeNames() throws ProActiveException {
       
        String methodName = "getLocalNodeNames";
        RuntimeRequest req = new RuntimeRequest(methodName);
        Object o = sendRequest(req);

        return (String[]) o;
    }

    public VMInformation getVMInformation() {
        if (runtimeadapter.vmInformation == null ) {
        	String methodName = "getVMInformation";
        RuntimeRequest req = new RuntimeRequest(methodName);
        Object o = null;
        try {
            o = sendRequest(req);
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
        runtimeadapter.vmInformation =  (VMInformation) o;
        }
        return runtimeadapter.vmInformation;
    }

    public void register(ProActiveRuntime proActiveRuntimeDist,
        String proActiveRuntimeName, String creatorID, String creationProtocol,
        String vmName) {
    	
        try {
            String methodName = "register";
            ArrayList params = new ArrayList();
            ArrayList paramsTypes = new ArrayList();

            params.add(proActiveRuntimeDist);
            paramsTypes.add(ProActiveRuntime.class);
            params.add(proActiveRuntimeName);
            paramsTypes.add(String.class);
            params.add(creatorID);
            paramsTypes.add(String.class);
            params.add(creationProtocol);
            paramsTypes.add(String.class);
            params.add(vmName);
            paramsTypes.add(String.class);

            RuntimeRequest req = new RuntimeRequest(methodName, params,
                    paramsTypes);

            Object o = sendRequest(req);

            //this.remoteProActiveRuntime.register(proActiveRuntimeDist, proActiveRuntimeName, creatorID, creationProtocol, vmName);
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
    }

    public ProActiveRuntime[] getProActiveRuntimes() throws ProActiveException {
        
        String methodName = "getProActiveRuntime";
        RuntimeRequest req = new RuntimeRequest(methodName);
        Object o = sendRequest(req);

        return (ProActiveRuntime[]) o;
    }

    public ProActiveRuntime getProActiveRuntime(String proActiveRuntimeName)
        throws ProActiveException {
      
        String methodName = "getProActiveRuntime";
        ArrayList params = new ArrayList();
        params.add(proActiveRuntimeName);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        Object o = sendRequest(req);

        return (ProActiveRuntime) o;
    }

    public void killRT(boolean softly) throws ProActiveException {
       
        if (!runtimeadapter.alreadykilled) {
            String methodName = "killRT";
            ArrayList params = new ArrayList();
            params.add(new Boolean(softly));

            RuntimeRequest req = new RuntimeRequest(methodName, params);
            sendRequest(req);
        }

        runtimeadapter.alreadykilled = true;
    }

    public String getURL() throws ProActiveException {
        return runtimeadapter.url + ":" + runtimeadapter.port + "/" + getVMInformation().getName() + "/";
    }

    public ArrayList getActiveObjects(String nodeName)
        throws ProActiveException {
      
        String methodName = "getActiveObjects";
        ArrayList params = new ArrayList();
        params.add(nodeName);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        Object o = sendRequest(req);

        return (ArrayList) o;
    }

    public ArrayList getActiveObjects(String nodeName, String objectName)
        throws ProActiveException {
     
        String methodName = "getActiveObjects";
        ArrayList params = new ArrayList();
        params.add(nodeName);
        params.add(objectName);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        Object o = sendRequest(req);

        return (ArrayList) o;
    }

    public VirtualNode getVirtualNode(String virtualNodeName)
        throws ProActiveException {
    
        String methodName = "getVirtualNode";
        ArrayList params = new ArrayList();
        params.add(virtualNodeName);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        Object o = sendRequest(req);

        return (VirtualNode) o;
    }

    public void registerVirtualNode(String virtualNodeName,
        boolean replacePreviousBinding) throws ProActiveException {
       
        String methodName = "registerVirtualNode";
        ArrayList params = new ArrayList();
        params.add(virtualNodeName);
        params.add(new Boolean(replacePreviousBinding));
        RuntimeRequest req = new RuntimeRequest(methodName, params);
        Object o = sendRequest(req);
    }

    public void unregisterVirtualNode(String virtualNodeName)
        throws ProActiveException {
     
        String methodName = "unregisterVirtualNode";
        ArrayList params = new ArrayList();
        params.add(virtualNodeName);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        sendRequest(req);
    }

    public void unregisterAllVirtualNodes() throws ProActiveException {
      
        String methodName = "unregisterAllVirtualNodes";
        RuntimeRequest req = new RuntimeRequest(methodName);
        sendRequest(req);
    }

    public UniversalBody createBody(String nodeName,
        ConstructorCall bodyConstructorCall, boolean isNodeLocal)
        throws ProActiveException {
        String methodName = "createBody";
        ArrayList params = new ArrayList();
        params.add(nodeName);
        params.add(bodyConstructorCall);
        params.add(new Boolean(isNodeLocal));

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        Object o = sendRequest(req);

        return (UniversalBody) o;
    }

    public UniversalBody receiveBody(String nodeName, Body body)
        throws ProActiveException {
        
        String methodName = "receiveBody";
        ArrayList params = new ArrayList();
        params.add(nodeName);
        params.add(body);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        Object o = sendRequest(req);

        return (UniversalBody) o;
    }

    // SECURITY 
    public PolicyServer getPolicyServer() throws ProActiveException {
       
        String methodName = "getPolicyServer";
        RuntimeRequest req = new RuntimeRequest(methodName);
        Object o = sendRequest(req);

        return (PolicyServer) o;
    }

    public void setProActiveSecurityManager(ProActiveSecurityManager ps)
        throws ProActiveException {
      
        String methodName = "setProActiveSecurityManager";
        ArrayList params = new ArrayList();
        params.add(ps);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        sendRequest(req);
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getCreatorCertificate()
     */
    public X509Certificate getCreatorCertificate() throws ProActiveException {
     
        String methodName = "getCreatorCertificate";
        RuntimeRequest req = new RuntimeRequest(methodName);
        Object o = sendRequest(req);

        return (X509Certificate) o;
    }

    public String getVNName(String nodename) throws ProActiveException {
     
        String methodName = "getVNName";
        ArrayList params = new ArrayList();
        params.add(nodename);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        Object o = sendRequest(req);

        return (String) o;
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#setDefaultNodeVirtualNodeName(java.lang.String)
     */
    public void setDefaultNodeVirtualNodeName(String s)
        throws ProActiveException {
    
        String methodName = "setDefaultNodeVirtualNodeName";
        ArrayList params = new ArrayList();
        params.add(s);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        sendRequest(req);
    }

    
    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getNodePolicyServer(java.lang.String)
     */
    public PolicyServer getNodePolicyServer(String nodeName)
        throws ProActiveException {
    
        String methodName = "getNodePolicyServer";
        ArrayList params = new ArrayList();
        params.add(nodeName);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        Object o = sendRequest(req);

        return (PolicyServer) o;
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#enableSecurityIfNeeded()
     */
    public void enableSecurityIfNeeded() throws ProActiveException {
      
        String methodName = "enableSecurityIfNeeded";
        RuntimeRequest req = new RuntimeRequest(methodName);
        sendRequest(req);
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getNodeCertificate(java.lang.String)
     */
    public X509Certificate getNodeCertificate(String nodeName)
        throws ProActiveException {
     
        String methodName = "getNodeCertificate";
        ArrayList params = new ArrayList();
        params.add(nodeName);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        Object o = sendRequest(req);

        return (X509Certificate) o;
    }

    /**
     * @param nodeName
     * @return returns all entities associated to the node
     */
    public ArrayList getEntities(String nodeName) throws ProActiveException {
    
        String methodName = "getEntities";
        ArrayList params = new ArrayList();
        params.add(nodeName);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        Object o = sendRequest(req);

        return (ArrayList) o;
    }

    /**
     * @param nodeName
     * @return returns all entities associated to the node
     */
    public ArrayList getEntities(UniversalBody uBody) throws ProActiveException {
    
        String methodName = "getEntities";
        ArrayList params = new ArrayList();
        params.add(uBody);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        Object o = sendRequest(req);

        return (ArrayList) o;
    }

    /**
     * @return returns all entities associated to this runtime
     */
    public ArrayList getEntities() throws ProActiveException {
     
        String methodName = "getEntities";
        RuntimeRequest req = new RuntimeRequest(methodName);
        Object o = sendRequest(req);

        return (ArrayList) o;
    }


    /**
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getJobID(java.lang.String)
     */
    public String getJobID(String nodeUrl) throws ProActiveException {
       
        String methodName = "getJobID";
        ArrayList params = new ArrayList();
        params.add(nodeUrl);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        Object o = sendRequest(req);

        return (String) o;
    }

    
    public String [] getNodesNames() throws ProActiveException {
    	
    	String methodName = "getNodesNames";
        RuntimeRequest req = new RuntimeRequest(methodName);
        Object o = sendRequest(req);

        return (String[]) o;
    	
    }
    
    /**
     * @see org.objectweb.proactive.Job#getJobID()
     */
    public String getJobID() {
    	String methodName = "getJobID";
        RuntimeRequest req = new RuntimeRequest(methodName);
        Object o=null;
		try {
			o = sendRequest(req);
		} catch (ProActiveException e) {
			e.printStackTrace();
		}
		return (String) o;
    }
  
   ///////////////
    public void addParent(String proActiveRuntimeName) {
    	
    	String methodName = "addParent";
        ArrayList params = new ArrayList();
        params.add(proActiveRuntimeName);

        RuntimeRequest req = new RuntimeRequest(methodName, params);
        try {
			sendRequest(req);
		} catch (ProActiveException e) {
			e.printStackTrace();
		}
      
    }
    
    public String[] getParents() {
    	
    	String methodName = "getParents";
        RuntimeRequest req = new RuntimeRequest(methodName);
        Object o=null;
		try {
			o = sendRequest(req);
		} catch (ProActiveException e) {
			e.printStackTrace();
		}
		return (String[]) o;

    }
 
    public SecurityContext getPolicy(SecurityContext sc)
        throws ProActiveException, SecurityNotAvailableException {
    	   
    	String methodName = "getPolicy";
    		ArrayList params = new ArrayList();
           params.add(sc);

           RuntimeRequest req = new RuntimeRequest(methodName, params);
           Object o = sendRequest(req);

           return (SecurityContext) o;
    }

    public void listVirtualNodes() throws ProActiveException {
    	//  remoteProActiveRuntime.updateLocalNodeVirtualName();
        String methodName = "listVirtualNodes";
        RuntimeRequest req = new RuntimeRequest(methodName);
        sendRequest(req);
    }
    
    
    
    
    
    
}

	
	
 
