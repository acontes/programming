package org.objectweb.proactive.extensions.structuredp2p.responses.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.messages.Key;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.LookupResponseMessage;


/**
 * Defines a response for the {@link CANLookupMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANLookupResponseMessage extends LookupResponseMessage {

    /**
     * Constructor.
     * 
     * @param timestampCreationMessage
     *            the timestamp indicating the time creation of the message which has been sent.
     * @param peer
     *            the searched peer.
     * @param coordinates
     *            the searched coordinates.
     */
    public CANLookupResponseMessage(long timestampCreationMessage, Peer peer, Coordinate[] coordinates) {
        super(timestampCreationMessage, new Key<Coordinate[]>(coordinates), peer);
    }

    /**
     * Returns the coordinates used in order to find the peer.
     * 
     * @return the coordinates used in order to find the peer.
     */
    public Coordinate[] getCoordinates() {
        return (Coordinate[]) this.getKey().getValue();
    }
}
