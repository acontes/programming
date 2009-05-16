package org.objectweb.proactive.extensions.structuredp2p.messages.can;

import java.util.Stack;

import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.responses.ActionResponseMessage;


/**
 * A {@code CANJoinMessage} is used when a {@link Peer} want to join an another {@link Peer} which
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
    private final NeighborsDataStructure neighbors;

    /**
     * Area associated to the sender.
     */
    private final Area area;

    /**
     * Splits history of the sender.
     */
    private final Stack<int[]> splitHistory;

    /**
     * Constructor.
     * 
     * @param neighbors
     *            the neighbors.
     * @param history
     *            the split history.
     */
    public CANJoinMessage(NeighborsDataStructure neighbors, Area area, Stack<int[]> splitHistory) {
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
    public NeighborsDataStructure getNeighbors() {
        return this.neighbors;
    }

    /**
     * Returns splits history of the peer which has sent the message.
     * 
     * @return splits history of the peer which has sent the message.
     */
    public Stack<int[]> getSplitHistory() {
        return this.splitHistory;
    }
}
