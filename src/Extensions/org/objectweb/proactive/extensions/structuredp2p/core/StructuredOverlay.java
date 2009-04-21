package org.objectweb.proactive.extensions.structuredp2p.core;

import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * Defines an abstract interface that all structured peer-to-peer protocols must implements.
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
public interface StructuredOverlay {
    /**
     * Adds a peer to
     * 
     * @param peer
     *            the peer who wants to join at the current position.
     */
    public void join(Peer peer);

    /**
     * 
     */
    public void leave();

    /**
     * 
     */
    public void update();

    /**
     * 
     */
    public void checkNeighbors();

    /**
     * 
     * @param peer
     * @param msg
     * @return
     */
    public ResponseMessage sendMessageTo(Peer peer, Message msg);
}
