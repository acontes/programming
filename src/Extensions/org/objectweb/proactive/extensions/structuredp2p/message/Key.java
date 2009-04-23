package org.objectweb.proactive.extensions.structuredp2p.message;

/**
 * The Key is an abstraction for the various {@link StructuredOverlay}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class Key<T> {
    private final T value;

    public Key(T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }
}
