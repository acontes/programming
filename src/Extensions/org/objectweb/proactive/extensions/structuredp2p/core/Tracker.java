package org.objectweb.proactive.extensions.structuredp2p.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public void addOnNetwork(Peer remotePeer) {
        if (remotePeer.getType() != this.type) {
            throw new IllegalArgumentException("Illegal Peer type. This tracker manages a " + this.type +
                " network.");
        } else if (this.remotePeers.size() == 0) {
            this.remotePeers.add(remotePeer);
        } else {
            Peer peerToJoin = this.getRandomPeer();

            try {
                if (peerToJoin.join(remotePeer)) {
                    Random rand = new Random();
                    if (rand.nextInt(2) == 0) {
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
        Random rand = new Random();
        return this.remotePeers.get(rand.nextInt(this.remotePeers.size()));
    }
}
