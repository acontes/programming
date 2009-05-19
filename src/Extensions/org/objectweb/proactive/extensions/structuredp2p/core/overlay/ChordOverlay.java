package org.objectweb.proactive.extensions.structuredp2p.core.overlay;

import java.util.HashMap;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.messages.AddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.LeaveMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.messages.RemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.ActionResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.LookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.ResponseMessage;


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
    private String identifier;
    private HashMap<String, Peer> fingers;

    /**
     * Constructor.
     * 
     * @param peer
     */
    public ChordOverlay(Peer peer) {
        super(peer);
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
     * Returns the fingers associated to this overlay.
     * 
     * @return the fingers associated to this overlay.
     */
    public HashMap<String, Peer> getFingers() {
        return this.fingers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkNeighbors() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public Boolean join(Peer remotePeer) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean leave() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public LookupResponseMessage sendMessage(LookupMessage msg) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ResponseMessage sendMessageTo(Peer peer, Message msg) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void update() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handleAddNeighborMessage(AddNeighborMessage msg) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handleJoinMessage(Message msg) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handleLeaveMessage(LeaveMessage leaveMessage) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public LookupResponseMessage handleLookupMessage(LookupMessage msg) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ActionResponseMessage handleRemoveNeighborMessage(RemoveNeighborMessage removeNeighborMessage) {
        // TODO Auto-generated method stub
        return null;
    }
}