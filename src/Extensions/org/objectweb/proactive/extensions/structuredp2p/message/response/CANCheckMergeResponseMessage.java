package org.objectweb.proactive.extensions.structuredp2p.message.response;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


/**
 * A response message is the appropriate answer to the message.
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
     */
    public CANCheckMergeResponseMessage(Peer remotePeer, boolean mergeable) {
        super();
        this.remotePeer = remotePeer;
        this.mergeable = mergeable;
    }

    /**
     * Says if the areas are mergeable.
     * 
     * @return <code>true</code> if the areas are mergeable.
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
