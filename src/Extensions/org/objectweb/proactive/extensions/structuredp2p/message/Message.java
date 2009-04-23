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
public interface Message extends Serializable {

    /**
     * Handles the message.
     * 
     * @param peer
     * @return a response message.
     */
    public ResponseMessage handle(StructuredOverlay overlay);

}
