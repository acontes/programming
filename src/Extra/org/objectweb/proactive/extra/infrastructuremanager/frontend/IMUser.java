package org.objectweb.proactive.extra.infrastructuremanager.frontend;

import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;


/**
 * An interface Front-End for the User to communicate with
 * the Infrastructure Manager
 */
public interface IMUser {
    // for testing
    public StringWrapper echo();

    /**
     * Reserves nb nodes verifying the verifying script,
     * if the infrastructure manager (IM) don't have nb free nodes
     * then it returns the max of valid free nodes
     * @param nb the number of nodes
     * @param verifyingScript : script to be verified by the returned nodes
     * @return an arraylist of nodes
     */
    public NodeSet getAtMostNodes(IntWrapper nbNodes,
        VerifyingScript verifyingScript);

    /**
     * Reserves nb nodes verifying the verifying script,
     * if the infrastructure manager (IM) don't have nb free nodes
     * then it returns an empty node set.
     * @param nb the number of nodes
     * @param verifyingScript : script to be verified by the returned nodes
     * @return an arraylist of nodes
     */
    public NodeSet getExactlyNodes(IntWrapper nbNodes,
        VerifyingScript verifyingScript);

    /**
     * Release the node reserve by the user
     * @param node : the node to release
     * @param postScript : script to execute before releasing the node
     */
    public void freeNode(Node node);

    /**
     * Release the nodes reserve by the user
     * @param nodes : a table of nodes to release
     * @param postScript : script to execute before releasing the nodes
     */
    public void freeNodes(NodeSet nodes);
}
