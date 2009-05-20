package org.objectweb.proactive.extensions.structuredp2p.core;

import java.io.Serializable;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.CANOverlay;


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
     * Determinates the middle between two coordinates.
     * 
     * @param min
     *            the coordinate min.
     * @param max
     *            the coordinate max.
     * @return coordinates of the middle.
     */
    public static Coordinate getMiddle(Coordinate min, Coordinate max) {
        // FIXME check with strings
        int minL = Integer.parseInt(min.getValue());
        int maxL = Integer.parseInt(max.getValue());

        return new Coordinate("" + ((maxL + minL) / 2));
    }

    /**
     * Calculates the Levenshtein distance between two strings.
     * 
     * @param str1
     *            string one.
     * @param str2
     *            string two.
     * @return the Levenshtein distance between two strings.
     */
    public int levenshteinDistance(Coordinate coord) {

        String str1 = this.value;
        String str2 = coord.getValue();
        int[][] distance = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            distance[i][0] = i;
        }
        for (int j = 0; j <= str2.length(); j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                distance[i][j] = this.minimum(distance[i - 1][j] + 1, distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));
            }
        }
        return distance[str1.length()][str2.length()];
    }

    /**
     * Calculates the minimum of three int.
     * 
     * @param a
     * @param b
     * @param c
     * @return the minimum.
     */
    private int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    /**
     * Returns the maximum coordinate between the coordinates.
     * 
     * @param coord
     *            the coordinate.
     * @return the maximum coordinate between the coordinates.
     */
    public static Coordinate max(Coordinate coord1, Coordinate coord2) {
        if (coord1.compareTo(coord2) > 0) {
            return coord1;
        } else {
            return coord2;
        }
    }

    /**
     * Returns the minimum coordinate between the coordinates.
     * 
     * @param coord
     * @return the minimum coordinate between the coordinates.
     */
    public static Coordinate min(Coordinate coord1, Coordinate coord2) {
        if (coord1.compareTo(coord2) < 0) {
            return coord1;
        } else {
            return coord2;
        }
    }

    /*
     * private String stringValue(int n){ return ""; }
     * 
     * public String middle(String str1 , String str2){ int midCor = this.lenvensteinDistance(str1,
     * str2) / 2; return stringValue(str1.hashCode() + midCor); }
     */

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
        return this.value.equals(coord.value);
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
        int val = Integer.parseInt(coord.getValue());
        int cur = Integer.parseInt(this.getValue());

        return cur - val;
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
}
