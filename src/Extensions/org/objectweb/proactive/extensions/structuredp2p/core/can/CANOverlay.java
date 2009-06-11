package org.objectweb.proactive.extensions.structuredp2p.core.can;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.util.converter.MakeDeepCopy;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.exception.ZoneException;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.AddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.LeaveMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.messages.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.RemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANAddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANJoinMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANMergeMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANRemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.ActionResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.ResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.can.CANJoinResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.can.CANLookupResponseMessage;


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
    private NeighborsDataStructure neighbors;

    /**
     * The zone which is currently managed.
     */
    private Zone zone;

    /**
     * The history of zone splits.
     */
    private Stack<int[]> splitHistory;

    /**
     * Future state for the current peer when a neighbor leaves.
     */
    private CANMergeMessage futureState = null;

    /**
     * Constructor.
     * 
     * Initialize the neighbors array.
     */
    public CANOverlay(Peer localPeer) {
        super(localPeer);
        this.zone = new Zone();
        this.neighbors = new NeighborsDataStructure(localPeer.getStub());
        this.splitHistory = new Stack<int[]>();
    }

    /**
     * {@inheritDoc}
     */
    public Boolean join(Peer remotePeerOnNetwork) {
        try {
            CANJoinResponseMessage response = (CANJoinResponseMessage) PAFuture.getFutureValue(this
                    .sendMessageTo(remotePeerOnNetwork, new CANJoinMessage(this.getRemotePeer())));

            if (response.hasSucceeded()) {
                int dimension = response.getDimension();
                int direction = response.getDirection();

                /* Actions on local peer */
                this.setZone(response.getLocalZone());
                this.splitHistory = response.getSplitHistory();
                this.saveSplit(dimension, direction);
                this.neighbors.addAll(response.getNeighbors());

                this.updateNeighbors();

                this.neighbors.add(response.getRemotePeer(), response.getRemoteZone(), dimension, CANOverlay
                        .getOppositeDirection(direction));

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean leave() {
        /* If the split history is not empty, ie. there is more than one peer on the network */
        if (this.splitHistory.size() > 0) {
            int[] lastOperation = this.splitHistory.pop();
            int lastDimension = lastOperation[0];
            int lastDirection = lastOperation[1];

            List<ActionResponseMessage> responses = new ArrayList<ActionResponseMessage>();
            List<NeighborsDataStructure> neighbors = new ArrayList<NeighborsDataStructure>();

            List<Peer> neighborsToMergeWith = this.neighbors.getNeighbors(lastDimension, CANOverlay
                    .getOppositeDirection(lastDirection));
            Zone zoneToSplit = this.getZone();

            /*
             * Notify all the neighbors to remove the current peer which leave the network from
             * their neighbors list.
             */
            try {
                this.sendMessageTo(this.neighbors, new CANRemoveNeighborMessage(this.getRemotePeer(),
                    lastDimension, CANOverlay.getOppositeDirection(lastDirection)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*
             * For the last dimension and direction of the split, we split N-1 times, where N is the
             * number of neighbors in the last dimension and last reverse direction.
             */
            for (int i = 0; i < neighborsToMergeWith.size() - 1; i++) {
                Zone[] newZones = null;
                try {
                    Zone zone = this.neighbors.getZone(neighborsToMergeWith.get(i));
                    newZones = zoneToSplit.split(CANOverlay.getNextDimension(lastDimension), this.neighbors
                            .getZone(neighborsToMergeWith.get(i)).getCoordinateMax(
                                    CANOverlay.getNextDimension(lastDimension)));
                } catch (ZoneException e) {
                    e.printStackTrace();
                }

                NeighborsDataStructure neighborsOfCurrentNeighbor = new NeighborsDataStructure(
                    neighborsToMergeWith.get(i));
                neighborsOfCurrentNeighbor.addAll(this.neighbors);

                zoneToSplit = newZones[1];

                try {
                    ActionResponseMessage response = (ActionResponseMessage) PAFuture.getFutureValue(this
                            .sendMessageTo(neighborsToMergeWith.get(i), new CANMergeMessage(this
                                    .getRemotePeer(), lastDimension, lastDirection,
                                neighborsOfCurrentNeighbor, newZones[0], this.getLocalPeer().getDataStorage()
                                        .getDataFromZone(this.getZone()))));
                    responses.add(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                neighbors.add(neighborsOfCurrentNeighbor);
            }

            this.setZone(null);

            try {
                PAFuture.waitForAll(this.sendMessageTo(this.getNeighbors(), new LeaveMessage(this
                        .getRemotePeer())));
            } catch (Exception e) {
                // TODO: handle proactive exception on sending message / receive response
            }
        }

        /**
         * Set all neighbors reference to null.
         */
        this.neighbors.removeAll();

        return true;
    }

    /**
     * @return the splitHistory
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
        return this.neighbors.add(remotePeer, dimension, direction);
    }

    /**
     * Add all the neighbors managed by the specified <code>NeighborsDataStructure</code>.
     * 
     * @param neighbors
     *            the data structure used.
     * @return <code>true</code> if the neighbors have been add, <code>false</code> if not.
     */
    public boolean addNeighbor(NeighborsDataStructure neighbors) {
        return this.neighbors.addAll(neighbors);
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
                    for (Peer neighbor : this.neighbors.getNeighbors(dim, dir)) {
                        try {
                            ResponseMessage response = null;
                            if (this.getZone() == null || this.neighbors.getZone(neighbor) == null ||
                                this.getZone().getBorderedDimension(this.neighbors.getZone(neighbor)) == -1) {
                                response = this.sendMessageTo(neighbor, new CANRemoveNeighborMessage(this
                                        .getRemotePeer(), dim, CANOverlay.getOppositeDirection(dir)));
                                peers.add(neighbor);
                            } else {
                                response = this.sendMessageTo(neighbor, new CANAddNeighborMessage(this
                                        .getRemotePeer(), this.getZone(), dim, CANOverlay
                                        .getOppositeDirection(dir)));
                            }

                            PAFuture.waitFor(response);
                        } catch (Exception e) {
                            // TODO a response returns an exception
                        }
                    }

                    for (Peer peer : peers) {
                        this.neighbors.remove(peer, dim, dir);
                    }
                }
            }
        }
    }

    /**
     * Check neighbors list following for all dimensions in order to see if a peer is always a
     * neighbor, if not, it is removed from its neighbors.
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
        return this.neighbors.hasNeighbor(remotePeer);
    }

    /**
     * Add a split log to the split history.
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
    public void checkNeighbors() {
        for (Peer neighbor : this.neighbors) {
            try {
                this.sendMessageTo(neighbor, new PingMessage());
            } catch (Exception e) {
                // TODO a response returns an exception
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    public void update() {
        // TODO what is the update method for ?
    }

    /**
     * Returns the neighbors of the managed zone which is {@link NeighborsDataStructure} .
     * 
     * @return the neighbors of the managed zone.
     */
    public NeighborsDataStructure getNeighbors() {
        return this.neighbors;
    }

    /**
     * {@inheritDoc}
     */
    public CANLookupResponseMessage sendMessage(LookupMessage msg) {
        CANLookupMessage lookupMessage = (CANLookupMessage) msg;

        if (this.contains(lookupMessage.getCoordinates())) {
            return new CANLookupResponseMessage(msg.getCreationTimestamp(), msg.getNbSteps(), this
                    .getRemotePeer(), ((CANLookupMessage) msg).getCoordinates());
        } else {
            int pos;

            for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
                int direction = NeighborsDataStructure.SUPERIOR_DIRECTION;
                pos = this.contains(dim, lookupMessage.getCoordinates()[dim]);

                if (pos == -1) {
                    direction = NeighborsDataStructure.INFERIOR_DIRECTION;
                }

                if (pos != 0) {
                    List<Peer> neighbors = this.neighbors.getNeighbors(dim, direction);

                    if (neighbors.size() > 0) {
                        msg.incrementNbSteps();
                        Peer nearestPeer = this.neighbors.getNearestNeighborFrom(lookupMessage
                                .getCoordinates()[CANOverlay.getNextDimension(dim)], dim, direction);
                        try {
                            return (CANLookupResponseMessage) PAFuture.getFutureValue(this.sendMessageTo(
                                    nearestPeer, msg));
                        } catch (Exception e) {
                            // TODO: a response returns an exception
                            // nearest.remove(nearestPeer);
                            List<Peer> nearest = this.neighbors.getNeighbors(dim, direction);
                            for (Peer peer : nearest) {
                                try {
                                    msg.incrementNbSteps();
                                    return (CANLookupResponseMessage) PAFuture.getFutureValue(this
                                            .sendMessageTo(nearestPeer, msg));
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }

        // TODO
        throw new IllegalStateException("The searched position doesn't exist.");
    }

    /**
     * {@inheritDoc}
     */
    public ResponseMessage sendMessageTo(Peer remotePeer, Message msg) throws Exception {
        return this.getLocalPeer().sendMessageTo(remotePeer, msg);
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
    public List<ResponseMessage> sendMessageTo(List<Peer> remotePeers, Message msg) throws Exception {
        List<ResponseMessage> responses = new ArrayList<ResponseMessage>(remotePeers.size());

        for (Peer remotePeer : remotePeers) {
            responses.add(this.sendMessageTo(remotePeer, msg));
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
    public List<ResponseMessage> sendMessageTo(NeighborsDataStructure dataStructure, Message msg)
            throws Exception {
        List<ResponseMessage> responses = new ArrayList<ResponseMessage>();

        for (Peer remotePeer : dataStructure) {
            responses.add(this.sendMessageTo(remotePeer, msg));
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

        return new ActionResponseMessage(msg.getCreationTimestamp(), this.neighbors.add(remotePeer,
                remoteZone, dimension, direction));
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
        } catch (ZoneException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // Set neighbors for the new peer
        NeighborsDataStructure newNeighbors = new NeighborsDataStructure(message.getRemotePeer());
        newNeighbors.addAll(this.neighbors);
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
        this.neighbors.add(message.getRemotePeer(), newZones[directionInv], dimension, directionInv);

        return new CANJoinResponseMessage(msg.getCreationTimestamp(), this.getRemotePeer(),
            newZones[direction], dimension, directionInv, newZones[directionInv], newNeighbors, newHistory);
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handleLeaveMessage(LeaveMessage msg) {
        boolean res = true;

        if (this.futureState != null) {
            try {
                this.setZone(this.zone.merge(this.futureState.getZone()));
                this.getLocalPeer().getDataStorage().addData(this.futureState.getResources());
            } catch (ZoneException e) {
                e.printStackTrace();
                this.futureState = null;
                return new ActionResponseMessage(msg.getCreationTimestamp(), false);
            }

            int dimension = this.futureState.getDimension();
            int direction = this.futureState.getDirection();

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

            res &= this.neighbors.remove(this.futureState.getRemotePeer(), dimension, direction);
            res &= this.neighbors.addAll(this.futureState.getNeighbors());

            this.futureState = null;
            this.updateNeighbors();

        } else {
            res = this.neighbors.remove(msg.getPeer());
        }

        return new ActionResponseMessage(msg.getCreationTimestamp(), res);
    }

    /**
     * {@inheritDoc}
     */
    public CANLookupResponseMessage handleLookupMessage(LookupMessage msg) {
        return this.sendMessage(msg);
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
        if (this.futureState != null && !this.futureState.getRemotePeer().equals(message.getRemotePeer())) {
            return new ActionResponseMessage(msg.getCreationTimestamp(), false);
        }

        this.futureState = message;
        return new ActionResponseMessage(msg.getCreationTimestamp(), true);
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
        return new ActionResponseMessage(msg.getCreationTimestamp(), this.neighbors.remove(message
                .getRemotePeer(), message.getDimension(), message.getDirection()));
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