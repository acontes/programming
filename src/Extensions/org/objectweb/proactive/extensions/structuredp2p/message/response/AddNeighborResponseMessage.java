package org.objectweb.proactive.extensions.structuredp2p.message.response;

import org.objectweb.proactive.extensions.structuredp2p.message.UpdateMessage;


/**
 * Defines a response for the {@link UpdateMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public abstract class AddNeighborResponseMessage extends ResponseMessage {
    /**
     * Constructor.
     * 
     * @param timestampMessageCreation
     *            the timestamp indicating the time creation of the message which has been sent.
     */
    public AddNeighborResponseMessage(long timestampMessageCreation) {
        super(timestampMessageCreation);
    }
}
