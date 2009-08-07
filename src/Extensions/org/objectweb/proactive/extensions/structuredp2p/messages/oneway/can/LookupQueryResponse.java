package org.objectweb.proactive.extensions.structuredp2p.messages.oneway.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.AbstractQueryResponse;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query;


/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/05/2009
 */
@SuppressWarnings("serial")
public class LookupQueryResponse extends AbstractQueryResponse<Coordinate> {

    private Peer peerFound;

    private LookupQuery query;

    public LookupQueryResponse() {
        super();
    }

    public LookupQueryResponse(LookupQuery query, Peer peerFound) {
        super(query, query.getCoordinatesBelongToSender());
        System.out.println("LookupQueryResponse.LookupQueryResponse()");

        // this.query = new LookupQuery(peerFound, query.getCoordinatesBelongToSender());
        this.query = query;
        this.query.setKeyToReach(query.getCoordinatesBelongToSender());

        this.peerFound = peerFound;
    }

    /**
     * @{inheritDoc
     */
    public void handle(StructuredOverlay overlay) {
        System.out.println("LookupQueryResponse.handle()");
        CANOverlay CANOverlay = ((CANOverlay) overlay);

        super.setDeliveryTime();

        synchronized (CANOverlay.getLocalPeer().getOneWayResponses()) {
            CANOverlay.getLocalPeer().getOneWayResponses().put(super.getUUID(), this);
            CANOverlay.getLocalPeer().getOneWayResponses().notifyAll();
        }
    }

    /**
     * @{inheritDoc
     */
    public void route(StructuredOverlay overlay) {
        System.err.println("LookupQueryResponse.route()");
        this.query.route(overlay, this);
    }

    /**
     * {@inheritDoc}
     */
    public void route(StructuredOverlay overlay, Query query) {
        System.err.println("LookupQueryResponse.route()");
        this.query.route(overlay, query);
    }

    /**
     * @{inheritDoc
     */
    public boolean validKeyConstraints(StructuredOverlay overlay) {
        System.err.println("LookupQueryResponse.validKeyConstraints()");
        return this.query.validKeyConstraints(overlay);
    }

    /**
     * Returns the peer found.
     * 
     * @return the peer found.
     */
    public Peer getPeerFound() {
        return this.peerFound;
    }

}
