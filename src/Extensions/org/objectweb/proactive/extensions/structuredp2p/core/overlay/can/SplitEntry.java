package org.objectweb.proactive.extensions.structuredp2p.core.overlay.can;

import java.io.Serializable;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


/**
 * The <code>SplitEntry</code> class stores the dimension and direction split of a {@link Zone}
 * managed by a {@link Peer}.
 * 
 * @author Laurent Pellegrino
 * @version 0.1, 08/03/2009
 */
@SuppressWarnings("serial")
public class SplitEntry implements Serializable {

    private int dimension = 0;
    private int direction = 0;

    public SplitEntry() {
    }

    /**
     * Constructor.
     * 
     * @param dimension
     *            the dimension of the split.
     * @param direction
     *            the direction of the split.
     */
    public SplitEntry(int dimension, int direction) {
        this.dimension = dimension;
        this.direction = direction;
    }

    /**
     * Returns the dimension of the split.
     * 
     * @return the dimension of the split.
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * Returns the direction of a split.
     * 
     * @return the direction of a split.
     */
    public int getDirection() {
        return this.direction;
    }

}
