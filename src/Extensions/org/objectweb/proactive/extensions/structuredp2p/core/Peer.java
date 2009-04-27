package org.objectweb.proactive.extensions.structuredp2p.core;

import java.io.Serializable;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.util.converter.MakeDeepCopy.WithMarshallStream;
import org.objectweb.proactive.extensions.structuredp2p.data.DataStorage;
import org.objectweb.proactive.extensions.structuredp2p.message.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.response.LookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


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
     * The timeout to wait before the check of neighbors {@link WithMarshallStream}
     * {@link StructuredOverlay#checkNeighbors()}.
     */
    public static final int CHECK_NEIGHBORS_TIMEOUT = 1000;

    /**
     * The structured protocol which is used by the peer.
     */
    private StructuredOverlay structuredOverlay;

    /**
     * The type of the overlay which is used by the peer. The type is equal to one of
     * {@link OverlayType}.
     */
    private OverlayType type;

    /**
     * Contains data that are store in the peer.
     */
    private DataStorage dataStorage;

    /**
     * Time when the last request has been served.
     */
    private long lastRequestDuration;

    /**
     * The no-argument constructor as commanded by ProActive.
     */
    public Peer() {
    }

    /**
     * Constructor.
     * 
     * @param type
     *            the type of the overlay which is used by the peer.
     */
    public Peer(OverlayType type) {
        this.type = type;
    }

    /**
     * Constructor.
     * 
     * @param entrypoint
     *            the peer to join the overlay.
     */
    public Peer(Peer peer) {
        this(peer.getType());
        // FIXME
        this.join(peer);
    }

    /**
     * Sends a {@link LookupMessage} on the network from the current peer.
     * 
     * @param msg
     *            the message to send
     * @return the response in agreement with the type of message sent.
     */
    public LookupResponseMessage sendMessage(LookupMessage msg) {
        return this.structuredOverlay.sendMessage(msg);
    }

    /**
     * Sends a {@link Message} to a known {@link Peer}.
     * 
     * @param peer
     *            the peer to which we want to send the message.
     * @param msg
     *            the message to send.
     * 
     * @return the response in agreement with the type of message sent.
     */
    public ResponseMessage sendMessageTo(Peer peer, Message msg) {
        return peer.receiveMessage(msg);
    }

    /**
     * Adds the current peer in the network.
     * 
     * @param peer
     *            the peer which serves as entry point.
     */
    public void join(Peer peer) {
        this.structuredOverlay.join(peer);
    }

    /**
     * Unregister the peer from the current structured network.
     */
    public void leave() {
        this.structuredOverlay.leave();
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
     * Returns the {@link StructuredOverlay} which is used by the peer.
     * 
     * @return the {@link StructuredOverlay} which is used by the peer.
     */
    public StructuredOverlay getStructuredOverlay() {
        return this.structuredOverlay;
    }

    public void setStructuredOverlay(StructuredOverlay structuredOverlay) {
        this.structuredOverlay = structuredOverlay;
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
     * {@inheritDoc}
     */
    @Override
    public void initActivity(Body body) {
        switch (this.type) {
            case CAN:
                this.structuredOverlay = new CanOverlay(this);
                break;
            case CHORD:
                this.structuredOverlay = new ChordOverlay(this);
                break;
            default:
                throw new IllegalArgumentException("The peer type must be one of OverlayType.");
        }

        this.lastRequestDuration = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runActivity(Body body) {
        Service service = new Service(body);

        while (body.isActive()) {
            if (service.hasRequestToServe()) {
                service.serveOldest();
                this.lastRequestDuration = System.currentTimeMillis();
            } else {
                if (System.currentTimeMillis() - this.lastRequestDuration >= CHECK_NEIGHBORS_TIMEOUT) {
                    this.structuredOverlay.checkNeighbors();
                }
                service.waitForRequest();
            }

        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Peer))
            throw new IllegalArgumentException();

        Peer peer = (Peer) o;

        if (this.getType() != peer.getType())
            return false;

        if (this.getType() == OverlayType.CAN) {
            CanOverlay thisOverlay = (CanOverlay) this.getStructuredOverlay();
            CanOverlay peerOverlay = (CanOverlay) peer.getStructuredOverlay();

            return thisOverlay.getArea().equals(peerOverlay.getArea());
        } else if (this.getType() == OverlayType.CHORD) {
            // TODO
        }

        return false;
    }

    public Peer getStub() {
        return (Peer) PAActiveObject.getStubOnThis();
    }
}
