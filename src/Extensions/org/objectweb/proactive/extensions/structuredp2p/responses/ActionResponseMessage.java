package org.objectweb.proactive.extensions.structuredp2p.responses;

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
     * Indicates the status of the action : succeeded or not.
     */
    private boolean success = false;

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
     * Indicates if the action has succeeded.
     * 
     * @return <code>true</code> if the action has succeeded, <code>false</code> otherwise.
     */
    public boolean hasSucceeded() {
        return this.success;
    }

}
