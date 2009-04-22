package org.objectweb.proactive.extensions.structuredp2p.core;

/**
 * Coordinate is used in a CAN network in order to specify the position of a given peer in the
 * space.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class Coordinate {
    /**
     * The content of the coordinate.
     */
    private String value;
    
    public Coordinate(String value) {
        this.value = value;
    }

    /**
     * Returns the content of the coordinate.
     * 
     * @return the content of the coordinate.
     */
    public String getValue() {
        return this.value;
    }

}
