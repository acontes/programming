package org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.can;

import java.util.Stack;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can.CANJoinMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ResponseMessage;


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
public class CANJoinResponseMessage implements ResponseMessage {

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
    private final Zone affectedZone;

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
     * @param remotePeer
     *            the remote peer already in the overlay.
     * @param dimension
     *            the dimension.
     * @param directionInv
     *            the direction.
     * @param zone
     *            the zone of the new peer.
     * @param affectedNeighbors
     *            the neighbors of the remote peer.
     * @param splitHistory
     *            the splitHistory of the remote peer.
     */
    public CANJoinResponseMessage(int dimension, int directionInv, Zone zone,
            NeighborsDataStructure affectedNeighbors, Stack<int[]> splitHistory) {
        this.dimension = dimension;
        this.direction = directionInv;
        this.affectedZone = zone;
        this.neighbors = affectedNeighbors;
        this.splitHistory = splitHistory;
    }

    /**
     * {@inheritDoc}
     */
    public CANJoinResponseMessage(boolean succeded) {
        this.dimension = -1;
        this.direction = -1;
        this.affectedZone = null;
        this.neighbors = null;
        this.splitHistory = null;
    }

    /**
     * Returns the current dimension.
     * 
     * @return the dimension.
     */
    public int getAffectedDimension() {
        return this.dimension;
    }

    /**
     * Returns the current direction.
     * 
     * @return the direction.
     */
    public int getAffectedDirection() {
        return this.direction;
    }

    /**
     * Returns the current zone.
     * 
     * @return the zone.
     */
    public Zone getAffectedZone() {
        return this.affectedZone;
    }

    /**
     * Returns the current neighbors.
     * 
     * @return the neighbors.
     */
    public NeighborsDataStructure getAffectedNeighborsDataStructure() {
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
