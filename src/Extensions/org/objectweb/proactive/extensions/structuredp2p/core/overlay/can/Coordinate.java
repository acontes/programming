package org.objectweb.proactive.extensions.structuredp2p.core.overlay.can;

import java.io.Serializable;
import java.math.BigDecimal;


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
public class Coordinate implements Serializable {

    /**
     * The content of the coordinate.
     */
    private final String value;

    /**
     * Constructor.
     * 
     * @param value
     *            the value used in order to initialized the coordinate.
     */
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

    /**
     * Returns the middle between two coordinates.
     * 
     * @param min
     *            the coordinate min.
     * @param max
     *            the coordinate max.
     * @return the middle between two coordinates.
     */
    public static Coordinate getMiddle(Coordinate min, Coordinate max) {
        return new Coordinate((new BigDecimal(min.getValue()).add(new BigDecimal(max.getValue()))
                .divide(new BigDecimal(2))).toString());
    }

    /**
     * Returns the maximum coordinate between the specified coordinates.
     * 
     * @param coord1
     *            the first coordinate.
     * @param coord2
     *            the second coordinate.
     * @return the coordinate which is the greatest between the two coordinates.
     */
    public static Coordinate max(Coordinate coord1, Coordinate coord2) {
        if (coord1.compareTo(coord2) > 0) {
            return coord1;
        } else {
            return coord2;
        }
    }

    /**
     * Returns the minimum coordinate between the specified coordinates.
     * 
     * @param coord1
     *            the first coordinate.
     * @param coord2
     *            the second coordinate.
     * @return the coordinate which is the smallest between the two coordinates.
     */
    public static Coordinate min(Coordinate coord1, Coordinate coord2) {
        if (coord1.compareTo(coord2) < 0) {
            return coord1;
        } else {
            return coord2;
        }
    }

    /**
     * Compares two coordinates.
     * 
     * @param coord
     *            the value to compare with.
     * @return The value <code>0</code> if the argument coordinate is equal to this coordinate; a
     *         value less than <code>0</code> if this coordinate is lexicographically less than the
     *         coordinate argument; and a value greater than <code>0</code> if this coordinate is
     *         lexicographically greater than the coordinate argument.
     */
    public int compareTo(Coordinate coord) {
        return new BigDecimal(this.getValue()).compareTo(new BigDecimal(coord.getValue()));
    }

    /**
     * Says if the this coordinate is between twice in argument.
     * 
     * @param coord1
     *            a coordinate
     * @param coord2
     *            a coordinate
     */
    public boolean isBetween(Coordinate coord1, Coordinate coord2) {
        if (coord1.compareTo(coord2) < 0) {
            return (this.compareTo(coord1) >= 0) && (this.compareTo(coord2) < 0);
        } else if (coord1.compareTo(coord2) > 0) {
            return (this.compareTo(coord2) >= 0) && (this.compareTo(coord1) < 0);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return this.getValue();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (!(o instanceof Coordinate)) {
            throw new IllegalArgumentException();
        }

        Coordinate coord = (Coordinate) o;
        return new BigDecimal(this.value).equals(new BigDecimal(coord.getValue()));
    }
}
