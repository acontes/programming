package org.objectweb.proactive.extensions.structuredp2p.messages.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.data.DataStorage;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.responses.ActionResponseMessage;


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
public class CANMergeMessage extends Message {

    /**
     * The peer which is leaving.
     */
    private final Peer remotePeer;

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
     * The zone to merge with.
     */
    private final Zone zone;

    /**
     * The resources to merge with.
     */
    private final DataStorage resources;

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
     * @param remoteArea
     *            the zone to merge with.
     * @param remoteResources
     *            the resources to merge with.
     */
    public CANMergeMessage(Peer remotePeer, int dimension, int direction, NeighborsDataStructure neighbors,
            Zone remoteArea, DataStorage remoteResources) {
        this.remotePeer = remotePeer;
        this.dimension = dimension;
        this.direction = direction;
        this.neighbors = neighbors;
        this.zone = remoteArea;
        this.resources = remoteResources;
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handle(StructuredOverlay overlay) {
        return ((CANOverlay) overlay).handleMergeMessage(this);
    }

    /**
     * Returns the leaving remote peer.
     * 
     * @return the remote peer.
     */
    public Peer getRemotePeer() {
        return this.remotePeer;
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
     * Returns the zone to merge with.
     * 
     * @return the zone to merge with.
     */
    public Zone getArea() {
        return this.zone;
    }

    /**
     * Returns the resources to merge with.
     * 
     * @return the resources to merge with.
     */
    public DataStorage getResources() {
        return this.resources;
    }
}