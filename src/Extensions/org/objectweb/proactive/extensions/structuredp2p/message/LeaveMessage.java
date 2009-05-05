package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.EmptyResponseMessage;


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

    private final Peer remotePeer;

    public LeaveMessage(Peer remotePeer) {
        super();
        this.remotePeer = remotePeer;
    }

    @Override
    public EmptyResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleLeaveMessage(this);
    }

    public Peer getPeer() {
        return this.remotePeer;
    }
}
