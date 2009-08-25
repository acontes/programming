package org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;


/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/05/2009
 */
@SuppressWarnings("serial")
public class RDFRangeQuery extends RDFQueryMessage {

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

}
