package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CanResponseMessage;


/**
 * 
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
public class CanMessage implements Message {

    private Coordinate coordinate[];

    /**
     * 
     * @param cord
     */
    public CanMessage(Coordinate cord[]) {

    }

    @Override
    public CanResponseMessage handle(Peer peer) {
        // TODO Auto-generated method stub
        return null;

    }

}
