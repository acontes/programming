package org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous;

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
public class ActionResponseMessage implements ResponseMessage {

    /**
     * Indicates the status of the action : succeeded or not.
     */
    private boolean success = false;

    /**
     * Constructor.
     * 
     * Indicates if the neighbor has been correctly removed.
     */
    public ActionResponseMessage(boolean success) {
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
