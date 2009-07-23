package org.objectweb.proactive.extensions.structuredp2p.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.LexicographicCoordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.chord.ChordOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.requests.BlockingRequestReceiverException;
import org.objectweb.proactive.extensions.structuredp2p.core.requests.StructuredMetaObjectFactory;
import org.objectweb.proactive.extensions.structuredp2p.datastorage.DataStorage;
import org.objectweb.proactive.extensions.structuredp2p.datastorage.owlim.OWLIMStorage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.Message;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.QueryResponse;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ResponseMessage;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;


/**
 * Defines a peer which connects itself in structured network. The network topology is one of
 * {@link OverlayType}.
 * 
 * This class must never be instantiate directly. In order to create a new active peer you must use
 * the static functions {@link #newActivePeer(OverlayType)} and
 * {@link #newActivePeer(OverlayType, Node)}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class Peer implements InitActive, RunActive, Serializable {

    /**
     * The structured protocol which is used by the peer.
     */
    private StructuredOverlay structuredOverlay;

    /**
     * Responses associated to the oneWay search on the network.
     */
    private Map<UUID, QueryResponse> oneWayResponses = new HashMap<UUID, QueryResponse>();

    /**
     * Peer which are currently not accessible because they are preparing to leave.
     */
    private Set<Peer> peersWhichAreLeaving = new HashSet<Peer>();

    /**
     * The type of the overlay which is used by the peer. The type is equal to one of
     * {@link OverlayType}.
     */
    private OverlayType type;

    /**
     * Contains data that are stored in the peer.
     */
    private transient DataStorage dataStorage;

    /**
     * The stub associated to the current peer.
     */
    private Peer stub;

    /**
     * The no-argument constructor as commanded by ProActive.
     */
    public Peer() {
    }

    /**
     * Constructor.
     * 
     * @param type
     *            @ the type of the overlay which is used by the peer.
     */
    public Peer(OverlayType type) {
        this.type = type;
    }

    /**
     * Sends a {@link Query} on the network from the current peer.
     * 
     * @param query
     *            the message to send.
     * @return the response in agreement with the type of message sent.
     */
    public QueryResponse search(Query query) {
        UUID uid = UUID.randomUUID();
        query.setUUID(uid);
        this.structuredOverlay.send(query);

        synchronized (this.oneWayResponses) {
            while (this.oneWayResponses.get(uid) == null) {
                try {
                    this.oneWayResponses.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return this.oneWayResponses.get(uid);
    }

    /**
     * Sends a {@link Query} on the network without response.
     * 
     * @param query
     *            the query to send.
     */
    public void send(Query query) {
        this.structuredOverlay.send(query);
    }

    /**
     * Sends a {@link Message} to a known {@link Peer}.
     * 
     * @param remotePeer
     *            the peer to which we want to send the message.
     * @param msg
     *            the message to send.
     * 
     * @return the response in agreement with the type of message sent.
     * @throws Exception
     *             this exception appears when a message cannot be send to a peer.
     */
    public ResponseMessage sendTo(Peer remotePeer, Message msg) {
        ResponseMessage response = null;

        try {
            response = remotePeer.receiveMessage(msg);
        } catch (BlockingRequestReceiverException e) {
            System.out.println("Peer.sendTo()");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Adds the current peer in the network.
     * 
     * @param remotePeer
     *            the peer which serves as entry point.
     */
    public Boolean join(Peer remotePeer) throws Exception {
        return this.structuredOverlay.join(remotePeer);
    }

    /**
     * Unregister the peer from the current structured network.
     */
    public Boolean leave() {
        this.structuredOverlay.leave();
        return true;
    }

    /**
     * 
     * @param remoteNeighbor
     * @return
     */
    public boolean notifyNeighborStartLeave(Peer remoteNeighbor) {
        this.peersWhichAreLeaving.add(remoteNeighbor);
        return true;
    }

    /**
     * 
     * @param remoteNeighbor
     * @return
     */
    public boolean notifyNeighborEndLeave(Peer remoteNeighbor) {
        return this.peersWhichAreLeaving.remove(remoteNeighbor);
    }

    /**
     * Receive a message from an another peer.
     * 
     * @param msg
     *            the message to receive.
     * @return the response in correspondence with the received message.
     */
    public ResponseMessage receiveMessage(Message msg) {
        return msg.handle(this.structuredOverlay);
    }

    /**
     * Returns the data that are managed by the peer.
     * 
     * @return the data that are managed by the peer.
     */
    public DataStorage getDataStorage() {
        return this.dataStorage;
    }

    /**
     * Returns the stub associated to the current peer.
     * 
     * @return the stub associated to the current peer.
     */
    public Peer getStub() {
        return this.stub;
    }

    /**
     * Returns the {@link Body} of the current active object.
     * 
     * @return the {@link Body} of the current active object.
     */
    public Body getBody() {
        return PAActiveObject.getBodyOnThis();
    }

    /**
     * Returns the {@link StructuredOverlay} which is used by the peer.
     * 
     * @return the {@link StructuredOverlay} which is used by the peer.
     */
    public StructuredOverlay getStructuredOverlay() {
        return this.structuredOverlay;
    }

    /**
     * Returns the type of overlay that is used by the peer.
     * 
     * @return the type of overlay that is used by the peer.
     */
    public OverlayType getType() {
        return this.type;
    }

    /**
     * 
     * @return
     */
    public Set<Peer> getPeersWhichArePreparingToLeave() {
        return this.peersWhichAreLeaving;
    }

    /**
     * Sets the overlay.
     * 
     * @param structuredOverlay
     *            the new overlay to set.
     */
    public void setStructuredOverlay(StructuredOverlay structuredOverlay) {
        this.structuredOverlay = structuredOverlay;
    }

    /**
     * {@inheritDoc}
     */
    public void initActivity(Body body) {
        try {
            this.dataStorage = new OWLIMStorage();

            switch (this.type) {
                case CAN:
                    this.structuredOverlay = new CANOverlay(this);
                    break;
                case CHORD:
                    this.structuredOverlay = new ChordOverlay(this);
                    break;
                default:
                    throw new IllegalArgumentException("The peer type must be one of OverlayType.");
            }

            PAActiveObject.setImmediateService("search");
            this.stub = (Peer) PAActiveObject.getStubOnThis();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void runActivity(Body body) {
        Service service = new Service(body);

        while (body.isActive()) {
            Request request = null;
            if (this.peersWhichAreLeaving.size() > 0) {
                /*
                 * boolean receiveFromLeaver = false; request = service.blockingRemoveOldest();
                 * 
                 * for (Peer peer : this.peersWhichAreLeaving) { if
                 * (peer.getBody().getID().equals(request.getSender().getID())) {
                 * System.out.println("OK FIND !!!"); receiveFromLeaver = true;
                 * service.serve(request); break; } }
                 * 
                 * if (!receiveFromLeaver) { System.out.println("NT FIND");
                 */
                service.serveOldest("notifyNeighborEndLeave");
                // }
            } else {
                request = service.blockingRemoveOldest(StructuredOverlay.UPDATE_TIMEOUT);

                if (request == null) {
                    this.structuredOverlay.update();
                } else {
                    service.serve(request);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (!(o instanceof Peer)) {
            throw new IllegalArgumentException();
        }

        Peer peer = (Peer) o;

        if (this.getType() != peer.getType()) {
            return false;
        }

        if (this.getType() == OverlayType.CAN) {
            CANOverlay thisOverlay = (CANOverlay) this.getStructuredOverlay();
            CANOverlay peerOverlay = (CANOverlay) peer.getStructuredOverlay();

            return (thisOverlay.getZone().equals(peerOverlay.getZone()) && thisOverlay
                    .getNeighborsDataStructure().equals(peerOverlay.getNeighborsDataStructure()));
        } else if (this.getType() == OverlayType.CHORD) {
            // TODO Chord implementation
        }

        return false;
    }

    /**
     * Returns the oneWay responses.
     * 
     * @return the oneWay responses.
     */
    public Map<UUID, QueryResponse> getOneWayResponses() {
        return this.oneWayResponses;
    }

    /**
     * Create a new Peer ActiveObject.
     * 
     * @param type
     *            the type of the peer, which is one of {@link OverlayType}.
     * @param node
     *            the node used by the peer.
     * @return the new Peer object created.
     * @throws ActiveObjectCreationException
     * @throws NodeException
     */
    public static Peer newActivePeer(OverlayType type, Node node) throws ActiveObjectCreationException,
            NodeException {
        return (Peer) PAActiveObject.newActive(Peer.class.getName(), null, new Object[] { type }, node, null,
                new StructuredMetaObjectFactory());
    }

    /**
     * Create a new Peer ActiveObject.
     * 
     * @param type
     *            the type of the peer, which is one of {@link OverlayType}.
     * @return the new Peer object created.
     * @throws ActiveObjectCreationException
     * @throws NodeException
     */
    public static Peer newActivePeer(OverlayType type) throws ActiveObjectCreationException, NodeException {
        return Peer.newActivePeer(type, null);
    }

    public Boolean addData() {
        ValueFactory valueFactory = this.getDataStorage().getRepository().getValueFactory();
        this.getDataStorage().add(
                valueFactory.createStatement(valueFactory.createURI("http://" +
                    LexicographicCoordinate.random(10).getValue()), valueFactory.createURI("http://" +
                    LexicographicCoordinate.random(10).getValue()), valueFactory.createURI("http://" +
                    LexicographicCoordinate.random(10).getValue())));
        return true;
    }

    public Set<Statement> query(Statement stmt) {
        return this.dataStorage.query(stmt);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return this.structuredOverlay.toString();
    }
}
