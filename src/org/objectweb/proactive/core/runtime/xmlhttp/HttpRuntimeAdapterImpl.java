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
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.runtime.VMInformation;
import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.ext.security.PolicyServer;
import org.objectweb.proactive.ext.security.ProActiveSecurityManager;
import org.objectweb.proactive.ext.security.SecurityContext;
import org.objectweb.proactive.ext.security.exceptions.SecurityNotAvailableException;

import java.io.IOException;

import java.lang.reflect.InvocationTargetException;

import java.net.UnknownHostException;

import java.security.cert.X509Certificate;

import java.util.ArrayList;


public class HttpRuntimeAdapterImpl implements HttpRuntimeStrategyAdapter {
    private static transient Logger logger = Logger.getLogger("XML_HTTP");
    private HttpRuntimeAdapter runtimeadapter;
    ProActiveRuntime remoteProActiveRuntime;

    public HttpRuntimeAdapterImpl(HttpRuntimeAdapter newruntimeadapter) {
        runtimeadapter = newruntimeadapter;
        remoteProActiveRuntime = ProActiveRuntimeImpl.getProActiveRuntime();

        String host = getVMInformation().getInetAddress().getCanonicalHostName();
        
        //runtimeadapter.url = "http://"+host+":"+runtimeadapter.port;
        runtimeadapter.url = UrlBuilder.buildUrl(host,"","http:",runtimeadapter.port);
        
        logger.debug("url adapter = " + runtimeadapter.url);
    }

