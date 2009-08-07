package org.objectweb.proactive.extensions.structuredp2p.core.overlay.chord;

import java.util.HashMap;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.AddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.Message;
import org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.RemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ActionResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.JoinResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ResponseMessage;


/**
 * Chord is a peer-to-peer lookup algorithm.
 * 
 * It allows a distributed set of participants to agree on a single node as a rendezvous point for a
 * given key, without any central coordination. In particular, it provides a distributed evaluation
 * of the successor(ID) function: given the identifier of a key ID, the successor function returns
 * the address of the node whose identifier most closely follows ID in a circular identifier space.
 * The identifier space is typically a 160-bit number.
 * 
 * The Chord algorithm handles adjusting this mapping as the population of nodes changes over time.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class ChordOverlay extends StructuredOverlay {
    private HashMap<String, Peer> fingers;
    private String identifier;

    /**
     * Constructor.
     * 
     * @param peer
     */
    public ChordOverlay(Peer peer) {
        super(peer);
    }

    /**
     * Returns the fingers associated to this overlay.
     * 
     * @return the fingers associated to this overlay.
     */
    public HashMap<String, Peer> getFingers() {
        return this.fingers;
    }

    /**
     * Returns the identifier associated to this overlay.
     * 
     * @return the identifier associated to this overlay.
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handleAddNeighborMessage(AddNeighborMessage msg) {
        // TODO handleAddNeighborMessage(AddNeighborMessage msg)
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public JoinResponseMessage handleJoinMessage(Message msg) {
        // TODO handleJoinMessage(Message msg)
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handleRemoveNeighborMessage(RemoveNeighborMessage removeNeighborMessage) {
        // TODO handleRemoveNeighborMessage(RemoveNeighborMessage removeNeighborMessage)
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean join(Peer remotePeer) {
        // TODO join(Peer remotePeer)
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean leave() {
        // TODO leave()
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public ResponseMessage sendTo(Peer peer, Message msg) {
        // TODO sendMessageTo(Peer peer, Message msg)
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void update() {
        // TODO update()

    }

    /**
     * @{inheritDoc
     */
    @Override
    public void send(Query query) {
        // TODO Auto-generated method stub

    }
}