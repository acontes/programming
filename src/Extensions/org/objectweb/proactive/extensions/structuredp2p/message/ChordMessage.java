package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ChordResponseMessage;


/**
 * A ChordMessage is used in order to found a peer on the network. In response the caller will
 * receive a CanResponseMessage that contains the peer that has been found.
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
     * Handles message by delegation.
     * 
     * @param a
     *            peer to which the message will be sent.
     * @return a ChordResponseMessage for routing.
     */
    public ChordResponseMessage handle(Peer peer) {
        return peer.handleChordMessage(this);

    }

    /**
     * Returns the id searched.
     * 
     * @return the id searched.
     */

    public String getId() {
        return id;
    }
}
