package org.objectweb.proactive.extensions.structuredp2p.messages.oneway.can;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query;


/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/05/2009
 */
@SuppressWarnings("serial")
public class RDFRangeQuery extends RDFQuery {

    public RDFRangeQuery(Coordinate[] coordinatesToFind) {
        super(coordinatesToFind);
        // TODO Auto-generated constructor stub
    }

    /**
     * @{inheritDoc
     */
    public void handle(StructuredOverlay overlay) {
        // TODO Auto-generated method stub

    }

    /**
     * @{inheritDoc
     */
    public void route(StructuredOverlay overlay) {
        // TODO Auto-generated method stub

    }

    /**
     * @{inheritDoc
     */
    public boolean validKeyConstraints(StructuredOverlay overlay) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void route(StructuredOverlay overlay, Query query) {
        // TODO Auto-generated method stub

    }

}
