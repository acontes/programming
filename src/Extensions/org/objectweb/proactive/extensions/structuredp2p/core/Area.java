package org.objectweb.proactive.extensions.structuredp2p.core;

/**
 * An area indicates the space which is managed by a peer. The minimum is the left higher corner.
 * The maximum is the corner lower right.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class Area {

    /**
     * The minimum coordinates.
     */
    private Coordinate[] coordinatesMin;
    /**
     * The maximum coordinates.
     */
    private Coordinate[] coodinatesMax;

    public Area(Coordinate[] min, Coordinate[] max) {
        this.coordinatesMin = min;
        this.coodinatesMax = max;
    }

    /**
     * Returns the minimum coordinates that indicates the area which is managed by a peer.
     * 
     * @return the minimum coordinates that indicates the area which is managed by a peer.
     */
    public Coordinate[] getCoordinatesMin() {
        return this.coordinatesMin;
    }

    /**
     * Returns the maximum coordinates that indicates the area which is managed by a peer.
     * 
     * @return the maximum coordinates that indicates the area which is managed by a peer.
     */
    public Coordinate[] getCoordinatesMax() {
        return this.coodinatesMax;
    }

}
