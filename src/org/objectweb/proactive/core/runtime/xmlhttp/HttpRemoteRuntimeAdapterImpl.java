/*
 * Created on 26 juin 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.objectweb.proactive.core.runtime.xmlhttp;

import org.apache.log4j.Logger;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.mop.ConstructorCall;
import org.objectweb.proactive.core.mop.ConstructorCallExecutionFailedException;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.process.UniversalProcess;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.runtime.VMInformation;
import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.ext.security.PolicyServer;
import org.objectweb.proactive.ext.security.ProActiveSecurityManager;
import org.objectweb.proactive.ext.security.SecurityContext;
import org.objectweb.proactive.ext.security.exceptions.SecurityNotAvailableException;
import org.objectweb.proactive.ext.webservices.utils.HTTPRemoteException;
import org.objectweb.proactive.ext.webservices.utils.ProActiveXMLUtils;

import java.io.IOException;

import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;

import java.security.cert.X509Certificate;

import java.util.ArrayList;


public class HttpRemoteRuntimeAdapterImpl implements HttpRuntimeStrategyAdapter {
    private static transient Logger logger = Logger.getLogger("XML_HTTP");
    private HttpRuntimeAdapter runtimeadapter;

    //private VMInformation vmInformation;

    /**
     *
     * @param url
     */
    public HttpRemoteRuntimeAdapterImpl(HttpRuntimeAdapter newruntimeadapter,
        String newurl) {

     	logger.debug("URL de l'adapter = " + newurl);
    	runtimeadapter = newruntimeadapter;
    	runtimeadapter.url = newurl;
    	createURL();
    	logger.debug("New Remote XML Adapter : " + runtimeadapter.url +
                " port = " + runtimeadapter.port);
    }
    
    public void createURL() {	
    
    	/* !!! */
    	if (!runtimeadapter.url.startsWith("http:")) {
    	  	runtimeadapter.url = "http:" + runtimeadapter.url;
        }
        if (runtimeadapter.port == 0) {
        	runtimeadapter.port = UrlBuilder.getPortFromUrl(runtimeadapter.url);
        }
        try {
			runtimeadapter.url = "http://"+UrlBuilder.getHostNameAndPortFromUrl(runtimeadapter.url);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        runtimeadapter.vmInformation = runtimeadapter.vmInformation;
    
    }

  
    //
    // -- Implements ProActiveRuntime -----------------------------------------------
    //
    public String createLocalNode(String nodeName,
        boolean replacePreviousBinding, PolicyServer ps, String vname,
        String jobId) throws NodeException {
        try {
            String methodName = "createLocalNode";

            // first we  v a well-formed url
            String nodeURL = null;

            nodeURL = runtimeadapter.buildNodeURL(nodeName);

            // then take the name of the node
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
        } catch (NodeException e) {
            throw e;
        } catch (Exception e) {
            throw new NodeException(e);
        }
    }

    /**
     *
     * @param req
     * @return
     * @throws ProActiveException
     */
    private Object sendRequest(RuntimeRequest req) throws Exception {
        logger.debug("Send request to : " + runtimeadapter.url + ":" +
            runtimeadapter.port);

        if (req.getMethodName() == null) {
            throw new ProActiveException("Null request");
        }

        RuntimeReply reply = (RuntimeReply) ProActiveXMLUtils.sendMessage(runtimeadapter.url,
                runtimeadapter.port, req, ProActiveXMLUtils.RUNTIME_REQUEST);

        if (reply != null) {
            return reply.getReturnedObject();
        }

        return null;
    }

    public void killAllNodes() throws ProActiveException {
        try {
            Object o = sendRequest(new RuntimeRequest("killAllNodes"));
        } catch (Exception re) {
            throw new ProActiveException(re);

            // behavior to be defined
        }
    }

    public void killNode(String nodeName) throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(nodeName);

        try {
            Object o = sendRequest(new RuntimeRequest("killNode", params));
        } catch (Exception re) {
            throw new ProActiveException(re);

            // behavior to be defined
        }
    }

    public void createVM(UniversalProcess remoteProcess)
        throws IOException, ProActiveException {
        ArrayList params = new ArrayList();
        params.add(remoteProcess);

        try {
            Object o = sendRequest(new RuntimeRequest("createVM", params));
        } catch (Exception re) {
            throw new ProActiveException(re);

            // behavior to be defined
        }
    }

    public String[] getLocalNodeNames() throws ProActiveException {
        try {
            return (String[]) sendRequest(new RuntimeRequest(
                    "getLocalNodeNames"));
        } catch (Exception re) {
            throw new ProActiveException(re);

            // behavior to be defined
        }
    }

    public VMInformation getVMInformation() {
        //return vmInformation;
        if (runtimeadapter.vmInformation == null) {
            try {
                return (VMInformation) sendRequest(new RuntimeRequest(
                        "getVMInformation"));
            } catch (Exception re) {
                //throw new ProActiveException(re);
                re.printStackTrace();

                // behavior to be defined
            }
        }

        //return runtimeadapter.getVMInformation();
        return null;
    }

    public void register(ProActiveRuntime proActiveRuntimeDist,
        String proActiveRuntimeName, String creatorID, String creationProtocol,
        String vmName) {
        try {
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

            RuntimeRequest req = new RuntimeRequest("register", params,
                    paramsTypes);

            Object o = sendRequest(req);

            //runtimeadapter.register(proActiveRuntimeDist, proActiveRuntimeName, creatorID, creationProtocol, vmName);
        } catch (Exception e) {
            e.printStackTrace();

            // behavior to be defined
        }
    }

    public ProActiveRuntime[] getProActiveRuntimes() throws ProActiveException {
        try {
            return (ProActiveRuntime[]) sendRequest(new RuntimeRequest(
                    "getProActiveRuntime"));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    public ProActiveRuntime getProActiveRuntime(String proActiveRuntimeName)
        throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(proActiveRuntimeName);

        try {
            return (ProActiveRuntime) sendRequest(new RuntimeRequest(
                    "getProActiveRuntime", params));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    public void killRT(boolean softly) throws Exception {
        if (!runtimeadapter.alreadykilled) {
            ArrayList params = new ArrayList();
            params.add(new Boolean(softly));

            try {
                sendRequest(new RuntimeRequest("killRT", params));
            } catch (HTTPRemoteException e) {
            	// do nothing (results from distant System.exit(0))
            } catch (Exception e) {
                throw new ProActiveException(e);
            }
        }

        runtimeadapter.alreadykilled = true;
    }

    public String getURL() throws ProActiveException {
    	return runtimeadapter.getStrategyURL();
    }

    public ArrayList getActiveObjects(String nodeName)
        throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(nodeName);

        try {
            return (ArrayList) sendRequest(new RuntimeRequest(
                    "getActiveObjects", params));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    public ArrayList getActiveObjects(String nodeName, String objectName)
        throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(nodeName);
        params.add(objectName);

        try {
            return (ArrayList) sendRequest(new RuntimeRequest(
                    "getActiveObjects", params));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    public VirtualNode getVirtualNode(String virtualNodeName)
        throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(virtualNodeName);

        try {
            return (VirtualNode) sendRequest(new RuntimeRequest(
                    "getVirtualNode", params));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    public void registerVirtualNode(String virtualNodeName,
        boolean replacePreviousBinding) throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(virtualNodeName);
        params.add(new Boolean(replacePreviousBinding));

        try {
            sendRequest(new RuntimeRequest("registerVirtualNode", params));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    public void unregisterVirtualNode(String virtualNodeName)
        throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(virtualNodeName);

        try {
            sendRequest(new RuntimeRequest("unregisterVirtualNode", params));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    public void unregisterAllVirtualNodes() throws ProActiveException {
        try {
            sendRequest(new RuntimeRequest("unregisterAllVirtualNodes"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public UniversalBody createBody(String nodeName,
        ConstructorCall bodyConstructorCall, boolean isNodeLocal)
        throws ProActiveException, ConstructorCallExecutionFailedException, 
            InvocationTargetException {
        ArrayList params = new ArrayList();
        params.add(nodeName);
        params.add(bodyConstructorCall);
        params.add(new Boolean(isNodeLocal));

        try {
            return (UniversalBody) sendRequest(new RuntimeRequest(
                    "createBody", params));
        } catch (ConstructorCallExecutionFailedException e) {
            throw e;
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    public UniversalBody receiveBody(String nodeName, Body body)
        throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(nodeName);
        params.add(body);

        try {
            return (UniversalBody) sendRequest(new RuntimeRequest(
                    "receiveBody", params));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    // SECURITY 
    public PolicyServer getPolicyServer() throws ProActiveException {
        try {
            return (PolicyServer) sendRequest(new RuntimeRequest(
                    "getPolicyServer"));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    public void setProActiveSecurityManager(ProActiveSecurityManager ps)
        throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(ps);

        try {
            sendRequest(new RuntimeRequest("setProActiveSecurityManager", params));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getCreatorCertificate()
     */
    public X509Certificate getCreatorCertificate() throws ProActiveException {
        try {
            return (X509Certificate) sendRequest(new RuntimeRequest(
                    "getCreatorCertificate"));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    public String getVNName(String nodename) throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(nodename);

        try {
            return (String) sendRequest(new RuntimeRequest("getVNName", params));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#setDefaultNodeVirtualNodeName(java.lang.String)
     */
    public void setDefaultNodeVirtualNodeName(String s)
        throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(s);

        try {
            sendRequest(new RuntimeRequest("setDefaultNodeVirtualNodeName",
                    params));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getNodePolicyServer(java.lang.String)
     */
    public PolicyServer getNodePolicyServer(String nodeName)
        throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(nodeName);

        try {
            return (PolicyServer) sendRequest(new RuntimeRequest(
                    "getNodePolicyServer", params));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#enableSecurityIfNeeded()
     */
    public void enableSecurityIfNeeded() throws ProActiveException {
        try {
            sendRequest(new RuntimeRequest("enableSecurityIfNeeded"));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getNodeCertificate(java.lang.String)
     */
    public X509Certificate getNodeCertificate(String nodeName)
        throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(nodeName);

        try {
            return (X509Certificate) sendRequest(new RuntimeRequest(
                    "getNodeCertificate", params));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    /**
     * @param nodeName
     * @return returns all entities associated to the node
     */
    public ArrayList getEntities(String nodeName) throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(nodeName);

        try {
            return (ArrayList) sendRequest(new RuntimeRequest("getEntities",
                    params));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    /**
     * @param nodeName
     * @return returns all entities associated to the node
     */
    public ArrayList getEntities(UniversalBody uBody) throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(uBody);

        try {
            return (ArrayList) sendRequest(new RuntimeRequest("getEntities",
                    params));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    /**
     * @return returns all entities associated to this runtime
     */
    public ArrayList getEntities() throws ProActiveException {
        try {
            return (ArrayList) sendRequest(new RuntimeRequest("getEntities"));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    /**
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getJobID(java.lang.String)
     */
    public String getJobID(String nodeUrl) throws ProActiveException {
        ArrayList params = new ArrayList();
        params.add(nodeUrl);

        try {
            return (String) sendRequest(new RuntimeRequest("getJobID", params));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    public String[] getNodesNames() throws ProActiveException {
        try {
            return (String[]) sendRequest(new RuntimeRequest("getNodesNames"));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    /**
     * @see org.objectweb.proactive.Job#getJobID()
     */
    public String getJobID() {
        String methodName = "getJobID";
        RuntimeRequest req = new RuntimeRequest(methodName);
        Object o = null;

        try {
            o = sendRequest(req);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (String) o;
    }

    ///////////////
    public void addParent(String proActiveRuntimeName) {
        ArrayList params = new ArrayList();
        params.add(proActiveRuntimeName);

        try {
            sendRequest(new RuntimeRequest("addParent", params));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getParents() {
        try {
            return (String[]) sendRequest(new RuntimeRequest("getParents"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SecurityContext getPolicy(SecurityContext sc)
        throws ProActiveException, SecurityNotAvailableException {
        ArrayList params = new ArrayList();
        params.add(sc);

        try {
            return (SecurityContext) sendRequest(new RuntimeRequest(
                    "getPolicy", params));
        } catch (SecurityNotAvailableException e) {
            throw e;
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }

    public void listVirtualNodes() throws ProActiveException {
        try {
            //  remoteProActiveRuntime.updateLocalNodeVirtualName();
            sendRequest(new RuntimeRequest("listVirtualNodes"));
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }
}
