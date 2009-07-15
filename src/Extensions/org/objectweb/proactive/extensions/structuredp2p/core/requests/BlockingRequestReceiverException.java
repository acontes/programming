package org.objectweb.proactive.extensions.structuredp2p.core.requests;

import org.objectweb.proactive.core.ProActiveRuntimeException;


/**
 * @author Pellegrino Laurent
 * @version 0.1, 07/09/2009
 */
@SuppressWarnings("serial")
public class BlockingRequestReceiverException extends ProActiveRuntimeException {

    public BlockingRequestReceiverException() {
        super();
    }

    public BlockingRequestReceiverException(String objectName) {
        super(objectName + " cannot receive request any more.");
    }
}
