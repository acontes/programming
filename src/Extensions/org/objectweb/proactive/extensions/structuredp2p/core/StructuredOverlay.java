package org.objectweb.proactive.extensions.structuredp2p.core;

import java.io.Serializable;

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
@SuppressWarnings("serial")
public abstract class StructuredOverlay implements Serializable {
    /**
     * The local peer which is associated with the overlay.
     */
    private final Peer localPeer;

    /**
     * Constructor.
     * 
     * @param peer
     *            the peer which is associated to the overlay.
     */
    public StructuredOverlay(Peer peer) {
        this.localPeer = peer;
    }

    /**
     * Adds a peer to the network.
     * 
     * @param remotePeer
     *            the peer (entry point) which is used in order to join the network.
     */
    public abstract Boolean join(Peer remotePeer);

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
        return new LoadBalancingResponseMessage();
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
    public Peer getLocalPeer() {
        return this.localPeer;
    }

    /**
     * Returns the stub associated to the local peer.
     * 
     * @return the stub associated to the local peer.
     */
    public Peer getRemotePeer() {
        return this.localPeer.getStub();
    }

}
