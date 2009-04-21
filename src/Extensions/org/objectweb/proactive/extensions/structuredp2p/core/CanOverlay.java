package org.objectweb.proactive.extensions.structuredp2p.core;

import java.util.Collection;

import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
public class CanOverlay implements StructuredOverlay {
    private static final int NB_DIMENSIONS = 3;

    private Collection<Group<Peer>[]> neighbors;
    private Area area;

    /**
     * 
     * @param peer
     */
    public void split(Peer peer) {
        // FIXME comment couper la zone ?
        ResponseMessage response = this.sendMessageTo(peer, new PingMessage());

        if (response != null) {

        }
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

    /**
     * Verify if the coordinates in arguments are in the peer area.
     * 
     * @param coordinates
     * @return
     */
    public boolean contains(Coordinate[] coordinates) {
        // FIXME c'est bon ??
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

}
