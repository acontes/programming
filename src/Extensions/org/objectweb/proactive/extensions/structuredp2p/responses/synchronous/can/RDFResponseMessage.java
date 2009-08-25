package org.objectweb.proactive.extensions.structuredp2p.responses.synchronous.can;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can.RDFQueryMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.synchronous.AbstractResponseMessage;
import org.openrdf.model.Statement;


/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/05/2009
 */
@SuppressWarnings("serial")
public class RDFResponseMessage extends AbstractResponseMessage<Coordinate, RDFQueryMessage> {

    private Set<Statement> retrievedStatements = new HashSet<Statement>();

    public RDFResponseMessage() {
        super();
    }

    public RDFResponseMessage(RDFQueryMessage query, Coordinate[] coordinatesToReach,
            Set<Statement> retrievedStatements) {
        super(query, coordinatesToReach);

        this.retrievedStatements.addAll(retrievedStatements);
    }

    public RDFResponseMessage(RDFQueryMessage query, Coordinate[] coordinatesToReach) {
        super(query, coordinatesToReach);
    }

    public Set<Statement> getRetrievedStatements() {
        return this.retrievedStatements;
    }

    public boolean addAll(Set<Statement> statements) {
        return this.retrievedStatements.addAll(statements);
    }

    /**
     * {@inheritDoc}
     */
    public void handle(StructuredOverlay overlay) {

        // URIImpl subject = (this.getKeyToReach()[0] == null) ? null : new
        // URIImpl(this.getKeyToReach()[0]
        // .getValue());
        //
        // URIImpl predicate = (this.getKeyToReach()[1] == null) ? null : new
        // URIImpl(this.getKeyToReach()[1]
        // .getValue());
        //
        // URIImpl object = (this.getKeyToReach()[2] == null) ? null : new
        // URIImpl(this.getKeyToReach()[2]
        // .getValue());
        //
        // this.addAll(overlay.getLocalPeer().query(new StatementImpl(subject, predicate, object)));
        // overlay.getLocalPeer().addOneWayResponse(this);

        // this.getQuery().removeLastVisitedPeer().send(this);
        ((CANOverlay) this.getQuery().removeLastVisitedPeer().getStructuredOverlay()).addOneWayResponse(this);
    }

    /**
     * {@inheritDoc}
     */
    public void route(StructuredOverlay overlay) {
        System.out.println("RDFQueryResponse.route()");
        if (!super.getQuery().hasPeersToVisit()) {
            System.out.println("  * RDFQueryResponse.route() has no peer to visit : handle last step.");
            this.setDeliveryTime();
            ((CANOverlay) overlay).addOneWayResponse(this);
        } else {
            this.handle(overlay);
        }

    }

    public boolean validKeyConstraints(StructuredOverlay overlay) {
        return this.getQuery().validKeyConstraints(overlay);
    }
}
