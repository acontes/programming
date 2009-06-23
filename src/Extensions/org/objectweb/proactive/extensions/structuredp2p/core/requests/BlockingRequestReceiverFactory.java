package org.objectweb.proactive.extensions.structuredp2p.core.requests;

import org.objectweb.proactive.core.body.request.RequestReceiver;
import org.objectweb.proactive.core.body.request.RequestReceiverFactory;


/**
 * @author Pellegrino Laurent
 */
public class BlockingRequestReceiverFactory implements RequestReceiverFactory {

    /**
     * {@inheritDoc}
     */
    public RequestReceiver newRequestReceiver() {
        return new BlockingRequestReceiver();
    }

}
