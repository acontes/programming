package org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous;

import java.io.Serializable;


/**
 * A response message is a representation of an empty response. It contains the round-trip time for
 * the sent of the message for which we answer. All responses must extend this class.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public interface ResponseMessage extends Serializable {
}
