package org.objectweb.proactive.extensions.structuredp2p.responses.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANSwitchMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.ResponseMessage;


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

    /**
     * The remote peer to switch with.
     */
    private final Peer remotePeer;

    /**
     * Constructor.
     * 
     * @param timestampMessageCreation
     *            the timestamp indicating the time creation of the message which has been sent.
     * @param remotePeer
     *            the remote peer to switch with.
     */
    public CANSwitchResponseMessage(long timestampMessageCreation, Peer remotePeer) {
        super(timestampMessageCreation);
        this.remotePeer = remotePeer;
    }

    /**
     * Returns the remote peer to switch with.
     * 
     * @return the remote peer.
     */
    public Peer getPeer() {
        return this.remotePeer;
    }
}
