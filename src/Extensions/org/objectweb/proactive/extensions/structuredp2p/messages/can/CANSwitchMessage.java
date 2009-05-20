package org.objectweb.proactive.extensions.structuredp2p.messages.can;

import java.util.ArrayList;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.data.DataStorage;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.responses.can.CANSwitchResponseMessage;


/**
 * A {@code CANSwitchMessage} is used in order to switch two {@link Peer}.
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
    private final NeighborsDataStructure neighbors;

    /**
     * The peer to switch with.
     */
    private final Peer remotePeer;

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
    public CANSwitchMessage(NeighborsDataStructure neighbors, Peer peer, Area area, DataStorage resources,
            ArrayList<int[]> splitHistory) {
        super();
        this.neighbors = neighbors;
        this.remotePeer = peer;
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
     * Returns the neighbors from the peer to switch with.
     * 
     * @return the neighbors from the peer to switch with.
     */
    public NeighborsDataStructure getNeighbors() {
        return this.neighbors;
    }

    /**
     * Returns the peer to switch with.
     * 
     * @return the peer to switch with.
     */
    public Peer getPeer() {
        return this.remotePeer;
    }

    /**
     * Returns the area from the peer to switch with.
     * 
     * @return the area from the peer to switch with.
     */
    public Area getArea() {
        return this.area;
    }

    /**
     * Returns the resources from the peer to switch with.
     * 
     * @return the resources from the peer to switch with.
     */
    public DataStorage getResources() {
        return this.resources;
    }

    /**
     * Returns the split history from the peer to switch with.
     * 
     * @return the split history from the peer to switch with.
     */
    public ArrayList<int[]> getSplitHistory() {
        return this.splitHistory;
    }

}
