package org.objectweb.proactive.extensions.structuredp2p.core.overlay;

/**
 * Defines which type of overlay which are available in the implementation and that can be used by
 * peers.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public enum OverlayType {

    /**
     * CAN : Content-Addressable Network.
     */
    CAN,
    /**
     * CHORD : a scalable peer-to-peer lookup service for internet applications where topology is a
     * ring.
     */
    CHORD
}
