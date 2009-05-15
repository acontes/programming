package org.objectweb.proactive.extensions.structuredp2p.responses;

import org.objectweb.proactive.extensions.structuredp2p.messages.LoadBalancingMessage;


/**
 * Defines a response for the {@link LoadBalancingMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class LoadBalancingResponseMessage extends ResponseMessage {

    /**
     * Constructor.
     * 
     * @param timestampCreationMessage
     *            the timestamp indicating the time creation of the message which has been sent.
     */
    public LoadBalancingResponseMessage(long timestampCreationMessage) {
        super(timestampCreationMessage);
    }
}
