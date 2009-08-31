package org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.AsynchronousMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ActionResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ResponseMessage;


/**
 * The <code>TestRDFTripplePatternKeyConstraints.java</code> class...
 *
 * @author Laurent Pellegrino
 * @version 0.1, 08/27/2009
 *
 */
@SuppressWarnings("serial")
public class TestRDFTripplePatternKeyConstraints implements AsynchronousMessage {

    private Coordinate[] coordinatesToReach;

    public TestRDFTripplePatternKeyConstraints(Coordinate[] coordinatesToReach) {
        this.coordinatesToReach = coordinatesToReach;
    }

    /**
     * {@inheritDoc}
     */
    public ResponseMessage handle(StructuredOverlay overlay) {
        Zone zone = ((CANOverlay) overlay).getZone();

        for (int i = 0; i < this.coordinatesToReach.length; i++) {
            // if coordinate is null we skip the test
            if (this.coordinatesToReach[i] != null) {
                // the specified overlay does not contains the key
                if (zone.contains(i, this.coordinatesToReach[i]) != 0) {
                    return new ActionResponseMessage(false);
                }
            }
        }

        return new ActionResponseMessage(true);
    }

}
