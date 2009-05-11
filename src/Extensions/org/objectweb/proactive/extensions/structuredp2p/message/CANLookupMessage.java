package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.LookupResponseMessage;


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
public class CANLookupMessage extends LookupMessage {

    /**
     * Constructor.
     * 
     * @param coordinates
     *            the coordinates the message must reached.
     * 
     */
    public CANLookupMessage(Coordinate[] coordinates) {
        super(new Key<Coordinate[]>(coordinates));
    }

    /**
     * Returns the coordinates that the message must reached.
     * 
     * @return the coordinates that the message must reached.
     */
    public Coordinate[] getCoordinates() {
        return (Coordinate[]) super.getKey().getValue();
    }

    /**
     * Handles message by delegation.
     * 
     * @param a
     *            peer to which the message will be sent.
     * @return a CanResponseMessage for routing.
     */
    public LookupResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleLookupMessage(this);
    }

}
