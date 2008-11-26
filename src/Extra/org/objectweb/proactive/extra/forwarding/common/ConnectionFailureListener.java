package org.objectweb.proactive.extra.forwarding.common;

/**
 * Interface implemented by any object which need to be
 * notified if a connection fails.
 *
 */
public interface ConnectionFailureListener {

    /**
     * Connection has failed.
     * @param e cause
     */
    public void connectionHasFailed(Exception e);
}
