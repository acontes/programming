package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * A CANMergeMessage is a concrete message to merge two CAN peers.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANMergeMessage extends Message {

    private final Peer remotePeer;

    /**
     * Constructor.
     */
    public CANMergeMessage(Peer remotePeer) {
        this.remotePeer = remotePeer;
    }

    /**
     * {@inheritDoc}
     */
    public ResponseMessage handle(StructuredOverlay overlay) {
        return ((CANOverlay) overlay).handleMergeMessage(this);
    }

    /**
     * Returns the peer to merge with.
     * 
     * @return the peer to merge with.
     */
    public Peer getPeer() {
        return this.remotePeer;
    }
}
