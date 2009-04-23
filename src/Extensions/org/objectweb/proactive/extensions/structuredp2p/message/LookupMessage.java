package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * Each lookup message contains the key used in order to found the peer on the network to which we
 * want to send the message.
 */
@SuppressWarnings("serial")
public abstract class LookupMessage implements Message {
    /**
     * Coordinates of the peer we lookup.
     */
    protected final Key<?> key;

    /**
     * Constructor.
     * 
     * @param key
     *            the key used in order to found the peer in the network to which we want to send
     *            this message.
     */
    public LookupMessage(Key<?> key) {
        this.key = key;
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

    public abstract ResponseMessage handle(StructuredOverlay overlay);
}
