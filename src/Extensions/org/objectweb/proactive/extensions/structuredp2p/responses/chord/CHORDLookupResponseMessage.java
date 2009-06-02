package org.objectweb.proactive.extensions.structuredp2p.responses.chord;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.messages.Key;
import org.objectweb.proactive.extensions.structuredp2p.messages.chord.CHORDLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.LookupResponseMessage;


/**
 * Defines a response for the {@link CHORDLookupMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CHORDLookupResponseMessage extends LookupResponseMessage {

    /**
     * Constructor.
     * 
     * @param timestampMessageCreation
     *            the timestamp indicating the time creation of the message which has been sent.
     * @param nbSteps
     *            the number of steps that have been performed in order to reach the response.
     * @param peer
     *            the searched peer.
     * @param id
     *            the identificator.
     */
    public CHORDLookupResponseMessage(long timestampMessageCreation, int nbSteps, Peer peer, String id) {
        super(timestampMessageCreation, nbSteps, new Key<String>(id), peer);
    }

    /**
     * Returns the identifier used in order to find the peer.
     * 
     * @return the identifier used in order to find the peer.
     */
    public String getId() {
        return (String) super.getKey().getValue();
    }
}
