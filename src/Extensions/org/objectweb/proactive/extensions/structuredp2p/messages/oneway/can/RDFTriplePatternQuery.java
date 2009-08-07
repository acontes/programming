package org.objectweb.proactive.extensions.structuredp2p.messages.oneway.can;

import java.util.List;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.requests.BlockingRequestReceiverException;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query;


/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/04/2009
 */
@SuppressWarnings("serial")
public class RDFTriplePatternQuery extends RDFQuery {

    public RDFTriplePatternQuery() {
        super();
    }

    public RDFTriplePatternQuery(Coordinate[] coordinatesToFind) {
        super(coordinatesToFind);
    }

    public RDFTriplePatternQuery(Coordinate subject, Coordinate predicate, Coordinate object) {
        super(new Coordinate[] { subject, predicate, object });
    }

    /**
     * {@inheritDoc}
     */
    public void handle(StructuredOverlay overlay) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public void route(StructuredOverlay overlay) {
        CANOverlay CANOverlay = ((CANOverlay) overlay);

        Coordinate[] coordinatesToReach = this.getKeyToReach();

        if (this.validKeyConstraints(overlay)) {
            this.handle(overlay);
        } else {
            int direction;
            int pos;

            for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
                direction = NeighborsDataStructure.SUPERIOR_DIRECTION;
                pos = CANOverlay.contains(dim, coordinatesToReach[dim]);

                if (pos == -1) {
                    direction = NeighborsDataStructure.INFERIOR_DIRECTION;
                }

                if (pos != 0) {
                    List<Peer> neighbors = CANOverlay.getNeighborsDataStructure()
                            .getNeighbors(dim, direction);

                    if (neighbors.size() > 0) {
                        Peer nearestPeer = CANOverlay.getNeighborsDataStructure().getNearestNeighborFrom(
                                CANOverlay.getZone(), coordinatesToReach[CANOverlay.getNextDimension(dim)],
                                dim, direction);
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

    /**
     * {@inheritDoc}
     */
    public void route(StructuredOverlay overlay, Query query) {
        // TODO Auto-generated method stub

    }

}
