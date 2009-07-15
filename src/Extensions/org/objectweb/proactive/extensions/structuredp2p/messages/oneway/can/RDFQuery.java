package org.objectweb.proactive.extensions.structuredp2p.messages.oneway.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.messages.Key;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query;


/**
 * An RDF query is a query used in order to retrieve data from peer by Resource Description
 * Framework criteria which is a method for conceptual description or modeling of information that
 * is implemented in web resources.
 * 
 * @author Pellegrino Laurent
 */
@SuppressWarnings("serial")
public class RDFQuery extends Query {

    /**
     * Constructor.
     * 
     * @param sender
     *            the peer which sent the query.
     * 
     * @param coordinatesToFind
     *            the coordinates to reach.
     */
    public RDFQuery(Peer sender, Coordinate[] coordinatesToFind) {
        super(new Key<Coordinate[]>(coordinatesToFind), new Key<Coordinate[]>(((CANOverlay) sender
                .getStructuredOverlay()).getZone().getCoordinatesMin()));
    }

    /**
     * {@inheritDoc}
     */
    public void handle(StructuredOverlay overlay) {
        overlay.handleQuery(this);
    }

    /**
     * Returns the coordinates to reach.
     * 
     * @return the coordinates to reach.
     */
    public Coordinate[] getCoordinatesToReach() {
        return (Coordinate[]) super.getKeyToReach().getValue();
    }

    /**
     * Returns the coordinates managed by the peer sending the query.
     * 
     * @return the coordinates managed by the peer sending the query.
     */
    public Coordinate[] getCoordinatesFromSender() {
        return (Coordinate[]) super.getKeyFromSender().getValue();
    }
}
