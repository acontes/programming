/*
 * Created on 26 juin 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.objectweb.proactive.core.runtime.xmlhttp;

import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.mop.ConstructorCall;
import org.objectweb.proactive.core.process.UniversalProcess;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.runtime.VMInformation;
import org.objectweb.proactive.ext.security.PolicyServer;
import org.objectweb.proactive.ext.security.ProActiveSecurityManager;
import org.objectweb.proactive.ext.security.SecurityContext;
import org.objectweb.proactive.ext.security.exceptions.SecurityNotAvailableException;





public interface HttpRuntimeStrategyAdapter extends ProActiveRuntime {
	
    
    public String createLocalNode(String nodeName,
        boolean replacePreviousBinding, PolicyServer ps, String vname,
        String jobId);
    
   
    public void killAllNodes() throws ProActiveException ;
    

    public void killNode(String nodeName) throws ProActiveException ;
    
    public void createVM(UniversalProcess remoteProcess)
        throws ProActiveException ;
    
    public String[] getLocalNodeNames() throws ProActiveException ;
    
    
    public VMInformation getVMInformation() ;

    public void register(ProActiveRuntime proActiveRuntimeDist,
        String proActiveRuntimeName, String creatorID, String creationProtocol,
        String vmName) ;
    
    public ProActiveRuntime[] getProActiveRuntimes() throws ProActiveException;
    
    public ProActiveRuntime getProActiveRuntime(String proActiveRuntimeName)
    throws ProActiveException;

    public void killRT(boolean softly) throws ProActiveException ;
    

    public String getURL() throws ProActiveException;
    
    public ArrayList getActiveObjects(String nodeName)
        throws ProActiveException ;

    public ArrayList getActiveObjects(String nodeName, String objectName)
        throws ProActiveException ;

    public VirtualNode getVirtualNode(String virtualNodeName)
        throws ProActiveException ;

    public void registerVirtualNode(String virtualNodeName,
        boolean replacePreviousBinding) throws ProActiveException ;

    public void unregisterVirtualNode(String virtualNodeName)
        throws ProActiveException ;

    public void unregisterAllVirtualNodes() throws ProActiveException ;
    
    public UniversalBody createBody(String nodeName,
        ConstructorCall bodyConstructorCall, boolean isNodeLocal)
        throws ProActiveException ;

    public UniversalBody receiveBody(String nodeName, Body body)
        throws ProActiveException ;

    // SECURITY 
    public PolicyServer getPolicyServer() throws ProActiveException ;
    
    public void setProActiveSecurityManager(ProActiveSecurityManager ps)
        throws ProActiveException ;

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getCreatorCertificate()
     */
    public X509Certificate getCreatorCertificate() throws ProActiveException ;

    public String getVNName(String nodename) throws ProActiveException ;
    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#setDefaultNodeVirtualNodeName(java.lang.String)
     */
    public void setDefaultNodeVirtualNodeName(String s)
        throws ProActiveException ;

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#updateLocalNodeVirtualName()
     */
   // public void listVirtualNodes() throws ProActiveException ;
    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getNodePolicyServer(java.lang.String)
     */
    public PolicyServer getNodePolicyServer(String nodeName)
        throws ProActiveException ;
    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#enableSecurityIfNeeded()
     */
    public void enableSecurityIfNeeded() throws ProActiveException ;

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getNodeCertificate(java.lang.String)
     */
    public X509Certificate getNodeCertificate(String nodeName)
        throws ProActiveException;
    /**
     * @param nodeName
     * @return returns all entities associated to the node
     */
    public ArrayList getEntities(String nodeName) throws ProActiveException ;

    /**
     * @param nodeName
     * @return returns all entities associated to the node
     */
    public ArrayList getEntities(UniversalBody uBody) throws ProActiveException ;

    /**
     * @return returns all entities associated to this runtime
     */
    public ArrayList getEntities() throws ProActiveException ;

    /**
     * @see org.objectweb.proactive.core.runtime.ProActiveRuntime#getJobID(java.lang.String)
     */
    public String getJobID(String nodeUrl) throws ProActiveException ;
    
    public String [] getNodesNames() throws ProActiveException;
    

    public void addParent(String proActiveRuntimeName) ;

   
    public String[] getParents() ;
  
    public SecurityContext getPolicy(SecurityContext sc) throws ProActiveException, SecurityNotAvailableException;


}

	
	

