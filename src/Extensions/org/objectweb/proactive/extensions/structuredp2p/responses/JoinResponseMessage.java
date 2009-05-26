package org.objectweb.proactive.extensions.structuredp2p.responses;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


/**
 * Defines a basic response for the {@link JoinMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class JoinResponseMessage extends ResponseMessage {

    /**
     * The remote peer.
     */
    private final Peer remotePeer;

    /**
     * Constructor.
     * 
     * @param creationTimestamp
     *            the timestamp indicating the time creation of the message which has been sent.
     * @param remotePeer
     */
    public JoinResponseMessage(long creationTimestamp, Peer remotePeer) {
        super(creationTimestamp);
        this.remotePeer = remotePeer;
    }

    /**
     * Returns the remote peer.
     * 
     * @return the remotePeer
     */
    public Peer getRemotePeer() {
        return this.remotePeer;
    }
}
