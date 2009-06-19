package org.objectweb.proactive.extensions.structuredp2p.messages.asynchronous.chord;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query;


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
public class ChordOverlay extends Query {

    /**
     * Constructor.
     * 
     * @param id
     */
    public ChordOverlay(String id) {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public void handle(StructuredOverlay overlay) {
        overlay.handleQuery(this);
    }

    /**
     * Returns the identifier.
     * 
     * @return the identifier.
     */
    public String getId() {
        return null;
    }
}
