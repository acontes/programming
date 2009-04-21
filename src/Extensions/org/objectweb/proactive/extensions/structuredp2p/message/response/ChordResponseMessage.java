package org.objectweb.proactive.extensions.structuredp2p.message.response;

/**
 * A chord response message gives a CHORD peer for routing.
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class ChordResponseMessage extends ResponseMessage {
    private Peer response;

    public ChordResponseMessage(Peer peer) {
        this.response = peer;
    }

    public Peer getResponse() {
        return response;
    }
}
