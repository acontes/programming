package org.objectweb.proactive.extra.infrastructuremanager.frontend;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.core.IMCore;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.dynamic.DynamicNodeSource;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.frontend.DynamicNSInterface;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.frontend.PADNSInterface;


public class IMAdminImpl implements IMAdmin, Serializable {

    /**  */
    private static final long serialVersionUID = 320085562179242055L;
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.IM_ADMIN);

    // Attributes
    private IMCore imcore;

    //----------------------------------------------------------------------//
    // CONSTRUTORS

    /** ProActive compulsory no-args constructor */
    public IMAdminImpl() {
    }

    /**
     * @param imcore
     */
    public IMAdminImpl(IMCore imcore) {
        if (logger.isInfoEnabled()) {
            logger.info("IMAdmin constructor");
        }
        this.imcore = imcore;
    }

    // =======================================================//
    // TEST
    public StringWrapper echo() {
        return new StringWrapper("Je suis le IMAdmin");
    }

    // =======================================================//

    /**
     * @see the IMAdmin interface
     */
    public void deployAllVirtualNodes(File xmlDescriptor, Node remoteNode)
        throws Exception {
        imcore.getPADNodeSource().deployAllVirtualNodes(xmlDescriptor, remoteNode);
    }

    /**
     * @see the IMAdmin interface
     */
    public void deployVirtualNode(File xmlDescriptor, Node remoteNode,
        String vnName) throws Exception {
        imcore.getPADNodeSource().deployVirtualNode(xmlDescriptor, remoteNode, vnName);
    }

    /**
     * @see the IMAdmin interface
     */
    public void deployVirtualNodes(File xmlDescriptor, Node remoteNode,
        String[] vnNames) throws Exception {
    	imcore.getPADNodeSource().deployVirtualNodes(xmlDescriptor, remoteNode,
        vnNames);
    }

    //----------------------------------------------------------------------//
    // GET DEPLOYED VIRTUAL NODES BY PAD 
    // FOR KILL OR REDEPLOY VIRTUAL NODE(S)

    /**
     * @see the IMAdmin interface
     */
    public HashMap<String, ArrayList<VirtualNode>> getDeployedVirtualNodeByPad() {
        return imcore.getDeployedVirtualNodeByPad();
    }

    //----------------------------------------------------------------------//
    // REDEPLOY

    /**
     * @see the IMAdmin interface
     */
    public void redeploy(String padName) {
        this.imcore.redeploy(padName);
    }

    /**
     * @see the IMAdmin interface
     */
    public void redeploy(String padName, String vnName) {
        this.imcore.redeploy(padName, vnName);
    }

    /**
         * @see the IMAdmin interface
         */
    public void redeploy(String padName, String[] vnNames) {
        this.imcore.redeploy(padName, vnNames);
    }

    //----------------------------------------------------------------------//
    // KILL

    /**
         * @see the IMAdmin interface
         */
    public void killAll() throws ProActiveException {
        this.imcore.killAll();
    }

    /**
     * @see the IMAdmin interface
     */
    public void killPAD(String padName) throws ProActiveException {
        this.imcore.killPAD(padName);
    }

    /**
     * @see the IMAdmin interface
     */
    public void killPAD(String padName, String vnName) {
        this.imcore.killPAD(padName, vnName);
    }

    /**
     * @see the IMAdmin interface
     */
    public void killPAD(String padName, String[] vnNames) {
        this.imcore.killPAD(padName, vnNames);
    }

    //----------------------------------------------------------------------//
    // SHUTDOWN

    /**
     * @throws ProActiveException
     * @see the IMAdmin interface
     */
    public void shutdown() throws ProActiveException {
        this.imcore.shutdown();
    }

	public ArrayList<DynamicNSInterface> getDynamicNodeSources() {
		return imcore.getDynamicNodeSources();
	}

	public PADNSInterface getPADNodeSource() {
		return imcore.getPADNodeSource();
	}

	public void addDynamicNodeSources(DynamicNodeSource dns) {
		imcore.addDynamicNodeSources(dns);
	}

	public void removeDynamicNodeSources(DynamicNodeSource dns) {
		imcore.removeDynamicNodeSources(dns);
	}
}
