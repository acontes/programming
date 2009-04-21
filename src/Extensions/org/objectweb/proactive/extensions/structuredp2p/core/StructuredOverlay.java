package org.objectweb.proactive.extensions.structuredp2p.core;

import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * Defines an abstract interface that all structured peer-to-peer protocols must implements.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
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
     * Left the current network. Area are merged.
     */
    public void leave();

    /**
     * Check neighbors list in order to see if a neighbor is died, if it is, so the list is updated.
     */
    public void checkNeighbors();

    /**
     * TODO FIXME Big PROBLEM !!! parameters or not ?
     */
    public void update();

    /**
     * Send a message to a given peer an get a {@link ResponseMessage} in response. This function
     * use double dispatch.
     * 
     * @param peer
     *            the peer to which we sends the message.
     * @param msg
     *            the message to send.
     * @return the response.
     */
    public ResponseMessage sendMessageTo(Peer peer, Message msg);
}
