package org.objectweb.proactive.extensions.structuredp2p.core;

import java.io.Serializable;
import java.util.concurrent.Future;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.data.DataStorage;
import org.objectweb.proactive.extensions.structuredp2p.messages.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.responses.LookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.ResponseMessage;


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
    public static final int CHECK_NEIGHBORS_TIMEOUT = 10;

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
     * Contains data that are stored in the peer.
     */
    private DataStorage dataStorage;

    /**
     * Timestamp when the last request has been served.
     */
    private long lastRequestDuration;

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
     *            the type of the overlay which is used by the peer.
     */
    public Peer(OverlayType type) {
        this.type = type;
    }

    /**
     * Sends a {@link LookupMessage} on the network from the current peer.
     * 
     * @param msg
     *            the message to send
     * @return the response in agreement with the type of message sent.
     */
    public LookupResponseMessage sendMessage(LookupMessage msg) {
        LookupResponseMessage response = this.structuredOverlay.sendMessage(msg);
        // PAEventProgramming.addActionOnFuture(response, "setResponseMessageDeliveryTime");
        return response;
    }

    public ResponseMessage sendMessageToWithoutCallback(Peer remotePeer, Message msg) {
        return remotePeer.receiveMessage(msg);
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
    public ResponseMessage sendMessageTo(Peer remotePeer, Message msg) {
        ResponseMessage future = remotePeer.receiveMessage(msg);

        // Callback on ResponseMessage
        // PAEventProgramming.addActionOnFuture(future, "setResponseMessageDeliveryTime");

        return future;
    }

    /**
     * Setup the delivery timestamp of the {@link ResponseMessage}.
     * 
     * @param future
     *            the response message to initialize.
     */
    public void setResponseMessageDeliveryTime(Future<ResponseMessage> future) {
        try {
            ResponseMessage response = future.get();
            response.setDeliveryTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the current peer in the network.
     * 
     * @param peer
     *            the peer which serves as entry point.
     */
    public boolean join(Peer remotePeer) {
        return this.structuredOverlay.join(remotePeer);
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
     * Returns the stub associated to the current peer.
     * 
     * @return the stub associated to the current peer.
     */
    public Peer getStub() {
        return this.stub;
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
            default:
                throw new IllegalArgumentException("The peer type must be one of OverlayType.");
        }

        PAActiveObject.setImmediateService("setResponseMessageDeliveryTime");
        this.lastRequestDuration = System.currentTimeMillis();
        this.stub = (Peer) PAActiveObject.getStubOnThis();
    }

    /**
     * {@inheritDoc}
     */
    public void runActivity(Body body) {
        Service service = new Service(body);
        while (body.isActive()) {
            if (service.hasRequestToServe()) {
                service.serveOldest();
                this.lastRequestDuration = System.currentTimeMillis();
            } else {
                if (System.currentTimeMillis() - this.lastRequestDuration >= Peer.CHECK_NEIGHBORS_TIMEOUT) {
                    // this.structuredOverlay.checkNeighbors();
                }
                service.waitForRequest();
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

            return (thisOverlay.getArea().equals(peerOverlay.getArea()) && thisOverlay.getNeighbors().equals(
                    peerOverlay.getNeighbors()));
        } else if (this.getType() == OverlayType.CHORD) {
            // TODO
        }

        return false;
    }
}
