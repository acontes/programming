package org.objectweb.proactive.extensions.structuredp2p.messages.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.responses.can.CANCheckMergeResponseMessage;


/**
 * A {@code CANMergeMessage} is used when a {@link Peer} must check if it can merge with an another
 * {@link Peer}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANCheckMergeMessage extends Message {

    /**
     * The zone of the sender.
     */
    private final Zone remoteZone;

    /**
     * Constructor.
     * 
     * @param remoteZone
     *            the zone to merge with.
     */
    public CANCheckMergeMessage(Zone remoteZone) {
        super();
        this.remoteZone = remoteZone;
    }

    /**
     * {@inheritDoc}
     */
    public CANCheckMergeResponseMessage handle(StructuredOverlay overlay) {
        return ((CANOverlay) overlay).handleCheckMergeMessage(this);
    }

    /**
     * Returns the zone of the sender of the message.
     * 
     * @return the zone of the sender of the message.
     */
    public Zone getZone() {
        return this.remoteZone;
    }
}
