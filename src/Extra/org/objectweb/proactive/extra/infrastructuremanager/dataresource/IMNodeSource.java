package org.objectweb.proactive.extra.infrastructuremanager.dataresource;

import org.objectweb.proactive.extra.infrastructuremanager.frontend.NodeSet;


/**
 * A node Source is an entity that can provide nodes
 * to the Infrastructure Manager.
 * But the given nodes may be available only for some short time.
 * This is specially designed to be used with a ProActive P2P Network.
 *
 * @author proactive
 */
public interface IMNodeSource {
	
	/**
	 * String identifying the NodeSource. This must be unique.
	 * @return
	 */
	public String getSourceId();
	
	/**
	 * Get a set of nbNodes nodes from the NodeSource.
	 * It may return less than that, if the source can't give nbNodes nodes.
	 * @param nbNodes
	 * @return
	 */
    public NodeSet getIMNodes(int nbNodes);

    /**
     * Release a node, meaning don't use it anymore.
     * @param imnode
     */
    public void releaseNode(IMNode imnode);

    /**
     * Release nodes, meaning don't use them anymore.
     * @param imnodes the nodes to release
     */
    public void releaseNodes(NodeSet imnodes);
}
