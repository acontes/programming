package org.objectweb.proactive.extensions.structuredp2p.message.response;

/**
 * Defines an abstract class for {@link JoinResponseMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public abstract class JoinResponseMessage extends ResponseMessage {

    private Boolean isSuccess = false;

    /**
     * Constructor.
     * 
     * @param timestampCreationMessage
     *            the timestamp indicating the time creation of the message which has been sent.
     */
    public JoinResponseMessage(long timestampCreationMessage, boolean result) {
        super(timestampCreationMessage);
        this.isSuccess = new Boolean(result);
    }

    /**
     * Indicates if the join has succeeded.
     * 
     * @return true if the join has succeeded, false otherwise.
     */
    public Boolean hasSucceeded() {
        return this.isSuccess;
    }
}
