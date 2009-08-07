package org.objectweb.proactive.extensions.structuredp2p.messages.oneway.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.AbstractQuery;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query;


/**
 * <code>AbstractCANQuery</code> is an abstraction of the query in the CAN side. It is mainly used
 * for checking {@link Query} type when a search is performed in a {@link Peer}.
 * 
 * @author Laurent Pellegrino
 * @version 0.1, 08/05/2009
 */
@SuppressWarnings("serial")
public abstract class AbstractCANQuery extends AbstractQuery<Coordinate> {

    public AbstractCANQuery() {

    }

    public AbstractCANQuery(Coordinate[] coordinatesToReach) {
        super(coordinatesToReach);
    }

    /**
     * {@inheritDoc}
     */
    public abstract void handle(StructuredOverlay overlay);

    /**
     * {@inheritDoc}
     */
    public abstract void route(StructuredOverlay overlay);

    /**
     * {@inheritDoc}
     */
    public abstract void route(StructuredOverlay overlay, Query query);

    /**
     * {@inheritDoc}
     */
    public abstract boolean validKeyConstraints(StructuredOverlay overlay);

}
