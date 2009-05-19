package org.objectweb.proactive.extensions.structuredp2p.core.overlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.util.converter.MakeDeepCopy;
import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.exception.AreaException;
import org.objectweb.proactive.extensions.structuredp2p.messages.AddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.LeaveMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.messages.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.RemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANAddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANCheckMergeMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANJoinMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANMergeMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANRemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANSwitchMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.ActionResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.LookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.ResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.can.CANCheckMergeResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.can.CANLookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.can.CANSwitchResponseMessage;


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
     * Neighbors of the {@link Area} which is managed.
     */
    private NeighborsDataStructure neighbors;

    /**
     * The area which is currently managed.
     */
    private Area area;

    /**
     * The history of area splits.
     */
    private Stack<int[]> splitHistory;

    /**
     * Constructor.
     * 
     * Initialize the neighbors array.
     */
    public CANOverlay(Peer localPeer) {
        super(localPeer);
        this.area = new Area();
        this.neighbors = new NeighborsDataStructure();
        this.splitHistory = new Stack<int[]>();
    }

    /**
     * {@inheritDoc}
     */
    public Boolean join(Peer remotePeerExisting) {
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
        NeighborsDataStructure newNeighbors;

        ActionResponseMessage response = null;
        try {
            newNeighbors = (NeighborsDataStructure) MakeDeepCopy.WithProActiveObjectStream
                    .makeDeepCopy(this.neighbors);

            newNeighbors.removeAll(dimension, directionInv);
            newNeighbors.add(this.getRemotePeer(), this.getArea(), dimension, directionInv);

            /* Actions on remote peer */
            response = (ActionResponseMessage) PAFuture.getFutureValue(this.sendMessageTo(remotePeerExisting,
                    new CANJoinMessage(newNeighbors, newArea[directionInv], this.splitHistory)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (response.hasSucceeded()) {
            /* Actions on local peer */
            this.neighbors.removeAll(dimension, direction);
            this.neighbors.add(remotePeerExisting, newArea[directionInv], dimension, direction);
            this.setArea(newArea[direction]);
            this.updateNeighbors(dimension);
            this.saveSplit(dimension, direction);

            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean leave() {
        if (this.splitHistory.size() > 0) {
            int[] lastOP = this.splitHistory.pop();
            int dimension = lastOP[0];
            int direction = lastOP[1];
            int directionInv = this.getOppositeDirection(direction);
            Area tmpArea = this.getArea();

            Collection<ActionResponseMessage> responses = new ArrayList<ActionResponseMessage>();
            for (Peer neighbor : this.neighbors.getNeighbors(dimension, direction)) {
                Coordinate border = this.neighbors.getArea(neighbor).getCoordinateMax(dimension);
                Area[] newAreas = tmpArea.split(dimension, border);

                CANMergeMessage message = new CANMergeMessage(this.getRemotePeer(), dimension, directionInv,
                    this.neighbors, newAreas[0], this.getLocalPeer().getDataStorage().getDataFromArea(
                            newAreas[0]));

                responses.add((ActionResponseMessage) PAFuture.getFutureValue(this.sendMessageTo(neighbor,
                        message)));

                tmpArea = newAreas[1];
            }

            this.setArea(null);
            this.updateNeighbors();
        }

        PAActiveObject.terminateActiveObject(false);

        return true;
    }

    /**
     * Add a new neighbor at the specified <code>dimension</code>, <code>direction</code>. Warning,
     * the {@link Area} contained by the peer which is specified in parameters must be initialized.
     * 
     * @param remotePeer
     *            the remote peer to add as neighbor.
     * @param dimension
     *            the dimension index (must be in 0 and {@link #NB_DIMENSIONS - 1} include).
     * @param direction
     *            the direction ({@link #INFERIOR_DIRECTION} or {@link #SUPERIOR_DIRECTION}).
     * @return <code>true</code> if the neighbor has been add, <code>false</code> if not or if the
     *         specified peer is already neighbor.
     */
    public boolean addNeighbor(Peer remotePeer, int dimension, int direction) {
        return this.neighbors.add(remotePeer, dimension, direction);
    }

    /**
     * Check neighbors list following a dimension in order to see if a peer is always a neighbor, if
     * not, it is removed from its neighbors.
     * 
     * @param dimension
     *            the dimension to not check.
     */
    private void updateNeighbors(int dimension) {

        for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            if (i != dimension) {
                for (int j = 0; j < 2; j++) {
                    ArrayList<Peer> peers = new ArrayList<Peer>();
                    for (Peer neighbor : this.neighbors.getNeighbors(i, j)) {

                        if (this.getArea() == null ||
                            this.getArea().getBorderedDimension(this.neighbors.getArea(neighbor)) == -1) {

                            if (((ActionResponseMessage) PAFuture.getFutureValue(this.sendMessageTo(neighbor,
                                    new CANRemoveNeighborMessage(this.getRemotePeer(), i, this
                                            .getOppositeDirection(j))))).hasSucceeded()) {
                                peers.add(neighbor);
                            }
                        }
                    }

                    for (Peer peer : peers) {
                        this.neighbors.remove(peer, i, j);
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
     * Updates the current peer with a new area and a new split history.
     * 
     * @param area
     *            the new area.
     * @param history
     *            the new split history.
     */
    private void update(Area area, Stack<int[]> history) {
        this.setArea(area);
        this.setHistory(history);
    }

    /**
     * {@inheritDoc}
     */
    public void checkNeighbors() {
        for (Peer neighbor : this.neighbors) {
            try {
                this.sendMessageTo(neighbor, new PingMessage());
            } catch (Exception e) {
                // TODO
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    public void update() {
        // TODO Auto-generated method stub
    }

    /**
     * Returns the neighbors of the managed area which is {@link NeighborsDataStructure} .
     * 
     * @return the neighbors of the managed area.
     */
    public NeighborsDataStructure getNeighbors() {
        return this.neighbors;
    }

    /**
     * {@inheritDoc}
     */
    public LookupResponseMessage sendMessage(LookupMessage msg) {
        CANLookupMessage lookupMessage = (CANLookupMessage) msg;

        if (this.contains(lookupMessage.getCoordinates())) {
            return lookupMessage.handle(this);
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

                    if (neighbors.size() > 1 && dim < CANOverlay.NB_DIMENSIONS - 1) {
                        return this.neighbors.getNearestNeighborFrom(lookupMessage.getCoordinates()[dim + 1],
                                dim, direction).sendMessageWithoutCallback(msg);
                    } else {
                        return neighbors.get(0).sendMessageWithoutCallback(msg);
                    }
                }
            }
        }

        throw new IllegalStateException("The searched position doesn't exist.");
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
    public Collection<ResponseMessage> sendMessageTo(List<Peer> remotePeers, Message msg) {
        Collection<ResponseMessage> responses = new HashSet<ResponseMessage>(remotePeers.size());

        for (Peer remotePeer : remotePeers) {
            try {
                responses.add(this.sendMessageTo(remotePeer, msg));
            } catch (Exception e) {
                // TODO
            }
        }

        return responses;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<ResponseMessage> sendMessageTo(NeighborsDataStructure remotePeers, Message msg) {
        Collection<ResponseMessage> responses = new ArrayList<ResponseMessage>();

        for (Peer remotePeer : remotePeers) {
            try {
                responses.add(this.sendMessageTo(remotePeer, msg));
            } catch (Exception e) {
                // TODO
            }
        }

        return responses;
    }

    /**
     * Switch a peer with
     * 
     * @param remotePeer
     * @return
     */
    private Peer switchWith(Peer remotePeer) {
        Boolean result = ((ActionResponseMessage) PAFuture.getFutureValue(this.getLocalPeer().sendMessageTo(
                remotePeer, new CANJoinMessage(this.neighbors, this.area, this.splitHistory))))
                .hasSucceeded();

        if (result) {
            this.setArea(null);
            this.setHistory(null);

            return this.getRemotePeer();
        }

        return null;
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
        return this.area.contains(coordinates);
    }

    /**
     * Check if the dimension index of the current area contains the specified coordinate.
     * 
     * @param dimension
     *            the dimension index used for the check.
     * 
     * @param coordinate
     *            the coordinate to check.
     * 
     * @return <code>0</code> if the coordinate is contained by the area on the specified axe,
     *         <code>-1</code> if the coordinate is smaller than the line which is managed by the
     *         specified dimension, <code>1</code> otherwise.
     */
    public int contains(int dimension, Coordinate coordinate) {
        return this.getArea().contains(dimension, coordinate);
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handleAddNeighborMessage(AddNeighborMessage msg) {
        CANAddNeighborMessage message = (CANAddNeighborMessage) msg;
        boolean condition = false;

        if (!this.neighbors.getNeighbors(message.getDimension(), message.getDirection()).contains(
                message.getPeer())) {
            condition = this.neighbors.add(message.getPeer(), message.getArea(), message.getDimension(),
                    message.getDirection());
        }

        return new ActionResponseMessage(msg.getCreationTimestamp(), condition);
    }

    /**
     * {@inheritDoc}
     */
    public CANCheckMergeResponseMessage handleCheckMergeMessage(Message msg) {
        CANCheckMergeMessage message = (CANCheckMergeMessage) msg;
        return new CANCheckMergeResponseMessage(msg.getCreationTimestamp(), this.getRemotePeer(), this.area
                .getBorderedDimension(message.getArea()) == 0);
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handleJoinMessage(Message msg) {
        CANJoinMessage message = (CANJoinMessage) msg;
        this.neighbors = message.getNeighbors();

        for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            for (int j = 0; j < 2; j++) {
                this.sendMessageTo(this.neighbors, new CANAddNeighborMessage(this.getRemotePeer(), this
                        .getArea(), i, j));
            }
        }

        this.update(message.getArea(), message.getSplitHistory());

        return new ActionResponseMessage(msg.getCreationTimestamp(), true);
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handleLeaveMessage(LeaveMessage msg) {
        return new ActionResponseMessage(msg.getCreationTimestamp(), this.neighbors.remove(msg.getPeer()));
    }

    /**
     * {@inheritDoc}
     */
    public CANLookupResponseMessage handleLookupMessage(LookupMessage msg) {
        return new CANLookupResponseMessage(msg.getCreationTimestamp(), this.getRemotePeer(),
            ((CANLookupMessage) msg).getCoordinates());
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handleMergeMessage(Message msg) {
        CANMergeMessage message = (CANMergeMessage) msg;

        try {
            this.setArea(this.area.merge(message.getArea()));
        } catch (AreaException e) {
            return new ActionResponseMessage(msg.getCreationTimestamp(), false);
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

        this.neighbors.remove(message.getRemotePeer(), dimension, direction);
        this.neighbors.addAll(this.neighbors);
        this.updateNeighbors();
        this.getLocalPeer().getDataStorage().addData(message.getResources());

        return new ActionResponseMessage(msg.getCreationTimestamp(), true);
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handleRemoveNeighborMessage(RemoveNeighborMessage msg) {
        CANRemoveNeighborMessage message = ((CANRemoveNeighborMessage) msg);
        return new ActionResponseMessage(msg.getCreationTimestamp(), this.neighbors.remove(message
                .getRemotePeer(), message.getDimension(), message.getDirection()));
    }

    /**
     * {@inheritDoc}
     */
    public CANSwitchResponseMessage handleSwitchMessage(Message msg) {
        this.switchWith(((CANSwitchMessage) msg).getPeer());
        return new CANSwitchResponseMessage(msg.getCreationTimestamp(), this.getRemotePeer());
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
     *            the new split history to set.
     */
    public void setHistory(Stack<int[]> history) {
        this.splitHistory = history;
    }
}