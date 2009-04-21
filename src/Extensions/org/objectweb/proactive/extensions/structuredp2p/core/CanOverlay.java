package org.objectweb.proactive.extensions.structuredp2p.core;

import java.util.Collection;

import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * Content-Addressable Network (CAN) as a distributed infrastructure that provides hash table-like
 * functionality on Internet-like scales. The CAN is scalable, fault-tolerant and completely
 * self-organizing.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class CanOverlay implements StructuredOverlay {
    private static final int NB_DIMENSIONS = 3;

    private Collection<Group<Peer>[]> neighbors;
    private Area area;

    public CanOverlay() {

    }

    /**
     * 
     * @param peer
     */
    public void split(Peer peer) {
        // FIXME How to cut the area ?
        ResponseMessage response = this.sendMessageTo(peer, new PingMessage());

        if (response != null) {

        }
    }

    /**
     * Verify if the coordinates in arguments are in the peer area.
     * 
     * @param coordinates
     * @return
     */
    public boolean contains(Coordinate[] coordinates) {
        int i = 0;
        Coordinate[] minArea = this.area.getCoordinatesMin();
        Coordinate[] maxArea = this.area.getCoordinatesMax();

        for (Coordinate coord : coordinates) {
            if (coord != null) {
                // If the current coordinates aren't in the peer area.
                if (minArea[i].getValue().compareTo(coord.getValue()) >= 0 &&
                    maxArea[i].getValue().compareTo(coord.getValue()) <= 0)
                    return false;
            }

            i++;
        }

        return true;
    }

    /**
     * 
     * @param peer
     */
    public void merge(Peer peer) {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkNeighbors() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void join(Peer peer) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void leave() {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage sendMessageTo(Peer peer, Message msg) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        // TODO Auto-generated method stub
    }

    /**
     * Set the new area covered by the peer.
     * 
     * @param area
     *            the new area.
     */
    public void setArea(Area area) {
        this.area = area;
    }
}
