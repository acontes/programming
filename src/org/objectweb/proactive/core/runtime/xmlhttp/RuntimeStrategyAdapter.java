/*
 * Created on
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.objectweb.proactive.core.runtime.xmlhttp;

import java.io.IOException;
import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.mop.ConstructorCall;
import org.objectweb.proactive.core.process.UniversalProcess;
import org.objectweb.proactive.core.rmi.ClassServer;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.runtime.VMInformation;
import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.ext.security.PolicyServer;
import org.objectweb.proactive.ext.security.ProActiveSecurityManager;
import org.objectweb.proactive.ext.security.SecurityContext;
import org.objectweb.proactive.ext.security.exceptions.SecurityNotAvailableException;



public class RuntimeStrategyAdapter implements ProActiveRuntime,
    Serializable {
    protected int port = ClassServer.getServerSocketPort();

    protected String url = ClassServer.getUrl();

    //this boolean is used when killing the runtime. Indeed in case of co-allocation, we avoid a second call to the runtime
    // which is already dead
    protected boolean alreadykilled = false;
    
    //private static transient Logger logger = ProActive.xmlLogger;

    private transient RuntimeAdapter runtimeadapter;
    
    
    protected VMInformation vmInformation;
   
    
    public RuntimeStrategyAdapter() {	
       	runtimeadapter = new RuntimeAdapterImpl(this);
    	
    }

    /**
     *
     * @param url
     */
    public RuntimeStrategyAdapter(String newurl) {

    	runtimeadapter = new RemoteRuntimeAdapterImpl(this,newurl);
    }

    //
    // -- Implements ProActiveRuntime -----------------------------------------------
    //
    public String createLocalNode(String nodeName,
        boolean replacePreviousBinding, PolicyServer ps, String vname,
        String jobId) {
    	return runtimeadapter.createLocalNode(nodeName,replacePreviousBinding,ps,vname,jobId);
    }

        public void killAllNodes() throws ProActiveException {
        	runtimeadapter.killAllNodes();
    }

    public void killNode(String nodeName) throws ProActiveException {
    	runtimeadapter.killNode(nodeName);
    }

    public void createVM(UniversalProcess remoteProcess)
    	throws ProActiveException {
    	runtimeadapter.createVM(remoteProcess);
    }

    public String[] getLocalNodeNames() throws ProActiveException {
      
        return runtimeadapter.getLocalNodeNames();
    }

    public VMInformation getVMInformation() {
        return runtimeadapter.getVMInformation();
    }

    public void register(ProActiveRuntime proActiveRuntimeDist,
        String proActiveRuntimeName, String creatorID, String creationProtocol,
        String vmName) {
    	runtimeadapter.register(proActiveRuntimeDist,proActiveRuntimeName,
    			creatorID,creationProtocol,vmName);
    }

    public ProActiveRuntime[] getProActiveRuntimes() throws ProActiveException {
      
        return runtimeadapter.getProActiveRuntimes();
    }

    public ProActiveRuntime getProActiveRuntime(String proActiveRuntimeName)
        throws ProActiveException {

        return runtimeadapter.getProActiveRuntime(proActiveRuntimeName);
    }

    public void killRT(boolean softly) throws ProActiveException {
    	runtimeadapter.killRT(softly);
    }

    public String getURL() throws ProActiveException {
        return runtimeadapter.getURL();
    }

    public ArrayList getActiveObjects(String nodeName)
        throws ProActiveException {
             return runtimeadapter.getActiveObjects(nodeName);
    }

    public ArrayList getActiveObjects(String nodeName, String objectName)
        throws ProActiveException {
    		return runtimeadapter.getActiveObjects(nodeName,objectName);
    }

    public VirtualNode getVirtualNode(String virtualNodeName)
        throws ProActiveException {
    		return runtimeadapter.getVirtualNode(virtualNodeName);
    }

    public void registerVirtualNode(String virtualNodeName,
        boolean replacePreviousBinding) throws ProActiveException {
      runtimeadapter.registerVirtualNode(virtualNodeName,replacePreviousBinding);
    }

    public void unregisterVirtualNode(String virtualNodeName)
        throws ProActiveException {
    	runtimeadapter.unregisterVirtualNode(virtualNodeName);
    }

    public void unregisterAllVirtualNodes() throws ProActiveException {
     runtimeadapter.unregisterAllVirtualNodes();
    }

    public UniversalBody createBody(String nodeName,
        ConstructorCall bodyConstructorCall, boolean isNodeLocal)
        throws ProActiveException {
    		return runtimeadapter.createBody(nodeName,bodyConstructorCall,isNodeLocal);
    }

    public UniversalBody receiveBody(String nodeName, Body body)
        throws ProActiveException {
    		return runtimeadapter.receiveBody(nodeName,body);
    }

    // SECURITY 
    public PolicyServer getPolicyServer() throws ProActiveException {
        return runtimeadapter.getPolicyServer();
    }

    public void setProActiveSecurityManager(ProActiveSecurityManager ps)
        throws ProActiveException {
    		runtimeadapter.setProActiveSecurityManager(ps);
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getCreatorCertificate()
     */
    public X509Certificate getCreatorCertificate() throws ProActiveException {
        return runtimeadapter.getCreatorCertificate();
    }

    public String getVNName(String nodename) throws ProActiveException {
    	return runtimeadapter.getVNName(nodename);
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#setDefaultNodeVirtualNodeName(java.lang.String)
     */
    public void setDefaultNodeVirtualNodeName(String s)
        throws ProActiveException {
  
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#updateLocalNodeVirtualName()
     */
    public void listVirtualNodes() throws ProActiveException {
        // vide !!!!
    	//  remoteProActiveRuntime.updateLocalNodeVirtualName();
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getNodePolicyServer(java.lang.String)
     */
    public PolicyServer getNodePolicyServer(String nodeName)
        throws ProActiveException {
    	return runtimeadapter.getNodePolicyServer(nodeName);
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#enableSecurityIfNeeded()
     */
    public void enableSecurityIfNeeded() throws ProActiveException {
    	runtimeadapter.enableSecurityIfNeeded();
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getNodeCertificate(java.lang.String)
     */
    public X509Certificate getNodeCertificate(String nodeName)
        throws ProActiveException {
       return runtimeadapter.getNodeCertificate(nodeName);
    }

    /**
     * @param nodeName
     * @return returns all entities associated to the node
     */
    public ArrayList getEntities(String nodeName) throws ProActiveException {
    	return runtimeadapter.getEntities(nodeName);
    }

    /**
     * @param nodeName
     * @return returns all entities associated to the node
     */
    public ArrayList getEntities(UniversalBody uBody) throws ProActiveException {
  
        return runtimeadapter.getEntities(uBody);
    }

    /**
     * @return returns all entities associated to this runtime
     */
    public ArrayList getEntities() throws ProActiveException {
     
        return runtimeadapter.getEntities();
    }

    /**
     * @see org.objectweb.proactive.Job#getJobID()
     */
    public String getJobID() {
        return vmInformation.getJobID();
    }

    /**
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getJobID(java.lang.String)
     */
    public String getJobID(String nodeUrl) throws ProActiveException {
     
        return runtimeadapter.getJobID(nodeUrl);
    }

  
    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#addParent(java.lang.String)
     */
    public void addParent(String proActiveRuntimeName) {
    	// vide !!!
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getParents()
     */
    public String[] getParents() {
        // vide !!!
    	// TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getPolicy(org.objectweb.proactive.ext.security.SecurityContext)
     */
    public SecurityContext getPolicy(SecurityContext sc)
        throws ProActiveException, SecurityNotAvailableException {
        // vide !!!!
    	// TODO Auto-generated method stub
        return null;
    }
    
    private void writeObject(java.io.ObjectOutputStream out)
    throws IOException {
    
      	out.defaultWriteObject();	
    }
    
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
	    	
    	    	
    	in.defaultReadObject();
    	this.runtimeadapter = new RemoteRuntimeAdapterImpl(this,this.url);

	}
    
    protected String buildNodeURL(String url)
    throws java.net.UnknownHostException {
    int i = url.indexOf('/');
    if (i == -1) {
        //it is an url given by a descriptor
        String host = getVMInformation().getInetAddress()
                          .getCanonicalHostName();

        return UrlBuilder.buildUrl(host, url, "http:", port);
    } else {
        return UrlBuilder.checkUrl(url);
    }
}
    
    public String [] getNodesNames() throws ProActiveException {
    	
    	return runtimeadapter.getNodesNames();
    }
    
}
