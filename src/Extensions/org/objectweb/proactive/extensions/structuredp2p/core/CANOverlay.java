package org.objectweb.proactive.extensions.structuredp2p.core;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;

import org.objectweb.proactive.api.PAGroup;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.extensions.structuredp2p.core.exception.AreaException;
import org.objectweb.proactive.extensions.structuredp2p.message.CANJoinMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.CANLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.CANMergeMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LoadBalancingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANJoinResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANLookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANMergeResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.JoinResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.LookupResponseMessage;
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
@SuppressWarnings("serial")
public class CANOverlay extends StructuredOverlay {
    /**
     * The number of dimensions which is equals to the number of axes.
     */
    public static final int NB_DIMENSIONS = 4;

    /**
     * Neighbors of the current area. The neighbors are an array of ProActive groups.
     */
    private final Group<Peer>[][] neighbors;

    /**
     * The area which is currently managed.
     */
    private Area area;

    /**
     * Constructor.
     */
    public CANOverlay(Peer localPeer) {
        super(localPeer);
        this.neighbors = new Group[NB_DIMENSIONS][2];

        try {
            int i;
            for (i = 0; i < NB_DIMENSIONS; i++) {
                this.neighbors[i][0] = PAGroup.getGroup((Peer) PAGroup.newGroup(Peer.class.getName()));
                this.neighbors[i][1] = PAGroup.getGroup((Peer) PAGroup.newGroup(Peer.class.getName()));
            }
        } catch (ClassNotReifiableException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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

        if (!response.isNull()) {
            // TODO How to split data ?
            // FIXME Split the data in two parts (basic method)
            /*
             * Coordinate[] middle = this.getMiddleArea(this.getRandomDimension()); Area newArea =
             * new Area(middle, this.area.getCoordinatesMax()); this.area = new
             * Area(this.area.getCoordinatesMin(), middle);
             */

            int axe = this.getRandomDimension(); // split axe
            Coordinate minCord[] = this.area.getCoordinatesMin();
            Coordinate maxCord[] = this.area.getCoordinatesMax();
            Coordinate mid1[] = this.area.getCoordinatesMax();
            mid1[axe] = Coordinate.getMiddle(minCord[axe], maxCord[axe]);
            Coordinate mid2[] = this.area.getCoordinatesMin();
            mid2[axe] = Coordinate.getMiddle(minCord[axe], maxCord[axe]);
            this.setArea(new Area(minCord, mid1));
            CANOverlay overlay = new CANOverlay(peer);
            overlay.setArea(new Area(mid2, maxCord));
            peer.setStructuredOverlay(overlay);

        }

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
                if (minArea[i].getValue().compareTo(coord.getValue()) <= 0 &&
                    maxArea[i].getValue().compareTo(coord.getValue()) > 0)
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
     * Merge two area when a peer leave the network. The split consists to give the data that are
     * managed by the peer which left the network to his neighbors and after to merge this area with
     * its closest neighbors.
     * 
     * @param peer
     *            the peer which left the network.
     */
    public boolean merge(Peer remotePeer) {
        CANOverlay overlay = ((CANOverlay) remotePeer.getStructuredOverlay());
        Area remoteArea = overlay.getArea();
        try {
            this.area.mergeArea(remoteArea);
            this.getLocalPeer().getDataStorage().addData(remotePeer.getDataStorage());
        } catch (AreaException e) {
            // TODO Rollback
            return false;
        }
        // TODO
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void checkNeighbors() {
        try {
            for (Group<Peer>[] groupArray : neighbors) {
                for (Group<Peer> group : groupArray) {
                    Group<ResponseMessage> groupFutures;
                    groupFutures = (Group<ResponseMessage>) PAGroup.newGroup(ResponseMessage.class.getName());

                    ListIterator<Peer> peers = group.listIterator();
                    while (peers.hasNext()) {
                        groupFutures.add(this.sendMessageTo(peers.next(), new PingMessage()));
                    }

                    PAGroup.waitAll(groupFutures);
                    Iterator<ResponseMessage> it = groupFutures.listIterator();

                    while (it.hasNext()) {
                        try {
                            // FIXME
                        } catch (Exception e) {
                            // FIXME
                        }
                    }

                }
            }
        } catch (ClassNotReifiableException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Boolean join(Peer remotePeer) {
        // FIXME
        int dim = this.getRandomDimension();

        if (this.addNeighbor(remotePeer, dim, 1).booleanValue() &&
            ((JoinResponseMessage) this.getLocalPeer().sendMessageTo(remotePeer,
                    new CANJoinMessage(this.getRemotePeer(), dim, 0))).hasSucceeded()) {
            return new Boolean(true);
        }

        return new Boolean(false);
    }

    /**
     * {@inheritDoc}
     */
    public void leave() {
        try {
            Group<Peer> groupAvailablePeer = (Group<Peer>) PAGroup.newGroup(Peer.class.getName());

            // Check if there is a valid neighbor
            for (Group<Peer>[] neighborsAxe : this.neighbors) {
                for (Group<Peer> neighbor : neighborsAxe) {
                    ListIterator<Peer> list = neighbor.listIterator();

                    while (list.hasNext()) {
                        Peer current = list.next();
                        Area area = ((CANOverlay) current.getStructuredOverlay()).getArea();
                        if (this.area.isValidMergingArea(area)) {
                            groupAvailablePeer.add(current);
                        }
                    }
                }
            }

            // Check if there is at least one
            if (groupAvailablePeer.size() > 0) {
                this.getLocalPeer().sendMessageTo(groupAvailablePeer.waitAndGetOne(),
                        new CANMergeMessage(this.getRemotePeer()));
            }
            // TODO Else : split more before merge !

        } catch (ClassNotReifiableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public LookupResponseMessage sendMessage(LookupMessage msg) {
        CANLookupMessage msgCan = (CANLookupMessage) msg;
        if (this.contains(msgCan.getCoordinates())) {
            return msgCan.handle(this);
        } else {

            Group<Peer>[][] neighbors = this.neighbors;
            int pos;
            int neighborIndex;

            for (Group<Peer>[] neighborsGroup : neighbors) {
                for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
                    pos = this.contains(i, msgCan.getCoordinates()[i]);

                    if (pos == -1) {
                        this.sendMessageTo(((Peer) neighborsGroup[0].getGroupByType()),
                                new LoadBalancingMessage());
                        neighborIndex = neighborsGroup[0].waitOneAndGetIndex();
                        return neighborsGroup[0].get(neighborIndex).sendMessage(msg);
                    } else if (pos == 1) {
                        this.sendMessageTo(((Peer) neighborsGroup[1].getGroupByType()),
                                new LoadBalancingMessage());
                        neighborIndex = neighborsGroup[1].waitOneAndGetIndex();
                        return neighborsGroup[1].get(neighborIndex).sendMessage(msg);
                    }
                }
            }
        }

        throw new IllegalArgumentException("The searched position doesn't exist.");
    }

    /**
     * {@inheritDoc}
     */
    public ResponseMessage sendMessageTo(Peer remotePeer, Message msg) {
        return remotePeer.receiveMessage(msg);
    }

    /**
     * {@inheritDoc}
     */
    public void update() {
        // TODO Auto-generated method stub
    }

    /**
     * Add a new neighbor with a dimension and an order.
     * 
     * @param peer
     *            the new neighbor.
     * @param dimension
     *            the dimension.
     * @param order
     *            the order.
     */
    public Boolean addNeighbor(Peer remotePeer, int dimension, int order) {
        return this.neighbors[dimension][order].add(remotePeer);
    }

    /**
     * Indicates if the given peer is the neighbor of the current area.
     * 
     * @param peer
     *            the peer which is used to check.
     * @return true if the peer is a neighbor, false otherwise.
     */
    public boolean hasNeighbor(Peer peer) {
        for (Group<Peer>[] neighborsAxe : this.neighbors) {
            for (Group<Peer> neighbor : neighborsAxe) {
                if (neighbor.contains(peer)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public CANLookupResponseMessage handleLookupMessage(LookupMessage msg) {
        return new CANLookupResponseMessage(this.getLocalPeer(), ((CANLookupMessage) msg).getCoordinates());
    }

    /**
     * Handles a {@link CANJoinMessage}.
     * 
     * @param msg
     *            the message.
     * @return the response.
     */
    public CANJoinResponseMessage handleJoinMessage(Message msg) {
        CANJoinMessage message = (CANJoinMessage) msg;
        return new CANJoinResponseMessage(this.addNeighbor(message.getPeer(), message.getDimesion(), message
                .getOrder()));

    }

    /**
     * Handles a {@link CANMergeMessage}.
     * 
     * @param msg
     *            the message.
     * @return the response.
     */
    public CANMergeResponseMessage handleMergeMessage(Message msg) {
        CANMergeMessage message = (CANMergeMessage) msg;
        return new CANMergeResponseMessage(this.merge(message.getPeer()));
    }

    /**
     * Returns the area which is managed by the overlay.
     * 
     * @return the area which is managed by the overlay.
     */
    public Area getArea() {
        return this.area;
    }

    /**
     * Returns the neighbors of the managed area.
     * 
     * @return the neighbors of the managed area.
     */
    public Group<Peer>[][] getNeighbors() {
        return this.neighbors;
    }

    /**
     * Gets a random axe number.
     * 
     * @return the random axe number.
     */
    private int getRandomDimension() {
        Random rand = new Random();
        return rand.nextInt(CANOverlay.NB_DIMENSIONS);
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
}
