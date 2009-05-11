package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.AddNeighborResponseMessage;


/**
 * A CANUpdateMessage is a concrete message to update a CAN peer.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANAddNeighborMessage extends AddNeighborMessage {

    private final Peer remotePeer;
    private final int dimension;
    private final int direction;

    /**
     * Constructor.
     */
    public CANAddNeighborMessage(Peer remotePeer, int dimension, int direction) {
        this.remotePeer = remotePeer;
        this.dimension = dimension;
        this.direction = direction;
    }

    /**
     * {@inheritDoc}
     */
    public AddNeighborResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleAddNeighborMessage(this);
    }

    /**
     * Returns the peer to add as neighbor.
     * 
     * @return the peer.
     */
    public Peer getPeer() {
        return this.remotePeer;
    }

    /**
     * Returns the dimension to add the peer as neighbor.
     * 
     * @return the dimension.
     */
    public int getDimesion() {
        return this.dimension;
    }

    /**
     * Returns the direction to add the peer as neighbor.
     * 
     * @return the direction.
     */
    public int getDirection() {
        return this.direction;
    }
}
