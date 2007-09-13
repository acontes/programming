package org.objectweb.proactive.extra.infrastructuremanager.nodesource.dynamic;

import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;


/**
 * Implementation of a Peer to Peer dynamic node source.
 * TODO the methods {@link #getNode()} and {@link #releaseNode(IMNode)} must be implemented
 * @author proactive team
 *
 */
public class P2PNodeSource extends DynamicNodeSource {
    private static final long serialVersionUID = -9077907016230441233L;

    public P2PNodeSource(String id, int nbMaxNodes, int nice, int ttr) {
        super("P2PNS:" + id, nbMaxNodes, nice, ttr);
    }

    public P2PNodeSource() {
    }

    @Override
    protected void releaseNode(IMNode node) {
        // TODO Auto-generated method stub
    }

    @Override
    protected IMNode getNode() {
        // TODO Auto-generated method stub
        return null;
    }
}
