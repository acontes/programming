package org.objectweb.proactive.extensions.structuredp2p.response;

import org.objectweb.proactive.extensions.structuredp2p.message.can.CANMergeMessage;


/**
 * Defines a response for the {@link CANMergeMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANMergeResponseMessage extends ResponseMessage {

    /**
     * Constructor.
     * 
     * @param timestampMessageCreation
     *            the timestamp indicating the time creation of the message which has been sent.
     */
    public CANMergeResponseMessage(long timestampMessageCreation) {
        super(timestampMessageCreation);
    }

}
