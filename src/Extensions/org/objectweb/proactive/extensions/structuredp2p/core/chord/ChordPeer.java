package org.objectweb.proactive.extensions.structuredp2p.core.chord;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;


/**
 * Peer of type CHORD.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class ChordPeer extends Peer {

    /**
     * Constructor.
     */
    public ChordPeer() {
        super(OverlayType.CHORD);
    }

    /**
     * Returns the structured overlay associated to this peer.
     * 
     * @return the structured overlay associated to this peer.
     */
    public ChordOverlay getStructuredOverlay() {
        return (ChordOverlay) super.getStructuredOverlay();
    }
}
