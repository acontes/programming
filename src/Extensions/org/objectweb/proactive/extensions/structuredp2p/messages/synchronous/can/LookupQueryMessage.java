package org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can;

import java.util.List;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.requests.BlockingRequestReceiverException;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.AbstractQueryMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.SynchronousMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.SynchronousMessageEntry;
import org.objectweb.proactive.extensions.structuredp2p.responses.synchronous.can.LookupResponseMessage;


/**
 * A <code>LookupQueryMessage</code> is a query which can be used in order to find a peer which
 * manages a set of known coordinates on a CAN structured peer-to-peer network.
 * 
 * @author Laurent Pellegrino
 * @version 0.1, 08/04/2009
 */
@SuppressWarnings("serial")
public class LookupQueryMessage extends AbstractQueryMessage<Coordinate> {

    /**
     * The coordinates that are managed by the sender. They are used in order to send the response
     * when the keyToReach has been reached.
     */
    private Coordinate[] coordinatesBelongToSender;

    public LookupQueryMessage() {
        super();
    }

    public LookupQueryMessage(Peer sender, Coordinate[] coordinatesToReach) {
        super(coordinatesToReach);
        this.coordinatesBelongToSender = ((CANOverlay) sender.getStructuredOverlay()).getZone()
                .getCoordinatesMin();
    }

    /**
     * {@inheritDoc}
     */
    public void handle(StructuredOverlay overlay) {
        new LookupResponseMessage(this, overlay.getRemotePeer()).route(overlay);
    }

    public void route(StructuredOverlay overlay, SynchronousMessage queryResponse) {
        CANOverlay canOverlay = ((CANOverlay) overlay);
        Coordinate[] coordinatesToReach = this.getKeyToReach();

        boolean isQueryResponse = (queryResponse == null) ? false : true;

        SynchronousMessage msg = (queryResponse == null) ? this : queryResponse;

        if (super.getNbSteps() == 0) {
            overlay.getSynchronousMessages().put(super.getUUID(), new SynchronousMessageEntry(1));
        }

        if (canOverlay.contains(coordinatesToReach)) {
            msg.handle(overlay);
        } else {
            int direction;
            int pos;

            for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
                direction = NeighborsDataStructure.SUPERIOR_DIRECTION;
                pos = canOverlay.contains(dim, coordinatesToReach[dim]);

                if (pos == -1) {
                    direction = NeighborsDataStructure.INFERIOR_DIRECTION;
                }

                if (pos != 0) {
                    List<Peer> neighbors = canOverlay.getNeighborsDataStructure()
                            .getNeighbors(dim, direction);

                    if (neighbors.size() > 0) {
                        Peer nearestPeer = canOverlay.getNeighborsDataStructure().getNearestNeighborFrom(
                                canOverlay.getZone(), coordinatesToReach[CANOverlay.getNextDimension(dim)],
                                dim, direction);

                        if (!isQueryResponse) {
                            super.incrementNbStepsBy(1);
                        }

                        try {
                            nearestPeer.send(msg);
                        } catch (BlockingRequestReceiverException e) {
                            overlay.bufferizeSynchronousMessage(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                }
            }
        }
    }

    public void route(StructuredOverlay overlay) {
        this.route(overlay, null);
    }

    /**
     * {@inheritDoc}
     */
    public boolean validKeyConstraints(StructuredOverlay overlay) {
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
