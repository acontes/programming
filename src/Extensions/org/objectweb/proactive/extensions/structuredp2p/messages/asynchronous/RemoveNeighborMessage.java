package org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ActionResponseMessage;


/**
 * A RemoveNeighborMessage is a concrete message to remove a neighbor from a peer.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class RemoveNeighborMessage implements AsynchronousMessage {

    /**
     * The neighbor peer to remove.
     */
    private final Peer remotePeer;

    /**
     * Constructor.
     * 
     * @param remotePeer
     *            the neighbor peer to remove.
     */
    public RemoveNeighborMessage(Peer remotePeer) {
        super();
        this.remotePeer = remotePeer;
    }

    /**
     * Returns the neighbor peer to remove.
     * 
     * @return the remote peer.
     */
    public Peer getRemotePeer() {
        return this.remotePeer;
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleRemoveNeighborMessage(this);
    }
}
