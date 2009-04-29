package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * A PingMessage is a concrete message for ping.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANJoinMessage implements Message {
    private final Peer peer;
    private final int dimension;
    private final int order;

    /**
     * Constructor.
     */
    public CANJoinMessage(Peer peer, int dim, int order) {
        this.peer = peer;
        this.dimension = dim;
        this.order = order;
    }

    /**
     * Handles message by delegation.
     * 
     * @param a
     *            peer to which the message will be send.
     * @return a PingResponseMessage for routing.
     * 
     */
    public ResponseMessage handle(StructuredOverlay overlay) {
        return ((CANOverlay) overlay).handleCANJoinMessage(this);
    }

    public Peer getPeer() {
        return this.peer;
    }

    public int getOrder() {
        return this.order;
    }

    public int getDimesion() {
        return this.dimension;
    }
}
