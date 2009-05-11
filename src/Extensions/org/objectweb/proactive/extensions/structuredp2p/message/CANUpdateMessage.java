package org.objectweb.proactive.extensions.structuredp2p.message;

import java.util.ArrayList;

import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANUpdateResponseMessage;


/**
 * A CANUpdateMessage is a concrete message to update a CAN peer.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANUpdateMessage extends UpdateMessage {

    private final Area area;
    private final ArrayList<int[]> history;

    /**
     * Constructor.
     */
    public CANUpdateMessage(Area area, ArrayList<int[]> history) {
        this.area = area;
        this.history = history;
    }

    /**
     * {@inheritDoc}
     */
    public CANUpdateResponseMessage handle(StructuredOverlay overlay) {
        return ((CANOverlay) overlay).handleUpdateMessage(this);
    }

    /**
     * Returns the area to update.
     * 
     * @return the area.
     */
    public Area getArea() {
        return this.area;
    }

    /**
     * Returns the split history to update.
     * 
     * @return the history.
     */
    public ArrayList<int[]> getHistory() {
        return this.history;
    }
}
