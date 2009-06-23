package org.objectweb.proactive.extensions.structuredp2p.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
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
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.chord.ChordOverlay;
import org.objectweb.proactive.extensions.structuredp2p.data.DataStorage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.Message;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.QueryResponse;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ResponseMessage;


/**
 * Defines a peer which connects itself in structured network. The network topology is one of
 * {@link OverlayType}.
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
     * The timeout to wait before to check neighbors via the call of
     * {@link StructuredOverlay#checkNeighbors()} .
     */
    public static final int CHECK_NEIGHBORS_TIMEOUT = 7777;

    /**
     * The structured protocol which is used by the peer.
     */
    private StructuredOverlay structuredOverlay;

    /**
     * Responses associated to the oneWay search on the network.
     */
    private Map<UUID, QueryResponse> oneWayResponses = new HashMap<UUID, QueryResponse>();

    /**
     * The type of the overlay which is used by the peer. The type is equal to one of
     * {@link OverlayType}.
     */
    private OverlayType type;

    /**
     * Contains data that are stored in the peer.
     */
    private DataStorage dataStorage;

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
        this.dataStorage = new DataStorage();
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
        System.out.println("Peer.search() " + System.identityHashCode(this.oneWayResponses));
        System.out.println("Peer.search() zone hashcode " +
            System.identityHashCode(((CANOverlay) this.getStructuredOverlay()).getZone()));
        this.structuredOverlay.send(query);
        System.out.println("Peer.search() " + this.oneWayResponses.getClass());
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
    public ResponseMessage sendTo(Peer remotePeer, Message msg) throws Exception {
        try {
            return remotePeer.receiveMessage(msg);
        } catch (Exception e) {
            throw new Exception("Error while sending a message to a peer.");
        }
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
        return this.structuredOverlay.leave();
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
    }

    /**
     * {@inheritDoc}
     */
    public void runActivity(Body body) {

        Service service = new Service(body);
        while (body.isActive()) {
            Request req = service.blockingRemoveOldest(Peer.CHECK_NEIGHBORS_TIMEOUT);
            if (req == null) {
                // this.structuredOverlay.checkNeighbors();
            } else {
                service.serve(req);
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
                null);
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

}
