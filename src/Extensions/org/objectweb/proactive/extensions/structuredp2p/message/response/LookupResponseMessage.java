package org.objectweb.proactive.extensions.structuredp2p.message.response;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.Key;


/**
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public abstract class LookupResponseMessage extends ResponseMessage {
    /**
     * Coordinates of the peer we lookup.
     */
    private Key<?> key;

    /**
     * The peer which is found for the given key.
     */
    private Peer peer;

    /**
     * Constructor.
     * 
     * @param isNull
     *            indicates if the response is an empty response or not.
     */
    public LookupResponseMessage(boolean isNull) {
        super(isNull);
    }

    /**
     * Constructor.
     * 
     * @param key
     *            the key used in order to found the peer in the network to which we want to send
     *            this message.
     */
    public LookupResponseMessage(Key<?> key, Peer peer) {
        this.key = key;
        this.peer = peer;
    }

    /**
     * Returns the key user in order to found the peer in the network to which we want to send this
     * message.
     * 
     * @return the key user in order to found the peer in the network.
     */
    public Key<?> getKey() {
        return this.key;
    }

    /**
     * Returns the peer which has been found.
     * 
     * @return the peer which has been found.
     */
    public Peer getPeer() {
        return this.peer;
    }
}
