package org.objectweb.proactive.extensions.structuredp2p.messages.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.responses.ActionResponseMessage;


/**
 * A {@code CANMergeMessage} is used when a {@link Peer} must merge with an another {@link Peer}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANPrepareToLeaveMessage extends Message {

    /**
     * The peer which is leaving.
     */
    private final Peer remotePeer;

    /**
     * The current dimension of the leaving peer.
     */
    private final int dimension;

    /**
     * The current direction of the leaving peer.
     */
    private final int direction;

    /**
     * Constructor.
     * 
     * @param remotePeer
     *            the remote peer.
     * @param dimension
     *            the dimension.
     * @param direction
     *            the direction.
     */
    public CANPrepareToLeaveMessage(Peer remotePeer, int dimension, int direction) {
        this.remotePeer = remotePeer;
        this.dimension = dimension;
        this.direction = direction;
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handle(StructuredOverlay overlay) {
        return ((CANOverlay) overlay).handlePrepareToLeaveMessage(this);
    }

    /**
     * Returns the leaving remote peer.
     * 
     * @return the remote peer.
     */
    public Peer getRemotePeer() {
        return this.remotePeer;
    }

    /**
     * Returns the dimension of leaving.
     * 
     * @return the dimension.
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * Returns the dimension of leaving.
     * 
     * @return the direction
     */
    public int getDirection() {
        return this.direction;
    }
}
