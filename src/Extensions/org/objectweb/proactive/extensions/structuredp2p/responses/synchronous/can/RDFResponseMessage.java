package org.objectweb.proactive.extensions.structuredp2p.responses.synchronous.can;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
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
        if (statements != null) {
            return this.retrievedStatements.addAll(statements);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void handle(StructuredOverlay overlay) {
        this.addAll(super.getQuery().retrieveStatements(overlay));
        this.getQuery().removeLastPeerToVisitForStepTwo().send(this);
        overlay.getSynchronousMessages().remove(super.getUUID());
    }

    /**
     * {@inheritDoc}
     */
    public void route(StructuredOverlay overlay) {
        if (super.getQuery().getPeersToVisitForStepTwo().size() > 0) {
            this.handle(overlay);
        } else if (super.getQuery().getPeersToVisitForStepOne().size() > 0) {
            super.getQuery().removeLastPeerToVisitForStepOne().send(this);
        } else {
            synchronized (overlay.getSynchronousMessages()) {
                overlay.getSynchronousMessages().notifyAll();
            }
        }
    }

    public boolean validKeyConstraints(StructuredOverlay overlay) {
        return this.getQuery().validKeyConstraints(overlay);
    }
}
