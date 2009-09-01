package org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.requests.BlockingRequestReceiverException;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.AbstractQueryMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.SynchronousMessageEntry;
import org.objectweb.proactive.extensions.structuredp2p.responses.synchronous.can.RDFResponseMessage;
import org.openrdf.model.Statement;


/**
 * An RDF query is a query used in order to retrieve data from peer by Resource Description
 * Framework criteria which is a method for conceptual description or modeling of information. This
 * kind of request will query some {@link Peer} (each maintains an RDF datastore) in order to
 * retrieve results.
 * 
 * This kind of query can only be used on a CAN structured peer-to-peer network.
 * 
 * @author Laurent Pellegrino
 * @version 0.2, 09/01/2009
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
    public void handle(StructuredOverlay overlay) {
        NeighborsDataStructure subSetOfNeighbors = new NeighborsDataStructure();
        subSetOfNeighbors.addAll(((CANOverlay) overlay).getNeighborsDataStructure());

        for (Peer peer : this.lastPeersWhichHaveReceiptTheQuery) {
            subSetOfNeighbors.remove(peer);
        }

        for (int dimension = 0; dimension < CANOverlay.NB_DIMENSIONS; dimension++) {
            for (int direction = 0; direction < 2; direction++) {
                Iterator<Peer> it = subSetOfNeighbors.getNeighbors(dimension, direction).iterator();

                while (it.hasNext()) {
                    Peer peer = it.next();
                    if (!this.validKeyConstraints(peer.getStructuredOverlay())) {
                        it.remove();
                    } else {
                        this.lastPeersWhichHaveReceiptTheQuery.add(peer);
                    }
                }
            }
        }

        /*
         * At this step, subSetOfNeighbors contains the difference between all the neighbors (which
         * valid constraints) of the current peer manages by the overlay and the peers which have
         * already been visited (lastpeersWhichHaveReceiptTheQuery).
         */
        this.lastPeersWhichHaveReceiptTheQuery.add(overlay.getRemotePeer());

        Stack<Peer> peersToVisit = new Stack<Peer>();
        for (Peer p : this.getPeersToVisitForStepTwo()) {
            peersToVisit.add(p);
        }

        /*
         * At this step there are two cases : 
         * - nbOfSends is equals 0 means that we don't have performed send. We are on a leaf
         *   and response must be returned;
         * - nbOfSends > 0 means we have performed one or many send. The current peer must now
         *   wait for the number of response sent.
         */
        if (subSetOfNeighbors.size() == 0) {
            RDFResponseMessage response = new RDFResponseMessage(this, this.getKeyToReach());
            response.addAll(this.retrieveStatements(overlay));
            response.getQuery().removeLastPeerToVisitForStepTwo().send(response);
        } else {
            overlay.getSynchronousMessages().put(this.getUUID(),
                    new SynchronousMessageEntry(subSetOfNeighbors.size()));

            for (Peer neighbor : subSetOfNeighbors) {
                this.getPeersToVisitForStepTwo().clear();

                for (Peer peer : peersToVisit) {
                    this.getPeersToVisitForStepTwo().push(peer);
                }

                this.getPeersToVisitForStepTwo().push(overlay.getRemotePeer());
                super.incrementNbStepsBy(1);
                neighbor.send(this);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void route(StructuredOverlay overlay) {
        CANOverlay canOverlay = ((CANOverlay) overlay);
        Coordinate[] coordinatesToReach = this.getKeyToReach();

        if (this.validKeyConstraints(overlay)) {
            this.handle(overlay);
        } else { // we must found a peer which valid the constraints
            int direction;
            int pos;

            for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
                if (coordinatesToReach[dim] == null) {
                    continue;
                }

                direction = NeighborsDataStructure.SUPERIOR_DIRECTION;
                pos = canOverlay.contains(dim, coordinatesToReach[dim]);

                if (pos == -1) {
                    direction = NeighborsDataStructure.INFERIOR_DIRECTION;
                }

                if (pos != 0) {
                    List<Peer> neighbors = canOverlay.getNeighborsDataStructure()
                            .getNeighbors(dim, direction);

                    if (neighbors.size() > 0) {
                        Peer nearestPeer;

                        if (coordinatesToReach[CANOverlay.getNextDimension(dim)] == null) {
                            nearestPeer = canOverlay.getNeighborsDataStructure().getNeighbors(dim, direction)
                                    .get(0);
                        } else {
                            nearestPeer = canOverlay.getNeighborsDataStructure().getNearestNeighborFrom(
                                    canOverlay.getZone(),
                                    coordinatesToReach[CANOverlay.getNextDimension(dim)], dim, direction);
                        }

                        this.getPeersToVisitForStepOne().push(overlay.getRemotePeer());
                        this.incrementNbStepsBy(1);

                        overlay.getSynchronousMessages().put(this.getUUID(), new SynchronousMessageEntry(1));

                        try {
                            nearestPeer.send(this);
                        } catch (BlockingRequestReceiverException e) {
                            overlay.bufferizeSynchronousMessage(this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                }
            }

        }

    }

    public abstract Set<Statement> retrieveStatements(StructuredOverlay overlay);

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
