package org.objectweb.proactive.extensions.structuredp2p.message.response;

/**
 * A ping response message is the appropriate answer to a ping message. It returns the latency
 * between the creation of the message and the time when we call for the first time
 * {@link #getLatency()}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class PingResponseMessage extends ResponseMessage {
    private final long startLatency = System.currentTimeMillis();
    private int latency = 0;

    /**
     * Constructor.
     */
    public PingResponseMessage() {
    }

    /**
     * Returns the latency between the creation of the message and when we call for th first time
     * this function.
     * 
     * @return the latency between the creation of the message and when we call for th first time
     *         this function.
     */
    public int getLatency() {
        if (latency == 0) {
            this.latency = (int) (System.currentTimeMillis() - this.startLatency);
        }

        return this.latency;
    }
}
