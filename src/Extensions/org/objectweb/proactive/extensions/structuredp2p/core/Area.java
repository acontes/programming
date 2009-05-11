package org.objectweb.proactive.extensions.structuredp2p.core;

import java.io.Serializable;

import org.objectweb.proactive.extensions.structuredp2p.core.exception.AreaException;


/**
 * An area indicates the space which is managed by a {@link Peer}. The minimum coordinates are the
 * left higher corner. The maximum coordinates are the corner lower right.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class Area implements Serializable {

    /**
     * The minimal value we manage.
     */
    private static int MIN_COORD = 0;

    /**
     * The maximal value we manage.
     */
    private static int MAX_COORD = 512;

    /**
     * The minimum coordinates.
     */
    private Coordinate[] coordinatesMin;

    /**
     * The maximum coordinates.
     */
    private Coordinate[] coordinatesMax;

    /**
     * Constructor. Create the biggest area with all coordinates.
     */
    public Area() {
        Coordinate[] minCoords = new Coordinate[CANOverlay.NB_DIMENSIONS];
        Coordinate[] maxCoords = new Coordinate[CANOverlay.NB_DIMENSIONS];

        int i;
        for (i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            minCoords[i] = new Coordinate("" + Area.MIN_COORD);
            maxCoords[i] = new Coordinate("" + Area.MAX_COORD);
        }

        this.coordinatesMin = minCoords;
        this.coordinatesMax = maxCoords;
    }

    /**
     * Constructor.
     * 
     * @param min
     *            the minimum coordinates.
     * @param max
     *            the maximum coordinates.
     */
    public Area(Coordinate[] min, Coordinate[] max) {
        this.coordinatesMin = min;
        this.coordinatesMax = max;
    }

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
        return this.coordinatesMax;
    }

    /**
     * Returns the minimum coordinates at a specified dimension that indicates the area which is
     * managed by a peer.
     * 
     * @param dimension
     *            the dimension on which we recover the coordinates.
     * @return the minimum coordinates at the specified dimension that indicates the area which is
     *         managed by a peer.
     */
    public Coordinate getCoordinatesMin(int dimension) {
        return this.coordinatesMin[dimension];
    }

    /**
     * Returns the maximum coordinates at a specified dimension that indicates the area which is
     * managed by a peer.
     * 
     * @param dimension
     *            the dimension on which we recover the coordinates.
     * @return the maximum coordinates at a specified dimension that indicates the area which is
     *         managed by a peer.
     */
    public Coordinate getCoordinatesMax(int dimension) {
        return this.coordinatesMax[dimension];
    }

    /**
     * Check if the area in argument is bordered to the current area.
     * 
     * @param area
     *            the area to check.
     * @return the dimension in which they are bordered, <code>-1</code> if they aren't.
     */
    public int isBordered(Area area) {
        int i;
        int nbDim = this.coordinatesMax.length;

        for (i = 0; i < nbDim; i++) {
            if (this.getCoordinatesMax(i) == area.getCoordinatesMin(i) ||
                this.getCoordinatesMin(i) == area.getCoordinatesMax(i)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Merges two bordered areas.
     * 
     * @param a
     *            the Area to which we merge the current.
     * @return the merged area.
     * @throws AreaException
     */
    public Area merge(Area a) throws AreaException {
        int border = this.isBordered(a);
        Coordinate[] minCoord = new Coordinate[this.getCoordinatesMax().length];
        Coordinate[] maxCoord = new Coordinate[this.getCoordinatesMax().length];
        if (border == -1) {
            throw new AreaException("Areas are not bordered.");
        } else if (this.isValidMergingArea(a)) {
            // FIXME test also the load balancing to choose the good area
            // merge the two areas
            for (int i = 0; i < this.getCoordinatesMax().length; i++) {
                if (i != border) {
                    minCoord[i] = this.getCoordinatesMin(i);
                    maxCoord[i] = this.getCoordinatesMax(i);
                } else {
                    minCoord[i] = this.getCoordinatesMin(i).min(a.getCoordinatesMin(i));
                    maxCoord[i] = this.getCoordinatesMax(i).max(a.getCoordinatesMax(i));
                }
            }
            return new Area(minCoord, maxCoord);

        } else {
            // FIXME is it necessary to throw a exception here ??
            throw new AreaException("Areas cant be merged");
        }
        // TODO how to split ??
        // return new Area(null, null);
    }

    /**
     * Checks if it is possible to merge the current area with the area in argument.
     * 
     * @param area
     *            the area to check.
     * @return return true if we can merge the area, false otherwise.
     */
    public boolean isValidMergingArea(Area area) {
        int axe = this.isBordered(area);
        if (axe != -1) {
            return (this.coordinatesMax[axe].equals(area.getCoordinatesMax()[axe])) &&
                (this.coordinatesMin[axe].equals(area.getCoordinatesMin()[axe]));
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Area)) {
            throw new IllegalArgumentException();
        }

        Area area = (Area) o;

        int i;
        int nbDim = this.coordinatesMax.length;

        for (i = 0; i < nbDim; i++) {
            if (this.getCoordinatesMax(i) != area.getCoordinatesMax(i) ||
                this.getCoordinatesMin(i) != area.getCoordinatesMin(i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns two areas representing the original one splited following a dimension.
     * 
     * @param dimension
     *            the dimension.
     * @return the two split areas.
     */
    public Area[] split(int dimension) {
        return this.split(dimension, Coordinate.getMiddle(this.getCoordinatesMin(dimension), this
                .getCoordinatesMax(dimension)));
    }

    /**
     * Returns two areas representing the original one split following a dimension at the specified
     * coordinate.
     * 
     * @param dimension
     *            the dimension.
     * @param coordinate
     *            the coordinate.
     * @return the two split areas.
     */
    public Area[] split(int dimension, Coordinate coordinate) {
        Coordinate[] maxCoordLessArea = this.getCoordinatesMax();
        maxCoordLessArea[dimension] = coordinate;

        Coordinate[] minCoordGreaterArea = this.getCoordinatesMin();
        minCoordGreaterArea[dimension] = coordinate;

        return new Area[] { new Area(this.getCoordinatesMin(), maxCoordLessArea),
                new Area(minCoordGreaterArea, this.getCoordinatesMax()) };
    }

    /**
     * Is the coordinate in the area following a dimension ?
     * 
     * @param dimension
     *            the dimension.
     * @param coordinate
     *            the coordinate to check.
     * @return <code>0</code> if the current coordinate is in the area, <code>-1</code> if the
     *         coordinate is lexicographically less than the minimal coordinate of the area and
     *         <code>1</code> if the coordinate is lexicographically grater than the maximal
     *         coordinate of the area and .
     */
    public int contains(int dimension, Coordinate coordinate) {
        boolean isGreaterThanMin = this.getCoordinatesMin(dimension).getValue().compareTo(
                coordinate.getValue()) <= 0;
        boolean isLessThanMax = this.getCoordinatesMax(dimension).getValue().compareTo(coordinate.getValue()) > 0;

        if (!isLessThanMax) {
            return 1;
        } else if (!isGreaterThanMin) {
            return -1;
        }

        return 0;
    }
}
