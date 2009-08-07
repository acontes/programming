package org.objectweb.proactive.extensions.structuredp2p.messages.oneway;

import java.io.Serializable;
import java.util.UUID;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;


/**
 * An <code>AbstractQuery</code> is an abstraction of a query that can be sent on a structured
 * peer-to-peer network in order to find some data by a key which is an array of type <code>K</code>
 * . In response an object of type {@link AbstractQueryResponse} is returned with the data searched.
 * <p>
 * A query is performed step by step with "oneWay" method. So, we can't say when the response will
 * be returned. Suppose that the peer A is sending a query in order to reach the peer B managing the
 * key <code>keyToReach</code>. The first step consists in setting the <code>keyToReach</code> to
 * <code>keyToFound</code>. After that the query is sending gradually until the peer managing the
 * <code>keyToReach</code> is found. When it is found, the <code>keyToReach</code> change for
 * <code>keyFromSender</code>. At this time the response is routed to the sender in the opposite
 * direction without necessarily using the same route. This last point depends on the concrete type
 * of query which can implement the desired behavior.
 * 
 * @author Laurent Pellegrino
 * @version 0.3, 08/05/2009
 * 
 * @see Query
 * @see AbstractQueryResponse
 */
@SuppressWarnings("serial")
public abstract class AbstractQuery<K> implements Query, Serializable {

    /**
     * Timestamp of the creation of the message.
     */
    private long dispatchTimestamp = System.currentTimeMillis();

    /**
     * The key used in order to route the query on the network.
     */
    private K[] keyToReach;

    /**
     * The number of steps that have been performed in order to reach the peer managing the key.
     */
    private int nbSteps = 0;

    /**
     * Universally unique identifier used in order to identify the response.
     */
    private UUID uuid;

    /**
     * Constructor.
     */
    public AbstractQuery() {
    }

    /**
     * Constructor.
     * 
     * @param keyToFind
     *            the key to find.
     * @param keyFromSender
     *            a key that the sender manages.
     */
    public AbstractQuery(K[] keyToFind) {
        this.keyToReach = keyToFind;
    }

    /**
     * Constructor.
     * 
     * @param uuid
     *            the universally unique identifier associated to the query.
     * @param keyToReach
     *            the key to reach.
     * @param timestampOfDispatch
     *            the dispatch timestamp of the query.
     */
    public AbstractQuery(UUID uid, K[] keyToReach, long dispatchTimestamp) {
        this.keyToReach = keyToReach;
        this.dispatchTimestamp = dispatchTimestamp;
        this.uuid = uid;
    }

    /**
     * {@inheritDoc}
     */
    public abstract void handle(StructuredOverlay overlay);

    /**
     * {@inheritDoc}
     */
    public abstract void route(StructuredOverlay overlay, Query query);

    /**
     * {@inheritDoc}
     */
    public abstract void route(StructuredOverlay overlay);

    /**
     * {@inheritDoc}
     */
    public abstract boolean validKeyConstraints(StructuredOverlay overlay);

    /**
     * {@inheritDoc}
     */
    public long getDispatchTimestamp() {
        return this.dispatchTimestamp;
    }

    /**
     * Returns the key to reach.
     * 
     * @return the key to reach.
     */
    public K[] getKeyToReach() {
        return this.keyToReach;
    }

    /**
     * {@inheritDoc}
     */
    public int getNbSteps() {
        return this.nbSteps;
    }

    /**
     * {@inheritDoc}
     */
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * {@inheritDoc}
     */
    public void incrementNbStepsBy(int increment) {
        this.nbSteps += increment;
    }

    /**
     * Sets the key to reach, which is used in order to perform a lookup by query on the network.
     * 
     * @param keyToReach
     *            the new key to reach.
     */
    public void setKeyToReach(K[] keyToReach) {
        this.keyToReach = keyToReach;
    }

    /**
     * {@inheritDoc}
     */
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

}
