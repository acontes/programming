package org.objectweb.proactive.extensions.structuredp2p.responses;

import java.io.Serializable;


/**
 * A response message is a representation of an empty response. It contains the round-trip time for
 * the sent of the message for which we answer. All responses must extend this class.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class ResponseMessage implements Serializable {

    /**
     * The timestamp on which the message for which was created we create this response.
     */
    private long timestampMessageCreation;

    /**
     * The round trip time.
     */
    private int latency = 0;

    /**
     * Constructor.
     */
    public ResponseMessage() {
    }

    /**
     * Constructor.
     * 
     * @param timestampMessageCreation
     *            the timestamp on which the message for which was created we create this response.
     */
    public ResponseMessage(long timestampMessageCreation) {
        this.timestampMessageCreation = timestampMessageCreation;
    }

    /**
     * Sets the delivery time of the response (ie. when the response has been receive). The latency
     * is automatically calculated.
     */
    public void setDeliveryTime() {
        this.latency = (int) (System.currentTimeMillis() - this.timestampMessageCreation);
    }

    /**
     * Returns the latency (in milliseconds) between the moment of the creation of the message and
     * when the response has been received.
     * 
     * @return the latency between the moment of the creation of the message and when the response
     *         has been received.
     */
    public int getLatency() {
        if (this.latency == 0) {
            throw new IllegalStateException("The response message has not been initialized by callback.");
        }
        return this.latency;
    }
}
