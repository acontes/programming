package org.objectweb.proactive.extensions.structuredp2p.core.overlay.can;

import java.io.Serializable;


/**
 * Coordinate is used in {@link CANOverlay} in order to specify the space which is managed by a peer
 * on a network.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public abstract class Coordinate implements Comparable<Coordinate>, Serializable {

    private String value;

    /**
     * Constructor.
     */
    public Coordinate(String value) {
        this.value = value;
    }

    public abstract int compareTo(Coordinate coordinate);

    public abstract Coordinate getMiddleWith(Coordinate coordinate);

    /*
     * * Says if the this coordinate is between twice in argument.
     * 
     * @param coord1 a coordinate
     * 
     * @param coord2 a coordinate
     */
    public static boolean isBetween(Coordinate current, Coordinate a, Coordinate b) {
        if (a.compareTo(b) < 0) {
            return (current.compareTo(a) >= 0) && (current.compareTo(b) < 0);
        } else if (a.compareTo(b) > 0) {
            return (current.compareTo(b) >= 0) && (current.compareTo(a) < 0);
        }

        return false;
    }

    /**
     * 
     */
    public static Coordinate min(Coordinate coord1, Coordinate coord2) {
        if (coord1.compareTo(coord2) < 0) {
            return (coord1);
        } else {
            return (coord2);
        }
    }

    /***
     * 
     */
    public static Coordinate max(Coordinate coord1, Coordinate coord2) {
        if (coord1.compareTo(coord2) > 0) {
            return (coord1);
        } else {
            return coord2;
        }
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Coordinate)) {
            throw new IllegalArgumentException();
        }
        return this.value.equals(((Coordinate) obj).getValue());
    }

    public String toString() {
        return this.getValue();
    }
}
