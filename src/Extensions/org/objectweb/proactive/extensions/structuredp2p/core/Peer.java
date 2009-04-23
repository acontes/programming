package org.objectweb.proactive.extensions.structuredp2p.core;

import java.io.Serializable;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.core.util.converter.MakeDeepCopy.WithMarshallStream;
import org.objectweb.proactive.extensions.structuredp2p.data.DataStorage;
import org.objectweb.proactive.extensions.structuredp2p.message.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
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
     * The timeout to wait before check for neighbors {@link WithMarshallStream}
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
     * 
     * @param msg
     * @return
     */
    public ResponseMessage sendMessage(LookupMessage msg) {
        return this.structuredOverlay.sendMessage(msg);
    }

    /**
     * 
     * @param peer
     * @param msg
     * @return
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
     * FIXME
     * 
     * @return
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
}
