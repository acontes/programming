package org.objectweb.proactive.extensions.structuredp2p.message;

import java.io.Serializable;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * A message is a interface for each kind of message
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
public interface Message extends Serializable {

    /**
     * 
     * @param peer
     * @return a response message
     */
    public ResponseMessage handle(Peer peer);

}
