package org.objectweb.proactive.extensions.structuredp2p.message.response;

/**
 * A response message is the appropriate answer to the message.
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
     */
    public CANMergeResponseMessage(boolean merged) {
        super(merged);
    }

}
