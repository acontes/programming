/*
 * IMCore.java
 *
 * Modified on 22 f�vrier 2007, 19h32
 *
 */
package org.objectweb.proactive.extra.infrastructuremanager.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.infrastructuremanager.dataresource.IMDataResource;
import org.objectweb.proactive.extra.infrastructuremanager.dataresource.IMNode;
import org.objectweb.proactive.extra.infrastructuremanager.dataresource.database.IMDataResourceImpl;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMAdmin;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMAdminImpl;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMMonitoring;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMMonitoringImpl;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMUser;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMUserImpl;


public class IMCore implements InitActive, IMConstants, Serializable {
    private final static Logger logger = ProActiveLogger.getLogger(Loggers.IM_CORE);

    // Attributes
    private Node nodeIM;
    private IMAdmin admin;
    private IMMonitoring monitoring;
    private IMUser user;
    private IMDataResource dataresource;
    private IMDeploymentFactory fac;

    // TODO add ActionStatus for the futur access
    private ArrayList<IMActionStatus> currentActionStatus;

    // ----------------------------------------------------------------------//
    // CONSTRUCTORS

    /** ProActive compulsory no-args constructor */
    public IMCore() {
    }

    public IMCore(Node nodeIM)
        throws ActiveObjectCreationException, NodeException {
        if (logger.isInfoEnabled()) {
            logger.info("instanciation IMCore");
        }
        this.nodeIM = nodeIM;
        if (logger.isInfoEnabled()) {
            logger.info("instanciation IMDataResourceImpl");
        }
        this.dataresource = new IMDataResourceImpl();
        if (logger.isInfoEnabled()) {
            logger.info("instanciation IMDeploymentFactory");
        }
    }

