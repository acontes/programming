package org.objectweb.proactive.extensions.structuredp2p.core.requests;

import java.io.Serializable;

import org.objectweb.proactive.core.body.request.RequestReceiver;
import org.objectweb.proactive.core.body.request.RequestReceiverFactory;


/**
 * @author Pellegrino Laurent
 * @version 0.1, 07/09/2009
 */
@SuppressWarnings("serial")
public class BlockingRequestReceiverFactory implements RequestReceiverFactory, Serializable {

    public BlockingRequestReceiverFactory() {

    }

    /**
     * {@inheritDoc}
     */
    public RequestReceiver newRequestReceiver() {
        return new BlockingRequestReceiver();
    }

}
