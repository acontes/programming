package org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.requests.BlockingRequestReceiverException;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.SynchronousMessageEntry;
import org.objectweb.proactive.extensions.structuredp2p.responses.synchronous.can.RDFResponseMessage;


/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/04/2009
 */
@SuppressWarnings("serial")
public class RDFTriplePatternQueryMessage extends RDFQueryMessage {

    public RDFTriplePatternQueryMessage() {
        super();
    }

    public RDFTriplePatternQueryMessage(Coordinate[] coordinatesToFind) {
        super(coordinatesToFind);
    }

    public RDFTriplePatternQueryMessage(Coordinate subject, Coordinate predicate, Coordinate object) {
        super(new Coordinate[] { subject, predicate, object });

        if (subject == null && predicate == null && object == null) {
            throw new IllegalArgumentException(
                "The three arguments for RDFTriplePatternQuery cannot be null. You must specified two of them.");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void handle(StructuredOverlay overlay) {
        NeighborsDataStructure subSetOfNeighbors = new NeighborsDataStructure();
        subSetOfNeighbors.addAll(((CANOverlay) overlay).getNeighborsDataStructure());

        for (Peer peer : super.getLastPeersWhichHaveReceiptTheQuery()) {
            subSetOfNeighbors.remove(peer);
        }

        for (int dimension = 0; dimension < CANOverlay.NB_DIMENSIONS; dimension++) {
            for (int direction = 0; direction < 2; direction++) {
                Iterator<Peer> it = subSetOfNeighbors.getNeighbors(dimension, direction).iterator();

                while (it.hasNext()) {
                    Peer peer = it.next();
//                    if (!((ActionResponseMessage) PAFuture.getFutureValue(overlay.sendTo(peer,
//                            new TestRDFTripplePatternKeyConstraints(this.getKeyToReach())))).hasSucceeded()) {
                    if (!this.validKeyConstraints(peer.getStructuredOverlay())) {
                        it.remove();
                    } else {
                        super.lastPeersWhichHaveReceiptTheQuery.add(peer);
                    }
                }
            }
        }

        /*
         * At this step, subSetOfNeighbors contains the difference between all the neighbors (which
         * valid constraints) of the current peer manages by the overlay and the peers which have
         * already been visited (lastpeersWhichHaveReceiptTheQuery).
         */
        super.lastPeersWhichHaveReceiptTheQuery.add(overlay.getRemotePeer());

        Stack<Peer> peersToVisit = new Stack<Peer>();
        for (Peer p : this.getPeersToVisitForStepTwo()) {
            peersToVisit.add(p);
        }

        /*
         * At this step there are two cases : 
         * - nbOfSends is equals 0 means that we don't have performed send. We are on a leaf and response must be returned;
         * - nbOfSends > 0 means we have performed one or many send. The current peer must now wait for nbOfSends responses.
         */
        if (subSetOfNeighbors.size() == 0) {
            RDFResponseMessage response = new RDFResponseMessage(this, this.getKeyToReach());

//          URIImpl subject = (this.getKeyToReach()[0] == null) ? null : new URIImpl(this.getKeyToReach()[0]
//                  .getValue());
//
//          URIImpl predicate = (this.getKeyToReach()[1] == null) ? null : new URIImpl(
//              this.getKeyToReach()[1].getValue());
//
//          URIImpl object = (this.getKeyToReach()[2] == null) ? null : new URIImpl(this.getKeyToReach()[2]
//                  .getValue());
//
//          Set<Statement> stmts = overlay.getLocalPeer()
//                  .query(new StatementImpl(subject, predicate, object));
//
//          response.addAll(stmts);

            Peer p = response.getQuery().removeLastPeerToVisitForStepTwo();
            p.send(response);
        } else {
            overlay.getSynchronousMessages().put(this.getUUID(),
                    new SynchronousMessageEntry(subSetOfNeighbors.size()));

            for (Peer neighbor : subSetOfNeighbors) {
                super.getPeersToVisitForStepTwo().clear();

                for (Peer peer : peersToVisit) {
                    super.getPeersToVisitForStepTwo().push(peer);
                }
                super.getPeersToVisitForStepTwo().push(overlay.getRemotePeer());
                super.incrementNbStepsBy(1);
                System.out.println("  - " + neighbor);
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

    /**
     * {@inheritDoc}
     */
    public boolean validKeyConstraints(StructuredOverlay overlay) {
        Zone zone = ((CANOverlay) overlay).getZone();

        for (int i = 0; i < super.getKeyToReach().length; i++) {
            // if coordinate is null we skip the test
            if (super.getKeyToReach()[i] != null) {
                // the specified overlay does not contains the key
                if (zone.contains(i, super.getKeyToReach()[i]) != 0) {
                    return false;
                }
            }
        }

        return true;
    }

}
