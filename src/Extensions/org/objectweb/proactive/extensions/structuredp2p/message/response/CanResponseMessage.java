package org.objectweb.proactive.extensions.structuredp2p.message.response;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


/**
 * A chord response message gives a CAN peer for routing.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CanResponseMessage extends ResponseMessage {
    private final Peer response;

    /**
     * 
     * @param peer
     */
    public CanResponseMessage(Peer peer) {
        this.response = peer;
    }

    /**
     * 
     * @return
     */
    public Peer getResponse() {
        return response;
    }
}
