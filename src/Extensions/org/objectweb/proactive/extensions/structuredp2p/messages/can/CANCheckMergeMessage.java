package org.objectweb.proactive.extensions.structuredp2p.messages.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.CANOverlay;
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
     * The area of the sender.
     */
    private final Area remoteArea;

    /**
     * Constructor.
     * 
     * @param remoteArea
     *            the area to merge with.
     */
    public CANCheckMergeMessage(Area remoteArea) {
        super();
        this.remoteArea = remoteArea;
    }

    /**
     * {@inheritDoc}
     */
    public CANCheckMergeResponseMessage handle(StructuredOverlay overlay) {
        return ((CANOverlay) overlay).handleCheckMergeMessage(this);
    }

    /**
     * Returns the area of the sender of the message.
     * 
     * @return the area of the sender of the message.
     */
    public Area getArea() {
        return this.remoteArea;
    }
}
