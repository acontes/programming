package org.objectweb.proactive.extensions.structuredp2p.core.overlay;

import java.io.Serializable;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.messages.AddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.LeaveMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.messages.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.RemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANRemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.ActionResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.JoinResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.LookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.ResponseMessage;


/**
 * Defines an abstract class that all structured peer-to-peer protocols must extend.
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
     * @throws Exception
     */
    public abstract Boolean join(Peer remotePeer) throws Exception;

    /**
     * Left the current network.
     * 
     * @return <code>true</code> if the peer has correctly leave the overlay.
     */
    public abstract Boolean leave();

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
     * @throws Exception
     *             this exception appears when a message cannot be send to a peer.
     */
    public abstract ResponseMessage sendMessageTo(Peer peer, Message msg) throws Exception;

    /**
     * Handles a {@link AddNeighborMessage}.
     * 
     * @param msg
     *            the message that is handled.
     * @return the {@link EmptyResponseMessage} response.
     */
    public abstract ActionResponseMessage handleAddNeighborMessage(AddNeighborMessage msg);

    /**
     * Handles a {@code JoinMessage}.
     * 
     * @param msg
     *            the message that is handled.
     * @return the {@link JoinResponseMessage} response.
     */
    public abstract JoinResponseMessage handleJoinMessage(Message msg);

    /**
     * Handles a {@link LeaveMessage}.
     * 
     * @param msg
     *            the message that is handled.
     * @return the {@link EmptyResponseMessage} response.
     */
    public abstract ActionResponseMessage handleLeaveMessage(LeaveMessage msg);

    /**
     * Handles a {@link LookupMessage}.
     * 
     * @param msg
     *            the lookup message that is handled.
     * @return the {@link LookupResponseMessage} response.
     */
    public abstract LookupResponseMessage handleLookupMessage(LookupMessage msg);

    /**
     * Handles a {@link PingMessage}.
     * 
     * @param msg
     *            the message that is handled.
     * @return the {@link PingResponseMessage} response.
     */
    public ResponseMessage handleMessage(PingMessage msg) {
        return new ResponseMessage(msg.getCreationTimestamp());
    }

    /**
     * Handles a {@link CANRemoveNeighborMessage}.
     * 
     * @param msg
     *            the message that is handled.
     * @return the {@link ActionResponseMessage} response.
     */
    public abstract ActionResponseMessage handleRemoveNeighborMessage(RemoveNeighborMessage msg);

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
