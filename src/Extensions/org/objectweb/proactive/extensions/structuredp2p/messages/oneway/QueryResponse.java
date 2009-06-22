package org.objectweb.proactive.extensions.structuredp2p.messages.oneway;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;


/**
 * A {@link QueryResponse} is the response associated to a {@link Query}. The response contains some
 * crutial information like :
 * <ul>
 * <li>the latency in ms.
 * <li>the number of steps for the send.
 * <li>the number of steps for the reception.
 * </ul>
 * 
 * @author Pellegrino Laurent
 */
@SuppressWarnings("serial")
public class QueryResponse extends Query {

    /**
     * The number of steps in order to reach the peer which manage the good key from the peer which
     * has sent the query.
     */
    private int nbStepsForSend;

    /**
     * The remote peer which has been found.
     */
    private Peer remotePeerFound;

    /**
     * Constructor.
     */
    public QueryResponse() {

    }

    /**
     * Constructor.
     * 
     * @param query
     *            the query associated to this response.
     * @param remotePeerFound
     *            the remote peer which has been found.
     */
    public QueryResponse(Query query, Peer remotePeerFound) {
        super(query.getKeyToFind(), query.getKeyFromSender(), query.getKeyFromSender(), query
                .getCreationTimestamp(), query.getUUID());
        this.remotePeerFound = remotePeerFound;
        this.nbStepsForSend = query.getNbSteps();
    }

    /**
     * Handles the current query by delegation.
     * 
     * @param overlay
     *            the overlay to use in order to handle the query.
     */
    public void handle(StructuredOverlay overlay) {
        overlay.handleQueryResponse(this);
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
     * Returns the latency (in milliseconds) between the moment of the creation of the message and
     * when the response has been received.
     * 
     * @return the latency between the moment of the creation of the message and when the response
     *         has been received.
     */
    public int getLatency() {
        if (super.latency < 0) {
            throw new IllegalStateException("The response has not been receive from network after a query.");
        }
        return super.latency;
    }

    /**
     * Returns the remote peer which has been found.
     * 
     * @return the remote peer which has been found.
     */
    public Peer getRemotePeerFound() {
        return this.remotePeerFound;
    }
}
