package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANJoinResponseMessage;


/**
 * A {@link CANJoinMessage} is a concrete message in order to ping.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANJoinMessage extends Message {

    private final Peer peer;
    private final int dimension;
    private final int order;

    /**
     * Constructor.
     */
    public CANJoinMessage(Peer peer, int dimension, int direction) {
        this.peer = peer;
        this.dimension = dimension;
        this.order = direction;
    }

    /**
     * Handles message by delegation.
     * 
     * @param a
     *            peer to which the message will be send.
     * @return a PingResponseMessage for routing.
     * 
     */
    @Override
    public CANJoinResponseMessage handle(StructuredOverlay overlay) {
        return ((CANOverlay) overlay).handleJoinMessage(this);
    }

    public Peer getPeer() {
        return this.peer;
    }

    public int getDirection() {
        return this.order;
    }

    public int getDimesion() {
        return this.dimension;
    }
}