    // ----------------------------------------------------------------------//
    // INIT ACTIVE FRONT-END
    public void initActivity(Body body) {
        if (logger.isInfoEnabled()) {
            logger.info("IMCore start : initActivity");
        }
        try {
            if (logger.isInfoEnabled()) {
                logger.info("active object IMAdmin");
            }
            admin = (IMAdminImpl) ProActive.newActive(IMAdminImpl.class.getName(),
                    new Object[] { ProActive.getStubOnThis() }, nodeIM);

            if (logger.isInfoEnabled()) {
                logger.info("active object IMMonitoring");
            }
            monitoring = (IMMonitoringImpl) ProActive.newActive(IMMonitoringImpl.class.getName(),
                    new Object[] { ProActive.getStubOnThis() }, nodeIM);

            if (logger.isInfoEnabled()) {
                logger.info("active object IMUser");
            }
            user = (IMUserImpl) ProActive.newActive(IMUserImpl.class.getName(),
                    new Object[] { ProActive.getStubOnThis() }, nodeIM);
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
        if (logger.isInfoEnabled()) {
            logger.info("IMCore end : initActivity");
        }
    }

    // ----------------------------------------------------------------------//
    // TEST
    public String echo() {
        return "Je suis le IMCore";
    }

    // ----------------------------------------------------------------------//
    // ACCESSORS
    public Node getNodeIM() {
        return this.nodeIM;
    }

    public IMAdmin getAdmin() {
        return this.admin;
    }

    public IMMonitoring getMonitoring() {
        return this.monitoring;
    }

    public IMUser getUser() {
        return this.user;
    }

    /*
    public IMDataResource getDataResource() {
            return this.dataresource;
    }*/

    // ----------------------------------------------------------------------//
    // ADMIN
    public void addNode(Node node, String vnName, String padName) {
        if (logger.isInfoEnabled()) {
            logger.info("IMCore - addNode : node=" +
                node.getNodeInformation().getName() + ", vnName=" + vnName +
                ", padName=" + padName);
        }
        this.dataresource.addNewDeployedNode(node, vnName, padName);
    }

    public void addPAD(String padName, ProActiveDescriptor pad) {
        this.dataresource.putPAD(padName, pad);
    }

    // ----------------------------------------------------------------------//	
    // REDEPLOY
    private void redeployVNode(VirtualNode vnode, String padName,
        ProActiveDescriptor pad) {
        if (vnode.isActivated()) { // REDEPLOY
            vnode.killAll(false);
            //vnode.activate();
            this.dataresource.removeNode(padName, vnode.getName());
            IMDeploymentFactory.deployVirtualNode(this, padName, pad,
                vnode.getName());
        } else { // DEPLOY
            IMDeploymentFactory.deployVirtualNode(this, padName, pad,
                vnode.getName());
        }
    }

    public void redeploy(String padName) {
        if (this.dataresource.isDeployedPad(padName)) {
            ProActiveDescriptor pad = this.dataresource.getDeployedPad(padName);
            VirtualNode[] vnodes = pad.getVirtualNodes();
            for (VirtualNode vnode : vnodes) {
                redeployVNode(vnode, padName, pad);
            }
        }
    }

    public void redeploy(String padName, String vnName) {
        if (this.dataresource.isDeployedPad(padName)) {
            ProActiveDescriptor pad = this.dataresource.getDeployedPad(padName);
            VirtualNode vnode = pad.getVirtualNode(vnName);
            redeployVNode(vnode, padName, pad);
        }
    }

    public void redeploy(String padName, String[] vnNames) {
        if (this.dataresource.isDeployedPad(padName)) {
            ProActiveDescriptor pad = this.dataresource.getDeployedPad(padName);
            VirtualNode vnode;
            for (String vnName : vnNames) {
                vnode = pad.getVirtualNode(vnName);
                redeployVNode(vnode, padName, pad);
            }
        }
    }

    // ----------------------------------------------------------------------//	
    // KILL
    public void killPAD(String padName) throws ProActiveException {
        if (this.dataresource.isDeployedPad(padName)) {
            ProActiveDescriptor pad = this.dataresource.getDeployedPad(padName);
            pad.killall(false);
            this.dataresource.removeNode(padName);
            this.dataresource.removePad(padName);
            // TODO : delete the pad file
            // find the temp directory but how ????
            // File tempPAD = new File( tempDir + padName) 
            // if ( tempPAD.exists() ) tempPAD.delete();
        }
    }

    public void killPAD(String padName, String vnName) {
        if (this.dataresource.isDeployedPad(padName)) {
            ProActiveDescriptor pad = this.dataresource.getDeployedPad(padName);
            VirtualNode vnode = pad.getVirtualNode(vnName);
            vnode.killAll(false);
            this.dataresource.removeNode(padName, vnName);
        }
    }

    public void killPAD(String padName, String[] vnNames) {
        if (this.dataresource.isDeployedPad(padName)) {
            ProActiveDescriptor pad = this.dataresource.getDeployedPad(padName);
            VirtualNode[] vnodes = pad.getVirtualNodes();
            for (VirtualNode vnode : vnodes) {
                vnode.killAll(false);
            }
            this.dataresource.removeNode(padName, vnNames);
        }
    }

    public void killAll() throws ProActiveException {
        for (String padName : this.dataresource.getListPad().keySet()) {
            killPAD(padName);
        }
    }

    // ----------------------------------------------------------------------//
    // MONITORING
    public int getSizeListFreeIMNode() {
        return dataresource.getSizeListFreeIMNode();
    }

    public int getSizeListBusyIMNode() {
        return dataresource.getSizeListBusyIMNode();
    }

    public int getSizeListPad() {
        return dataresource.getSizeListPad();
    }

    public HashMap<String, ProActiveDescriptor> getListPAD() {
        return this.dataresource.getListPad();
    }

    public HashMap<String, ArrayList<VirtualNode>> getDeployedVirtualNodeByPad() {
        return this.dataresource.getDeployedVirtualNodeByPad();
    }

    public ArrayList<IMNode> getListFreeIMNode() {
        return this.dataresource.getListFreeIMNode();
    }

    public ArrayList<IMNode> getListBusyIMNode() {
        return this.dataresource.getListBusyIMNode();
    }

    public ArrayList<IMNode> getListAllNodes() {
        return this.dataresource.getListAllIMNode();
    }

    // ----------------------------------------------------------------------//
    // USER
    public Node getNode() throws NodeException {
        return this.dataresource.getNode();
    }

    public Node[] getAtLeastNNodes(int nb) throws NodeException {
        return this.dataresource.getAtLeastNNodes(nb);
    }

    public void freeNode(Node node) throws NodeException {
        this.dataresource.freeNode(node);
    }

    public void freeNodes(Node[] nodes) throws NodeException {
        this.dataresource.freeNodes(nodes);
    }

    // ----------------------------------------------------------------------//
    // SHUTDOWN
    public void shutdown() throws ProActiveException {
        killAll();
        ProActive.exitSuccess();
    }

    // ----------------------------------------------------------------------//
}
