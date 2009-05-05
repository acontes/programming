package org.objectweb.proactive.extensions.structuredp2p.message.response;

/**
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
     * @param isSuccess
     *            indicates if the join has succeeded.
     */
    public JoinResponseMessage(boolean isSuccess) {
        super();
        this.isSuccess = isSuccess;
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
