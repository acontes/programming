package org.objectweb.proactive.extensions.structuredp2p.messages.can;

import java.util.ArrayList;

import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.NeighborsArray;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.data.DataStorage;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.responses.can.CANSwitchResponseMessage;


/**
 * A {@link CANSwitchMessage} is used in order to switch two {@link Peer}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANSwitchMessage extends Message {
    /**
     * The neighbors from the peer to switch with.
     */
    private final NeighborsArray neighbors;

    /**
     * The area from the peer to switch with.
     */
    private final Area area;

    /**
     * The resources from the peer to switch with.
     */
    private final DataStorage resources;

    /**
     * The split history from the peer to switch with.
     */
    private final ArrayList<int[]> splitHistory;

    /**
     * Constructor.
     * 
     * @param neighbors
     *            neighbors from the peer to switch with.
     * @param area
     *            area from the peer to switch with.
     * @param resources
     *            resources from the peer to switch with.
     * @param splitHistory
     *            split history from the peer to switch with.
     */
    public CANSwitchMessage(NeighborsArray neighbors, Area area, DataStorage resources,
            ArrayList<int[]> splitHistory) {
        super();
        this.neighbors = neighbors;
        this.area = area;
        this.resources = resources;
        this.splitHistory = splitHistory;
    }

    /**
     * {@inheritDoc}
     */
    public CANSwitchResponseMessage handle(StructuredOverlay overlay) {
        return ((CANOverlay) overlay).handleSwitchMessage(this);
    }

    /**
     * The neighbors from the peer to switch with.
     * 
     * @return the neighbors from the peer to switch with.
     */
    public NeighborsArray getNeighbors() {
        return this.neighbors;
    }

    /**
     * The area from the peer to switch with.
     * 
     * @return the area.
     */
    public Area getArea() {
        return this.area;
    }

    /**
     * The resources from the peer to switch with.
     * 
     * @return the resources.
     */
    public DataStorage getResources() {
        return this.resources;
    }

    /**
     * The split history from the peer to switch with.
     * 
     * @return the splitHistory
     */
    public ArrayList<int[]> getSplitHistory() {
        return this.splitHistory;
    }

}
