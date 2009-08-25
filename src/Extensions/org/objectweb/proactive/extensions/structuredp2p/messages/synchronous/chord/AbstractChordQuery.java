package org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.chord;

import org.objectweb.proactive.extensions.structuredp2p.api.messages.synchronous.Query;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.AbstractQueryMessage;


/**
 * <code>AbstractChordQuery</code> is an abstraction of the query in the Chord side. It is mainly
 * used for checking {@link Query} type when a search is performed in a {@link Peer}.
 * 
 * @author Laurent Pellegrino
 * @version 0.1, 08/05/2009
 */
@SuppressWarnings("serial")
public abstract class AbstractChordQuery extends AbstractQueryMessage<String> {

    public AbstractChordQuery() {

    }

    public AbstractChordQuery(String[] coordinatesToReach) {
        super(coordinatesToReach);
    }

    /**
     * @{inheritDoc
     */
    public abstract void handle(StructuredOverlay overlay);

    /**
     * @{inheritDoc
     */
    public abstract void route(StructuredOverlay overlay);

    /**
     * @{inheritDoc
     */
    public abstract boolean validKeyConstraints(StructuredOverlay overlay);

}
