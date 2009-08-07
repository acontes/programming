package org.objectweb.proactive.extensions.structuredp2p.messages.oneway.can;

import java.util.Stack;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;


/**
 * An RDF query is a query used in order to retrieve data from peer by Resource Description
 * Framework criteria which is a method for conceptual description or modeling of information. This
 * kind of request will query some {@link Peer} (each maintains an RDF datastore) in order to
 * retrieve results.
 * 
 * This kind of query can only be used on a CAN structured peer-to-peer network.
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 08/03/2009
 */
@SuppressWarnings("serial")
public abstract class RDFQuery extends AbstractCANQuery {

    Stack<Peer> visitedPeers = new Stack<Peer>();

    public RDFQuery() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param coordinatesToFind
     *            the coordinates to reach.
     */
    public RDFQuery(Coordinate[] coordinatesToFind) {
        super(coordinatesToFind);
    }

    /**
     * {@inheritDoc}
     */
    public abstract void handle(StructuredOverlay overlay);

    /**
     * {@inheritDoc}
     */
    public abstract void route(StructuredOverlay overlay);

    /**
     * {@inheritDoc}
     */
    public abstract boolean validKeyConstraints(StructuredOverlay overlay);

    /**
     * Adds a peer to the visited peers stack.
     * 
     * @param remotePeer
     *            the peer to add.
     */
    public void addVisitedPeer(Peer remotePeer) {
        this.visitedPeers.push(remotePeer);
    }

    /**
     * Returns the peers which has been visited.
     * 
     * @return the peers which has been visited.
     */
    public Stack<Peer> getVisitedPeers() {
        return this.visitedPeers;
    }

    /**
     * Removes the last peer visited.
     * 
     * @return <code>true</code> if the remove has succeeded, <code>false</code> otherwise.
     */
    public boolean removeLastVisitedPeer() {
        return this.visitedPeers.remove(this.visitedPeers.lastElement());
    }

}
