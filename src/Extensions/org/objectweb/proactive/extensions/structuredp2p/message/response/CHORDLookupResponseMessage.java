package org.objectweb.proactive.extensions.structuredp2p.message.response;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.CHORDLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.Key;


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
     * @param peer
     * @param id
     */
    public CHORDLookupResponseMessage(long timestampMessageCreation, Peer peer, String id) {
        super(timestampMessageCreation, new Key<String>(id), peer);
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
