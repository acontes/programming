package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ChordResponseMessage;


/**
 * 
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
public class ChordMessage implements Message {

    String id;

    /**
     * 
     * @param id
     */
    public ChordMessage(String id) {

    }

    @Override
    public ChordResponseMessage handle(Peer peer) {
        // TODO Auto-generated method stub
        return null;

    }

}
