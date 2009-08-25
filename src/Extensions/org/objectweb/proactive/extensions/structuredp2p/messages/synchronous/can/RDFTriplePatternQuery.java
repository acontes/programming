package org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.requests.BlockingRequestReceiverException;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;


/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/04/2009
 */
@SuppressWarnings("serial")
public class RDFTriplePatternQuery extends RDFQuery {

    private Set<Peer> lastPeersWhichHaveReceiptTheQuery = new HashSet<Peer>();

    public RDFTriplePatternQuery() {
        super();
    }

    public RDFTriplePatternQuery(Coordinate[] coordinatesToFind) {
        super(coordinatesToFind);
    }

    public RDFTriplePatternQuery(Coordinate subject, Coordinate predicate, Coordinate object) {
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

        for (Peer peer : this.lastPeersWhichHaveReceiptTheQuery) {
            subSetOfNeighbors.remove(peer);
        }

        for (Peer neighbor : subSetOfNeighbors) {
            this.lastPeersWhichHaveReceiptTheQuery.add(neighbor);
        }
        this.lastPeersWhichHaveReceiptTheQuery.add(overlay.getRemotePeer());

        int nbOfSends = 0;
        Stack<Peer> peersToVisit = super.getVisitedPeers();
        for (Peer neighbor : subSetOfNeighbors) {
            if (this.validKeyConstraints(neighbor.getStructuredOverlay())) {
                System.out.println("Send to Neighbor=" + neighbor);

                super.removeAllVisitedPeers();

                for (Peer peer : peersToVisit) {
                    super.addVisitedPeer(peer);
                }
                super.addVisitedPeer(overlay.getRemotePeer());

                super.incrementNbStepsBy(1);
                neighbor.send(this);
                nbOfSends++;
            }
        }

        if (nbOfSends == 0) {
            RDFQueryResponse response = null;

            response = new RDFQueryResponse(this, this.getKeyToReach());

            // response.getQuery().getVisitedPeers().remove(overlay.getRemotePeer());

            // System.out.println("before=" + response.getQuery().getVisitedPeers().size());
            Peer lastPeer = response.getQuery().removeLastVisitedPeer();
            // System.out.println("after=" + response.getQuery().getVisitedPeers().size());
            // System.out.println("On peer " + overlay.getLocalPeer() + " send response to " +
            // lastPeer +
            // " with uuid=" + response.getUUID());

            URIImpl subject = (this.getKeyToReach()[0] == null) ? null : new URIImpl(this.getKeyToReach()[0]
                    .getValue());

            URIImpl predicate = (this.getKeyToReach()[1] == null) ? null : new URIImpl(
                this.getKeyToReach()[1].getValue());

            URIImpl object = (this.getKeyToReach()[2] == null) ? null : new URIImpl(this.getKeyToReach()[2]
                    .getValue());

            Set<Statement> stmts = overlay.getLocalPeer()
                    .query(new StatementImpl(subject, predicate, object));

            System.out.println("NbStatements=" + stmts.size());

            System.out.println("Zone courante=" + overlay);

            System.out.println("Pairs a visiter (" + this.lastPeersWhichHaveReceiptTheQuery.size() + ") :");
            for (Peer peer : this.lastPeersWhichHaveReceiptTheQuery) {
                System.out.println("- " + peer);
            }

            response.addAll(stmts);
            lastPeer.send(response);
        } else {
            ((CANOverlay) overlay).handleRDFTriplePatternQuery(this, nbOfSends);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void route(StructuredOverlay overlay) {
        CANOverlay canOverlay = ((CANOverlay) overlay);

        Coordinate[] coordinatesToReach = this.getKeyToReach();

        // We are on a peer which respects constraints but we need to send request to many other
        if (this.validKeyConstraints(overlay)) {
            System.out.println("* On peer which valid constraint " + overlay);

            if (super.keyToReachContainsAllCoordinates()) {
                RDFQueryResponse response = new RDFQueryResponse(this, this.getKeyToReach(), null);
                response.route(overlay);
            } else {
                this.handle(overlay);
            }
        } else { // We must found a peer which valid the constraints
            System.out.println("* Basic Routing");
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

                        this.addVisitedPeer(overlay.getRemotePeer());
                        this.incrementNbStepsBy(1);

                        try {
                            nearestPeer.send(this);
                        } catch (BlockingRequestReceiverException e) {
                            // super.bufferizeQuery(this);
                        } catch (Exception e) {
                            System.out.println("CANOverlay.send()");
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
