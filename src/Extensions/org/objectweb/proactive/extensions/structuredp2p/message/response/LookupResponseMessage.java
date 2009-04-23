package org.objectweb.proactive.extensions.structuredp2p.message.response;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.Key;


/**
 * FIXME
 */
@SuppressWarnings("serial")
public abstract class LookupResponseMessage extends ResponseMessage {
    /**
     * Coordinates of the peer we lookup.
     */
    private Key<?> key;

    /**
     * FIXME
     */
    private Peer peer;

    /**
     * FIXME
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
     * FIXME
     * 
     * @return
     */
    public Peer getPeer() {
        return this.peer;
    }
}
