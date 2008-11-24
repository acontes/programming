/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication;

import static org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers.GCMA_LOGGER;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.remoteobject.RemoteObjectExposer;
import org.objectweb.proactive.core.remoteobject.RemoteObjectHelper;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.util.ProActiveRandom;
import org.objectweb.proactive.core.xml.VariableContractImpl;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.CommandBuilder;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.GCMDeploymentDescriptor;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.GCMDeploymentDescriptorImpl;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.GCMDeploymentResources;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.bridge.Bridge;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.group.Group;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.HostInfo;
import org.objectweb.proactive.extensions.gcmdeployment.core.GCMVirtualNodeImpl;
import org.objectweb.proactive.extensions.gcmdeployment.core.GCMVirtualNodeInternal;
import org.objectweb.proactive.extensions.gcmdeployment.core.GCMVirtualNodeRemoteObjectAdapter;
import org.objectweb.proactive.extensions.gcmdeployment.core.TopologyImpl;
import org.objectweb.proactive.extensions.gcmdeployment.core.TopologyRootImpl;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.objectweb.proactive.gcmdeployment.Topology;


public class GCMApplicationImpl implements GCMApplicationInternal {
//    static private Map<Long, GCMApplication> localDeployments = new HashMap<Long, GCMApplication>();

    /** An unique identifier for this deployment */
    final private long deploymentId;

    /** descriptor file */
    final private URL descriptor;

    /** GCM Application parser (statefull) */
    final private GCMApplicationParser parser;
    
    final private Application application;

    /** All Node Providers referenced by the Application descriptor */
    private Map<String, NodeProvider> nodeProviders = null;

    /** A mapping to associate deployment IDs to Node Provider */
    private Map<Long, NodeProvider> topologyIdToNodeProviderMapping;

    /** The Command builder to use to start the deployment */
    private CommandBuilder commandBuilder;

    private Object deploymentMutex = new Object();
    private boolean isStarted;
    private boolean isKilled;

    private ArrayList<String> currentDeploymentPath;
    
    private VariableContractImpl vContract;

//    static public GCMApplication getLocal(long deploymentId) {
//        return localDeployments.get(deploymentId);
//    }

    public GCMApplicationImpl(String filename) throws ProActiveException, MalformedURLException {
        this(new URL("file", null, filename), null);
    }

    public GCMApplicationImpl(String filename, VariableContractImpl vContract) throws ProActiveException,
            MalformedURLException {
        this(new URL("file", null, filename), vContract);
    }

    public GCMApplicationImpl(URL file) throws ProActiveException {
        this(file, null);
    }

    public GCMApplicationImpl(URL file, VariableContractImpl vContract) throws ProActiveException {
        if (file == null) {
            throw new ProActiveException("Failed to create GCM Application: URL cannot be null !");
        }

        try {
            file.openStream();
        } catch (IOException e) {
            throw new ProActiveException("Failed to create GCM Application: URL " + file.toString() +
                " cannot be opened");
        }

        try {
            if (vContract == null) {
                vContract = new VariableContractImpl();
            }
            this.vContract = vContract;
            this.descriptor = file;
            
            deploymentId = ProActiveRandom.nextPosLong();
//            localDeployments.put(deploymentId, this);
            isStarted = false;
            isKilled = false;

            currentDeploymentPath = new ArrayList<String>();
            topologyIdToNodeProviderMapping = new HashMap<Long, NodeProvider>();
            
            // vContract will be modified by the Parser to include variable defined in the descriptor
            parser = new GCMApplicationParserImpl(descriptor, this.vContract);
            application = parser.getApplication();
            nodeProviders = parser.getNodeProviders();

            this.vContract.close();

            
            // Export this GCMApplication as a remote object
            RemoteObjectExposer<GCMApplication> roe = new RemoteObjectExposer<GCMApplication>(
                GCMApplication.class.getName(), this, GCMApplicationRemoteObjectAdapter.class);
            URI uri = RemoteObjectHelper.generateUrl(deploymentId + "/GCMApplication");
            roe.createRemoteObject(uri);

            // Export all VirtualNodes as remote objects
            for (GCMVirtualNode vn : virtualNodes.values()) {
                RemoteObjectExposer<GCMVirtualNode> vnroe = new RemoteObjectExposer<GCMVirtualNode>(
                    GCMVirtualNode.class.getName(), vn, GCMVirtualNodeRemoteObjectAdapter.class);
                uri = RemoteObjectHelper.generateUrl(deploymentId + "/VirtualNode/" + vn.getName());
                vnroe.createRemoteObject(uri);
            }
        } catch (Exception e) {
            throw new ProActiveException("Failed to create GCMApplication: " + e.getMessage() +
                ", see embded message for more details", e);
        }
    }

    /*
     * ----------------------------- GCMApplicationDescriptor interface
     */
    public void startDeployment() {
        synchronized (deploymentMutex) {
            if (isStarted) {
                GCMA_LOGGER.warn("A GCM Application descriptor cannot be started twice", new Exception());
                return;
            }

            isStarted = true;

            deploymentTree = buildDeploymentTree();
            for (GCMVirtualNodeInternal virtualNode : virtualNodes.values()) {
                virtualNode.setDeploymentTree(deploymentTree);
            }

            for (NodeProvider nodeProvider : nodeProviders.values()) {
                nodeProvider.start(commandBuilder, this);
            }
        }
    }

    public boolean isStarted() {
        synchronized (deploymentMutex) {
            return isStarted;
        }
    }



    public void kill() {
        isKilled = true;
        for (ProActiveRuntime part : deployedRuntimes) {
            try {
                part.killRT(false);
            } catch (Exception e) {
                // Connection between the two runtimes will be interrupted 
                // Eat the exception: Miam Miam Miam
            }
        }
    }


   

