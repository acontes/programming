package org.objectweb.proactive.extensions.structuredp2p.messages.synchronous;

import java.io.Serializable;
import java.util.UUID;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;


/**
 * Interface which must be implemented by all query. It defines some standard operations and some
 * basics operations which may differ depending on the type of query to execute.
 * 
 * @author Laurent Pellegrino
 * @version 0.1, 08/05/2009
 */
public interface Query extends Serializable {

    /**
     * Returns the timestamp of the creation of the message.
     * 
     * @return the timestamp of the creation of the message.
     */
    public abstract long getDispatchTimestamp();

    /**
     * Returns the number of steps that have been performed in order to reach the
     * <code>keyToReach</code>.
     * 
     * @return the number of steps that have been performed in order to reach the
     *         <code>keyToReach</code>.
     */
    public abstract int getNbSteps();

    /**
     * Returns the universally unique identifier used in order to identify the response.
     * 
     * @return the universally unique identifier used in order to identify the response.
     */
    public abstract UUID getUUID();

    /**
     * Handles the query.
     * 
     * @param overlay
     *            the overlay used in order to handle the query by delegation.
     */
    public abstract void handle(StructuredOverlay overlay);

    /**
     * Increments the number of steps by the specified number.
     * 
     * @param increment
     *            the size of the increment.
     */
    public abstract void incrementNbStepsBy(int increment);

    /**
     * Route the query to the proper peer. If the current peer contains the key to reach, the query
     * is handled and a response is routed to the sender.
     * 
     * @param overlay
     *            the overlay used in order to route the request.
     */
    public abstract void route(StructuredOverlay overlay);

    /**
     * Sets the universally unique identifier used in order to identify the response.
     * 
     * @param uuid
     *            the new uuid to set.
     */
    public abstract void setUUID(UUID uuid);

    /**
     * Indicates if the specified overlay contains the key to reach.
     * 
     * @param overlay
     *            the overlay used to check key.
     * @return <code>true</code> if the specified overlay contains the key to reach.
     *         <code>false</code> otherwise.
     */
    public abstract boolean validKeyConstraints(StructuredOverlay overlay);
}
