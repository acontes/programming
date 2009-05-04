package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANCheckMergeResponseMessage;


/**
 * A CANMergeMessage is a concrete message to merge two CAN peers.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANCheckMergeMessage extends Message {
    private final Area remoteArea;

    /**
     * Constructor.
     */
    public CANCheckMergeMessage(Area area) {
        this.remoteArea = area;
    }

    /**
     * {@inheritDoc}
     */
    public CANCheckMergeResponseMessage handle(StructuredOverlay overlay) {
        return ((CANOverlay) overlay).handleCheckMergeMessage(this);
    }

    /**
     * Returns the area to merge with.
     * 
     * @return the area to merge with.
     */
    public Area getArea() {
        return this.remoteArea;
    }
}
