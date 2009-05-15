package org.objectweb.proactive.extensions.structuredp2p.response;

/**
 * Defines a basic response for the {@link Message}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class ActionResponseMessage extends ResponseMessage {

    /**
     * Is the neighbor correctly removed.
     */
    private final boolean success;

    /**
     * Constructor.
     * 
     * @param timestampMessageCreation
     *            the timestamp indicating the time creation of the message which has been sent.
     * @param removed
     *            is the neighbor correctly removed.
     */
    public ActionResponseMessage(long timestampMessageCreation, boolean success) {
        super(timestampMessageCreation);
        this.success = success;
    }

    /**
     * Is the neighbor correctly removed.
     * 
     * @return the remove status.
     */
    public boolean hasSucceded() {
        return this.success;
    }

}
