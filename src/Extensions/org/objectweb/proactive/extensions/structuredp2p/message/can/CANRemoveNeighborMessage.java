package org.objectweb.proactive.extensions.structuredp2p.message.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.RemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.response.ActionResponseMessage;


/**
 * A PingMessage is a concrete message to remove a neighbor from a peer.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANRemoveNeighborMessage extends RemoveNeighborMessage {

    /**
     * The dimension of the neighbor to remove.
     */
    private final int dimension;

    /**
     * The direction of the neighbor to remove.
     */
    private final int direction;

    /**
     * Constructor.
     * 
     * @param remotePeer
     *            the neighbor peer to remove.
     * @param dimension
     *            the dimension of the neighbor to remove.
     * @param direction
     *            the direction of the neighbor to remove
     */
    public CANRemoveNeighborMessage(Peer remotePeer, int dimension, int direction) {
        super(remotePeer);
        this.dimension = dimension;
        this.direction = direction;
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleRemoveNeighborMessage(this);
    }

    /**
     * Returns the dimension of the neighbor to remove.
     * 
     * @return the dimension of the neighbor to remove.
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * Returns the direction of the neighbor to remove.
     * 
     * @return the direction of the neighbor to remove.
     */
    public int getDirection() {
        return this.direction;
    }

}
