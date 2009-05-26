package org.objectweb.proactive.extensions.structuredp2p.responses.can;

import java.util.Stack;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.core.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANJoinMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.JoinResponseMessage;


/**
 * Defines a basic response for the {@link CANJoinMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANJoinResponseMessage extends JoinResponseMessage {

    /**
     * The remote area.
     */
    private final Zone remoteArea;

    /**
     * The current dimension.
     */
    private final int dimension;

    /**
     * The current direction.
     */
    private final int direction;

    /**
     * The current area.
     */
    private final Zone localArea;

    /**
     * The current neighbors.
     */
    private final NeighborsDataStructure neighbors;

    /**
     * The current history.
     */
    private final Stack<int[]> splitHistory;

    /**
     * Constructor.
     * 
     * @param creationTimestamp
     * @param remotePeer
     * @param dimension
     * @param directionInv
     * @param zone
     * @param newNeighbors
     * @param splitHistory
     */
    public CANJoinResponseMessage(long creationTimestamp, Peer remotePeer, Zone remoteArea, int dimension,
            int directionInv, Zone zone, NeighborsDataStructure newNeighbors, Stack<int[]> splitHistory) {
        super(creationTimestamp, remotePeer);
        this.remoteArea = remoteArea;
        this.dimension = dimension;
        this.direction = directionInv;
        this.localArea = zone;
        this.neighbors = newNeighbors;
        this.splitHistory = splitHistory;
    }

    /**
     * Returns the remote area.
     * 
     * @return the remoteArea
     */
    public Zone getRemoteArea() {
        return this.remoteArea;
    }

    /**
     * Returns the current dimension.
     * 
     * @return the dimension.
     */
    public int getDirection() {
        return this.dimension;
    }

    /**
     * Returns the current direction.
     * 
     * @return the direction.
     */
    public int getDimension() {
        return this.direction;
    }

    /**
     * Returns the current area.
     * 
     * @return the area.
     */
    public Zone getLocalArea() {
        return this.localArea;
    }

    /**
     * Returns the current neighbors.
     * 
     * @return the neighbors.
     */
    public NeighborsDataStructure getNeighbors() {
        return this.neighbors;
    }

    /**
     * Returns the current history.
     * 
     * @return the splitHistory
     */
    public Stack<int[]> getSplitHistory() {
        return this.splitHistory;
    }

}
