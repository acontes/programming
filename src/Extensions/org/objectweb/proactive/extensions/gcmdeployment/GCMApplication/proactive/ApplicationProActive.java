package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.proactive;

import static org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers.GCMA_LOGGER;

import java.net.URI;
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
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.remoteobject.RemoteObjectExposer;
import org.objectweb.proactive.core.remoteobject.RemoteObjectHelper;
import org.objectweb.proactive.core.remoteobject.exception.UnknownProtocolException;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.Application;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.FakeNode;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.NodeMapper;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.NodeProvider;
import org.objectweb.proactive.extensions.gcmdeployment.core.GCMVirtualNodeImpl;
import org.objectweb.proactive.extensions.gcmdeployment.core.GCMVirtualNodeInternal;
import org.objectweb.proactive.extensions.gcmdeployment.core.GCMVirtualNodeRemoteObjectAdapter;
import org.objectweb.proactive.extensions.gcmdeployment.core.TopologyImpl;
import org.objectweb.proactive.extensions.gcmdeployment.core.TopologyRootImpl;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.objectweb.proactive.gcmdeployment.Topology;


public class ApplicationProActive implements Application {
    protected static final String NODE_NAME = "proactive";

    private GCMApplicationInternal gcma;

    private ApplicationProActiveConfigurationBean configBean;

    /** Defined Virtual Nodes */
    private Map<String, ? extends GCMVirtualNodeInternal> virtualNodes = null;

    /** The node allocator in charge of Node dispatching */
    private NodeMapper nodeMapper;

    /** All the nodes created by this GCM Application */
    private List<Node> nodes;

    /** All the runtime created by this GCM Application */
    private Queue<ProActiveRuntime> deployedRuntimes;

    private boolean isKilled;
    private boolean isStarted;
    private Object deploymentMutex = new Object();

    public ApplicationProActive() {
        nodes = new LinkedList<Node>();
        deployedRuntimes = new ConcurrentLinkedQueue<ProActiveRuntime>();
    }

    public String getNodeName() {
        return NODE_NAME;
    }

    public void parse(org.w3c.dom.Node node, XPath xpath, Map<String, NodeProvider> nodeProvider)
            throws Exception {
        ApplicationParserProactive parser = new ApplicationParserProactive(configBean, null);
        parser.parseProActiveNode(node, xpath);

        virtualNodes = parser.getVirtualNodes();

        // apply Application-wide tech services on local node
        Node defaultNode = NodeFactory.getDefaultNode();
        Node halfBodiesNode = NodeFactory.getHalfBodiesNode();
        for (Map.Entry<String, HashMap<String, String>> tsp : configBean
                .getApplicationLevelTechnicalSerives()) {
            TechnicalService ts = TechnicalServicesFactory.create(tsp.getKey(), tsp.getValue());
            if (ts != null) {
                ts.apply(defaultNode);
                ts.apply(halfBodiesNode);
            }
        }

        // nodeMapper = new NodeMapper(this, virtualNodes.values());
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
        // To not block other threads too long we make a snapshot of the node
        // set
        Set<Node> nodesCopied;
        synchronized (nodes) {
            nodesCopied = new HashSet<Node>(nodes);
        }
        return TopologyImpl.createTopology(deploymentTree, nodesCopied);
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

    /*
     * MUST NOT BE USED IF A VIRTUAL NODE IS DEFINED
     * 
     * Asks all unused fakeNodes to the node mapper and creates corresponding
     * nodes.
     */
    private void updateNodes() {
        Set<FakeNode> fakeNodes = nodeMapper.getUnusedNode(true);
        for (FakeNode fakeNode : fakeNodes) {
            try {
                // create should not be synchronized since it's remote call
                Node node = fakeNode.create(GCMVirtualNodeImpl.DEFAULT_VN, null);
                synchronized (nodes) {
                    nodes.add(node);
                }
            } catch (NodeException e) {
                GCMA_LOGGER.warn("GCM Deployment failed to create a node on " + fakeNode.getRuntimeURL() +
                    ". Please check your network configuration", e);
            }
        }
    }

    public void waitReady() {
        for (GCMVirtualNode vn : virtualNodes.values()) {
            vn.waitReady();
        }
    }

    public Set<String> getVirtualNodeNames() {
        return new HashSet<String>(virtualNodes.keySet());
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

    public void startDeployment() {

        synchronized (deploymentMutex) {
            if (isStarted) {
                GCMA_LOGGER.warn("A GCM Application descriptor cannot be started twice", new Exception());
                return;
            }

            isStarted = true;

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

    public void configure(GCMApplicationInternal gcma) throws Exception {
        this.gcma = gcma;

        exportVirtualNodes();
    }

    public void exportVirtualNodes() throws UnknownProtocolException {

        // Export all VirtualNodes as remote objects
        for (GCMVirtualNode vn : virtualNodes.values()) {
            RemoteObjectExposer<GCMVirtualNode> vnroe = new RemoteObjectExposer<GCMVirtualNode>(
                GCMVirtualNode.class.getName(), vn, GCMVirtualNodeRemoteObjectAdapter.class);
            URI uri = RemoteObjectHelper.generateUrl(gcma.getDeploymentId() + "/VirtualNode/" + vn.getName());
            vnroe.createRemoteObject(uri);
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

}
