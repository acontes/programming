package org.objectweb.proactive.extensions.structuredp2p.message.response;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.Key;


/**
 * A chord response message gives a CHORD peer for routing.
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
     * 
     * @param peer
     * @param id
     */
    public CHORDLookupResponseMessage(Peer peer, String id) {
        super(new Key<String>(id), peer);
    }

    /**
     * 
     * @return
     */
    public String getId() {
        return (String) super.getKey().getValue();
    }
}
