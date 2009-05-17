package org.objectweb.proactive.extensions.structuredp2p.messages.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.Key;
import org.objectweb.proactive.extensions.structuredp2p.messages.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.LookupResponseMessage;


/**
 * A {@code CANLookupMessage} is used in order to find a peer on a network. In response the caller
 * will receive a {@link LookupResponseMessage} that contains the {@link Peer} that has been found.
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
     * {@inheritDoc}
     */
    public LookupResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleLookupMessage(this);
    }

}
