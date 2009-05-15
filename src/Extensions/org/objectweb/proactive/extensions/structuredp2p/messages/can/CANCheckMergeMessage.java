// FIXME
package org.objectweb.proactive.extensions.structuredp2p.messages.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.responses.can.CANCheckMergeResponseMessage;


/**
 * A CANMergeMessage is a concrete message in order to merge two CAN peers.
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
     * The area to merge with.
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
     * Returns the area to merge with.
     * 
     * @return the area to merge with.
     */
    public Area getRemoteArea() {
        return this.remoteArea;
    }
}
