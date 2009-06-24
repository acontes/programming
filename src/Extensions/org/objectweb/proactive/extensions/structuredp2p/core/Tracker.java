package org.objectweb.proactive.extensions.structuredp2p.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.ProActiveRandom;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;


/**
 * A tracker assists in the communication between peers. It is used in order to help a peer to join
 * an existing network.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class Tracker implements Serializable {

    /**
     * The probability to add a new peer in the tracker list.
     */
    private static final int PROBABILITY = 3;

    /**
     * The remote peers list that the tracker maintains.
     */
    private List<Peer> remotePeers = new ArrayList<Peer>();

    /**
     * The type of peers that can use this tracker in order to join the network.
     */
    private OverlayType type;

    /**
     * Constructor.
     */
    public Tracker() {
    }

    /**
     * Constructor.
     * 
     * @param type
     *            the type of tracker (ie. the kind of peers that can be add on the network).
     */
    public Tracker(OverlayType type) {
        this.type = type;
    }

    /**
     * Add on the network that the tracker manages, the given peer.
     * 
     * @param remotePeer
     *            the peer to add on the network.
     */
    public void addOnNetwork(Peer remotePeer) throws IllegalArgumentException {
        if (remotePeer.getType() != this.type) {
            throw new IllegalArgumentException("Illegal Peer type. This tracker manages a " + this.type +
                " network.");
        } else if (this.remotePeers.size() == 0) {
            this.remotePeers.add(remotePeer);
        } else {
            Peer peerToJoin = this.getRandomPeer();

            try {
                if (remotePeer.join(peerToJoin)) {
                    if (ProActiveRandom.nextInt(Tracker.PROBABILITY) == 0) {
                        this.remotePeers.add(remotePeer);
                    }
                }
            } catch (Exception e) {
                // The remote peer we contact in order to join is died, so we retry with an another
                this.remotePeers.remove(peerToJoin);
                this.addOnNetwork(remotePeer);
            }
        }
    }

    /**
     * Returns a random peer from the local list.
     * 
     * @return a random peer from the local list.
     */
    public Peer getRandomPeer() {
        if (this.remotePeers.size() == 0) {
            return null;
        }

        return this.remotePeers.get(ProActiveRandom.nextInt(this.remotePeers.size()));
    }

    /**
     * Returns the number of peers that the tracker manages.
     * 
     * @return the number of peers that the tracker manages.
     */
    public int getNumberOfManagedPeers() {
        return this.remotePeers.size();
    }

    /**
     * Create a new Tracker ActiveObject.
     * 
     * @param type
     *            the type of the peer, which is one of {@link OverlayType}.
     * @return the new Peer object created.
     * @throws ActiveObjectCreationException
     * @throws NodeException
     */
    public static Tracker newActiveTracker(OverlayType type) throws ActiveObjectCreationException,
            NodeException {
        return Tracker.newActiveTracker(type, null);
    }

    /**
     * Create a new Tracker ActiveObject.
     * 
     * @param type
     *            the type of the peer, which is one of {@link OverlayType}.
     * @param node
     *            the node to use.
     * @return the new Peer object created.
     * @throws ActiveObjectCreationException
     * @throws NodeException
     */
    public static Tracker newActiveTracker(OverlayType type, Node node) throws ActiveObjectCreationException,
            NodeException {
        return (Tracker) PAActiveObject.newActive(Tracker.class.getName(), new Object[] { type }, node);
    }
}
