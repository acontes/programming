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
     * @return the coordinatesMin
     */
    public Coordinate[] getCoordinatesMin() {
        return this.coordinatesMin;
    }

    /**
     * @return the coodinatesMax
     */
    public Coordinate[] getCoodinatesMax() {
        return this.coodinatesMax;
    }

}
