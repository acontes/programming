package org.objectweb.proactive.extensions.structuredp2p.messages;

import java.io.Serializable;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;


/**
 * The Key is an abstraction for the various {@link StructuredOverlay}. It is used when we want to
 * lookup a {@link Peer} from it position which can be an identifier, a coordinate, ...
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
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
