package org.objectweb.proactive.extensions.structuredp2p.message.response;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.CANSwitchMessage;


/**
 * Defines a response for the {@link CANSwitchMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANSwitchResponseMessage extends ResponseMessage {
    private final Peer remotePeer;

    /**
     * Constructor.
     */
    public CANSwitchResponseMessage(long timestampMessageCreation, Peer remotePeer) {
        super(timestampMessageCreation);
        this.remotePeer = remotePeer;
    }

    public Peer getPeer() {
        return this.remotePeer;
    }
}
