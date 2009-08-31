package org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.AbstractQueryMessage;


/**
 * An RDF query is a query used in order to retrieve data from peer by Resource Description
 * Framework criteria which is a method for conceptual description or modeling of information. This
 * kind of request will query some {@link Peer} (each maintains an RDF datastore) in order to
 * retrieve results.
 * 
 * This kind of query can only be used on a CAN structured peer-to-peer network.
 * 
 * @author Laurent Pellegrino
 * @version 0.1, 08/03/2009
 */
@SuppressWarnings("serial")
public abstract class RDFQueryMessage extends AbstractQueryMessage<Coordinate> {

    protected Set<Peer> lastPeersWhichHaveReceiptTheQuery = new HashSet<Peer>();

    private Stack<Peer> peersToVisitForStepOne = new Stack<Peer>();

    private Stack<Peer> peersToVisitForStepTwo = new Stack<Peer>();

    public RDFQueryMessage() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param coordinatesToFind
     *            the coordinates to reach.
     */
    public RDFQueryMessage(Coordinate[] coordinatesToFind) {
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

    private Peer removeLastPeer(Stack<Peer> stack) {
        Peer lastPeer = stack.lastElement();
        stack.remove(lastPeer);
        return lastPeer;
    }

    public Peer removeLastPeerToVisitForStepOne() {
        return this.removeLastPeer(this.peersToVisitForStepOne);
    }

    public Peer removeLastPeerToVisitForStepTwo() {
        return this.removeLastPeer(this.peersToVisitForStepTwo);
    }

    /**
     * Returns the lastPeersWhichHaveReceiptTheQuery
     *
     * @return the lastPeersWhichHaveReceiptTheQuery
     */
    public Set<Peer> getLastPeersWhichHaveReceiptTheQuery() {
        return this.lastPeersWhichHaveReceiptTheQuery;
    }

    /**
     * Returns the peersToVisitForStepOne
     *
     * @return the peersToVisitForStepOne
     */
    public Stack<Peer> getPeersToVisitForStepOne() {
        return this.peersToVisitForStepOne;
    }

    /**
     * Returns the peersToVisitForStepTwo
     *
     * @return the peersToVisitForStepTwo
     */
    public Stack<Peer> getPeersToVisitForStepTwo() {
        return this.peersToVisitForStepTwo;
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
