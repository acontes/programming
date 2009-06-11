package org.objectweb.proactive.extensions.structuredp2p.messages;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.responses.ResponseMessage;


/**
 * Each lookup message contains the key used in order to found the peer on the network to which we
 * want to send the message.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public abstract class LookupMessage extends Message {

    /**
     * Key used in order to lookup a peer.
     */
    private final Key<?> key;

    /**
     * The number of steps that have been performed in order to reach the response.
     */
    private int nbSteps = 0;

    /**
     * Constructor.
     * 
     * @param key
     *            the key used in order to found the peer in the network to which we want to send
     *            this message.
     */
    public LookupMessage(Key<?> key) {
        super();
        this.key = key;
    }

    /**
     * Returns the key user in order to found the peer in the network to which we want to send this
     * message.
     * 
     * @return the key user in order to found the peer in the network.
     */
    public Key<?> getKey() {
        return this.key;
    }

    /**
     * {@inheritDoc}
     */
    public abstract ResponseMessage handle(StructuredOverlay overlay);

    /**
     * Increments the number of steps by one.
     */
    public void incrementNbSteps() {
        this.nbSteps++;
    }

    /**
     * Returns the number of steps that have been performed in order to reach the response.
     * 
     * @return the number of steps that have been performed in order to reach the response.
     */
    public int getNbSteps() {
        return this.nbSteps;
    }
}