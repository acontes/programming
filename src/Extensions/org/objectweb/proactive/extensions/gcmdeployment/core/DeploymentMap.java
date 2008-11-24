package org.objectweb.proactive.extensions.gcmdeployment.core;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.NodeProvider;


public class DeploymentMap extends HashMap<Long, NodeProvider> {
    public DeploymentMap(Map<String, NodeProvider> nodeProviders, TopologyRootImpl deploymentTree) {
        super();

        parseNode(nodeProviders, deploymentTree);

    }

    private void parseNode(Map<String, NodeProvider> nodeProviders, TopologyImpl node) {
        Long key = node.getId();
        NodeProvider value = nodeProviders.get(node.getNodeProvider());
        this.put(key, value);

        for (TopologyImpl childNode : node.children) {
            parseNode(nodeProviders, childNode);
        }
    }
}
