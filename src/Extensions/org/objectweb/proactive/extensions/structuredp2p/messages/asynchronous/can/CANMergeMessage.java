package org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can;

import java.util.Set;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.AsynchronousMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ActionResponseMessage;
import org.openrdf.model.Statement;


/**
 * A {@code CANMergeMessage} is used when a {@link Peer} must merge with an another {@link Peer}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANMergeMessage implements AsynchronousMessage {

    /**
     * The current dimension of the leaving peer.
     */
    private final int dimension;

    /**
     * The current direction of the leaving peer.
     */
    private final int direction;

    /**
     * The neighbors of the leaving peer.
     */
    private final NeighborsDataStructure neighbors;

    /**
     * The peer which is leaving.
     */
    private final Peer remotePeerWichIsLeaving;

    /**
     * The resources to merge with.
     */
    private final Set<Statement> resources;

    /**
     * The zone to merge with.
     */
    private final Zone zone;

    /**
     * Constructor.
     * 
     * @param remotePeer
     *            the peer which is leaving.
     * @param dimension
     *            the current dimension of the leaving peer.
     * @param direction
     *            the current direction of the leaving peer.
     * @param neighbors
     *            the neighbors of the leaving peer.
     * @param remoteZone
     *            the zone to merge with.
     * @param remoteResources
     *            the resources to merge with.
     */
    public CANMergeMessage(Peer remotePeer, int dimension, int direction, NeighborsDataStructure neighbors,
            Zone remoteZone, Set<Statement> remoteResources) {
        this.remotePeerWichIsLeaving = remotePeer;
        this.dimension = dimension;
        this.direction = direction;
        this.neighbors = neighbors;
        this.zone = remoteZone;
        this.resources = remoteResources;

        if (remoteZone == null) {
            throw new NullPointerException("Cannot merge a zone with a NULL zone.");
        }
    }

    /**
     * Returns the dimension of leaving.
     * 
     * @return the dimension.
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * Returns the dimension of leaving.
     * 
     * @return the direction
     */
    public int getDirection() {
        return this.direction;
    }

    /**
     * Returns the neighbors of the leaving peer.
     * 
     * @return the neighbors.
     */
    public NeighborsDataStructure getNeighbors() {
        return this.neighbors;
    }

    /**
     * Returns the leaving remote peer.
     * 
     * @return the remote peer.
     */
    public Peer getRemotePeerWhichIsLeaving() {
        return this.remotePeerWichIsLeaving;
    }

    /**
     * Returns the resources to merge with.
     * 
     * @return the resources to merge with.
     */
    public Set<Statement> getResources() {
        return this.resources;
    }

    /**
     * Returns the zone to merge with.
     * 
     * @return the zone to merge with.
     */
    public Zone getZone() {
        return this.zone;
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handle(StructuredOverlay overlay) {
        return ((CANOverlay) overlay).handleMergeMessage(this);
    }
}
