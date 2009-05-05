package org.objectweb.proactive.extensions.structuredp2p.core;

import java.io.Serializable;


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
     * The max value of Unicode we managed.
     */
    private static int UNICODE = 255;

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
        // FIXME voir les bords !
        String maxS, minS;
        int minL = min.getValue().length();
        int maxL = max.getValue().length();
        char[] value = new char[maxL];

        // Take the lengthier String to modify it
        if (minL > maxL) {
            maxS = new String(min.getValue());
            minS = new String(max.getValue());

            int tmp = maxL;
            maxL = minL;
            minL = tmp;
        } else {
            maxS = new String(max.getValue());
            minS = new String(min.getValue());
        }

        int i;
        // TODO finir le calcul
        for (i = maxL; i > 0; i--) {
            if (i > minL) {
                // FIXME
                // value[i] = Character.toChars((Character.getNumericValue(maxS.charAt(i - 1)) /
                // 2));
            }
        }

        return new Coordinate(value.toString());
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
     * Returns the maximum coordinate between the current coordinate and the argument.
     * 
     * @param coord
     *            the coordinate.
     * @return the maximum coordinate between the current coordinate and the argument.
     */
    public Coordinate max(Coordinate coord) {
        if (coord.getValue().compareTo(this.value) >= 0) {
            return this;
        } else {
            return coord;
        }
    }

    /**
     * Returns the minimum coordinate between the current coordinate and the argument.
     * 
     * @param coord
     * @return
     */
    public Coordinate min(Coordinate coord) {
        if (coord.getValue().compareTo(this.value) <= 0) {
            return this;
        } else {
            return coord;
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
    @Override
    public String toString() {
        return this.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        Coordinate cord = (Coordinate) o;
        return this.value.equals(cord.value);
    }
}
