package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANSwitchResponseMessage;


/**
 * A CANSwitchMessage is a concrete message to switch two CAN peers.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANSwitchMessage extends Message {

    private final Peer remotePeer;

    /**
     * Constructor.
     */
    public CANSwitchMessage(Peer remotePeer) {
        super();
        this.remotePeer = remotePeer;
    }

    public CANSwitchResponseMessage handle(StructuredOverlay overlay) {
        return ((CANOverlay) overlay).handleSwitchMessage(this);
    }

    public Peer getPeer() {
        return this.remotePeer;
    }

}
