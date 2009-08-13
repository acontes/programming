package org.objectweb.proactive.extensions.structuredp2p.messages.oneway.can;

import java.util.Set;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.AbstractQueryResponse;
import org.openrdf.model.Statement;


/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/05/2009
 */
@SuppressWarnings("serial")
public class RDFQueryResponse extends AbstractQueryResponse<Coordinate, RDFQuery> {

    private Set<Statement> retrievedStatements;

    private Peer lastPeerToVisitRemoved;

    private boolean isOnLeaf = false;

    public RDFQueryResponse() {
        super();
    }

    public RDFQueryResponse(RDFQuery query, Coordinate[] coordinatesToReach,
            Set<Statement> retrievedStatements, boolean isOnLeaf) {
        super(query, coordinatesToReach);
        this.isOnLeaf = isOnLeaf;
        this.retrievedStatements = retrievedStatements;
    }

    public RDFQueryResponse(RDFQuery query, Coordinate[] coordinatesToReach,
            Set<Statement> retrievedStatements) {
        this(query, coordinatesToReach, retrievedStatements, false);

    }

    public Set<Statement> getRetrievedStatements() {
        return this.retrievedStatements;
    }

    public boolean addAll(Set<Statement> statements) {
        return this.retrievedStatements.addAll(statements);
    }

    public boolean hasPeersToVisit() {
        return this.getQuery().hasPeersToVisit();
    }

    /**
     * {@inheritDoc}
     */
    public void handle(StructuredOverlay overlay) {
        overlay.getLocalPeer().addOneWayResponse(this);
        this.getQuery().removeLastVisitedPeer().send(this);
    }

    /**
     * {@inheritDoc}
     */
    public void route(StructuredOverlay overlay) {
        System.out.println("isOnLeaf = " + this.isOnLeaf);
        if (!this.hasPeersToVisit()) {
            System.out.println("  * RDFQueryResponse.route() has no peer to visit : handle last step.");
            this.setDeliveryTime();
            overlay.getLocalPeer().addOneWayResponse(this);
        } else if (this.isOnLeaf) {
            System.out.println("  * RDFQueryResponse.route() is on a leaf. Need to send response.");
            this.isOnLeaf = false;
            this.lastPeerToVisitRemoved = this.getQuery().removeLastVisitedPeer();
            this.lastPeerToVisitRemoved.send(this);

        } else if (!this.isOnLeaf && this.hasPeersToVisit()) {
            System.out
                    .println("  * RDFQueryResponse.route() has peers to visit and must send back response.");
            this.printPeersToVisit(overlay);

            this.handle(overlay);
        } else {
            System.err.println("ooooops, but why ?");
        }

        this.printPeersToVisit(overlay);
        System.out.println("  [StateOfRDFQueryResponse: " + this.getQuery().getVisitedPeers().size() +
            " peers to route back]");
    }

    public void printPeersToVisit(StructuredOverlay overlay) {
        // System.out.println("Peers to visit from " + overlay);
        for (Peer p : this.getQuery().getVisitedPeers()) {
            System.out.println("  -" + p);
        }
    }

    public boolean validKeyConstraints(StructuredOverlay overlay) {
        return this.getQuery().validKeyConstraints(overlay);
    }
}
