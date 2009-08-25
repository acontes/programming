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

    private Stack<Peer> visitedPeers = new Stack<Peer>();

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
     * Indicates if the query must be routed to other peers.
     * 
     * @return <code>true</code> if the query must be routed to other peers, <code>false</code>
     *         otherwise.
     */
    public boolean hasPeersToVisit() {
        return this.visitedPeers.size() > 0;
    }

    /**
     * Removes the last peer visited.
     * 
     * @return the peer removed if the remove has succeeded, <code>null</code> otherwise.
     */
    public Peer removeLastVisitedPeer() {
        Peer lastPeer = this.visitedPeers.lastElement();

        if (this.visitedPeers.remove(lastPeer)) {
            return lastPeer;
        }

        return null;
    }

    public void removeAllVisitedPeers() {
        this.visitedPeers.clear();
    }

    /**
     * Indicates if the key to reach has all its coordinates fixed with a not <code>null</code>
     * value or not.
     * 
     * @return <code>true</code> if the key to reach has all its coordinates fixed with a not
     *         <code>null</code>. <code>false</code> otherwise.
     */
    public boolean keyToReachContainsAllCoordinates() {
        for (Coordinate coordinate : super.getKeyToReach()) {
            if (coordinate == null) {
                return false;
            }
        }
        return true;
    }

}
