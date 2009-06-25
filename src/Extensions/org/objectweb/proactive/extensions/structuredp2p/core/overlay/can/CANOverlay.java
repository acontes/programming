package org.objectweb.proactive.extensions.structuredp2p.core.overlay.can;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.body.migration.MigratableBody;
import org.objectweb.proactive.core.util.converter.MakeDeepCopy;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.requests.BlockingRequestReceiver;
import org.objectweb.proactive.extensions.structuredp2p.core.requests.BlockingRequestReceiverException;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.AddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.LeaveMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.Message;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.RemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can.CANAddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can.CANJoinMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can.CANMergeMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can.CANRemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ActionResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.can.CANJoinResponseMessage;


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
     * Neighbors of the {@link Zone} which is managed.
     */
    private NeighborsDataStructure neighborsDataStructure;

    /**
     * The zone which is currently managed.
     */
    private Zone zone;

    /**
     * The history of zone splits.
     */
    private Stack<int[]> splitHistory;

    /**
     * Constructor.
     * 
     * Initialize the neighbors array.
     */
    public CANOverlay(Peer localPeer) {
        super(localPeer);
        this.zone = new Zone();
        this.neighborsDataStructure = new NeighborsDataStructure(localPeer.getStub());
        this.splitHistory = new Stack<int[]>();
    }

    /**
     * {@inheritDoc}
     */
    public Boolean join(Peer remotePeerOnNetwork) throws Exception {

        CANJoinResponseMessage response = (CANJoinResponseMessage) PAFuture.getFutureValue(super.sendTo(
                remotePeerOnNetwork, new CANJoinMessage(this.getRemotePeer())));

        if (response.hasSucceeded()) {
            int dimension = response.getDimension();
            int direction = response.getDirection();

            /* Actions on local peer */
            this.setZone(response.getLocalZone());
            this.splitHistory = response.getSplitHistory();
            this.saveSplit(dimension, direction);
            this.neighborsDataStructure.addAll(response.getNeighbors());

            this.updateNeighbors();

            this.neighborsDataStructure.add(response.getRemotePeer(), response.getRemoteZone(), dimension,
                    CANOverlay.getOppositeDirection(direction));

            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean leave() {
        /* The current peer associated to this overlay is the only peer on the network */
        if (this.neighborsDataStructure.size() == 0) {
            return true;
        }

        // if (this.splitHistory.size() > 0) {
        int[] lastOperation = this.splitHistory.pop();
        int lastDimension = lastOperation[0];
        int lastDirection = lastOperation[1];

        List<ActionResponseMessage> responses = new ArrayList<ActionResponseMessage>();
        List<NeighborsDataStructure> neighbors = new ArrayList<NeighborsDataStructure>();

        List<Peer> neighborsToMergeWith = this.neighborsDataStructure.getNeighbors(lastDimension, CANOverlay
                .getOppositeDirection(lastDirection));

        /*
         * Terminate the current body in order to notify all the neighbors that they can't send
         * message to the current remote peer. If they try they will receive a
         * BlockingRequestReceiverException.
         */
        ((BlockingRequestReceiver) ((MigratableBody) super.getLocalPeer().getBody()).getRequestReceiver())
                .prohibitReception();

        switch (neighborsToMergeWith.size()) {
            case 0:
                break;
            case 1:
                try {
                    ActionResponseMessage response = (ActionResponseMessage) PAFuture.getFutureValue(this
                            .sendTo(neighborsToMergeWith.get(0), new CANMergeMessage(this.getRemotePeer(),
                                lastDimension, lastDirection, new NeighborsDataStructure(neighborsToMergeWith
                                        .get(0)), this.getZone(), this.getLocalPeer().getDataStorage()
                                        .getDataFromZone(this.getZone()))));
                    responses.add(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                Zone zoneToSplit = this.getZone();
                /*
                 * For the last dimension and direction of the split, we split N-1 times, where N is
                 * the number of neighbors in the last dimension and last reverse direction.
                 */
                for (int i = 0; i < neighborsToMergeWith.size() - 1; i++) {
                    Zone[] newZones = null;
                    try {
                        newZones = zoneToSplit.split(CANOverlay.getNextDimension(lastDimension),
                                this.neighborsDataStructure.getZone(neighborsToMergeWith.get(i))
                                        .getCoordinateMax(CANOverlay.getNextDimension(lastDimension)));
                    } catch (ZoneException e) {
                        e.printStackTrace();
                    }

                    NeighborsDataStructure neighborsOfCurrentNeighbor = new NeighborsDataStructure(
                        neighborsToMergeWith.get(i));
                    neighborsOfCurrentNeighbor.addAll(this.neighborsDataStructure);

                    zoneToSplit = newZones[1];

                    /*
                     * Merge the new zones obtained with the suitable neighbors.
                     */
                    try {
                        ActionResponseMessage response = (ActionResponseMessage) PAFuture.getFutureValue(this
                                .sendTo(neighborsToMergeWith.get(i), new CANMergeMessage(
                                    this.getRemotePeer(), lastDimension, lastDirection,
                                    neighborsOfCurrentNeighbor, newZones[0], this.getLocalPeer()
                                            .getDataStorage().getDataFromZone(this.getZone()))));
                        responses.add(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    neighbors.add(neighborsOfCurrentNeighbor);
                }
                break;
        }

        this.setZone(null);

        /*
         * Notify all the neighbors to remove the pear which leave from their neighbors data
         * structure.
         */
        try {
            PAFuture.waitForAll(this.sendTo(this.getNeighborsDataStructure(), new LeaveMessage(this
                    .getRemotePeer())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         * Set all neighbors reference to null.
         */
        this.neighborsDataStructure.removeAll();
        return true;
    }

    /**
     * Returns the history of the splits.
     * 
     * @return the history of the splits.
     */
    public Stack<int[]> getSplitHistory() {
        return this.splitHistory;
    }

    /**
     * Add a new neighbor at the specified <code>dimension</code>, <code>direction</code>. Warning,
     * the {@link Zone} contained by the peer which is specified in parameters must be initialized.
     * 
     * @param remotePeer
     *            the remote peer to add as neighbor.
     * @param dimension
     *            the dimension index (must be in 0 and {@link #NB_DIMENSIONS - 1} include).
     * @param direction
     *            the direction ({@link NeighborsDataStructure#INFERIOR_DIRECTION} or
     *            {@link NeighborsDataStructure#SUPERIOR_DIRECTION}).
     * @return <code>true</code> if the neighbor has been add, <code>false</code> if not.
     */
    public boolean addNeighbor(Peer remotePeer, int dimension, int direction) {
        return this.neighborsDataStructure.add(remotePeer, dimension, direction);
    }

    /**
     * Add all the neighbors managed by the specified <code>NeighborsDataStructure</code>.
     * 
     * @param neighbors
     *            the data structure used.
     * @return <code>true</code> if the neighbors have been add, <code>false</code> if not.
     */
    public boolean addNeighbor(NeighborsDataStructure neighbors) {
        return this.neighborsDataStructure.addAll(neighbors);
    }

    /**
     * Check neighbors list following a dimension in order to see if a peer is always a neighbor, if
     * it is, the neighbor is updated with the current zone, else, it is removed from its neighbors.
     * 
     * @param dimension
     *            the dimension to not check.
     */
    private void updateNeighbors(int dimension) {
        for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
            if (dim != dimension) {
                for (int dir = 0; dir < 2; dir++) {
                    ArrayList<Peer> peers = new ArrayList<Peer>();
                    for (Peer neighbor : this.neighborsDataStructure.getNeighbors(dim, dir)) {
                        try {
                            ResponseMessage response = null;
                            if (this.getZone() == null ||
                                this.neighborsDataStructure.getZone(neighbor) == null ||
                                this.getZone().getBorderedDimension(
                                        this.neighborsDataStructure.getZone(neighbor)) == -1) {
                                response = super.sendTo(neighbor, new CANRemoveNeighborMessage(this
                                        .getRemotePeer(), dim, CANOverlay.getOppositeDirection(dir)));
                                peers.add(neighbor);
                            } else {
                                response = super.sendTo(neighbor, new CANAddNeighborMessage(this
                                        .getRemotePeer(), this.getZone(), dim, CANOverlay
                                        .getOppositeDirection(dir)));
                            }

                            PAFuture.waitFor(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    for (Peer peer : peers) {
                        this.neighborsDataStructure.remove(peer, dim, dir);
                    }
                }
            }
        }
    }

    /**
     * Check neighbors list for all dimensions in order to see if a peer is always a neighbor, if
     * not, it is removed from its neighbors.
     */
    private void updateNeighbors() {
        this.updateNeighbors(-1);
    }

    /**
     * Indicates if the data structure contains the specified {@link Peer} as neighbor.
     * 
     * @param remotePeer
     *            the neighbor to check.
     * @return <code>true</code> if the data structure contains the peer as neighbor,
     *         <code>false</code> otherwise.
     */
    public boolean hasNeighbor(Peer remotePeer) {
        return this.neighborsDataStructure.hasNeighbor(remotePeer);
    }

    /**
     * Add a split entry to the split history.
     * 
     * @param dimension
     *            the last dimension used to split.
     * @param direction
     *            the last direction used by the split.
     * @return <code>true</code> if the save has succeeded, <code>false</code> otherwise.
     */
    private boolean saveSplit(int dimension, int direction) {
        return this.splitHistory.push(new int[] { dimension, direction }) != null;
    }

    /**
     * {@inheritDoc}
     */
    public void update() {
        super.update();

        for (Peer neighbor : this.neighborsDataStructure) {
            // if (!PAActiveObject.pingActiveObject(neighbor)) {
            // TODO neighbors is not accessible
            // }
        }
    }

    /**
     * Returns the neighbors of the managed zone which is {@link NeighborsDataStructure} .
     * 
     * @return the neighbors of the managed zone.
     */
    public NeighborsDataStructure getNeighborsDataStructure() {
        return this.neighborsDataStructure;
    }

    /**
     * {@inheritDoc}
     */
    public void send(Query query) {
        Coordinate[] coordinatesToReach = (Coordinate[]) query.getKeyToReach().getValue();
        if (this.contains(coordinatesToReach)) {
            query.handle(this);
        } else {
            int direction;
            int pos;

            for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
                direction = NeighborsDataStructure.SUPERIOR_DIRECTION;
                pos = this.contains(dim, coordinatesToReach[dim]);

                if (pos == -1) {
                    direction = NeighborsDataStructure.INFERIOR_DIRECTION;
                } else {
                }

                if (pos != 0) {
                    List<Peer> neighbors = this.neighborsDataStructure.getNeighbors(dim, direction);

                    if (neighbors.size() > 0) {
                        Peer nearestPeer = this.neighborsDataStructure.getNearestNeighborFrom(this.zone,
                                coordinatesToReach[CANOverlay.getNextDimension(dim)], dim, direction);
                        query.incrementNbSteps();

                        try {
                            nearestPeer.send(query);
         
                            
                        } catch (BlockingRequestReceiverException e) {
                            super.bufferizeQuery(query);
                            System.out.println("Blocking");
                        } catch (Exception e) {
                            // TODO Dirty Leave
                            System.out.println("DIRTY LEAVE");
                        }

                        break;
                    }
                }
            }
        }
    }

    /**
     * Send a {@link Message} to a list {@link Peer}.
     * 
     * @param remotePeers
     *            the list of peers to which to send the message.
     * @param msg
     *            the message to send.
     * 
     * @return the list of responses in agreement with the type of message sent.
     * @throws Exception
     *             this exception appears when a message cannot be send to a peer.
     */
    public List<ResponseMessage> sendTo(List<Peer> remotePeers, Message msg) throws Exception {
        List<ResponseMessage> responses = new ArrayList<ResponseMessage>(remotePeers.size());

        for (Peer remotePeer : remotePeers) {
            responses.add(super.sendTo(remotePeer, msg));
        }

        return responses;
    }

    /**
     * Send a {@link Message} to a peers that are managed by the {@link NeighborsDataStructure}.
     * 
     * @param dataStructure
     *            the peers managed by the data structure to which to send the message.
     * @param msg
     *            the message to send.
     * 
     * @return the list of responses in agreement with the type of message sent.
     * @throws Exception
     *             this exception appears when a message cannot be send to a peer.
     */
    public List<ResponseMessage> sendTo(NeighborsDataStructure dataStructure, Message msg) throws Exception {
        List<ResponseMessage> responses = new ArrayList<ResponseMessage>();

        for (Peer remotePeer : dataStructure) {
            responses.add(super.sendTo(remotePeer, msg));
        }

        return responses;
    }

    /**
     * Check if the coordinates in arguments are in the managed zone.
     * 
     * @param coordinates
     *            the coordinates to check.
     * 
     * @return true if the coordinates are in the zone, false otherwise.
     */
    public boolean contains(Coordinate[] coordinates) {
        return this.zone.contains(coordinates);
    }

    /**
     * Check if the dimension index of the current zone contains the specified coordinate.
     * 
     * @param dimension
     *            the dimension index used for the check.
     * 
     * @param coordinate
     *            the coordinate to check.
     * 
     * @return <code>0</code> if the coordinate is contained by the zone on the specified axe,
     *         <code>-1</code> if the coordinate is smaller than the line which is managed by the
     *         specified dimension, <code>1</code> otherwise.
     */
    public int contains(int dimension, Coordinate coordinate) {
        return this.getZone().contains(dimension, coordinate);
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handleAddNeighborMessage(AddNeighborMessage msg) {
        CANAddNeighborMessage message = (CANAddNeighborMessage) msg;
        int dimension = message.getDimension();
        int direction = message.getDirection();
        Peer remotePeer = message.getRemotePeer();
        Zone remoteZone = message.getRemoteZone();

        return new ActionResponseMessage(this.neighborsDataStructure.add(remotePeer, remoteZone, dimension,
                direction));
    }

    /**
     * {@inheritDoc}
     */
    public CANJoinResponseMessage handleJoinMessage(Message msg) {
        CANJoinMessage message = (CANJoinMessage) msg;

        int dimension = this.getRandomDimension();
        int direction = this.getRandomDirection();
        int directionInv = CANOverlay.getOppositeDirection(direction);

        // Get the next dimension to split onto
        if (!this.splitHistory.empty()) {
            dimension = CANOverlay.getNextDimension(this.splitHistory.lastElement()[0]);
        }

        // Create split zones
        Zone[] newZones = null;
        try {
            newZones = this.getZone().split(dimension);
        } catch (ZoneException e) {
            e.printStackTrace();
        }
        // Set neighbors for the new peer
        NeighborsDataStructure newNeighbors = new NeighborsDataStructure(message.getRemotePeer());
        newNeighbors.addAll(this.neighborsDataStructure);
        Stack<int[]> newHistory = null;

        try {
            newHistory = (Stack<int[]>) MakeDeepCopy.WithObjectStream.makeDeepCopy(this.splitHistory);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.setZone(newZones[direction]);
        this.saveSplit(dimension, direction);
        this.updateNeighbors();
        this.neighborsDataStructure.add(message.getRemotePeer(), newZones[directionInv], dimension,
                directionInv);

        return new CANJoinResponseMessage(this.getRemotePeer(), newZones[direction], dimension, directionInv,
            newZones[directionInv], newNeighbors, newHistory);
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handleLeaveMessage(LeaveMessage msg) {
        return new ActionResponseMessage(this.neighborsDataStructure.remove(msg.getPeer()));
    }

    /**
     * Handles a {@link CANMergeMessage}.
     * 
     * @param msg
     *            the message that is handled.
     * @return the {@link ActionResponseMessage} response.
     */
    public ActionResponseMessage handleMergeMessage(Message msg) {
        CANMergeMessage message = (CANMergeMessage) msg;
        boolean result = true;

        try {
            this.setZone(this.zone.merge(message.getZone()));
            this.getLocalPeer().getDataStorage().addData(message.getResources());
        } catch (ZoneException e) {
            e.printStackTrace();
            return new ActionResponseMessage(false);
        }

        int dimension = message.getDimension();
        int direction = message.getDirection();

        int index = -1, t = 0;
        for (int[] h : this.splitHistory) {
            if (h[0] == dimension && h[1] == direction) {
                index = t;
            }
            t++;
        }

        if (index >= 0) {
            this.splitHistory.remove(index);
        }

        result &= this.neighborsDataStructure.remove(message.getRemotePeer(), dimension, direction);
        result &= this.neighborsDataStructure.addAll(message.getNeighbors());

        this.updateNeighbors();

        return new ActionResponseMessage(result);
    }

    /**
     * Handles a {@link CANRemoveNeighborMessage}.
     * 
     * @param msg
     *            the message that is handled.
     * @return the {@link ActionResponseMessage} response.
     */
    public ActionResponseMessage handleRemoveNeighborMessage(RemoveNeighborMessage msg) {
        CANRemoveNeighborMessage message = ((CANRemoveNeighborMessage) msg);
        return new ActionResponseMessage(this.neighborsDataStructure.remove(message.getRemotePeer(), message
                .getDimension(), message.getDirection()));
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
     * Returns the next dimension following the specified dimension.
     * 
     * @param dimension
     *            the specified dimension.
     * @return the next dimension following the specified dimension.
     */
    public static int getNextDimension(int dimension) {
        return (dimension + 1) % CANOverlay.NB_DIMENSIONS;
    }

    /**
     * Gets the opposite direction number.
     * 
     * @return the opposite direction number.
     */
    public static int getOppositeDirection(int direction) {
        return (direction + 1) % 2;
    }

    /**
     * Returns the zone which is managed by the overlay.
     * 
     * @return the zone which is managed by the overlay.
     */
    public Zone getZone() {
        return this.zone;
    }

    /**
     * Sets the new zone covered.
     * 
     * @param zone
     *            the new zone covered.
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Sets the new split history.
     * 
     * @param history
     *            the new split history to set.
     */
    public void setHistory(Stack<int[]> history) {
        this.splitHistory = history;
    }
}