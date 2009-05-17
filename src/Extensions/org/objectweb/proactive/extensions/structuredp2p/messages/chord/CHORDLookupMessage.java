package org.objectweb.proactive.extensions.structuredp2p.messages.chord;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.Key;
import org.objectweb.proactive.extensions.structuredp2p.messages.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.LookupResponseMessage;


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
     * {@inheritDoc}
     */
    public LookupResponseMessage handle(StructuredOverlay overlay) {
        return overlay.handleLookupMessage(this);

    }

    /**
     * Returns the identifier.
     * 
     * @return the identifier.
     */
    public String getId() {
        return (String) super.getKey().getValue();
    }
}
