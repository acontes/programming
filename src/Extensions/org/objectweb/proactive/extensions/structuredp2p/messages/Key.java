package org.objectweb.proactive.extensions.structuredp2p.messages;

import java.io.Serializable;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;


/**
 * The Key is an abstraction for the various {@link StructuredOverlay}. It is used in order to
 * perform a search on the network by criteria which can be an Identifier, a Coordinate, ...
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @see Peer#search(org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query)
 * @see StructuredOverlay#send(org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query)
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class Key<T> implements Serializable {

    /**
     * The key content.
     */
    private final T value;

    /**
     * Constructor.
     * 
     * @param value
     *            the value of the key.
     */
    public Key(T value) {
        this.value = value;
    }

    /**
     * Returns the value of the key.
     * 
     * @return the value of the key.
     */
    public T getValue() {
        return this.value;
    }
}
