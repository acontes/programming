package org.objectweb.proactive.extensions.structuredp2p.core;

import org.objectweb.proactive.extensions.structuredp2p.message.LoadBalancingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.LoadBalancingResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.LookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.PingResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * Defines an abstract class that all structured peer-to-peer protocols must extends.
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
     * Adds a peer to the network.
     * 
     * @param peer
     *            the peer who wants to join at the current position.
     */
    public abstract void join(Peer peer);

    /**
     * Left the current network.
     */
    public abstract void leave();

    /**
     * Check neighbors list in order to see if a neighbor is died, if it is, so the list is updated.
     */
    public abstract void checkNeighbors();

    /**
     * FIXME do what ? parameters or not ?
     */
    public abstract void update();

    /**
     * Sends a {@link LookupMessage} on the network from the current peer.
     * 
     * @param msg
     *            the message to send
     * @return the response in agreement with the type of message sent.
     */
    public abstract LookupResponseMessage sendMessage(LookupMessage msg);

    /**
     * Send a {@link Message} to a known {@link Peer}.
     * 
     * @param peer
     *            the peer to which we want to send the message.
     * @param msg
     *            the message to send.
     * 
     * @return the response in agreement with the type of message sent.
     */
    public abstract ResponseMessage sendMessageTo(Peer peer, Message msg);

    /**
     * Handles a {@link PingMessage}.
     * 
     * @param msg
     *            the message that is handled.
     * @return the {@link PingResponseMessage} response.
     */
    public PingResponseMessage handlePingMessage(PingMessage msg) {
        return new PingResponseMessage();
    }

    /**
     * Handles a {@link LoadBalancingMessage}.
     * 
     * @param msg
     *            the message that is handled.
     * @return the {@link LoadBalancingResponseMessage} response.
     */
    public LoadBalancingResponseMessage handleLoadBalancingMessage(LoadBalancingMessage msg) {
        return null;
    }

    /**
     * Handles a {@link LookupMessage}.
     * 
     * @param msg
     *            the lookup message that is handled.
     * @return the {@link LookupResponseMessage} response.
     */
    public abstract LookupResponseMessage handleLookupMessage(LookupMessage msg);

    /**
     * Returns the current peer that use this overlay.
     * 
     * @return the current peer that use this overlay.
     */
    public Peer getPeer() {
        return this.peer;
    }
}
