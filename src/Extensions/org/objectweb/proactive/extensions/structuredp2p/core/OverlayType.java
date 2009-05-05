package org.objectweb.proactive.extensions.structuredp2p.core;

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
     * CAN (Content-Addressable Network)
     */
    CAN,
    /**
     * CHORD (A Scalable Peer-to-peer Lookup Service for Internet Applications where topology is a
     * ring.)
     */
    CHORD
}
