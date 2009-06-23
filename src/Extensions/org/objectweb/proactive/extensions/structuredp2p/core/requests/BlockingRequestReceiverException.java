package org.objectweb.proactive.extensions.structuredp2p.core.requests;

import org.objectweb.proactive.core.ProActiveRuntimeException;


@SuppressWarnings("serial")
public class BlockingRequestReceiverException extends ProActiveRuntimeException {

    public BlockingRequestReceiverException(String objectName) {
        super(objectName + " cannot receive request any more.");
    }
}
