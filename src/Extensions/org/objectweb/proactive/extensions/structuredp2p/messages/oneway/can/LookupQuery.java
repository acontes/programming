package org.objectweb.proactive.extensions.structuredp2p.messages.oneway.can;

import java.util.List;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.requests.BlockingRequestReceiverException;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query;


/**
 * A <code>LookupQuery</code> is a query which can be used in order to find a peer which manages a
 * set of known coordinates on a CAN structured peer-to-peer network.
 * 
 * @author Laurent Pellegrino
 * @version 0.1, 08/04/2009
 */
@SuppressWarnings("serial")
public class LookupQuery extends AbstractCANQuery {

    /**
     * The coordinates that are managed by the sender. They are used in order to send the response
     * when the keyToReach has been reached.
     */
    private Coordinate[] coordinatesBelongToSender;

    public LookupQuery() {
        super();
    }

    public LookupQuery(Peer sender, Coordinate[] coordinatesToReach) {
        super(coordinatesToReach);
        this.coordinatesBelongToSender = ((CANOverlay) sender.getStructuredOverlay()).getZone()
                .getCoordinatesMin();
    }

    /**
     * {@inheritDoc}
     */
    public void handle(StructuredOverlay overlay) {
        System.out.println("LookupQuery.handle()");

        LookupQueryResponse q = new LookupQueryResponse(this, overlay.getRemotePeer());

        // System.out.println("LookupQuery.handle() 2");

        System.out.print("Coordinates = " + ((CANOverlay) overlay).getZone() + "   -   ");
        System.out.print("Coordinates to reach = ");
        for (Coordinate coordinate : q.getKeyToReach()) {
            System.out.print(coordinate.getValue() + ", ");
        }
        System.out.println();

        ((CANOverlay) overlay).send(q);

        // System.out.println("LookupQuery.handle() 3");
    }

    /**
     * {@inheritDoc}
     */
    public void route(StructuredOverlay overlay, Query query) {
        System.out.println("LookupQuery.route()");
        CANOverlay CANOverlay = ((CANOverlay) overlay);

        Coordinate[] coordinatesToReach = this.getKeyToReach();

        if (CANOverlay.contains(coordinatesToReach)) {
            query.handle(overlay);
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
                        super.incrementNbStepsBy(1);

                        try {
                            nearestPeer.send(this);
                        } catch (BlockingRequestReceiverException e) {
                            overlay.bufferizeQuery(this);
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
    public void route(StructuredOverlay overlay) {
        this.route(overlay, this);
    }

    /**
     * {@inheritDoc}
     */
    public boolean validKeyConstraints(StructuredOverlay overlay) {
        System.out.println("LookupQuery.validKeyConstraints()");
        return ((CANOverlay) overlay).contains(this.getKeyToReach());
    }

    /**
     * Returns the key which is managed by the sender in order to send the response when the
     * keyToReach has been reached.
     * 
     * @return the key which is managed by the sender in order to send the response when the
     *         keyToReach has been reached.
     */
    public Coordinate[] getCoordinatesBelongToSender() {
        return this.coordinatesBelongToSender;
    }

}
