package org.objectweb.proactive.extensions.structuredp2p.core;

import org.objectweb.proactive.extensions.structuredp2p.message.CanLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.ChordLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LoadBalancingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CanLookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ChordLookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.LoadBalancingResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.LookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.PingResponseMessage;
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
public abstract class StructuredOverlay {
    private final Peer peer;

    public StructuredOverlay(Peer peer) {
        this.peer = peer;
    }

    /**
     * Adds a peer to
     * 
     * @param peer
     *            the peer who wants to join at the current position.
     */
    public abstract void join(Peer peer);

    /**
     * Left the current network. Area are merged.
     */
    public abstract void leave();

    /**
     * Check neighbors list in order to see if a neighbor is died, if it is, so the list is updated.
     */
    public abstract void checkNeighbors();

    /**
     * TODO FIXME Big PROBLEM !!! parameters or not ?
     */
    public abstract void update();

    /**
     * Send a message to a given peer an get a {@link ResponseMessage} in response. This function
     * use double dispatch.
     * 
     * @param msg
     *            the message to send.
     * @return the response.
     */
    public abstract LookupResponseMessage sendMessage(LookupMessage msg);

    /**
     * 
     * @param peer
     * @param msg
     * @return
     */
    public abstract ResponseMessage sendMessageTo(Peer peer, Message msg);

    /**
     * Handles a ping request.
     * 
     * @param msg
     *            the ping request that is receive.
     * @return the ping response.
     */
    public PingResponseMessage handlePingMessage(PingMessage msg) {
        return new PingResponseMessage();
    }

    /**
     * Handles a CAN request.
     * 
     * @param msg
     *            the ping request that is receive.
     * @return the ping response.
     */
    public CanLookupResponseMessage handleCanMessage(CanLookupMessage msg) {
        return null;
    }

    /**
     * Handles a CHORD request.
     * 
     * @param msg
     *            the ping request that is receive.
     * @return the ping response.
     */
    public ChordLookupResponseMessage handleChordMessage(ChordLookupMessage msg) {
        return null;
    }

    /**
     * FIXME
     * 
     * @param msg
     * @return
     */
    public LoadBalancingResponseMessage handleLoadBalancingMessage(LoadBalancingMessage msg) {
        return null;
    }
}
