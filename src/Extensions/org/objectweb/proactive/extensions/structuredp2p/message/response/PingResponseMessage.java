package org.objectweb.proactive.extensions.structuredp2p.message.response;

/**
 * A ping response message is the appropriate answer to a ping message.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class PingResponseMessage extends ResponseMessage {
    // FIXME peut-etre ne pas mettre la latence mais le timestamp de depart du message.
    private final int latency = 0;

    public PingResponseMessage() {

    }

    public int getLatency() {
        return latency;
    }
}
