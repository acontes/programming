package org.objectweb.proactive.extensions.structuredp2p.core;

import java.io.Serializable;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.extensions.structuredp2p.data.DataStorage;
import org.objectweb.proactive.extensions.structuredp2p.message.CanMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.ChordMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CanResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ChordResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.LoadBalancingResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.PingResponseMessage;
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
public class Peer implements InitActive, Serializable {
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
     * Sends a message to a peer.
     * 
     * @param peer
     *            the peer we want to send.
     * @param msg
     *            the message to send.
     */
    public void sendMessageTo(Peer peer, Message msg) {
        this.structuredOverlay.sendMessageTo(peer, msg);
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
        return msg.handle(this);
    }

    /**
     * Handles a ping request.
     * 
     * @param msg
     *            the ping request that is receive.
     * @return the ping response.
     */
    public PingResponseMessage handlePingMessage(PingMessage msg) {
        return new PingResponseMessage();
    }

    /**
     * Handles a CAN request.
     * 
     * @param msg
     *            the ping request that is receive.
     * @return the ping response.
     */
    public CanResponseMessage handleCanMessage(CanMessage msg) {
        CanOverlay canOverlay = (CanOverlay) this.structuredOverlay;

        if (canOverlay.contains(msg.getCoordinates())) {
            return new CanResponseMessage(this);
        } else {
            Group<Peer>[][] neighbors = canOverlay.getNeighbors();
            int pos;
            int neighborIndex;

            for (Group<Peer>[] neighborsGroup : neighbors) {
                for (int i = 0; i < CanOverlay.NB_DIMENSIONS; i++) {
                    pos = canOverlay.contains(i, msg.getCoordinates()[i]);

                    if (pos == -1) {
                        ((Peer) neighborsGroup[0].getGroupByType()).checkForLoadBalancing();
                        neighborIndex = neighborsGroup[0].waitOneAndGetIndex();
                        this.sendMessageTo(neighborsGroup[0].get(neighborIndex), msg);
                    } else if (pos == 1) {
                        ((Peer) neighborsGroup[1].getGroupByType()).checkForLoadBalancing();
                        neighborIndex = neighborsGroup[1].waitOneAndGetIndex();
                        this.sendMessageTo(neighborsGroup[1].get(neighborIndex), msg);
                    }
                }
            }
        }

        throw new IllegalStateException("The searched position doesn't exist.");

    }

    /**
     * Check for load balancing of the current peer.
     * 
     * @return a result containing the load of the current peer.
     */
    private LoadBalancingResponseMessage checkForLoadBalancing() {
        return new LoadBalancingResponseMessage();
    }

    /**
     * Handles a CHORD request.
     * 
     * @param msg
     *            the ping request that is receive.
     * @return the ping response.
     */
    public ChordResponseMessage handleChordMessage(ChordMessage msg) {
        // FIXME
        return new ChordResponseMessage(this);
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
                this.structuredOverlay = new CanOverlay();
                break;
            case CHORD:
                this.structuredOverlay = new ChordOverlay();
                break;
            default:
                throw new IllegalArgumentException("The peer type must be one of OverlayType.");
        }
    }
}
