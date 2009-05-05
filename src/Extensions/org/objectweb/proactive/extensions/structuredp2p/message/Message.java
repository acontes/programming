package org.objectweb.proactive.extensions.structuredp2p.message;

import java.io.Serializable;

import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * A message is used for each kind of message that can be sent to an another peer.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public abstract class Message implements Serializable {

    /**
     * Timestamp of the creation of the message.
     */
    private long creationTimestamp;

    /**
     * Constructor.
     */
    public Message() {
        this.creationTimestamp = System.currentTimeMillis();
    }

    /**
     * Handles the message.
     * 
     * @param overlay
     *            the overlay which handles the message.
     * @return the response in agreement with the type of message sent.
     */
    public abstract ResponseMessage handle(StructuredOverlay overlay);

    /**
     * Returns the timestamp of the creation of the message.
     * 
     * @return the timestamp of the creation of the message.
     */
    public long getCreationTimestamp() {
        return this.creationTimestamp;
    }
}
