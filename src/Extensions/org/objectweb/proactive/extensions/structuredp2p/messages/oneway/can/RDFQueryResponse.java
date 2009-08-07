package org.objectweb.proactive.extensions.structuredp2p.messages.oneway.can;

import java.util.List;
import java.util.Set;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.AbstractQueryResponse;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query;
import org.openrdf.model.Statement;


/**
 * The <code>RDFQueryResponse.java</code> class...
 * 
 * @author Laurent Pellegrino
 * @version 0.1, 08/05/2009
 * 
 */
@SuppressWarnings("serial")
public class RDFQueryResponse extends AbstractQueryResponse<Coordinate> {

    private Set<Statement> retrievedStatements;

    private RDFQuery query;

    public RDFQueryResponse() {
        super();
    }

    public RDFQueryResponse(RDFQuery query, Coordinate[] coordinatesToReach,
            Set<Statement> retrievedStatements) {
        super(query, coordinatesToReach);

        this.query = query;
        this.query.setKeyToReach(coordinatesToReach);

        this.retrievedStatements = retrievedStatements;
    }

    public boolean addAll(List<Statement> statements) {
        return this.retrievedStatements.addAll(statements);
    }

    /**
     * @{inheritDoc
     */
    public void handle(StructuredOverlay overlay) {
        this.query.handle(overlay);
    }

    /**
     * @{inheritDoc
     */
    public void route(StructuredOverlay overlay) {
        this.query.route(overlay);
    }

    /**
     * @{inheritDoc
     */
    public boolean validKeyConstraints(StructuredOverlay overlay) {
        return this.query.validKeyConstraints(overlay);
    }

    /**
     * {@inheritDoc}
     */
    public void route(StructuredOverlay overlay, Query query) {
        // TODO Auto-generated method stub

    }
}
