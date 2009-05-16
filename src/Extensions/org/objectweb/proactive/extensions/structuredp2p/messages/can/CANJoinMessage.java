package org.objectweb.proactive.extensions.structuredp2p.messages.can;

import java.util.ArrayList;

import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.NeighborsArray;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.responses.ActionResponseMessage;


/**
 * A {@link CANJoinMessage} is used when a {@link Peer} want to join an another {@link Peer} which
 * is already on the network.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANJoinMessage extends Message {

    /**
     * The neighbors of the sender.
     */
    private final NeighborsArray neighbors;

    /**
     * Area associated to the sender.
     */
    private final Area area;

    /**
     * Splits history of the sender.
     */
    private final ArrayList<int[]> splitHistory;

    /**
     * Constructor.
     * 
     * @param neighbors
     *            the neighbors.
     * @param history
     *            the split history.
     */
    public CANJoinMessage(NeighborsArray neighbors, Area area, ArrayList<int[]> splitHistory) {
        super();
        this.neighbors = neighbors;
        this.area = area;
        this.splitHistory = splitHistory;
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleJoinMessage(this);
    }

    /**
     * Returns the {@link Area} associated to the peer which has sent the message.
     * 
     * @return the {@link Area} associated to the peer which has sent the message.
     */
    public Area getArea() {
        return this.area;
    }

    /**
     * Returns the neighbors of the peer which has sent the message.
     * 
     * @return the neighbors of the peer which has sent the message.
     */
    public NeighborsArray getNeighbors() {
        return this.neighbors;
    }

    /**
     * Returns splits history of the peer which has sent the message.
     * 
     * @return splits history of the peer which has sent the message.
     */
    public ArrayList<int[]> getSplitHistory() {
        return this.splitHistory;
    }
}
