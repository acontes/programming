package org.objectweb.proactive.extensions.structuredp2p.core.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;


/**
 * Peer of type CAN.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANPeer extends Peer {

    /**
     * Constructor.
     */
    public CANPeer() {
        super(OverlayType.CAN);
    }

    /**
     * Returns the structured overlay associated to this peer.
     * 
     * @return the structured overlay associated to this peer.
     */
    public CANOverlay getStructuredOverlay() {
        return (CANOverlay) super.getStructuredOverlay();
    }
}
