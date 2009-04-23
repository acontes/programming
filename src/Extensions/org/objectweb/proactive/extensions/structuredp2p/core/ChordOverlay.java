package org.objectweb.proactive.extensions.structuredp2p.core;

import java.util.HashMap;

import org.objectweb.proactive.extensions.structuredp2p.message.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ChordLookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.LookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


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
public class ChordOverlay extends StructuredOverlay {
    private String identifier;
    private HashMap<String, Peer> fingers;

    /**
     * FIXME
     * 
     * @param peer
     */
    public ChordOverlay(Peer peer) {
        super(peer);
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
    @Override
    public void join(Peer peer) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void leave() {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        // TODO Auto-generated method stub
    }

    @Override
    public LookupResponseMessage sendMessage(LookupMessage msg) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResponseMessage sendMessageTo(Peer peer, Message msg) {
        // TODO Auto-generated method stub
        return null;
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

    @Override
    public ChordLookupResponseMessage handleLookupMessage(LookupMessage msg) {
        // TODO Auto-generated method stub
        return null;
    }
}
