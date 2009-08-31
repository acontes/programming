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
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.requests.BlockingRequestReceiver;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.AddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.AsynchronousMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.RemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can.CANAddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can.CANJoinMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can.CANLeaveMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can.CANMergeMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.can.CANRemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.SynchronousMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ActionResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.can.CANJoinResponseMessage;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResult;


/**
 * Content-Addressable Network (CAN) as a distributed infrastructure that provides hash table-like
 * functionality on Internet-like scales. The CAN is scalable, fault-tolerant and completely
 * self-organizing.
 * 
 * @author Alexandre Trovato
 * @author Fanny Kilanga
 * @author Laurent Pellegrino
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANOverlay extends StructuredOverlay {

    public static final int NB_DIMENSIONS = 3;

    private NeighborsDataStructure neighborsDataStructure;

    private Stack<SplitEntry> splitHistory;

    private Zone zone;

    /**
     * Constructor.
     * 
     * @param localPeer
     *            the local peer reference.
     */
    public CANOverlay(Peer localPeer) {
        super(localPeer);
        this.neighborsDataStructure = new NeighborsDataStructure();
        this.splitHistory = new Stack<SplitEntry>();
        this.zone = new Zone();
    }

    /**
     * Gets a random dimension number.
     * 
     * @return the random dimension number.
     */
    private int getRandomDimension() {
        return new Random().nextInt(CANOverlay.NB_DIMENSIONS);
    }

    /**
     * Gets a random direction number.
     * 
     * @return the random direction number.
     */
    private int getRandomDirection() {
        return new Random().nextInt(2);
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
        return this.splitHistory.push(new SplitEntry(dimension, direction)) != null;
    }

    /**
     * Check neighbors list for all dimensions in order to see if a peer is always a neighbor, if
     * not, it is removed from its neighbors.
     */
    private void updateNeighbors() {
        this.updateNeighbors(-1);
    }

    /**
     * Check neighbors list following a dimension in order to see if a peer is always a neighbor, if
     * it is, the neighbor is updated with the current zone, else, it is removed from its neighbors.
     * 
     * @param dimension
     *            the dimension to not check.
     */
    private void updateNeighbors(int dimension) {
        List<Peer> peersToRemove = new ArrayList<Peer>();

        for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
            if (dim != dimension) {
                for (int direction = 0; direction < 2; direction++) {
                    for (Peer neighbor : this.neighborsDataStructure.getNeighbors(dim, direction)) {
                        if (this.getZone().getBorderedDimension(
                                this.neighborsDataStructure.getZoneBy(neighbor)) == -1) {
                            peersToRemove.add(neighbor);
                        }
                    }
                }
            }
        }

        for (Peer peer : peersToRemove) {
            this.neighborsDataStructure.remove(peer);
        }
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
     * Returns the neighbors of the managed zone which is {@link NeighborsDataStructure} .
     * 
     * @return the neighbors of the managed zone.
     */
    public NeighborsDataStructure getNeighborsDataStructure() {
        return this.neighborsDataStructure;
    }

    /**
     * Returns the history of the splits.
     * 
     * @return the history of the splits.
     */
    public Stack<SplitEntry> getSplitHistory() {
        return this.splitHistory;
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
    public CANJoinResponseMessage handleJoinMessage(AsynchronousMessage msg) {
        int dimension = this.getRandomDimension();
        int direction = this.getRandomDirection();
        int directionInv = CANOverlay.getOppositeDirection(direction);

        // Gets the next dimension to split onto
        if (!this.splitHistory.empty()) {
            dimension = CANOverlay.getNextDimension(this.splitHistory.lastElement().getDimension());
        }

        // Splits zones
        Zone[] newZones = null;
        try {
            newZones = this.getZone().split(dimension);
        } catch (ZoneException e) {
            e.printStackTrace();
        }

        // Updates the zone of the peer which is already on the network
        this.setZone(newZones[direction]);
        this.saveSplit(dimension, direction);

        // Splits the data
        List<Statement> statementsResult = new ArrayList<Statement>();

        // Perform the data split only if the datastore contains statements
        if (this.getLocalPeer().getDataStorage().hasStatements()) {
            String[] names = new String[] { "s", "p", "o" };
            StringBuffer query = new StringBuffer();
            query.append("SELECT ?s ?p ?o WHERE {\n");
            query.append("  ?s ?p ?o .\n");
            query.append("  FILTER ( ");

            for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
                query.append("str(?");
                query.append(names[dim]);
                query.append(") > \"");
                query.append(newZones[directionInv].getCoordinateMin(dim));
                query.append("\" && str(?");
                query.append(names[dim]);
                query.append(") < \"");
                query.append(newZones[directionInv].getCoordinateMax(dim));
                query.append("\"");
                if (dim != CANOverlay.NB_DIMENSIONS - 1) {
                    query.append(" && ");
                }
            }

            query.append(").\n}");

            QueryResult<BindingSet> queryResults = this.getLocalPeer().getDataStorage().query(
                    QueryLanguage.SPARQL, query.toString());
            try {
                while (queryResults.hasNext()) {
                    BindingSet bindingSet = queryResults.next();
                    ValueFactory valueFactory = this.getLocalPeer().getDataStorage().getRepository()
                            .getValueFactory();
                    statementsResult.add(valueFactory.createStatement(valueFactory.createURI(bindingSet
                            .getValue("s").toString()), valueFactory.createURI(bindingSet.getValue("p")
                            .toString()), valueFactory.createURI(bindingSet.getValue("o").toString())));
                }
            } catch (QueryEvaluationException e) {
                e.printStackTrace();
            } finally {
                try {
                    queryResults.close();
                } catch (QueryEvaluationException e) {
                    e.printStackTrace();
                }
            }

            /*
             * Removes the outdated statements (statements that have been given to the new peer
             * which join the network) from the current peer
             */
            for (Statement stmt : statementsResult) {
                this.getLocalPeer().getDataStorage().remove(stmt);
            }
        }

        // Neighbors affected for the new peer which want to join the network
        NeighborsDataStructure neighborsOfThePeerWhichJoin = new NeighborsDataStructure();
        neighborsOfThePeerWhichJoin.addAll(this.neighborsDataStructure);
        neighborsOfThePeerWhichJoin.removeAll(dimension, direction);
        neighborsOfThePeerWhichJoin.add(this.getRemotePeer(), this.zone, dimension, direction);

        // Update the neighbors of the peer which is already on the network
        for (Peer neighborToRemove : this.neighborsDataStructure.getNeighbors(dimension, directionInv)) {
            PAFuture.waitFor(this.sendTo(neighborToRemove, new CANRemoveNeighborMessage(this.getRemotePeer(),
                dimension, direction)));
        }
        this.neighborsDataStructure.removeAll(dimension, directionInv);
        // Update neighbors in order to check the other dimensions
        this.updateNeighbors();

        Stack<SplitEntry> newHistory = null;
        try {
            newHistory = (Stack<SplitEntry>) MakeDeepCopy.WithObjectStream.makeDeepCopy(this.splitHistory);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new CANJoinResponseMessage(dimension, directionInv, newHistory, newZones[directionInv],
            neighborsOfThePeerWhichJoin, statementsResult);
    }

    /**
     * Handles a {@code CANLeaveMessage}.
     * 
     * @param msg
     *            the message that is handled.
     * @return the {@link ActionResponseMessage} response.
     */
    public ActionResponseMessage handleLeaveMessage(CANLeaveMessage msg) {
        boolean result = true;

        result &= this.neighborsDataStructure.remove(msg.getPeerToRemove());

        for (Peer newNeighbor : msg.getPeersToAdd()) {
            if (!this.getNeighborsDataStructure().hasNeighbor(newNeighbor)) {
                result &= this.neighborsDataStructure.add(newNeighbor, msg.getDimensionToAdd(), msg
                        .getDirectionToAdd());
            }
        }
        this.updateNeighbors();

        return new ActionResponseMessage(result);
    }

    /**
     * Handles a {@link CANMergeMessage}.
     * 
     * @param msg
     *            the message that is handled.
     * @return the {@link ActionResponseMessage} response.
     */
    public ActionResponseMessage handleMergeMessage(AsynchronousMessage msg) {
        CANMergeMessage message = (CANMergeMessage) msg;
        boolean result = true;

        try {
            this.setZone(this.zone.merge(message.getZone()));
            // this.getLocalPeer().getDataStorage().addData(message.getResources());
        } catch (ZoneException e) {
            e.printStackTrace();
            return new ActionResponseMessage(false);
        }

        int dimension = message.getDimension();
        int direction = message.getDirection();

        int index = -1, t = 0;
        for (SplitEntry entry : this.splitHistory) {
            if (entry.getDimension() == dimension && entry.getDirection() == direction) {
                index = t;
            }
            t++;
        }

        if (index >= 0) {
            this.splitHistory.remove(index);
        }

        result &= this.neighborsDataStructure.remove(message.getRemotePeerWhichIsLeaving(), dimension,
                direction);
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
     * {@inheritDoc}
     */
    public Boolean join(Peer remotePeerOnNetwork) {
        CANJoinResponseMessage response = (CANJoinResponseMessage) PAFuture.getFutureValue(super.sendTo(
                remotePeerOnNetwork, new CANJoinMessage(this.getRemotePeer())));

        /* Actions on local peer : the peer which join an existing peer */
        this.setZone(response.getAffectedZone());
        this.splitHistory = response.getSplitHistory();
        this.saveSplit(response.getAffectedDimension(), response.getAffectedDirection());
        this.neighborsDataStructure = response.getAffectedNeighborsDataStructure();

        for (Statement stmt : response.getAffectedStatements()) {
            this.getLocalPeer().getDataStorage().add(stmt);
        }

        for (int dimension = 0; dimension < CANOverlay.NB_DIMENSIONS; dimension++) {
            for (int direction = 0; direction < 2; direction++) {
                for (Peer neighbor : this.neighborsDataStructure.getNeighbors(dimension, direction)) {
                    PAFuture.waitFor(this.sendTo(neighbor, new CANAddNeighborMessage(this.getRemotePeer(),
                        this.zone, dimension, CANOverlay.getOppositeDirection(direction))));
                }
            }
        }
        this.updateNeighbors();

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean leave() {
        /* The current peer associated to this overlay is the only peer on the network */
        if (this.neighborsDataStructure.size() == 0) {
            return true;
        }

        SplitEntry lastSplitEntry = this.splitHistory.pop();
        int lastDimension = lastSplitEntry.getDimension();
        int lastDirection = lastSplitEntry.getDirection();

        List<Peer> neighborsToMergeWith = this.neighborsDataStructure.getNeighbors(lastDimension, CANOverlay
                .getOppositeDirection(lastDirection));

        /*
         * Notify all the neighbors the current peer is preparing to leave. That force the neighbors
         * to wait the current peer has finished to leave before to handle new requests.
         */
        for (Peer neighbor : this.getNeighborsDataStructure()) {
            neighbor.notifyNeighborStartLeave(this.getRemotePeer());
        }

        /*
         * Terminate the current body in order to notify all the neighbors that they can't send
         * message to the current remote peer. If they try they will receive a
         * BlockingRequestReceiverException : this exception is used by the tracker in order to
         * detect a peer which is preparing to leave when it have to add a peer on the network.
         */
        ((BlockingRequestReceiver) ((MigratableBody) super.getLocalPeer().getBody()).getRequestReceiver())
                .blockReception();

        switch (neighborsToMergeWith.size()) {
            case 0:
                /* We are alone on this pitiless world : nothing to do */
                break;
            case 1:
                this.sendTo(neighborsToMergeWith.get(0), new CANMergeMessage(this.getRemotePeer(),
                    lastDimension, lastDirection, new NeighborsDataStructure(), this.getZone(), this
                            .getLocalPeer().getDataStorage().query(
                                    this.getLocalPeer().getDataStorage().getRepository().getValueFactory()
                                            .createStatement(null, null, null))));
                break;
            default:
                Zone zoneToSplit = this.getZone();
                Zone[] newZones = null;

                /*
                 * For the last dimension and direction of the split, we split N-1 times, where N is
                 * the number of neighbors in the last dimension and last reverse direction from the
                 * current peer.
                 */
                for (int i = 0; i < neighborsToMergeWith.size() - 1; i++) {
                    try {
                        newZones = zoneToSplit.split(CANOverlay.getNextDimension(lastDimension),
                                this.neighborsDataStructure.getZoneBy(neighborsToMergeWith.get(i))
                                        .getCoordinateMax(CANOverlay.getNextDimension(lastDimension)));
                    } catch (ZoneException e) {
                        e.printStackTrace();
                    }

                    NeighborsDataStructure neighborsOfCurrentNeighbor = new NeighborsDataStructure();
                    neighborsOfCurrentNeighbor.addAll(this.neighborsDataStructure);

                    zoneToSplit = newZones[1];

                    /*
                     * Merge the new zones obtained with the suitable neighbors.
                     */
                    // FIXME The given Data are not good : we give all the resources to each
                    // neighbors to merge with
                    this.sendTo(neighborsToMergeWith.get(i), new CANMergeMessage(this.getRemotePeer(),
                        lastDimension, lastDirection, neighborsOfCurrentNeighbor, newZones[0], this
                                .getLocalPeer().getDataStorage().query(
                                        this.getLocalPeer().getDataStorage().getRepository()
                                                .getValueFactory().createStatement(null, null, null))));

                }
                break;
        }

        // this.neighborsDataStructure.removeAll(lastDimension,
        // CANOverlay.getOppositeDirection(lastDirection));

        /*
         * Send LeaveMessage in order to update the neighbors list.
         */
        for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
            for (int direction = 0; direction < 2; direction++) {
                for (Peer neighbor : this.neighborsDataStructure.getNeighbors(dim, direction)) {
                    if (!neighborsToMergeWith.contains(neighbor)) {
                        this.sendTo(neighbor, new CANLeaveMessage(this.getRemotePeer(), neighborsToMergeWith,
                            dim, CANOverlay.getOppositeDirection(direction)));
                    }
                }
            }
        }

        ((BlockingRequestReceiver) ((MigratableBody) super.getLocalPeer().getBody()).getRequestReceiver())
                .acceptReception();

        /*
         * Notify all the old neighbors of the peer which is leaving that it has terminated the
         * leave operation.
         */
        for (Peer neighbor : this.getNeighborsDataStructure()) {
            neighbor.notifyNeighborEndLeave(this.getRemotePeer());
        }
        for (Peer neighbor : neighborsToMergeWith) {
            neighbor.notifyNeighborEndLeave(this.getRemotePeer());
        }

        /*
         * Set all neighbors reference to null.
         */
        this.neighborsDataStructure.removeAll();

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void send(SynchronousMessage query) {
        query.route(this);
    }

    /**
     * Send a {@link AsynchronousMessage} to a list {@link Peer}.
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
    public List<ResponseMessage> sendTo(List<Peer> remotePeers, AsynchronousMessage msg) throws Exception {
        List<ResponseMessage> responses = new ArrayList<ResponseMessage>(remotePeers.size());

        for (Peer remotePeer : remotePeers) {
            responses.add(super.sendTo(remotePeer, msg));
        }

        return responses;
    }

    /**
     * Send a {@link AsynchronousMessage} to a peers that are managed by the
     * {@link NeighborsDataStructure}.
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
    public List<ResponseMessage> sendTo(NeighborsDataStructure dataStructure, AsynchronousMessage msg)
            throws Exception {
        List<ResponseMessage> responses = new ArrayList<ResponseMessage>();

        for (int dimension = 0; dimension < CANOverlay.NB_DIMENSIONS; dimension++) {
            for (int direction = 0; direction < 2; direction++) {
                for (Peer peer : dataStructure.getNeighbors(dimension, direction)) {
                    responses.add(super.sendTo(peer, msg));
                }
            }
        }

        return responses;
    }

    /**
     * Sets the new split history.
     * 
     * @param history
     *            the new split history to set.
     */
    public void setHistory(Stack<SplitEntry> history) {
        this.splitHistory = history;
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
     * {@inheritDoc}
     */
    public String toString() {
        return this.zone.toString();
    }

    /**
     * {@inheritDoc}
     */
    public void update() {
        super.update();

        /*
         * for (Peer neighbor : this.neighborsDataStructure) { if
         * (!PAActiveObject.pingActiveObject(neighbor)) { // TODO neighbors is not accessible } }
         */
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
     * Returns the opposite direction of the specified direction.
     * 
     * @param direction
     *            the specified direction.
     * 
     * @return the opposite direction.
     */
    public static int getOppositeDirection(int direction) {
        return (direction + 1) % 2;
    }

    /**
     * Returns the previous dimension following the specified dimension.
     * 
     * @param dimension
     *            the specified dimension.
     * @return the previous dimension following the specified dimension.
     */
    public static int getPreviousDimension(int dimension) {
        int dim = dimension - 1;
        if (dim < 0) {
            dim = CANOverlay.NB_DIMENSIONS;
        }
        return dim;
    }
}