    public String getDebugInformation() {
        Set<FakeNode> fakeNodes = nodeMapper.getUnusedNode(false);
        StringBuilder sb = new StringBuilder();
        sb.append("Number of unmapped nodes: " + fakeNodes.size() + "\n");
        for (FakeNode fakeNode : fakeNodes) {
            sb.append("\t" + fakeNode.getRuntimeURL() + "(capacity=" + fakeNode.getCapacity() + ")\n");
        }
        return sb.toString();
    }

    public void updateTopology(Topology topology) throws ProActiveException {
        if (!virtualNodes.isEmpty())
            throw new ProActiveException("updateTopology cannot be called if a VirtualNode is defined");

        this.updateNodes();
        // To not block other threads too long we make a snapshot of the node set
        Set<Node> nodesCopied;
        synchronized (nodes) {
            nodesCopied = new HashSet<Node>(nodes);
        }
        TopologyImpl.updateTopology(topology, nodesCopied);
    }

    public VariableContractImpl getVariableContract() {
        return this.vContract;
    }

    public URL getDescriptorURL() {
        return descriptor;
    }

    /*
     * ----------------------------- GCMApplicationDescriptorInternal interface
     */
    public long getDeploymentId() {
        return deploymentId;
    }






    /*
     * ----------------------------- Internal Methods
     */
    protected TopologyRootImpl buildDeploymentTree() {
        // make root node from local JVM
        TopologyRootImpl rootNode = new TopologyRootImpl();

        ProActiveRuntimeImpl proActiveRuntime = ProActiveRuntimeImpl.getProActiveRuntime();
        currentDeploymentPath.clear();
        pushDeploymentPath(proActiveRuntime.getVMInformation().getName());

        rootNode.setDeploymentDescriptorPath("none"); // no deployment descriptor here

        rootNode.setApplicationDescriptorPath(descriptor.toExternalForm());

        rootNode.setDeploymentPath(getCurrentDeploymentPath());
        popDeploymentPath();

        // Build leaf nodes
        for (NodeProvider nodeProvider : nodeProviders.values()) {
            for (GCMDeploymentDescriptor gdd : nodeProvider.getDescriptors()) {
                GCMDeploymentDescriptorImpl gddi = (GCMDeploymentDescriptorImpl) gdd;
                GCMDeploymentResources resources = gddi.getResources();

                HostInfo hostInfo = resources.getHostInfo();
                if (hostInfo != null) {
                    buildHostInfoTreeNode(rootNode, rootNode, hostInfo, nodeProvider, gdd);
                }

                for (Group group : resources.getGroups()) {
                    buildGroupTreeNode(rootNode, rootNode, group, nodeProvider, gdd);
                }

                for (Bridge bridge : resources.getBridges()) {
                    buildBridgeTree(rootNode, rootNode, bridge, nodeProvider, gdd);
                }
            }
        }

        return rootNode;
    }

    /**
     * return a copy of the current deployment path
     * 
     * @return
     */
    private List<String> getCurrentDeploymentPath() {
        return new ArrayList<String>(currentDeploymentPath);
    }

    private TopologyImpl buildHostInfoTreeNode(TopologyRootImpl rootNode, TopologyImpl parentNode,
            HostInfo hostInfo, NodeProvider nodeProvider, GCMDeploymentDescriptor gcmd) {
        pushDeploymentPath(hostInfo.getId());
        TopologyImpl node = new TopologyImpl();
        node.setDeploymentDescriptorPath(gcmd.getDescriptorURL().toExternalForm());
        node.setApplicationDescriptorPath(rootNode.getApplicationDescriptorPath());
        node.setDeploymentPath(getCurrentDeploymentPath());
        node.setNodeProvider(nodeProvider.getId());
        hostInfo.setTopologyId(node.getId());
        topologyIdToNodeProviderMapping.put(node.getId(), nodeProvider);
        rootNode.addNode(node, parentNode);
        popDeploymentPath(); // ???
        return node;
    }

    private void buildGroupTreeNode(TopologyRootImpl rootNode, TopologyImpl parentNode, Group group,
            NodeProvider nodeProvider, GCMDeploymentDescriptor gcmd) {
        pushDeploymentPath(group.getId());
        buildHostInfoTreeNode(rootNode, parentNode, group.getHostInfo(), nodeProvider, gcmd);
        popDeploymentPath();
    }

    private void buildBridgeTree(TopologyRootImpl rootNode, TopologyImpl parentNode, Bridge bridge,
            NodeProvider nodeProvider, GCMDeploymentDescriptor gcmd) {
        pushDeploymentPath(bridge.getId());

        TopologyImpl node = parentNode;

        // first look for a host info...
        //
        if (bridge.getHostInfo() != null) {
            HostInfo hostInfo = bridge.getHostInfo();
            node = buildHostInfoTreeNode(rootNode, parentNode, hostInfo, nodeProvider, gcmd);
        }

        // then groups...
        //
        if (bridge.getGroups() != null) {
            for (Group group : bridge.getGroups()) {
                buildGroupTreeNode(rootNode, node, group, nodeProvider, gcmd);
            }
        }

        // then bridges (and recurse)
        if (bridge.getBridges() != null) {
            for (Bridge subBridge : bridge.getBridges()) {
                buildBridgeTree(rootNode, node, subBridge, nodeProvider, gcmd);
            }
        }

        popDeploymentPath();
    }

    private boolean pushDeploymentPath(String pathElement) {
        return currentDeploymentPath.add(pathElement);
    }

    private void popDeploymentPath() {
        currentDeploymentPath.remove(currentDeploymentPath.size() - 1);
    }

    
    public boolean isKilled() {
    	return isKilled;
    }






 
}
