package org.objectweb.proactive.extensions.gcmdeployment.core;

import java.net.URL;
import java.util.Collection;
import java.util.Stack;

import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.NodeProvider;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.GCMDeploymentDescriptor;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.GCMDeploymentDescriptorImpl;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.GCMDeploymentResources;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.bridge.Bridge;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.group.Group;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.HostInfo;


public class DeploymentTreeBuilder {
    final private Collection<NodeProvider> nodeProviders;
    final private URL descriptorPath;
    final private TopologyRootImpl deploymentTree;
    final private Stack<String> pathStack;

    public DeploymentTreeBuilder(Collection<NodeProvider> nodeProviders, URL descriptorPath) {
        this.nodeProviders = nodeProviders;
        this.descriptorPath = descriptorPath;

        this.pathStack = new Stack<String>();
        this.deploymentTree = buildDeploymentTree();

    }

    public TopologyRootImpl getDeploymentTree() {
        return deploymentTree;
    }

    private TopologyRootImpl buildDeploymentTree() {
        // Create the root node and the local JVM to it
        ProActiveRuntimeImpl proActiveRuntime = ProActiveRuntimeImpl.getProActiveRuntime();
        pathStack.push(proActiveRuntime.getVMInformation().getName());

        TopologyRootImpl rootNode = new TopologyRootImpl();
        rootNode.setDeploymentDescriptorPath("none"); // no deployment descriptor here
        rootNode.setApplicationDescriptorPath(descriptorPath.toExternalForm());
        rootNode.setDeploymentPath(this.pathStack);
        this.pathStack.pop();

        // Build leaf nodes
        for (NodeProvider nodeProvider : nodeProviders) {
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

    private TopologyImpl buildHostInfoTreeNode(TopologyRootImpl rootNode, TopologyImpl parentNode,
            HostInfo hostInfo, NodeProvider nodeProvider, GCMDeploymentDescriptor gcmd) {
        this.pathStack.push(hostInfo.getId());

        TopologyImpl node = new TopologyImpl();
        node.setDeploymentDescriptorPath(gcmd.getDescriptorURL().toExternalForm());
        node.setApplicationDescriptorPath(rootNode.getApplicationDescriptorPath());
        node.setDeploymentPath(this.pathStack);
        node.setNodeProvider(nodeProvider.getId());
        hostInfo.setTopologyId(node.getId());
        rootNode.addNode(node, parentNode);

        this.pathStack.pop();
        return node;
    }

    private void buildGroupTreeNode(TopologyRootImpl rootNode, TopologyImpl parentNode, Group group,
            NodeProvider nodeProvider, GCMDeploymentDescriptor gcmd) {
        this.pathStack.push(group.getId());
        buildHostInfoTreeNode(rootNode, parentNode, group.getHostInfo(), nodeProvider, gcmd);
        this.pathStack.pop();
    }

    private void buildBridgeTree(TopologyRootImpl rootNode, TopologyImpl parentNode, Bridge bridge,
            NodeProvider nodeProvider, GCMDeploymentDescriptor gcmd) {
        this.pathStack.push(bridge.getId());

        TopologyImpl node = parentNode;

        // first look for a host info...
        if (bridge.getHostInfo() != null) {
            HostInfo hostInfo = bridge.getHostInfo();
            node = buildHostInfoTreeNode(rootNode, parentNode, hostInfo, nodeProvider, gcmd);
        }

        // then groups...
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

        this.pathStack.pop();
    }

}
