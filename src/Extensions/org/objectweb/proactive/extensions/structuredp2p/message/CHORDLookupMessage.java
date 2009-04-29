package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.response.LookupResponseMessage;


/**
 * A ChordMessage is used in order to found a peer on the network. In response the caller will
 * receive a CanResponseMessage that contains the peer that has been found.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CHORDLookupMessage extends LookupMessage {

    /**
     * Constructor.
     * 
     * @param id
     */
    public CHORDLookupMessage(String id) {
        super(new Key<String>(id));
    }

    /**
     * Handles message by delegation.
     * 
     * @param peer
     *            to which the message will be sent.
     * @return a ChordResponseMessage for routing.
     */
    @Override
    public LookupResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleLookupMessage(this);

    }

    /**
     * Returns the identifier.
     * 
     * @return the identifier.
     */

    public String getId() {
        return (String) super.key.getValue();
    }
}
