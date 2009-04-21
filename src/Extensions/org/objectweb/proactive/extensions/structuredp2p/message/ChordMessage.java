package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ChordResponseMessage;


/**
 * 
 * A ChordMessage is a concrete class for Chord message.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class ChordMessage implements Message {
    /**
     * The id to search.
     */
    private final String id;

    /**
     * Create a new ChordMessage with the id to search.
     * 
     * @param id
     *            the identifier to search.
     */
    public ChordMessage(String id) {
        this.id = id;
    }

    /**
     * @param a
     *            peer to which the message will be send
     * @return a ChordResponseMEssage for routing
     */
    public ChordResponseMessage handle(Peer peer) {
        return peer.handleChordMessage(this);

    }

    /**
     * 
     * @return the id searched.
     */

    public String getId() {
        return id;
    }
}
