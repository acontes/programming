package org.objectweb.proactive.extensions.structuredp2p.messages;

import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.responses.LoadBalancingResponseMessage;


/**
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class LoadBalancingMessage extends Message {

    /**
     * Constructor.
     */
    public LoadBalancingMessage() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public LoadBalancingResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleLoadBalancingMessage(this);
    }
}
