package org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


/**
 * Defines a basic response for a join message.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class JoinResponseMessage extends ActionResponseMessage {

    /**
     * The remote peer.
     */
    private final Peer remotePeer;

    /**
     * Constructor to use when the join operation didn't work.
     * 
     * @param succedeed
     *            <code>false</code> if the peer hasn't join the overlay.
     * @param remotePeer
     *            the remote peer.
     */
    public JoinResponseMessage(boolean succedeed, Peer remotePeer) {
        super(succedeed);
        this.remotePeer = remotePeer;
    }

    /**
     * Constructor.
     * 
     * @param remotePeer
     *            the remote peer.
     */
    public JoinResponseMessage(Peer remotePeer) {
        this(true, remotePeer);
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
