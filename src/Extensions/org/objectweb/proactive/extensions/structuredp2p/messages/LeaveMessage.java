package org.objectweb.proactive.extensions.structuredp2p.messages;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.responses.ActionResponseMessage;


/**
 * A LeaveMessage is a concrete message to unbind peers with the one which is leaving.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class LeaveMessage extends Message {

    /**
     * The leaving remote peer.
     */
    private final Peer remotePeer;

    public LeaveMessage(Peer remotePeer) {
        super();
        this.remotePeer = remotePeer;
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleLeaveMessage(this);
    }

    /**
     * Returns to leaving remote peer.
     * 
     * @return the leaving remote peer.
     */
    public Peer getPeer() {
        return this.remotePeer;
    }
}
