package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.CanOverlay;
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
public class CanJoinMessage implements Message {
    private Peer peer;
    private int dimension;
    private int order;

    /**
     * Constructor.
     */
    public CanJoinMessage(Peer peer, int dim, int order) {
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
        return ((CanOverlay) overlay).handleCanJoinMessage(this);
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

    @Override
    public String toString() {
        return "Send add neighbor message with (neighbor = " + this.getPeer() + ", dimension = " +
            this.getDimesion() + ", order = " + this.getOrder() + ")";
    }

}
