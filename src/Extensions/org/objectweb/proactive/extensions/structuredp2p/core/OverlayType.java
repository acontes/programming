package org.objectweb.proactive.extensions.structuredp2p.core;

/**
 * Defines which type of overlay are avaibles in the implementation and that can be used by peers.
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
public enum OverlayType {
    /**
     * CAN (Content-Addressable Network)
     */
    CAN,
    /**
     * CHORD (A Scalable Peer-to-peer Lookup Service for Internet Applications)
     */
    CHORD
}
