package org.objectweb.proactive.extensions.structuredp2p.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.api.PAGroup;
import org.objectweb.proactive.core.group.ExceptionInGroup;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.extensions.structuredp2p.core.exception.AreaException;
import org.objectweb.proactive.extensions.structuredp2p.message.CANCheckMergeMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.CANJoinMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.CANLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.CANMergeMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LeaveMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LoadBalancingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANCheckMergeResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANJoinResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANLookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANMergeResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.EmptyResponseMessage;
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
     * Neighbors of the current area. The neighbors are an array of ProActive groups. It is a
     * two-dimensional array of {@link Group}. Each line corresponds to a dimension. The number of
     * columns is always equal to two. The first column corresponds to the neighbors having a
     * coordinate lower than the current pair on the given dimension. The second column is the
     * reverse.
     */
    private final Group<Peer>[][] neighbors;

    /**
     * The area which is currently managed.
     */
    private Area area;

    /**
     * Constructor.
     * 
     * Initialize the neighbors array.
     */
    public CANOverlay(Peer localPeer) {
        super(localPeer);
        this.neighbors = new Group[CANOverlay.NB_DIMENSIONS][2];

        try {
            int i;
            for (i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
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
     * Splits the current area in two. The dimension which is used in direction to split is choose
     * randomly with {@link #getRandomDimension()}.
     * 
     * @param peer
     *            the new peer which want to join the area.
     */
    public void split(Peer peer) {
        // Check the availability of the peer.
        ResponseMessage response = this.sendMessageTo(peer, new PingMessage());

        // TODO How to split data ?
        // FIXME Split the data in two parts (basic method)

        /*
         * Coordinate[] middle = this.getMiddleArea(this.getRandomDimension()); Area newArea = new
         * Area(middle, this.area.getCoordinatesMax()); this.area = new
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

    /**
     * Check if the coordinates in arguments are in the managed area.
     * 
     * @param coordinates
     *            the coordinates to check.
     * 
     * @return true if the coordinates are in the area, false otherwise.
     */
    public boolean contains(Coordinate[] coordinates) {
        int i;
        boolean res = true;

        for (i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            res &= (this.getArea().contains(i, coordinates[i]) == 0);
        }

        return res;
    }

    /**
     * Check if the dimension index of the current area contains the given coordinate.
     * 
     * @param dimension
     *            the dimension index used for the check.
     * 
     * @param coordinate
     *            the coordinate to check.
     * 
     * @return 0 if the coordinate is contained by the area on the given axe, -1 if the coordinate
     *         is smaller than the line which is managed by the given dimension, 1 otherwise.
     */
    public int contains(int dimension, Coordinate coordinate) {
        return this.getArea().contains(dimension, coordinate);
    }

    /**
     * Merge two area when a peer leave the network cleanly. The split consists to give the data
     * that are managed by the peer which left the network to his neighbors and after to merge this
     * area with its closest neighbors.
     * 
     * @param peer
     *            the peer which left the network.
     * 
     * @return true if the merge has succeeded, false otherwise.
     */
    public boolean merge(Peer remotePeer) {
        CANOverlay overlay = ((CANOverlay) remotePeer.getStructuredOverlay());
        Area remoteArea = overlay.getArea();
        try {
            this.area.merge(remoteArea);
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
    @Override
    public void checkNeighbors() {
        for (Group<Peer>[] groupArray : this.neighbors) {
            for (Group<Peer> group : groupArray) {
                ResponseMessage groupFutures = (ResponseMessage) PAFuture.getFutureValue(this.getLocalPeer()
                        .sendMessageTo((Peer) group.getGroupByType(), new PingMessage()));
                PAGroup.waitAll(groupFutures);

                for (ExceptionInGroup e : PAGroup.getGroup(groupFutures).getExceptionList()) {
                    // (Peer)e.getObject();
                }
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean join(Peer remotePeer) {
        // FIXME
        int dimension = this.getRandomDimension();
        int direction = 0;

        return new Boolean(this.addNeighbor(remotePeer, dimension, direction).booleanValue() &&
            ((CANJoinResponseMessage) PAFuture.getFutureValue(this.getLocalPeer().sendMessageTo(remotePeer,
                    new CANJoinMessage(this.getRemotePeer(), dimension, (direction + 1) % 2))))
                    .hasSucceeded());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void leave() {
        try {
            ResponseMessage groupFutures = null;
            Group<Peer> groupAvailablePeer = PAGroup.getGroup((Peer) PAGroup.newGroup(Peer.class.getName()));

            // Check if there is a valid neighbor
            for (Group<Peer>[] neighborsAxe : this.neighbors) {
                for (Group<Peer> group : neighborsAxe) {
                    groupFutures = (ResponseMessage) PAFuture.getFutureValue(this.getLocalPeer()
                            .sendMessageTo((Peer) group.getGroupByType(),
                                    new CANCheckMergeMessage(this.getArea())));
                    PAGroup.waitAll(groupFutures);

                    Iterator<ResponseMessage> it = PAGroup.getGroup(groupFutures).listIterator();

                    while (it.hasNext()) {
                        try {
                            CANCheckMergeResponseMessage resp = (CANCheckMergeResponseMessage) it.next();

                            if (resp.isMergeable()) {
                                groupAvailablePeer.add(resp.getPeer());
                            }
                        } catch (Exception e) {
                            // FIXME
                        }
                    }
                }
            }

            // Check if there is at least one
            if (groupFutures != null) {
                if (groupAvailablePeer.size() > 0) {
                    this.getLocalPeer().sendMessageTo(groupAvailablePeer.waitAndGetOne(),
                            new CANMergeMessage(this.getRemotePeer()));
                }
                // TODO Else : split more before merge !
            }

            // Unset peer as neighbor
            for (Group<Peer>[] neighborsAxe : this.neighbors) {
                for (Group<Peer> group : neighborsAxe) {
                    PAFuture.getFutureValue(this.getLocalPeer().sendMessageTo((Peer) group.getGroupByType(),
                            new LeaveMessage(this.getRemotePeer())));
                }
            }

            PAActiveObject.terminateActiveObject(false);
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
    @Override
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
    @Override
    public ResponseMessage sendMessageTo(Peer remotePeer, Message msg) {
        return remotePeer.receiveMessage(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        // TODO Auto-generated method stub
    }

    /**
     * Adds a new neighbor at the specified dimension and direction.
     * 
     * @param peer
     *            the new neighbor.
     * 
     * @param dimension
     *            the dimension index (must be in 0 and {@link #NB_DIMENSIONS - 1} include).
     * 
     * @param direction
     *            the direction (0 or 1).
     */
    public Boolean addNeighbor(Peer remotePeer, int dimension, int direction) {
        return this.neighbors[dimension][direction].add(remotePeer);
    }

    /**
     * Removes a neighbor.
     * 
     * @param peer
     *            the neighbor to remove.
     */
    public void removeNeighbor(Peer peer) {
        for (Group<Peer>[] neighborsAxe : this.neighbors) {
            for (Group<Peer> neighbor : neighborsAxe) {
                if (neighbor.contains(peer)) {
                    neighbor.remove(peer);
                }
            }
        }
    }

    /**
     * Indicates if the given peer is the neighbor of the current area.
     * 
     * @param peer
     *            the peer which is used to check.
     * 
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
     * Returns the neighbors of the managed area with a certain organization. It returns a
     * two-dimensional array of {@link Group}. Each line corresponds to a dimension. The number of
     * columns is always equal to two. The first column corresponds to the neighbors having a
     * coordinate lower than the current pair on the given dimension. The second column is the
     * reverse.
     * 
     * @return the neighbors of the managed area.
     */
    public Group<Peer>[][] getNeighbors() {
        return this.neighbors;
    }

    /**
     * Returns the neighbors of the managed area as a {@link Collection} without any direction.
     * 
     * @return the neighbors of the managed area as a {@link Collection} without any direction.
     */
    public Collection<Peer> getNeighborsAsCollection() {
        Collection<Peer> neighbors = new ArrayList<Peer>();

        for (Group<Peer>[] neighborsAxe : this.neighbors) {
            for (Group<Peer> neighborGroups : neighborsAxe) {
                Iterator<Peer> it = neighborGroups.iterator();
                while (it.hasNext()) {
                    neighbors.add(it.next());
                }
            }
        }

        return neighbors;
    }

    /**
     * Returns the neighbors of the managed area for the given dimension.
     * 
     * @param dimension
     *            the dimension to use (dimension start to 0 and max is defined by
     *            {@link #NB_DIMENSIONS} - 1).
     * @return the neighbors of the managed area for the given dimension.
     */
    public Group<Peer>[] getNeighborsForDimension(int dimension) {
        return this.neighbors[dimension];
    }

    /**
     * Returns the neighbors of the managed area for the given dimension and order.
     * 
     * @param dimension
     *            the dimension to use (dimension start to 0 and max is defined by
     *            {@link #NB_DIMENSIONS} - 1).
     * @param direction
     *            the direction (0 or 1).
     * @return the neighbors of the managed area for the given dimension.
     */
    public Group<Peer> getNeighborsForDimensionAndDirection(int dimension, int direction) {
        return this.neighbors[dimension][direction];
    }

    /**
     * Returns the neighbors that have coordinates smaller than the current peer for the given
     * dimension.
     * 
     * @param dimension
     *            the neighbors that have coordinates smaller than the current peer for the given
     *            dimension. the dimension to use (dimension start to 0 and max is defined by
     *            {@link #NB_DIMENSIONS} - 1).
     * @return
     */
    public Group<Peer> getInferiorNeighborsForDimension(int dimension) {
        return this.neighbors[dimension][0];
    }

    /**
     * Returns the neighbors that have coordinates larger than the current peer for the given
     * dimension.
     * 
     * @param dimension
     *            the dimension to use (dimension start to 0 and max is defined by
     *            {@link #NB_DIMENSIONS} - 1).
     * @return the neighbors that have coordinates larger than the current peer for the given
     *         dimension.
     */
    public Group<Peer> getSuperiorNeighborsForDimension(int dimension) {
        return this.neighbors[dimension][1];
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
     * {@inheritDoc}
     */
    @Override
    public CANLookupResponseMessage handleLookupMessage(LookupMessage msg) {
        return new CANLookupResponseMessage(msg.getCreationTimestamp(), this.getLocalPeer(),
            ((CANLookupMessage) msg).getCoordinates());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CANJoinResponseMessage handleJoinMessage(Message msg) {
        CANJoinMessage message = (CANJoinMessage) msg;
        this.addNeighbor(message.getPeer(), message.getDimesion(), message.getDirection());
        return new CANJoinResponseMessage(msg.getCreationTimestamp());

    }

    /**
     * Handles a {@link CANMergeMessage}.
     * 
     * @param msg
     *            the message.
     * 
     * @return the response.
     */
    public CANMergeResponseMessage handleMergeMessage(Message msg) {
        this.merge(((CANMergeMessage) msg).getPeer());
        return new CANMergeResponseMessage(msg.getCreationTimestamp());
    }

    /**
     * Handles a {@link CANCheckMergeMessage}.
     * 
     * @param msg
     *            the message.
     * 
     * @return the response.
     */
    public CANCheckMergeResponseMessage handleCheckMergeMessage(Message msg) {
        CANCheckMergeMessage message = (CANCheckMergeMessage) msg;
        return new CANCheckMergeResponseMessage(msg.getCreationTimestamp(), this.getRemotePeer(), this.area
                .isValidMergingArea(message.getArea()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EmptyResponseMessage handleLeaveMessage(LeaveMessage msg) {
        LeaveMessage message = msg;
        this.removeNeighbor(message.getPeer());

        return new EmptyResponseMessage(msg.getCreationTimestamp());
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
     * Sets the new area covered.
     * 
     * @param area
     *            the new area covered.
     */
    public void setArea(Area area) {
        this.area = area;
    }
}