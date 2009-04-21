package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CanResponseMessage;


/**
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CanMessage implements Message {

    private Coordinate coordinate[];

    /**
     * create a new CanMessage with it cordinates
     * @param cord : cordinates of the peer to which the message will be send
     */
    public CanMessage(Coordinate cord[]) {

    }

    /**
     * @param a
     *            peer to which the message will be send
     * @return a CanResponseMessage for routing
     */
    public CanResponseMessage handle(Peer peer) {
    
        return peer.handleCanMessage(this);
    }
   /**
    * return a coordinate of a peer to which the message will be send
    * @return a coordinate 
    */
    public Coordinate[] getCoordinate() {
        return coordinate;
    }
    /**
     * set the coordinate
     * @param coordinate
     */
    public void setCoordinate(Coordinate[] coordinate) {
        this.coordinate = coordinate;
    }
    
    

}
