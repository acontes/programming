package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ChordResponseMessage;


/**
 * 
 * A ChordMessage is a concrete class for Chord message
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class ChordMessage implements Message {
    // the id of the the peer to which the message will be send
    private String id;

    /**
     * create a new ChordMessage with a id of the peer
     * 
     * @param id
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
     * @return the id of the peer to which the message will be send
     */

    public String getId() {
        return id;
    }

    /**
     * set the new id of the peer
     * 
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

}
