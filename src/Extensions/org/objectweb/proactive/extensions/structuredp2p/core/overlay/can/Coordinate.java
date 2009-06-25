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

    /**
     * Compares two coordinates.
     * 
     * @param coordinate
     *            the value to compare with.
     * @return The value <code>0</code> if the argument coordinate is equal to this coordinate; a
     *         value less than <code>0</code> if this coordinate is less than the coordinate
     *         argument; and a value greater than <code>0</code> if this coordinate is greater than
     *         the coordinate argument.
     */
    public abstract int compareTo(Coordinate coordinate);

    /**
     * Returns the middle coordinate between the current coordinate and the specified coordinate.
     * 
     * @param coordinate
     *            the specified coordinate.
     * 
     * @return the middle coordinate between the current coordinate and the specified coordinate.
     */
    public abstract Coordinate getMiddleWith(Coordinate coordinate);

    /**
     * Indicates if the specified coordinate <code>current</code> is between the coordinate
     * <code>a</code> and coordinate <code>b</code>.
     * 
     * @param a
     *            a coordinate.
     * 
     * @param b
     *            a coordinate.
     * 
     * @return <code>true</code> if the coordinate <code>current</code> is between <code>a</code>
     *         and <code>b</code>. <code>false</code> otherwise.
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
     * Returns the smallest coordinate among two specified coordinate.
     * 
     * @return the smallest coordinate among two specified coordinate.
     */
    public static Coordinate min(Coordinate coord1, Coordinate coord2) {
        if (coord1.compareTo(coord2) < 0) {
            return (coord1);
        } else {
            return (coord2);
        }
    }

    /**
     * Returns the greatest coordinate among two specified coordinate.
     * 
     * @return the greatest coordinate among two specified coordinate.
     */
    public static Coordinate max(Coordinate coord1, Coordinate coord2) {
        if (coord1.compareTo(coord2) > 0) {
            return (coord1);
        } else {
            return coord2;
        }
    }

    /**
     * Returns the value of the coordinate.
     * 
     * @return the value of the coordinate.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Coordinate)) {
            throw new IllegalArgumentException();
        }
        return this.value.equals(((Coordinate) obj).getValue());
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return this.getValue();
    }
}
