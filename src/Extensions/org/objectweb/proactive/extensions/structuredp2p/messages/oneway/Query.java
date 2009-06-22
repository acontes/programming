package org.objectweb.proactive.extensions.structuredp2p.messages.oneway;

import java.io.Serializable;
import java.util.UUID;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.Key;


/**
 * A query is a message which is sent on aF network in order to find a {@link Peer} which manage a
 * {@link Key} contained by the {@link Peer} which send the query. In response, it is given some
 * information that the peer found has, by an object of type {@link QueryResponse}.
 * <p>
 * A query is performed step by step with "oneWay" method. So, we can't say when the response will
 * be returned. Suppose that the peer A is sending a query in order to reach the peer B managing the
 * key <code>keyToReach</code>. The first step consists in setting the <code>keyToReach</code> to
 * <code>keyToFound</code>. After that the query is sending gradually until the peer managing the
 * <code>keyToReach</code> is found. When it is found, the <code>keyToReach</code> change for
 * <code>keyFromSender</code>. At this time the response is routed to the sender in the opposite
 * direction.
 * 
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public abstract class Query implements Serializable {

    /**
     * Universally unique identifier used in order to identify the response.
     */
    private UUID uuid;

    /**
     * The key used in order to route the query on the network.
     */
    private Key<?> keyToReach;

    /**
     * The criteria which is looked up on the network.
     */
    private Key<?> keyToFind;

    /**
     * The key which is managed by the sender in order to send the response when the keyToReach has
     * been reached.
     */
    private Key<?> keyFromSender;

    /**
     * Timestamp of the creation of the message.
     */
    private long creationTimestamp = System.currentTimeMillis();

    /**
     * The number of steps that have been performed in order to reach the peer managing the key.
     */
    private int nbSteps = 0;

    /**
     * The round trip time in milliseconds.
     */
    public int latency = -1;

    /**
     * Constructor.
     */
    public Query() {
    }

    /**
     * Constructor.
     * 
     * @param keyToFind
     *            the key to find.
     * @param keyFromSender
     *            a key that the sender manages.
     */
    public Query(Key<?> keyToFind, Key<?> keyFromSender) {
        this.keyToFind = keyToFind;
        this.keyToReach = keyToFind;
        this.keyFromSender = keyFromSender;
    }

    /**
     * Constructor.
     * 
     * @param keyToFind
     *            the key to find.
     * @param keyToReach
     *            the key to reach.
     * @param keyFromSender
     *            a key that the sender manages.
     */
    public Query(Key<?> keyToFind, Key<?> keyToReach, Key<?> keyFromSender, long creationTimestamp, UUID uid) {
        this.keyToFind = keyToFind;
        this.keyToReach = keyToReach;
        this.keyFromSender = keyFromSender;
        this.creationTimestamp = creationTimestamp;
        this.uuid = uid;
    }

    /**
     * Handles the query.
     * 
     * @param overlay
     *            the overlay used in order to handle the query by delegation.
     */
    public abstract void handle(StructuredOverlay overlay);

    /**
     * Increments the number of steps by one.
     */
    public void incrementNbSteps() {
        this.nbSteps++;
    }

    /**
     * Returns the timestamp of the creation of the message.
     * 
     * @return the timestamp of the creation of the message.
     */
    public long getCreationTimestamp() {
        return this.creationTimestamp;
    }

    /**
     * Returns the number of steps that have been performed in order to reach the response.
     * 
     * @return the number of steps that have been performed in order to reach the response.
     */
    public int getNbSteps() {
        return this.nbSteps;
    }

    /**
     * Returns the universally unique identifier used in order to identify the response.
     * 
     * @return the universally unique identifier used in order to identify the response.
     */
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * Returns the criteria which is looked up on the network.
     * 
     * @return the criteria which is looked up on the network.
     */
    public Key<?> getKeyToFind() {
        return this.keyToFind;
    }

    /**
     * Returns the key used in order to perform a lookup by query on the network.
     * 
     * @return the key used in order to perform a lookup by query on the network.
     */
    public Key<?> getKeyToReach() {
        return this.keyToReach;
    }

    /**
     * Returns the key which is managed by the sender in order to send the response when the
     * keyToReach has been reached.
     * 
     * @return the key which is managed by the sender in order to send the response when the
     *         keyToReach has been reached.
     */
    public Key<?> getKeyFromSender() {
        return this.keyFromSender;
    }

    /**
     * Sets the delivery time of the response (ie. when the response has been receive). The latency
     * is automatically calculated.
     */
    public void setDeliveryTime() {
        this.latency = (int) (System.currentTimeMillis() - this.creationTimestamp);

        if (this.latency < 0) {
            this.latency = 0;
        }
    }

    /**
     * Sets the key to reach, which is used in order to perform a lookup by query on the network.
     * 
     * @param keyToReach
     *            the new key to reach.
     */
    public void setKeyToReach(Key<?> keyToReach) {
        this.keyToReach = keyToReach;
    }

    /**
     * Sets the universally unique identifier used in order to identify the response.
     * 
     * @param uid
     *            the new uid to set.
     */
    public void setUUID(UUID uid) {
        this.uuid = uid;
    }
}
