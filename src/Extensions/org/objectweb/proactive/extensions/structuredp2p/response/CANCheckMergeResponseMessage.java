package org.objectweb.proactive.extensions.structuredp2p.response;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.can.CANCheckMergeMessage;


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
public class CANCheckMergeResponseMessage extends ResponseMessage {

    private final Peer remotePeer;
    private final boolean mergeable;

    /**
     * Constructor.
     * 
     * @param creationTimestamp
     *            the timestamp indicating the time creation of the message which has been sent.
     * @param remotePeer
     * @param mergeable
     */
    public CANCheckMergeResponseMessage(long creationTimestamp, Peer remotePeer, boolean mergeable) {
        super(creationTimestamp);
        this.remotePeer = remotePeer;
        this.mergeable = mergeable;
    }

    /**
     * Indicates if the areas are mergeable.
     * 
     * @return <code>true</code> if the areas are mergeable, false otherwise.
     */
    public boolean isMergeable() {
        return this.mergeable;
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
