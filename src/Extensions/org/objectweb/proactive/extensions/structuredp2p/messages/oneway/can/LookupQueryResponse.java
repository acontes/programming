package org.objectweb.proactive.extensions.structuredp2p.messages.oneway.can;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.AbstractQueryResponse;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.QueryResponse;


/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/05/2009
 */
@SuppressWarnings("serial")
public class LookupQueryResponse extends AbstractQueryResponse<Coordinate, LookupQuery> {

    private Peer peerFound;

    public LookupQueryResponse() {
        super();
    }

    public LookupQueryResponse(LookupQuery query, Peer peerFound) {
        super(query, query.getCoordinatesBelongToSender());
        this.peerFound = peerFound;
    }

    public Peer getPeerFound() {
        return this.peerFound;
    }

    /**
     * @{inheritDoc
     */
    public void handle(StructuredOverlay overlay) {
        CANOverlay canOverlay = ((CANOverlay) overlay);

        super.setDeliveryTime();

        List<QueryResponse> result = new ArrayList<QueryResponse>();
        result.add(this);

        canOverlay.addOneWayResponse(this);
    }

    /**
     * @{inheritDoc
     */
    public void route(StructuredOverlay overlay) {
        super.getQuery().route(overlay, this);
    }

    /**
     * @{inheritDoc
     */
    public boolean validKeyConstraints(StructuredOverlay overlay) {
        return super.getQuery().validKeyConstraints(overlay);
    }

}
