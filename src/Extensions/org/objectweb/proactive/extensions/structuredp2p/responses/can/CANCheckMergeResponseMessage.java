package org.objectweb.proactive.extensions.structuredp2p.responses.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANCheckMergeMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.ActionResponseMessage;


/**
 * Defines a response for the {@link CANCheckMergeMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANCheckMergeResponseMessage extends ActionResponseMessage {

    /**
     * The remote peer which seems to be mergeable.
     */
    private final Peer remotePeer;

    /**
     * Constructor.
     * 
     * @param creationTimestamp
     *            the timestamp indicating the time creation of the message which has been sent.
     * @param remotePeer
     *            the remote peer.
     * @param mergeable
     *            are peer mergeable.
     */
    public CANCheckMergeResponseMessage(long creationTimestamp, Peer remotePeer, boolean mergeable) {
        super(creationTimestamp, mergeable);
        this.remotePeer = remotePeer;
    }

    /**
     * Returns the peer to merge with.
     * 
     * @return the peer to merge with.
     */
    public Peer getPeer() {
        return this.remotePeer;
    }
}
