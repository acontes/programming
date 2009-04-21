package org.objectweb.proactive.extensions.structuredp2p.core;

/**
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
public class Area {
    private Coordinate[] coordinatesMin;
    private Coordinate[] coodinatesMax;

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
