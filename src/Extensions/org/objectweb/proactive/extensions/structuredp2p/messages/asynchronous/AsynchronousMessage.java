package org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous;

import java.io.Serializable;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ResponseMessage;


/**
 * @author Pellegrino Laurent
 */
public interface AsynchronousMessage extends Serializable {

    public abstract ResponseMessage handle(StructuredOverlay overlay);

}
