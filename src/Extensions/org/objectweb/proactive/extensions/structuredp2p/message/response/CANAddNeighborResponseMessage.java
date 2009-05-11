package org.objectweb.proactive.extensions.structuredp2p.message.response;

import org.objectweb.proactive.extensions.structuredp2p.message.CANUpdateMessage;


/**
 * Defines a response for the {@link CANUpdateMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANAddNeighborResponseMessage extends AddNeighborResponseMessage {

    /**
     * Constructor.
     * 
     * @param timestampMessageCreation
     *            the timestamp indicating the time creation of the message which has been sent.
     */
    public CANAddNeighborResponseMessage(long timestampMessageCreation) {
        super(timestampMessageCreation);
    }

}
