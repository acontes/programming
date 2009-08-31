package org.objectweb.proactive.extensions.structuredp2p.responses.synchronous.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.SynchronousMessageEntry;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can.LookupQueryMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.synchronous.AbstractResponseMessage;


/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/05/2009
 */
@SuppressWarnings("serial")
public class LookupResponseMessage extends AbstractResponseMessage<Coordinate, LookupQueryMessage> {

    private Peer peerFound;

    public LookupResponseMessage() {
        super();
    }

    public LookupResponseMessage(LookupQueryMessage query, Peer peerFound) {
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
        SynchronousMessageEntry entry = overlay.getSynchronousMessages().get(super.getUUID());
        entry.incrementNbResponsesReceived(1);
        entry.setSynchronousMessage(this);

        synchronized (overlay.getSynchronousMessages()) {
            overlay.getSynchronousMessages().notifyAll();
        }

        // List<SynchronousMessage> result = new ArrayList<SynchronousMessage>();
        // result.add(this);
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
