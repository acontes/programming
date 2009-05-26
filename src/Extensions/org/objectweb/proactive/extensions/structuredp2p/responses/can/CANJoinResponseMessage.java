package org.objectweb.proactive.extensions.structuredp2p.responses.can;

import java.util.Stack;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.can.Zone;
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
     * The remote zone.
     */
    private final Zone remoteZone;

    /**
     * The current dimension.
     */
    private final int dimension;

    /**
     * The current direction.
     */
    private final int direction;

    /**
     * The current zone.
     */
    private final Zone localZone;

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
    public CANJoinResponseMessage(long creationTimestamp, Peer remotePeer, Zone remoteZone, int dimension,
            int directionInv, Zone zone, NeighborsDataStructure newNeighbors, Stack<int[]> splitHistory) {
        super(creationTimestamp, remotePeer);
        this.remoteZone = remoteZone;
        this.dimension = dimension;
        this.direction = directionInv;
        this.localZone = zone;
        this.neighbors = newNeighbors;
        this.splitHistory = splitHistory;
    }

    /**
     * Returns the remote zone.
     * 
     * @return the remoteZone
     */
    public Zone getRemoteZone() {
        return this.remoteZone;
    }

    /**
     * Returns the current dimension.
     * 
     * @return the dimension.
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * Returns the current direction.
     * 
     * @return the direction.
     */
    public int getDirection() {
        return this.direction;
    }

    /**
     * Returns the current zone.
     * 
     * @return the zone.
     */
    public Zone getLocalZone() {
        return this.localZone;
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
