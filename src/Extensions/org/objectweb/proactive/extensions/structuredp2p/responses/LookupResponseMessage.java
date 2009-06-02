package org.objectweb.proactive.extensions.structuredp2p.responses;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.messages.Key;
import org.objectweb.proactive.extensions.structuredp2p.messages.LookupMessage;


/**
 * Defines a response for the {@link LookupMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public abstract class LookupResponseMessage extends ResponseMessage {

    /**
     * Key used in order to lookup the peer.
     */
    private final Key<?> key;

    /**
     * The peer which has been found for the given key.
     */
    private final Peer peer;

    /**
     * The number of steps that have been performed in order to reach the response.
     */
    private int nbSteps = 0;

    /**
     * Constructor.
     * 
     * @param timestampMessageCreation
     *            the timestamp indicating the time creation of the message which has been sent.
     * @param nbSteps
     *            the number of steps that have been performed in order to reach the response.
     * @param key
     *            the key used in order to found the peer in the network to which we want to send
     *            this message.
     * @param peer
     *            the peer which has sent the message.
     */
    public LookupResponseMessage(long timestampMessageCreation, int nbSteps, Key<?> key, Peer peer) {
        super(timestampMessageCreation);
        this.key = key;
        this.peer = peer;
        this.nbSteps = nbSteps;
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
     * Returns the peer which has been found.
     * 
     * @return the peer which has been found.
     */
    public Peer getPeer() {
        return this.peer;
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
