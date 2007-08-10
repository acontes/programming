package org.objectweb.proactive.extra.infrastructuremanager.dataresource;

import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.NodeSet;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;


public interface IMDataResource {

    //----------------------------------------------------------------------//
    // INIT
    public void init();

    //----------------------------------------------------------------------//
    // FREE
    public void freeNode(Node node);

    public void freeNodes(NodeSet nodes);

    public void freeNodes(VirtualNode vnode);

    //----------------------------------------------------------------------//
    // GET NODE 

    public NodeSet getAtMostNodes(IntWrapper nb, VerifyingScript verifyingScript);

    public NodeSet getExactlyNodes(IntWrapper nb, VerifyingScript verifyingScript);

    public void nodeIsDown(IMNode imNode);
}
