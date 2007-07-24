package org.objectweb.proactive.extra.infrastructuremanager.dataresource;

import org.objectweb.proactive.core.node.NodeException;


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
     * Get a node from the source.
     * If there is no node available, a {@link NodeException} is thrown.
     * @return the node given by the source
     * @throws NodeException meaning there's no node available.
     */
    public IMNode getIMNode() throws NodeException;

    /**
     * Release a node, meaning don't use it anymore.
     * @param imnode
     */
    public void releaseNode(IMNode imnode);
}
