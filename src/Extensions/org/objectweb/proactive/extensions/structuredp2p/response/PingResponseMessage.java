package org.objectweb.proactive.extensions.structuredp2p.response;

import org.objectweb.proactive.extensions.structuredp2p.message.PingMessage;


/**
 * Defines a response for the {@link PingMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class PingResponseMessage extends ResponseMessage {
    /**
     * Constructor.
     * 
     * @param timestampMessageCreation
     *            the timestamp indicating the time creation of the message which has been sent.
     */
    public PingResponseMessage(long timestampMessageCreation) {
        super(timestampMessageCreation);
    }
}
