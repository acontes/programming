package org.objectweb.proactive.extensions.structuredp2p.message;

/**
 * 
 * 
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
