package org.objectweb.proactive.extensions.structuredp2p.core;

import java.util.HashMap;

import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * 
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
public class ChordOverlay implements StructuredOverlay {
    private String identifier;
    private HashMap<String, Peer> fingers;

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
    public ResponseMessage sendMessageTo(Peer peer, Message msg) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        // TODO Auto-generated method stub
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * @param identifier
     *            the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the fingers
     */
    public HashMap<String, Peer> getFingers() {
        return this.fingers;
    }

    /**
     * 
     * @param fingers
     *            the fingers to set
     */
    public void setFingers(HashMap<String, Peer> fingers) {
        this.fingers = fingers;
    }

}
