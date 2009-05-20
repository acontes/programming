/**
 * 
 */
package org.objectweb.proactive.extensions.structuredp2p.core.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


/**
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class CANPeer extends Peer {

    public CANPeer() {
        super();
    }

    public CANOverlay getStructuredOverlay() {
        return (CANOverlay) super.getStructuredOverlay();
    }
}
