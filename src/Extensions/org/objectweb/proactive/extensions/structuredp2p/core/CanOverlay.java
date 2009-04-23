package org.objectweb.proactive.extensions.structuredp2p.core;

import java.util.Random;

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
    /**
     * The number of dimensions which is equals to the number of axes.
     */
    public static final int NB_DIMENSIONS = 4;

    /**
     * Neighbors of the current area. The neighbors are an array of ProActive groups.
     */
    private Group<Peer>[][] neighbors;

    /**
     * The area which is currently managed.
     */
    private Area area;

    /**
     * Constructor.
     */
    public CanOverlay() {
    }

    /**
     * Splits the current area in two. The axe which is used in order to split is choose randomly
     * with {@link #getRandomAxe()}.
     * 
     * @param peer
     *            the new peer which want to join the area.
     */
    public void split(Peer peer) {
        // Verify the availability of the peer.
        ResponseMessage response = this.sendMessageTo(peer, new PingMessage());

        if (response != null) {
            // TODO How to split data ?
            // FIXME Split the data in two parts (basic method)
            /*
             * Coordinate[] middle = this.getMiddleArea(this.getRandomDimension()); Area newArea =
             * new Area(middle, this.area.getCoordinatesMax()); this.area = new
             * Area(this.area.getCoordinatesMin(), middle);
             */
        }

    }

    /**
     * Gets a random axe number.
     * 
     * @return the random axe number.
     */
    private int getRandomDimension() {
        Random rand = new Random();
        return rand.nextInt(CanOverlay.NB_DIMENSIONS);
    }

    /**
     * Verify if the coordinates in arguments are in the managed area.
     * 
     * @param coordinates
     *            the coordinates to check.
     * @return true if the coordinates are in the area, else otherwise.
     */
    public boolean contains(Coordinate[] coordinates) {
        int i = 0;
        Coordinate[] minArea = this.area.getCoordinatesMin();
        Coordinate[] maxArea = this.area.getCoordinatesMax();

        for (Coordinate coord : coordinates) {
            if (coord != null) {
                // if the current coordinates aren't in the peer area.
                if (minArea[i].getValue().compareTo(coord.getValue()) > 0 &&
                    maxArea[i].getValue().compareTo(coord.getValue()) <= 0)
                    return false;
            }

            i++;
        }

        return true;
    }

    /**
     * Check if the axe index of the current area contains the given coordinate.
     * 
     * @param axeIndex
     *            the axe index to check.
     * @param coordinate
     *            the coordinate to check.
     * @return 0 if the coordinate is contained by the area on the given axe, -1 if the coordinate
     *         is smaller than the line which is managed by the given axe, 1 otherwise.
     */
    public int contains(int axeIndex, Coordinate coordinate) {
        Coordinate[] minArea = this.area.getCoordinatesMin();
        Coordinate[] maxArea = this.area.getCoordinatesMax();

        if (minArea[axeIndex].getValue().compareTo(coordinate.getValue()) > 0 &&
            maxArea[axeIndex].getValue().compareTo(coordinate.getValue()) <= 0) {
            return 0;
        } else if (minArea[axeIndex].getValue().compareTo(coordinate.getValue()) > 0) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * Merge two area when a peer leave the network. The split consists of give the data that are
     * managed by the peer which left the network to his neighbors and after to merge this area with
     * its closest neighbors.
     * 
     * @param peer
     *            the peer which left the network.
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
        // FIXME with splitting
        int dimension = 0;
        int order = 1;
        this.neighbors[dimension][order].add(peer);
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
        return peer.receiveMessage(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        // TODO Auto-generated method stub
    }

    /**
     * Set the new area covered.
     * 
     * @param area
     *            the new area covered.
     */
    public void setArea(Area area) {
        this.area = area;
    }

    /**
     * Returns the neighbors of the managed area.
     * 
     * @return the neighbors of the managed area.
     */
    public Group<Peer>[][] getNeighbors() {
        return this.neighbors;
    }
}
