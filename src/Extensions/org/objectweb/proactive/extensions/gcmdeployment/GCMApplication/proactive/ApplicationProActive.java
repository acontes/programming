package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.proactive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.xpath.XPath;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.services.TechnicalService;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.security.ProActiveSecurityManager;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.Application;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.NodeMapper;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.NodeProvider;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.TechnicalServicesFactory;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.TechnicalServicesProperties;
import org.objectweb.proactive.extensions.gcmdeployment.core.GCMVirtualNodeInternal;
import org.objectweb.proactive.extensions.gcmdeployment.core.TopologyImpl;
import org.objectweb.proactive.extensions.gcmdeployment.core.TopologyRootImpl;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.objectweb.proactive.gcmdeployment.Topology;


public class ApplicationProActive implements Application {
    protected static final String NODE_NAME = "proactive";
    
    private ApplicationProActiveConfigurationBean configBean;

    
    /** Defined Virtual Nodes */
    private Map<String, ? extends GCMVirtualNodeInternal> virtualNodes = null;

    /** The Deployment Tree */
    private TopologyRootImpl deploymentTree;

    /** The node allocator in charge of Node dispatching */
    private NodeMapper nodeMapper;

    /** All the nodes created by this GCM Application */
    private List<Node> nodes;

    /** All the runtime created by this GCM Application */
    private Queue<ProActiveRuntime> deployedRuntimes;

    public ApplicationProActive() {
        nodes = new LinkedList<Node>();
        deployedRuntimes = new ConcurrentLinkedQueue<ProActiveRuntime>();
    }
    
    public String getNodeName() {
        return NODE_NAME;
    }

    public void parse(org.w3c.dom.Node node, XPath xpath) throws Exception {
        ApplicationParserProactive parser = new ApplicationParserProactive(configBean, null);
        parser.parseProActiveNode(node, xpath);
        
        virtualNodes = parser.getVirtualNodes();
        
        // apply Application-wide tech services on local node
        Node defaultNode = NodeFactory.getDefaultNode();
        Node halfBodiesNode = NodeFactory.getHalfBodiesNode();
        for (Map.Entry<String, HashMap<String, String>> tsp :configBean.getApplicationLevelTechnicalSerives()) {
            TechnicalService ts = TechnicalServicesFactory.create(tsp.getKey(), tsp.getValue());
            if (ts != null) {
                ts.apply(defaultNode);
                ts.apply(halfBodiesNode);
            }
        }

        
//        nodeMapper = new NodeMapper(this, virtualNodes.values());
    }
    
    public void addNode(Node node) {
        synchronized (nodes) {
            nodes.add(node);
        }
    }
    
    public GCMVirtualNode getVirtualNode(String vnName) {
        return virtualNodes.get(vnName);
    }

    public Map<String, GCMVirtualNode> getVirtualNodes() {
        return new HashMap<String, GCMVirtualNode>(virtualNodes);
    }
    
    public Topology getTopology() throws ProActiveException {
        if (!virtualNodes.isEmpty())
            throw new ProActiveException("getTopology cannot be called if a VirtualNode is defined");

        this.updateNodes();
        // To not block other threads too long we make a snapshot of the node set
        Set<Node> nodesCopied;
        synchronized (nodes) {
            nodesCopied = new HashSet<Node>(nodes);
        }
        return TopologyImpl.createTopology(deploymentTree, nodesCopied);
    }

    
    public NodeProvider getNodeProviderFromTopologyId(Long topologyId) {
        return topologyIdToNodeProviderMapping.get(topologyId);
    }

    
    public List<Node> getAllNodes() {
        if (virtualNodes.size() != 0) {
            throw new IllegalStateException("getAllNodes cannot be called if a VirtualNode is defined");
        }

        this.updateNodes();
        return nodes;
    }
    
    public void addDeployedRuntime(ProActiveRuntime part) {
        if (isKilled) {
            try {
                part.killRT(false);
            } catch (Exception e) {
                // Connection between the two runtimes will be interrupted 
                // Eat the exception: Miam Miam Miam
            }
        } else {
            deployedRuntimes.add(part);
        }
    }
    
    
    public Set<String> getVirtualNodeNames() {
        return new HashSet<String>(virtualNodes.keySet());
    }
}
