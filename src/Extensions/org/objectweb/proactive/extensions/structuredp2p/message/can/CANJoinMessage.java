package org.objectweb.proactive.extensions.structuredp2p.message.can;

import java.util.ArrayList;
import java.util.HashMap;

import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.response.ResponseMessage;


/**
 * A {@link CANJoinMessage} is a concrete message in order to ping.
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
     * The neighbors of the receiver peer.
     */
    private final HashMap<Peer, Area>[][] neighbors;

    /**
     * The split history of the receiver peer.
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
    public CANJoinMessage(HashMap<Peer, Area>[][] neighbors, ArrayList<int[]> splitHistory) {
        super();
        this.neighbors = neighbors;
        this.splitHistory = splitHistory;
    }

    /**
     * {@inheritDoc}
     */
    public ResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleJoinMessage(this);
    }

    /**
     * Returns the peer neighbors.
     * 
     * @return the neighbors.
     */
    public HashMap<Peer, Area>[][] getNeighbors() {
        return this.neighbors;
    }

    /**
     * Returns the split history.
     * 
     * @return the split history.
     */
    public ArrayList<int[]> getSplitHistory() {
        return this.splitHistory;
    }
}
