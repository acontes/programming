package org.objectweb.proactive.extensions.structuredp2p.messages.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.responses.can.CANJoinResponseMessage;


/**
 * A {@code CANJoinMessage} is used when a {@link Peer} want to join an another {@link Peer} which
 * is already on the network.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANJoinMessage extends Message {

    /**
     * The remote peer.
     */
    private final Peer remotePeer;

    /**
     * Constructor.
     * 
     * @param directionInv
     * @param remotePeer
     */
    public CANJoinMessage(Peer remotePeer) {
        super();
        this.remotePeer = remotePeer;
    }

    /**
     * {@inheritDoc}
     */
    public CANJoinResponseMessage handle(StructuredOverlay overlay) {
        return (CANJoinResponseMessage) overlay.handleJoinMessage(this);
    }

    /**
     * Returns the remote peer.
     * 
     * @return the remotePeer
     */
    public Peer getRemotePeer() {
        return this.remotePeer;
    }
}
