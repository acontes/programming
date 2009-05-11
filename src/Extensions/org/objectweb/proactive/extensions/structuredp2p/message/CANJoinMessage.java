package org.objectweb.proactive.extensions.structuredp2p.message;

import java.util.ArrayList;

import org.objectweb.proactive.core.group.Group;
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

    private final Group<Peer>[][] neighbors;
    private final Area area;
    private final ArrayList<int[]> history;

    /**
     * Constructor.
     */
    public CANJoinMessage(Group<Peer>[][] neighbors, Area area, ArrayList<int[]> history) {
        this.neighbors = neighbors;
        this.area = area;
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

    public Group<Peer>[][] getNeighbors() {
        return this.neighbors;
    }

    public Area getArea() {
        return this.area;
    }

    public ArrayList<int[]> getHistory() {
        return this.history;
    }
}
