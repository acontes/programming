package org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.can;

import java.util.List;
import java.util.Stack;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can.CANJoinMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ResponseMessage;
import org.openrdf.model.Statement;


/**
 * Defines a basic response for the {@link CANJoinMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1, 07/21/2009
 */
@SuppressWarnings("serial")
public class CANJoinResponseMessage implements ResponseMessage {

    /**
     * The dimension.
     */
    private final int dimension;

    /**
     * The direction affected.
     */
    private final int direction;

    /**
     * The zone affected.
     */
    private final Zone affectedZone;

    /**
     * The neighbors affected.
     */
    private final NeighborsDataStructure affectedNeighbors;

    /**
     * The current history.
     */
    private final Stack<int[]> splitHistory;

    /**
     * The statements affected.
     */
    private final List<Statement> affectedStatements;

    /**
     * Constructor.
     * 
     * @param dimension
     *            the dimension.
     * @param directionInv
     *            the direction.
     * @param splitHistory
     *            the splitHistory of the remote peer.
     * @param zone
     *            the zone of the new peer.
     * @param affectedNeighbors
     *            the neighbors of the remote peer.
     */
    public CANJoinResponseMessage(int dimension, int directionInv, Stack<int[]> splitHistory,
            Zone affectedZone, NeighborsDataStructure affectedNeighbors, List<Statement> affectedStatements) {
        this.dimension = dimension;
        this.direction = directionInv;
        this.splitHistory = splitHistory;
        this.affectedZone = affectedZone;
        this.affectedNeighbors = affectedNeighbors;
        this.affectedStatements = affectedStatements;
    }

    /**
     * Returns the affected dimension.
     * 
     * @return the affected dimension.
     */
    public int getAffectedDimension() {
        return this.dimension;
    }

    /**
     * Returns the affected direction.
     * 
     * @return the affected direction.
     */
    public int getAffectedDirection() {
        return this.direction;
    }

    /**
     * Returns the affected history.
     * 
     * @return the affected splitHistory.
     */
    public Stack<int[]> getSplitHistory() {
        return this.splitHistory;
    }

    /**
     * Returns the affected zone.
     * 
     * @return the affected zone.
     */
    public Zone getAffectedZone() {
        return this.affectedZone;
    }

    /**
     * Returns the affected neighbors.
     * 
     * @return the affected neighbors.
     */
    public NeighborsDataStructure getAffectedNeighborsDataStructure() {
        return this.affectedNeighbors;
    }

    /**
     * Returns the affected statements.
     * 
     * @return the affected statements.
     */
    public List<Statement> getAffectedStatements() {
        return this.affectedStatements;
    }

}
