package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication;

import java.io.Serializable;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.xml.VariableContract;
import org.objectweb.proactive.extensions.gcmdeployment.core.GCMVirtualNodeSnapshot;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.objectweb.proactive.gcmdeployment.Topology;


/** An immutable snapshot of a GCM application
 * 
 * @see GCMApplication
 */
public class GCMApplicationSnapshot implements Serializable {
    final private boolean isStarted;
    final private Map<String, GCMVirtualNodeSnapshot> vns;
    final private VariableContract variableContract;
    final private Topology topology;
    final private List<Node> nodes;
    final private URL descriptorUrl;

    public GCMApplicationSnapshot(GCMApplication app) {
        this.isStarted = app.isStarted();

        this.vns = new Hashtable<String, GCMVirtualNodeSnapshot>();
        for (GCMVirtualNode mVn : app.getVirtualNodes().values()) {
            GCMVirtualNodeSnapshot iVn = new GCMVirtualNodeSnapshot(mVn);
            this.vns.put(iVn.getName(), iVn);
        }

        this.variableContract = app.getVariableContract();

        Topology topology = null;
        try {
            topology = app.getTopology();
        } catch (ProActiveException e) {
        }
        this.topology = topology;

        List<Node> nodes = null;
        try {
            nodes = app.getAllNodes();
        } catch (IllegalStateException e) {
        }
        this.nodes = nodes;

        this.descriptorUrl = app.getDescriptorURL();
    }

    /**
     * @see GCMApplication#isStarted()
     */
    public boolean isStarted() {
        return this.isStarted;
    }

    /**
     * @see GCMApplication#kill()
     */
    public void kill() {
        if (nodes != null) {
            for (Node node : nodes) {
                try {
                    node.getProActiveRuntime().killRT(false);
                } catch (Exception e) {
                    // Connection between the two runtimes will be interrupted 
                    // Eat the exception: Miam Miam Miam
                }
            }
        } else {
            for (GCMVirtualNodeSnapshot iVn : this.vns.values()) {
                for (Node node : iVn.getCurrentNodes()) {
                    try {
                        node.getProActiveRuntime().killRT(false);
                    } catch (Exception e) {
                        // Connection between the two runtimes will be interrupted 
                        // Eat the exception: Miam Miam Miam
                    }
                }
            }
        }
    }

    /**
     * @see GCMApplication#getVirtualNode(String)
     */
    public GCMVirtualNodeSnapshot getVirtualNode(String vnName) {
        return this.vns.get(vnName);
    }

    /**
     * @see GCMApplication#getVirtualNodeNames()
     */
    public Set<String> getVirtualNodeNames() {
        return this.vns.keySet();
    }

    /**
     * @see GCMApplication#getVirtualNodes()
     */
    public Map<String, GCMVirtualNodeSnapshot> getVirtualNodes() {
        return this.vns;
    }

    /**
     * @see GCMApplication#getVariableContract()
     */
    public VariableContract getVariableContract() {
        return this.variableContract;
    }

    /**
     * @see GCMApplication#getAllNodes()
     */
    public List<Node> getAllNodes() {
        if (this.nodes != null)
            return this.nodes;

        throw new IllegalStateException("getAllNodes cannot be called if a VirtualNode is defined");
    }

    /**
     * @see GCMApplication#getTopology()
     */
    public Topology getTopology() throws ProActiveException {
        if (topology != null)
            return topology;

        throw new ProActiveException("getTopology cannot be called if a VirtualNode is defined");
    }

    /**
     * @see GCMApplication#getDescriptorURL()
     */
    public URL getDescriptorURL() {
        return this.descriptorUrl;
    }
}
