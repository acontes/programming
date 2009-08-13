package org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates;

import java.math.BigDecimal;
import java.util.Random;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;


/**
 * Coordinate is used in {@link CANOverlay} in order to specify the position of a given peer in the
 * space.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class BigDecimalCoordinate extends Coordinate {

    /**
     * Constructor.
     */
    public BigDecimalCoordinate() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param value
     *            the value used in order to initialized the coordinate.
     */
    public BigDecimalCoordinate(String value) {
        super(value);
    }

    /**
     * Returns the middle between two coordinates.
     * 
     * @param max
     *            the coordinate max.
     * @return coordinates of the middle.
     */
    public Coordinate getMiddleWith(Coordinate max) {
        BigDecimal maxL = new BigDecimal(max.getValue());

        return new BigDecimalCoordinate(new BigDecimal(super.getValue()).add(maxL).divide(new BigDecimal(2))
                .toString());
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
        return new BigDecimal(super.getValue()).equals(new BigDecimal(coord.getValue()));
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
        BigDecimal val = new BigDecimal(coord.getValue());
        BigDecimal cur = new BigDecimal(this.getValue());

        return cur.compareTo(val);
    }

    /**
     * Indicates if the this coordinate is between two coordinates.
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
     * Generates a random {@link BigDecimalCoordinate}.
     * 
     * @return the generated {@link BigDecimalCoordinate}.
     */
    public static BigDecimalCoordinate random() {
        Random rand = new Random();
        return new BigDecimalCoordinate("" + rand.nextFloat());
    }
}