    //
    // -- Implements ProActiveRuntime -----------------------------------------------
    //
    public String createLocalNode(String nodeName,
        boolean replacePreviousBinding, PolicyServer ps, String vname,
        String jobId) {
        try {
            String nodeURL = null;

            try {
                nodeURL = runtimeadapter.buildNodeURL(nodeName);
            } catch (UnknownHostException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            //      then take the name of the node
            String name = UrlBuilder.getNameFromUrl(nodeURL);
            remoteProActiveRuntime.createLocalNode(name,
                replacePreviousBinding, ps, vname, jobId); 
 
            return nodeURL;
        } catch (NodeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void killAllNodes() throws ProActiveException {
        remoteProActiveRuntime.killAllNodes();
    }

    public void killNode(String nodeName) throws ProActiveException {
        remoteProActiveRuntime.killNode(nodeName);
    }

    public void createVM(UniversalProcess remoteProcess)
        throws ProActiveException {
        try {
            remoteProActiveRuntime.createVM(remoteProcess);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String[] getLocalNodeNames() throws ProActiveException {
        return remoteProActiveRuntime.getLocalNodeNames();
    }

    public VMInformation getVMInformation() {
        return remoteProActiveRuntime.getVMInformation();
    }

    public void register(ProActiveRuntime proActiveRuntimeDist,
        String proActiveRuntimeName, String creatorID, String creationProtocol,
        String vmName) {
        remoteProActiveRuntime.register(proActiveRuntimeDist,
            proActiveRuntimeName, creatorID, creationProtocol, vmName);
    }

    public ProActiveRuntime[] getProActiveRuntimes() throws ProActiveException {
        return remoteProActiveRuntime.getProActiveRuntimes();
    }

    public ProActiveRuntime getProActiveRuntime(String proActiveRuntimeName)
        throws ProActiveException {
        return remoteProActiveRuntime.getProActiveRuntime(proActiveRuntimeName);
    }

    public void killRT(boolean softly) throws ProActiveException {
        try {
            remoteProActiveRuntime.killRT(softly);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new ProActiveException(e);
        }
    }

    public String getURL() throws ProActiveException {
    	return runtimeadapter.getStrategyURL();
    }

    public ArrayList getActiveObjects(String nodeName)
        throws ProActiveException {
        return remoteProActiveRuntime.getActiveObjects(nodeName);
    }

    public ArrayList getActiveObjects(String nodeName, String objectName)
        throws ProActiveException {
        return remoteProActiveRuntime.getActiveObjects(nodeName, objectName);
    }

    public VirtualNode getVirtualNode(String virtualNodeName)
        throws ProActiveException {
        return remoteProActiveRuntime.getVirtualNode(virtualNodeName);
    }

    public void registerVirtualNode(String virtualNodeName,
        boolean replacePreviousBinding) throws ProActiveException {
        remoteProActiveRuntime.registerVirtualNode(virtualNodeName,
            replacePreviousBinding);
    }

    public void unregisterVirtualNode(String virtualNodeName)
        throws ProActiveException {
        remoteProActiveRuntime.unregisterVirtualNode(virtualNodeName);
    }

    public void unregisterAllVirtualNodes() throws ProActiveException {
        remoteProActiveRuntime.unregisterAllVirtualNodes();
    }

    public UniversalBody createBody(String nodeName,
        ConstructorCall bodyConstructorCall, boolean isNodeLocal)
        throws ProActiveException {
        try {
            return remoteProActiveRuntime.createBody(nodeName,
                bodyConstructorCall, isNodeLocal);
        } catch (ConstructorCallExecutionFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public UniversalBody receiveBody(String nodeName, Body body)
        throws ProActiveException {
        return remoteProActiveRuntime.receiveBody(nodeName, body);
    }

    // SECURITY 
    public PolicyServer getPolicyServer() throws ProActiveException {
        return remoteProActiveRuntime.getPolicyServer();
    }

    public void setProActiveSecurityManager(ProActiveSecurityManager ps)
        throws ProActiveException {
        remoteProActiveRuntime.setProActiveSecurityManager(ps);
    }

    public X509Certificate getCreatorCertificate() throws ProActiveException {
        return remoteProActiveRuntime.getCreatorCertificate();
    }

    public String getVNName(String nodename) throws ProActiveException {
        return remoteProActiveRuntime.getVNName(nodename);
    }

    public void setDefaultNodeVirtualNodeName(String s)
        throws ProActiveException {
        remoteProActiveRuntime.setDefaultNodeVirtualNodeName(s);
    }

    public PolicyServer getNodePolicyServer(String nodeName)
        throws ProActiveException {
        return remoteProActiveRuntime.getNodePolicyServer(nodeName);
    }

    public void enableSecurityIfNeeded() throws ProActiveException {
        remoteProActiveRuntime.enableSecurityIfNeeded();
    }

    public X509Certificate getNodeCertificate(String nodeName)
        throws ProActiveException {
        return remoteProActiveRuntime.getNodeCertificate(nodeName);
    }

    public ArrayList getEntities(String nodeName) throws ProActiveException {
        return remoteProActiveRuntime.getEntities(nodeName);
    }

    public ArrayList getEntities(UniversalBody uBody) throws ProActiveException {
        return remoteProActiveRuntime.getEntities(uBody);
    }

    public ArrayList getEntities() throws ProActiveException {
        return remoteProActiveRuntime.getEntities();
    }

    public String getJobID() {
        return remoteProActiveRuntime.getJobID();
    }

    public String getJobID(String nodeUrl) throws ProActiveException {
        return remoteProActiveRuntime.getJobID(nodeUrl);
    }

    public String[] getNodesNames() throws ProActiveException {
        return remoteProActiveRuntime.getLocalNodeNames();
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#addParent(java.lang.String)
     */
    public void addParent(String proActiveRuntimeName) {
        remoteProActiveRuntime.addParent(proActiveRuntimeName);
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getParents()
     */
    public String[] getParents() {
        return this.remoteProActiveRuntime.getParents();
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getPolicy(org.objectweb.proactive.ext.security.SecurityContext)
     */
    public SecurityContext getPolicy(SecurityContext sc)
        throws ProActiveException, SecurityNotAvailableException {
        return this.getPolicy(sc);
    }

    public void listVirtualNodes() throws ProActiveException {
        //  remoteProActiveRuntime.updateLocalNodeVirtualName();
        this.remoteProActiveRuntime.listVirtualNodes();
    }
}
