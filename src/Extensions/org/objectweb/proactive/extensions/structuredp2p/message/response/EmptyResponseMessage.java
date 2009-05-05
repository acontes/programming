package org.objectweb.proactive.extensions.structuredp2p.message.response;

/**
 * Defines an empty response.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class EmptyResponseMessage extends ResponseMessage {

    /**
     * Constructor.
     * 
     * @param timestampResponseMessage
     *            the timestamp indicating the time creation of the message which has been sent.
     */
    public EmptyResponseMessage(long timestampResponseMessage) {
        super(timestampResponseMessage);
    }

}
