package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CanResponseMessage;


/**
 * A CanMessage is a concrete class for Can message
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CanMessage implements Message {

    private Coordinate coordinate[];

    /**
     * create a new CanMessage with coordinates
     * 
     * @param cord
     */
    public CanMessage(Coordinate cord[]) {

    }

    /**
     * 
     *@param peer
     *            the too which the message will be send
     *@return a CanResponseMessage for routing
     */
    public CanResponseMessage handle(Peer peer) {
        return new CanResponseMessage(peer);

    }

    /**
     * 
     * @return a coordinate of the peer to which the message will be send
     */

    public Coordinate[] getCoordinate() {
        return coordinate;
    }

    /**
     * Set the coordinate of the peer
     * 
     * @param coordinate
     */
    public void setCoordinate(Coordinate[] coordinate) {
        this.coordinate = coordinate;
    }

}
