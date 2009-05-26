package org.objectweb.proactive.extensions.structuredp2p.messages.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.messages.AddNeighborMessage;


/**
 * A {@code CANAddNeighborMessage} is used in order to add a neighbor to {@link Peer} on the
 * network.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANAddNeighborMessage extends AddNeighborMessage {

    /**
     * The area from the peer to add as neighbor.
     */
    private final Zone remoteArea;

    /**
     * The dimension on which we add the peer as neighbor.
     */
    private final int dimension;

    /**
     * The direction on which we add the peer as neighbor.
     */
    private final int direction;

    /**
     * Constructor.
     * 
     * @param remotePeer
     *            the remote peer to add as neighbor.
     * @param dimension
     *            the dimension on which the peer must be add as neighbor.
     * @param direction
     *            the direction on which the peer must be add as neighbor.
     */
    public CANAddNeighborMessage(Peer remotePeer, Zone remoteArea, int dimension, int direction) {
        super(remotePeer);
        this.remoteArea = remoteArea;
        this.dimension = dimension;
        this.direction = direction;
    }

    /**
     * Returns the area of the sender of the message.
     * 
     * @return the area of the sender of the message.
     */
    public Zone getRemoteArea() {
        return this.remoteArea;
    }

    /**
     * Returns the dimension on which the peer must be add as neighbor.
     * 
     * @return the dimension on which the peer must be add as neighbor.
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * Returns the direction on which the peer must be add as neighbor.
     * 
     * @return the direction on which the peer must be add as neighbor.
     */
    public int getDirection() {
        return this.direction;
    }
}
