package org.objectweb.proactive.extensions.structuredp2p.messages;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.responses.ActionResponseMessage;


/**
 * A AddNeighborMessage is a concrete message to add a neighbor to a peer.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public abstract class AddNeighborMessage extends Message {

    /**
     * The peer to add as neighbor.
     */
    private final Peer remotePeer;

    /**
     * Constructor.
     * 
     * @param remotePeer
     *            the remote peer to add as neighbor.
     */
    public AddNeighborMessage(Peer remotePeer) {
        super();
        this.remotePeer = remotePeer;
    }

    /**
     * Handles a {@link AddNeighborMessage} by double dispatch.
     * 
     * @param overlay
     *            the overlay which handle the message.
     */
    public ActionResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleAddNeighborMessage(this);
    }

    /**
     * Returns the peer to add as neighbor.
     * 
     * @return the remote peer.
     */
    public Peer getRemotePeer() {
        return this.remotePeer;
    }
}
