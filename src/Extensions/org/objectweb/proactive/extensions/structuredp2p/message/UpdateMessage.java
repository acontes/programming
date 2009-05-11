package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * A CANUpdateMessage is a concrete message to update a CAN peer.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public abstract class UpdateMessage extends Message {
    /**
     * Handles a {@link UpdateMessage} by double dispatch.
     * 
     * @param overlay
     *            the overlay which handle the message.
     */
    public abstract ResponseMessage handle(StructuredOverlay overlay);
}
