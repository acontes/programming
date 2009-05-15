package org.objectweb.proactive.extensions.structuredp2p.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.api.PAGroup;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.util.converter.MakeDeepCopy;
import org.objectweb.proactive.extensions.structuredp2p.core.exception.AreaException;
import org.objectweb.proactive.extensions.structuredp2p.message.AddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.CANAddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.CANCheckMergeMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.CANJoinMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.CANLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.CANMergeMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.CANSwitchMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LeaveMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANAddNeighborResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANCheckMergeResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANJoinResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANLookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANMergeResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANSwitchResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.EmptyResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.LookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.PingResponseMessage;
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
    public static final int NB_DIMENSIONS = 2;

    /**
     * Neighbors of the current area. The neighbors are an array of ProActive groups. It is a
     * two-dimensional array of {@link Group}. Each line corresponds to a dimension. The number of
     * columns is always equal to two. The first column corresponds to the neighbors having a
     * coordinate lower than the current pair on the given dimension. The second column is the
     * reverse.
     */
    // private Group<Peer>[][] neighbors;
    private HashMap<Peer, Area>[][] neighbors;

    /**
     * The area which is currently managed.
     */
    private Area area;

    /**
     * The history of area changes.
     */
    private ArrayList<int[]> splitHistory;

    /**
     * Constructor.
     * 
     * Initialize the neighbors array.
     */
    public CANOverlay(Peer localPeer) {
        super(localPeer);
        this.splitHistory = new ArrayList<int[]>();
        // FIXME warning ?
        this.neighbors = new HashMap[CANOverlay.NB_DIMENSIONS][2];
        this.area = new Area();

        int i;
        for (i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            this.neighbors[i][0] = new HashMap<Peer, Area>();
            this.neighbors[i][1] = new HashMap<Peer, Area>();
        }
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
            if (!(res &= (this.getArea().contains(i, coordinates[i]) == 0))) {
                return false;
            }
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
    private boolean merge(Peer remotePeer) {
        CANOverlay overlay = ((CANOverlay) remotePeer.getStructuredOverlay());
        Area remoteArea = overlay.getArea();
        try {
            this.area.merge(remoteArea);
            this.getLocalPeer().getDataStorage().addData(remotePeer.getDataStorage());
            this.splitHistory.remove(this.splitHistory.size() - 1);
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
        for (HashMap<Peer, Area>[] neighborDim : this.neighbors) {
            for (HashMap<Peer, Area> neighborDir : neighborDim) {
                for (Peer neighbor : neighborDir.keySet()) {
                    PingResponseMessage response = (PingResponseMessage) PAFuture.getFutureValue(this
                            .getLocalPeer().sendMessageTo(neighbor, new PingMessage()));

                    if (response == null) {
                        // (Peer)e.getObject();
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Boolean join(Peer remotePeer) {
        int dimension = this.getRandomDimension();
        int direction = this.getRandomDirection();
        int directionInv = this.getOppositeDirection(direction);

        // Get the next dimension to split onto
        if (this.splitHistory != null && this.splitHistory.size() > 0) {
            dimension = (this.splitHistory.get(this.splitHistory.size() - 1)[0] + 1) %
                CANOverlay.NB_DIMENSIONS;
        }

        // Create split areas
        Area[] newArea = this.getArea().split(dimension);

        // Set neighbors for the new peer
        HashMap<Peer, Area>[][] newNeighbors;

        CANJoinResponseMessage response = null;
        try {
            newNeighbors = (HashMap<Peer, Area>[][]) MakeDeepCopy.WithProActiveObjectStream
                    .makeDeepCopy(this.neighbors);

            newNeighbors[dimension][directionInv].clear();
            newNeighbors[dimension][directionInv].put(this.getRemotePeer(), this.getArea());

            // Actions on remotePeer

            response = (CANJoinResponseMessage) this.getLocalPeer().sendMessageTo(remotePeer,
                    new CANJoinMessage(newNeighbors, this.splitHistory));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (PAFuture.getFutureValue(response) != null) {
            // Actions on local peer
            this.removeNeighbors(dimension, direction);
            this.addNeighbor(remotePeer, dimension, direction, newArea[directionInv]);
            this.setArea(newArea[direction]);
            this.checkNeighbors(dimension);
            this.rememberSplit(dimension, direction);

            return true;
        }

        // FIXME
        /*
         * return new Boolean(this.addNeighbor(remotePeer, dimension, direction).booleanValue() &&
         * ((CANJoinResponseMessage)
         * PAFuture.getFutureValue(this.getLocalPeer().sendMessageTo(remotePeer, new
         * CANJoinMessage(this.getRemotePeer(), dimension, (direction + 1) % 2)))) .hasSucceeded());
         */
        return false;
    }

    private void checkNeighbors(int dimension) {
        for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            if (i != dimension) {
                for (int j = 0; j < 2; j++) {
                    for (Peer neighbor : this.neighbors[i][j].keySet()) {
                        if (this.getArea().getBorderedDimension(this.neighbors[i][j].get(neighbor)) == -1) {
                            this.sendMessageTo(neighbor, new RemoveNeighborMessage(this.getRemotePeer()), i,
                                    this.getOppositeDirection(j));
                            this.neighbors[i][j].remove(neighbor);
                        } else {
                            this.sendMessageTo(neighbor, new UpdateNeighborMessage(this.getRemotePeer()),
                                    this.getArea());
                        }
                    }
                }
            }
        }
    }

    private boolean rememberSplit(int dimension, int direction) {
        return this.splitHistory.add(new int[] { dimension, direction });
    }

    /**
     * {@inheritDoc}
     */
    public Peer leave() {
        /*
         * if (this.splitHistory.size() > 0) {
         * 
         * int[] lastOP = this.splitHistory.get(this.splitHistory.size() - 1); int dimension =
         * lastOP[0]; int direction = lastOP[1]; Group<Peer> neighbors =
         * this.getNeighborsForDimensionAndDirection(dimension, direction); int nbNeigbors =
         * neighbors.size();
         * 
         * // If there is just one neighbor, easy if (nbNeigbors == 1) {
         * this.getLocalPeer().sendMessageTo(neighbors.waitAndGetOne(), new
         * CANMergeMessage(this.getRemotePeer())); } // Else, do the same thing recursively (it's a
         * little heavy with data transfer) else if (nbNeigbors > 1) {
         * 
         * this.switchWith(((CANSwitchResponseMessage) PAFuture
         * .getFutureValue(this.getLocalPeer().sendMessageTo(neighbors.waitAndGetOne(), new
         * CANSwitchMessage(this.getRemotePeer())))).getPeer()); }
         * 
         * }
         * 
         * PAFuture.getFutureValue(this.getLocalPeer().sendMessageTo( (Peer)
         * this.getNeighborsAsGroup().getGroupByType(), new LeaveMessage(this.getRemotePeer())));
         * 
         * // FIXME how to set non active with recursivity ?? //
         * PAActiveObject.terminateActiveObject(false);
         */
        return this.getRemotePeer();
    }

    /**
     * {@inheritDoc}
     */
    public void update() {
        // TODO Auto-generated method stub
    }

    private void update(Area area, ArrayList<int[]> history) {
        this.setArea(area);
        this.setHistory(history);
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
    public Boolean addNeighbor(Peer remotePeer, int dimension, int direction, Area coordinates) {
        return this.neighbors[dimension][direction].containsValue(this.neighbors[dimension][direction].put(
                remotePeer, coordinates));
    }

    /**
     * Removes a neighbor.
     * 
     * @param peer
     *            the neighbor to remove.
     */
    public Boolean removeNeighbor(Peer peer) {
        for (HashMap<Peer, Area>[] neighborsAxe : this.neighbors) {
            for (HashMap<Peer, Area> neighbor : neighborsAxe) {
                if (neighbor.containsKey(peer)) {
                    return neighbor.containsKey(neighbor.remove(peer));
                }
            }
        }

        return false;
    }

    /**
     * Removes a neighbor(s) at specified position.
     * 
     * @param dimension
     *            the dimension.
     * @param direction
     *            the direction.
     */
    public void removeNeighbors(int dimension, int direction) {
        this.neighbors[dimension][direction].clear();
    }

    /**
     * Indicates if the given peer is the neighbor of the current area.
     * 
     * @param peer
     *            the peer which is used to check.
     * 
     * @return true if the peer is a neighbor, false otherwise.
     */
    public Boolean hasNeighbor(Peer peer) {
        for (HashMap<Peer, Area>[] neighborsAxe : this.neighbors) {
            for (HashMap<Peer, Area> neighbor : neighborsAxe) {
                return neighbor.containsKey(neighbor);
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
    public HashMap<Peer, Area>[][] getNeighbors() {
        return this.neighbors;
    }

    /**
     * Returns the neighbors of the managed area as a {@link Group} without any direction.
     * 
     * @return the neighbors of the managed area as a {@link Group} without any direction.
     */
    public Peer getNeighborsAsGroup() {
        Peer allNeighbors = null;
        try {
            allNeighbors = (Peer) PAGroup.newGroup(Peer.class.getCanonicalName());
            for (HashMap<Peer, Area>[] neighborsDim : this.neighbors) {
                for (HashMap<Peer, Area> neighborDir : neighborsDim) {
                    PAGroup.getGroup(allNeighbors).addAll(neighborDir.keySet());
                }
            }

        } catch (ClassNotReifiableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return allNeighbors;
    }

    /**
     * Returns the neighbors of the managed area as a {@link Collection} without any direction.
     * 
     * @return the neighbors of the managed area as a {@link Collection} without any direction.
     */
    public Collection<Peer> getNeighborsAsCollection() {
        Collection<Peer> neighbors = new ArrayList<Peer>();

        for (HashMap<Peer, Area>[] neighborsDim : this.neighbors) {
            for (HashMap<Peer, Area> neighborDir : neighborsDim) {
                neighbors.addAll(neighborDir.keySet());
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
    public HashMap<Peer, Area>[] getNeighborsForDimension(int dimension) {
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
    public HashMap<Peer, Area> getNeighborsForDimensionAndDirection(int dimension, int direction) {
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
    public HashMap<Peer, Area> getInferiorNeighborsForDimension(int dimension) {
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
    public HashMap<Peer, Area> getSuperiorNeighborsForDimension(int dimension) {
        return this.neighbors[dimension][1];
    }

    /**
     * Gets a random dimension number.
     * 
     * @return the random dimension number.
     */
    private int getRandomDimension() {
        Random rand = new Random();
        return rand.nextInt(CANOverlay.NB_DIMENSIONS);
    }

    /**
     * Gets a random direction number.
     * 
     * @return the random direction number.
     */
    private int getRandomDirection() {
        Random rand = new Random();
        return rand.nextInt(2);
    }

    /**
     * Gets the opposite direction number.
     * 
     * @return the opposite direction number.
     */
    private int getOppositeDirection(int direction) {
        return (direction + 1) % 2;
    }

    /**
     * {@inheritDoc}
     */
    public LookupResponseMessage sendMessage(LookupMessage msg) {
        CANLookupMessage msgCan = (CANLookupMessage) msg;
        System.out.println("CANOverlay.sendMessage()");
        if (this.contains(msgCan.getCoordinates())) {
            return msgCan.handle(this);
        } else {
            int pos;
            int neighborIndex;

            for (HashMap<Peer, Area>[] remotePeers : this.neighbors) {
                for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
                    pos = this.contains(i, msgCan.getCoordinates()[i]);
                    System.out.println("i" + i);
                    if (pos == -1) {
                        // this.sendMessageTo(((Peer) neighborsGroup[0].getGroupByType()),
                        // new LoadBalancingMessage());
                        // neighborIndex = neighborsGroup[0].waitOneAndGetIndex();
                        // System.out.println("index = " + neighborIndex);
                        /*
                         * ResponseMessage response = ((Peer) neighborsGroup[0].getGroupByType())
                         * .receiveMessage(new LoadBalancingMessage());
                         */
                        // neighborIndex = neighborsGroup[0].waitOneAndGetIndex();
                        return null;

                        /*
                         * neighborIndex = PAGroup.waitOneAndGetIndex(response);
                         * System.out.println("index = " + neighborIndex); return
                         * neighborsGroup[0].get(neighborIndex).sendMessage(msg);
                         */
                    } else if (pos == 1) {
                        // this.sendMessageTo(((Peer) neighborsGroup[1].getGroupByType()),
                        // new LoadBalancingMessage());
                        /*
                         * ResponseMessage response = ((Peer) neighborsGroup[1].getGroupByType())
                         * .receiveMessage(new LoadBalancingMessage()); neighborIndex =
                         * neighborsGroup[1].waitOneAndGetIndex(); System.out.println("index = " +
                         * neighborIndex); return
                         * neighborsGroup[1].get(neighborIndex).sendMessage(msg);
                         */
                        return null;
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
        return this.getLocalPeer().sendMessageTo(remotePeer, msg);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<ResponseMessage> sendMessageTo(Collection<Peer> remotePeers, Message msg) {
        Collection<ResponseMessage> responses = new HashSet<ResponseMessage>(remotePeers.size());

        for (Peer remotePeer : remotePeers) {
            responses.add(this.sendMessageTo(remotePeer, msg));
        }

        return responses;
    }

    /**
     * {@inheritDoc}
     */
    public CANLookupResponseMessage handleLookupMessage(LookupMessage msg) {
        return new CANLookupResponseMessage(msg.getCreationTimestamp(), this.getLocalPeer(),
            ((CANLookupMessage) msg).getCoordinates());
    }

    /**
     * {@inheritDoc}
     */
    public CANJoinResponseMessage handleJoinMessage(Message msg) {
        CANJoinMessage message = (CANJoinMessage) msg;
        this.neighbors = message.getNeighbors();

        int i;
        for (i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            int j;
            for (j = 0; j < 2; j++) {
                this.sendMessageTo(this.neighbors[i][j].keySet(), new CANAddNeighborMessage(this
                        .getRemotePeer(), i, j));
            }
        }

        this.update(message.getArea(), message.getHistory());

        return new CANJoinResponseMessage(msg.getCreationTimestamp(), true);
    }

    /**
     * {@inheritDoc}
     */
    public EmptyResponseMessage handleLeaveMessage(LeaveMessage msg) {
        LeaveMessage message = msg;
        this.removeNeighbor(message.getPeer());

        return new EmptyResponseMessage(msg.getCreationTimestamp());
    }

    /**
     * {@inheritDoc}
     */
    public CANAddNeighborResponseMessage handleAddNeighborMessage(AddNeighborMessage msg) {
        CANAddNeighborMessage message = (CANAddNeighborMessage) msg;

        if (!this.neighbors[message.getDimesion()][message.getDirection()].containsKey(message.getPeer())) {
            this.addNeighbor(message.getPeer(), message.getDimesion(), message.getDirection(), message
                    .getArea());
        }

        return new CANAddNeighborResponseMessage(msg.getCreationTimestamp());
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
     * Handles a {@link CANSwitchMessage}.
     * 
     * @param msg
     *            the message.
     * 
     * @return the response.
     */
    public CANSwitchResponseMessage handleSwitchMessage(CANSwitchMessage msg) {
        this.switchWith(msg.getPeer());
        return new CANSwitchResponseMessage(msg.getCreationTimestamp(), this.getRemotePeer());
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

    /**
     * Sets the new split history.
     * 
     * @param area
     *            the new split history.
     */
    public void setHistory(ArrayList<int[]> history) {
        this.splitHistory = history;
    }

    /**
     * Switch a peer with
     * 
     * @param remotePeer
     * @return
     */
    private Peer switchWith(Peer remotePeer) {
        Boolean result = ((CANJoinResponseMessage) PAFuture.getFutureValue(this.getLocalPeer().sendMessageTo(
                remotePeer, new CANJoinMessage(this.neighbors, this.area, this.splitHistory))))
                .hasSucceeded();

        if (result) {
            this.setArea(null);
            this.setHistory(null);

            return this.getRemotePeer();
        }

        return null;
    }
}