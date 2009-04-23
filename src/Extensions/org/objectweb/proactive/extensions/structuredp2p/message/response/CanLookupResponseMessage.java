package org.objectweb.proactive.extensions.structuredp2p.message.response;

import org.objectweb.proactive.extensions.structuredp2p.core.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.Key;


/**
 * A chord response message gives a CAN peer for routing.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CanLookupResponseMessage extends LookupResponseMessage {

    /**
     * 
     * @param peer
     */
    public CanLookupResponseMessage(Peer peer, Coordinate[] coordinates) {
        super(new Key<Coordinate[]>(coordinates), peer);
    }

    /**
     * FIXME
     */
    public CanLookupResponseMessage() {
        super(true);
    }

    /**
     * FIXME
     * 
     * @return
     */
    public Coordinate[] getCoordinates() {
        return (Coordinate[]) this.getKey().getValue();
    }
}
