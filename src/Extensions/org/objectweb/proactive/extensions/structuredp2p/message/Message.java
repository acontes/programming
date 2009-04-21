package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
public interface Message {

    /**
     * 
     * @param peer
     * @return
     */
    public ResponseMessage handle(Peer peer);

}
