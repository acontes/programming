package org.objectweb.proactive.extensions.structuredp2p.message;

import java.util.ArrayList;
import java.util.HashMap;

import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.JoinResponseMessage;


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

    private final HashMap<Peer, Area>[][] neighbors;
    private final ArrayList<int[]> history;

    /**
     * Constructor.
     * 
     * @param directionInv
     * @param dimension
     * @param peer
     */
    public CANJoinMessage(HashMap<Peer, Area>[][] neighbors, ArrayList<int[]> history) {
        this.neighbors = neighbors;
        this.history = history;
    }

    /**
     * Handles message by delegation.
     * 
     * @param a
     *            peer to which the message will be send.
     * @return a PingResponseMessage for routing.
     * 
     */
    public JoinResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleJoinMessage(this);
    }

    public HashMap<Peer, Area>[][] getNeighbors() {
        return this.neighbors;
    }

    public ArrayList<int[]> getHistory() {
        return this.history;
    }
}
