package org.objectweb.proactive.extensions.structuredp2p.core.overlay;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.AddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.Message;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.RemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can.CANRemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.AbstractQuery;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.Query;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.QueryResponse;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.QueryResponseEntry;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ActionResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.JoinResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ResponseMessage;


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
     * The timeout in milliseconds to wait before checking the network with the call of
     * {@link StructuredOverlay#update()}.
     */
    public static final int UPDATE_TIMEOUT = 500;

    /**
     * Queries which are bufferized when a {@link Peer#sendTo(Peer, Message)} is not accepted.
     */
    private List<Query> bufferizedQueries = new Vector<Query>();

    /**
     * Responses associated to the oneWay search on the network.
     */
    private Map<UUID, QueryResponseEntry> oneWayResponses = new HashMap<UUID, QueryResponseEntry>();

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
    public void bufferizeQuery(Query query) {
        this.bufferizedQueries.add(query);
    }

    /**
     * Returns the queries that are bufferized.
     * 
     * @return the queries that are bufferized.
     */
    public List<Query> getBufferizedQueries() {
        return this.bufferizedQueries;
    }

    /**
     * Sends a {@link Query} on the network from the current peer.
     * 
     * @param query
     *            the query to send.
     * @return the response in agreement with the type of query sent.
     */
    public QueryResponse search(Query query) {
        // TODO To check the type of the query with the peer type
        UUID uuid = UUID.randomUUID();
        query.setUUID(uuid);
        this.send(query);

        synchronized (this.oneWayResponses) {
            while (this.oneWayResponses.get(uuid) == null) {
                try {
                    this.oneWayResponses.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        QueryResponse response = this.oneWayResponses.get(uuid).getQueryResponse();
        this.oneWayResponses.remove(uuid);

        return response;
    }

    /**
     * Returns the oneWay responses.
     * 
     * @return the oneWay responses.
     */
    public Map<UUID, QueryResponseEntry> getOneWayResponses() {
        return this.oneWayResponses;
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
    public abstract ResponseMessage handleJoinMessage(Message msg);

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
     * Sends a {@link AbstractQuery} on the network without response.
     * 
     * @param query
     *            the query to send.
     */
    public abstract void send(Query query);

    /**
     * Send a {@link Message} to a known {@link Peer}.
     * 
     * @param remotePeer
     *            the remote peer to which we want to send the message.
     * @param msg
     *            the message to send.
     * 
     * @return the response in agreement with the type of message sent.
     */
    public ResponseMessage sendTo(Peer remotePeer, Message msg) {
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
            for (Query query : this.getBufferizedQueries()) {
                this.getBufferizedQueries().remove(query);
                this.send(query);
            }
        }
    }
}
