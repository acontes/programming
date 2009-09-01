package org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can;

import java.util.Set;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.openrdf.model.Statement;


/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/05/2009
 */
@SuppressWarnings("serial")
public class RDFRangeQueryMessage extends RDFQueryMessage {

    public RDFRangeQueryMessage(Coordinate[] coordinatesToFind) {
        super(coordinatesToFind);
        // TODO Auto-generated constructor stub
    }

    public RDFRangeQueryMessage(Coordinate inferiorBoundForSubject, Coordinate superiorBoundForSubject,
            Coordinate inferiorBoundForPredicate, Coordinate superiorBoundForPredicate,
            Coordinate inferiorBoundForObject, Coordinate superiorBoundForObject) {

    }

    /**
     * @{inheritDoc
     */
    public boolean validKeyConstraints(StructuredOverlay overlay) {
        Zone zone = ((CANOverlay) overlay).getZone();

        for (int i = 0; i < super.getKeyToReach().length; i++) {
            // if coordinate is null we skip the test
            if (super.getKeyToReach()[i] != null) {
                // the specified overlay does not contains the key
                if (zone.contains(i, super.getKeyToReach()[i]) != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Statement> retrieveStatements(StructuredOverlay overlay) {
        // TODO Auto-generated method stub
        return null;
    }

}
