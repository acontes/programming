package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.PingResponseMessage;


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
public class PingMessage extends Message {

    /**
     * Constructor.
     */
    public PingMessage() {
    }

    /**
     * Handles message by delegation.
     * 
     * @param a
     *            peer to which the message will be send.
     * @return a PingResponseMessage for routing.
     * 
     */
    public PingResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handlePingMessage(this);
    }
}