package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CanResponseMessage;


/**
 * A CAN Message is used in order to find a peer on a network. In response the caller will receive a
 * CanResponseMessage that contains the peer that has been found.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CanMessage implements Message {

    /**
     * Coordinates of the peer we lookup.
     */
    private final Coordinate coordinates[];

    /**
     * Constructor.
     * 
     * @param coordinates
     *            coordinates of the peer we lookup on the network.
     */
    public CanMessage(Coordinate coordinates[]) {
        this.coordinates = coordinates;
    }

    /**
     * Handles message by delegation.
     * 
     * @param a
     *            peer to which the message will be sent.
     * @return a CanResponseMessage for routing.
     */
    public CanResponseMessage handle(Peer peer) {

        return peer.handleCanMessage(this);
    }

    /**
     * Returns the coordinates of a peer we lookup.
     * 
     * @return the coordinates of a peer we lookup.
     */
    public Coordinate[] getCoordinates() {
        return this.coordinates;
    }

}
