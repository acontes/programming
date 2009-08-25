package org.objectweb.proactive.extensions.structuredp2p.core.overlay;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.objectweb.proactive.extensions.structuredp2p.api.messages.synchronous.Query;
import org.objectweb.proactive.extensions.structuredp2p.api.messages.synchronous.RDFQuery;
import org.objectweb.proactive.extensions.structuredp2p.api.messages.synchronous.RDFResponse;
import org.objectweb.proactive.extensions.structuredp2p.api.messages.synchronous.Response;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.AddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.AsynchronousMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.RemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can.CANRemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.SynchronousMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.SynchronousMessageEntry;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can.RDFQueryMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can.RDFTriplePatternQuery;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ActionResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.JoinResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.synchronous.can.RDFResponseMessage;


/**
 * Defines an abstract class that all structured peer-to-peer protocols must extend.
 * 
 * @author Alexandre Trovato
 * @author Fanny Kilanga
 * @author Laurent Pellegrino
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public abstract class StructuredOverlay implements Serializable {

    /**
     * The timeout in milliseconds to wait before checking the network with the call of
     * {@link StructuredOverlay#update()}.
     */
    public static final int UPDATE_TIMEOUT = 500;

    /**
     * Queries which are bufferized when a {@link Peer#sendTo(Peer, AsynchronousMessage)} is not
     * accepted.
     */
    private List<SynchronousMessage> bufferizedSynchronousMessages = new Vector<SynchronousMessage>();

    /**
     * Responses associated to the oneWay search on the network.
     */
    private Map<UUID, SynchronousMessageEntry> synchronousMessageEntries = new HashMap<UUID, SynchronousMessageEntry>();

    /**
     * The local peer which is associated with the overlay.
     */
    private Peer localPeer = null;

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
     * Bufferize a new query that will be performed at the time of the next checking.
     * 
     * @param query
     *            the query to bufferize.
     */
    public void bufferizeSynchronousMessage(SynchronousMessage query) {
        this.bufferizedSynchronousMessages.add(query);
    }

    /**
     * Returns the queries that are bufferized.
     * 
     * @return the queries that are bufferized.
     */
    public List<SynchronousMessage> getBufferizedQueries() {
        return this.bufferizedSynchronousMessages;
    }

    private RDFQueryMessage createSynchronousMessage(RDFQuery query) {
        // TODO Parse query in order to check type and retrieve informations

        return new RDFTriplePatternQuery();
    }

    /**
     * Sends a {@link Query} on the network from the current peer.
     * 
     * @param query
     *            the query to send.
     * @return the response in agreement with the type of query sent.
     */
    public Response search(Query query) {

        SynchronousMessage msg = null;

        if (query.getType().equals("RDFQuery")) {
            msg = this.createSynchronousMessage((RDFQuery) query);
        } else {
            throw new IllegalArgumentException("Unknown query type !");
        }

        msg = this.search(msg);

        if (query.getType().equals("RDFQuery")) {
            return new RDFResponse((RDFResponseMessage) msg);
        }

        return null;
    }

    public SynchronousMessage search(SynchronousMessage msg) {

        UUID uuid = UUID.randomUUID();
        msg.setUUID(uuid);
        this.send(msg);

        synchronized (this.synchronousMessageEntries) {
            while (this.synchronousMessageEntries.get(uuid) == null) {
                try {
                    this.synchronousMessageEntries.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        SynchronousMessage response = this.synchronousMessageEntries.get(uuid).getSynchronousMessage();
        this.synchronousMessageEntries.remove(uuid);

        return response;
    }

    /**
     * Returns the oneWay responses.
     * 
     * @return the oneWay responses.
     */
    public Map<UUID, SynchronousMessageEntry> getSynchronousMessageEntries() {
        return this.synchronousMessageEntries;
    }

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

    /**
     * Handles a {@link AddNeighborMessage}.
     * 
     * @param msg
     *            the message that is handled.
     * @return the {@link ActionResponseMessage} response.
     */
    public abstract ActionResponseMessage handleAddNeighborMessage(AddNeighborMessage msg);

    /**
     * Handles a {@code JoinMessage}.
     * 
     * @param msg
     *            the message that is handled.
     * @return the {@link JoinResponseMessage} response.
     */
    public abstract ResponseMessage handleJoinMessage(AsynchronousMessage msg);

    /**
     * Handles a {@link CANRemoveNeighborMessage}.
     * 
     * @param msg
     *            the message that is handled.
     * @return the {@link ActionResponseMessage} response.
     */
    public abstract ActionResponseMessage handleRemoveNeighborMessage(RemoveNeighborMessage msg);

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
     * @return <code>true</code> if the peer has correctly leave the overlay. <code>false</code>
     *         otherwise.
     */
    public abstract Boolean leave();

    /**
     * Sends a {@link SynchronousMessage} on the network without response.
     * 
     * @param query
     *            the synchronous message to send.
     */
    public abstract void send(SynchronousMessage query);

    /**
     * Send a {@link AsynchronousMessage} to a known {@link Peer}.
     * 
     * @param remotePeer
     *            the remote peer to which we want to send the message.
     * @param msg
     *            the message to send.
     * 
     * @return the response in agreement with the type of message sent.
     */
    public ResponseMessage sendTo(Peer remotePeer, AsynchronousMessage msg) {
        return this.localPeer.sendTo(remotePeer, msg);
    }

    /**
     * {@inheritDoc}
     */
    public abstract String toString();

    /**
     * Check the network in order to update it.
     */
    public void update() {
        if (this.getBufferizedQueries().size() > 0) {
            for (SynchronousMessage query : this.getBufferizedQueries()) {
                this.getBufferizedQueries().remove(query);
                this.send(query);
            }
        }
    }
}
