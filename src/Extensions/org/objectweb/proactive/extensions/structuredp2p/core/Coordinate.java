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
     * The max value of Unicode
     */
    private static int UNICODE = 255;
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

    /**
     * Determinate the middle between two coordinates.
     * 
     * @param min
     *            the coordinate min
     * @param max
     *            the coordinate max
     * @return coordinates of the middle
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
                value[i] = Character.toChars((Character.getNumericValue(maxS.charAt(i - 1)) / 2));
            }
        }

        return new Coordinate(value.toString());
    }

    @Override
    public String toString() {
        return this.getValue();
    }

}
