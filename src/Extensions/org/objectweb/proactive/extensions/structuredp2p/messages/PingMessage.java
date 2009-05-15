package org.objectweb.proactive.extensions.structuredp2p.messages;

import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.responses.ResponseMessage;


/**
 * A PingMessage is a concrete message for ping.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class PingMessage extends Message {

    /**
     * Constructor.
     */
    public PingMessage() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public ResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleMessage(this);
    }
}
