package org.objectweb.proactive.extensions.structuredp2p.core.overlay;

/**
 * Defines type of overlay which are available and can be used for implementation and peers.
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
     * CHORD : a scalable peer-to-peer lookup service for Internet applications where topology is a
     * ring.
     */
    CHORD
}
