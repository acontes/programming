package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * 
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class LoadBalancingMessage implements Message {

    /**
     * Constructor.
     */
    public LoadBalancingMessage() {
    }

    @Override
    public ResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleLoadBalancingMessage(this);
    }
}
