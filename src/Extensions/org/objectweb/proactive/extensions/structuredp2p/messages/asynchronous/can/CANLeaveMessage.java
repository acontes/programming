package org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can;

import java.util.List;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.Message;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ActionResponseMessage;


/**
 * A {@code CANLeaveMessage} is used in order to notify the neighbors to remove the peer which leave
 * from their list and to update their list with the new neighbors.
 * 
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANLeaveMessage implements Message {

    private Peer peerToRemove;

    private List<Peer> peersToAdd;

    private int dimensionToAdd;

    private int directionToAdd;

    /**
     * Constructor.
     * 
     * @param remotePeer
     *            the neighbor to remove.
     * @param dimension
     *            the dimension of the neighbor to remove.
     * @param direction
     *            the direction of the neighbor to remove
     */
    public CANLeaveMessage(Peer peerToRemove, List<Peer> peersToAdd, int dimensionToAdd, int directionToAdd) {
        this.peerToRemove = peerToRemove;
        this.peersToAdd = peersToAdd;
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handle(StructuredOverlay overlay) {
        return ((CANOverlay) overlay).handleLeaveMessage(this);
    }

    public Peer getPeerToRemove() {
        return this.peerToRemove;
    }

    public List<Peer> getPeersToAdd() {
        return this.peersToAdd;
    }

    public int getDimensionToAdd() {
        return this.dimensionToAdd;
    }

    public void setDimensionToAdd(int dimensionToAdd) {
        this.dimensionToAdd = dimensionToAdd;
    }

    public int getDirectionToAdd() {
        return this.directionToAdd;
    }

    public void setDirectionToAdd(int directionToAdd) {
        this.directionToAdd = directionToAdd;
    }
}
