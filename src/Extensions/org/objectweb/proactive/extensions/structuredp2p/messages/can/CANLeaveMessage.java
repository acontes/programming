package org.objectweb.proactive.extensions.structuredp2p.messages.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.LeaveMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.ActionResponseMessage;


/**
 * A {@code CANRemoveNeighborMessage} is used in order to remove from a {@link Peer} one of its
 * neighbors.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANLeaveMessage extends LeaveMessage {

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
     *            the neighbor to remove.
     * @param dimension
     *            the dimension of the neighbor to remove.
     * @param direction
     *            the direction of the neighbor to remove
     */
    public CANLeaveMessage(Peer remotePeer, int dimension, int direction) {
        super(remotePeer);
        this.dimension = dimension;
        this.direction = direction;
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleLeaveMessage(this);
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
