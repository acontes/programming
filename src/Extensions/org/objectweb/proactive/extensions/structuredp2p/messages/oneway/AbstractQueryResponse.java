package org.objectweb.proactive.extensions.structuredp2p.messages.oneway;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;


/**
 * An <code>AbstractQueryResponse</code> is an abstract response associated to an abstract
 * {@link AbstractQuery}. In this kind of message (oneway) a response is too a query because the
 * response is sent to reach a key step by step. A response contains some crucial information like :
 * <ul>
 * <li>the latency in ms;
 * <li>the number of steps for the send;
 * <li>the number of steps for the reception;
 * <li>the original key to found.
 * </ul>
 * 
 * @author Laurent Pellegrino
 * @version 0.1, 08/05/2009
 */
@SuppressWarnings("serial")
public abstract class AbstractQueryResponse<K> extends AbstractQuery<K> implements QueryResponse {

    private long deliveryTimestamp;

    private int latency = -1;

    private int nbStepsForSend = 0;

    private K[] originalKeyToFind;

    public AbstractQueryResponse() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param query
     *            the original query which led to the creation of this response.
     * @param keyToReach
     *            the key used in order to route the response to it recipient.
     */
    public AbstractQueryResponse(AbstractQuery<K> query, K[] keyToReach) {
        super(query.getUUID(), keyToReach, query.getDispatchTimestamp());
        this.nbStepsForSend = query.getNbSteps();
    }

    /**
     * Returns the timestamp of the delivery.
     * 
     * @return the timestamp of the delivery.
     */
    public long getDeliveryTimestamp() {
        return this.deliveryTimestamp;
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
     * Returns the latency (in milliseconds) between the moment of the creation of the message and
     * when the response has been received.
     * 
     * @return the latency between the moment of the creation of the message and when the response
     *         has been received.
     */
    public int getLatency() {
        if (this.latency < 0) {
            throw new IllegalStateException("The response has not been receive from network after a query.");
        }
        return this.latency;
    }

    /**
     * Returns the number of steps performed in order to reach the sender of the query from the peer
     * which has been found.
     * 
     * @return the number of steps performed in order to reach the sender of the query from the peer
     *         which has been found.
     */
    public int getNbStepsForReceipt() {
        return super.getNbSteps();
    }

    /**
     * Returns the number of steps in order to reach the peer which manage the good key from the
     * peer which has sent the query.
     * 
     * @return the number of steps in order to reach the peer which manage the good key from the
     *         peer which has sent the query.
     */
    public int getNbStepsForSend() {
        return this.nbStepsForSend;
    }

    /**
     * Returns the original key to find.
     * 
     * @return the original key to find.
     */
    public K[] getOriginalKeyToFind() {
        return this.originalKeyToFind;
    }

    /**
     * Returns the total number of steps : number of steps for send the query + number of steps for
     * receipt the response.
     * 
     * @return the total number of steps.
     */
    public int getTotalNumberOfSteps() {
        return this.getNbStepsForSend() + this.getNbStepsForReceipt();
    }

    /**
     * Sets the delivery time of the response (ie. when the response has been receive). The latency
     * is automatically calculated.
     */
    public void setDeliveryTime() {
        this.deliveryTimestamp = System.currentTimeMillis();
        this.latency = (int) (this.deliveryTimestamp - super.getDispatchTimestamp());

        if (this.latency < 0) {
            this.latency = 0;
        }
    }

}
