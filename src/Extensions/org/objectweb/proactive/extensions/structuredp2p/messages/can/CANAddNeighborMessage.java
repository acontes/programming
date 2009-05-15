package org.objectweb.proactive.extensions.structuredp2p.messages.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.messages.AddNeighborMessage;


/**
 * A CANAddNeighborMessage is a concrete message to add a neighbor to a CAN peer.
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
    private final Area remoteArea;

    /**
     * The dimension to add the peer as neighbor.
     */
    private final int dimension;

    /**
     * The direction to add the peer as neighbor.
     */
    private final int direction;

    /**
     * Constructor.
     * 
     * @param remotePeer
     *            the remote peer to add as neighbor.
     * @param dimension
     *            the dimension of remote peer to add as neighbor.
     * @param direction
     *            the direction of remote peer to add as neighbor.
     */
    public CANAddNeighborMessage(Peer remotePeer, Area remoteArea, int dimension, int direction) {
        super(remotePeer);
        this.remoteArea = remoteArea;
        this.dimension = dimension;
        this.direction = direction;
    }

    /**
     * Returns the area of the peer to add as neighbor.
     * 
     * @return the area.
     */
    public Area getRemoteArea() {
        return this.remoteArea;
    }

    /**
     * Returns the dimension to add the peer as neighbor.
     * 
     * @return the dimension.
     */
    public int getDimension() {
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
