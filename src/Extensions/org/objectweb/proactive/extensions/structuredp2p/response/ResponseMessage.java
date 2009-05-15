package org.objectweb.proactive.extensions.structuredp2p.response;

import java.io.Serializable;


/**
 * A response message is an abstract representation of a response. It contains the round-trip time
 * for the sent of the message for which we answer. All responses must extend this class.
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
    public long timestampMessageCreation;

    /**
     * The round trip time.
     */
    public int latency = 0;

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
            throw new IllegalStateException("The response message must be initialize by the sent function !");
        }
        return this.latency;
    }
}
