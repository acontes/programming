package org.objectweb.proactive.extensions.structuredp2p.core.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;

/**
 * It indicates the state of a neighbor that each {@link Peer} can reference.
 * 
 * @author Pellegrino Laurent
 */
public enum NeighborState {
    /**
     * Ready to handle request.
     */
    ALIVE,
    /**
     * Being to leave the network, it can no more receive request.
     */
    PREPARE_TO_LEAVE
}
