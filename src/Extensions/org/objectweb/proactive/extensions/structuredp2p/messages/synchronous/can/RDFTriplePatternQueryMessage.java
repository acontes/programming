package org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can;

import java.util.Set;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;


/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/04/2009
 */
@SuppressWarnings("serial")
public class RDFTriplePatternQueryMessage extends RDFQueryMessage {

    public RDFTriplePatternQueryMessage() {
        super();
    }

    public RDFTriplePatternQueryMessage(Coordinate[] coordinatesToFind) {
        super(coordinatesToFind);
    }

    public RDFTriplePatternQueryMessage(Coordinate subject, Coordinate predicate, Coordinate object) {
        super(new Coordinate[] { subject, predicate, object });

        if (subject == null && predicate == null && object == null) {
            throw new IllegalArgumentException(
                "The three arguments for RDFTriplePatternQuery cannot be null. You must specified two of them.");
        }
    }

    public Set<Statement> retrieveStatements(StructuredOverlay overlay) {
        URIImpl subject = (this.getKeyToReach()[0] == null) ? null : new URIImpl(this.getKeyToReach()[0]
                .getValue());

        URIImpl predicate = (this.getKeyToReach()[1] == null) ? null : new URIImpl(this.getKeyToReach()[1]
                .getValue());

        URIImpl object = (this.getKeyToReach()[2] == null) ? null : new URIImpl(this.getKeyToReach()[2]
                .getValue());

        return overlay.getLocalPeer().query(new StatementImpl(subject, predicate, object));
    }

    /**
     * {@inheritDoc}
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

}
