package org.objectweb.proactive.extensions.structuredp2p.response;

import org.objectweb.proactive.extensions.structuredp2p.message.can.CANJoinMessage;


/**
 * Defines a response for the {@link CANJoinMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANJoinResponseMessage extends JoinResponseMessage {

    /**
     * Constructor
     * 
     * @param timestampCreationMessage
     *            the timestamp indicating the time creation of the message which has been sent.
     */
    public CANJoinResponseMessage(long timestampCreationMessage, boolean result) {
        super(timestampCreationMessage, result);
    }
}
