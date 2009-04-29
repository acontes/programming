package org.objectweb.proactive.extensions.structuredp2p.message.response;

import java.io.Serializable;


//FIXME
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
public abstract class ResponseMessage implements Serializable {
    private boolean isNull = false;

    /**
     * Constructor.
     */
    public ResponseMessage() {

    }

    /**
     * FIXME
     * 
     * @param isNull
     */
    public ResponseMessage(boolean isNull) {
        this.isNull = isNull;
    }

    /**
     * FIXME
     * 
     * @return
     */
    public boolean isNull() {
        return this.isNull;
    }
}
