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
     * The minimum coordinates.
     */
    private final Coordinate[] coordinatesMin;

    /**
     * The maximum coordinates.
     */
    private final Coordinate[] coodinatesMax;

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
        this.coodinatesMax = max;
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
        return this.coodinatesMax;
    }

    /**
     * Returns the minimum coordinates at a specified dimension that indicates the area which is
     * managed by a peer.
     * 
     * @param dimension
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
     *            the dimension on which one recovers the coordinates.
     * @return the maximum coordinates at a specified dimension that indicates the area which is
     *         managed by a peer.
     */
    public Coordinate getCoordinatesMax(int dimension) {
        return this.coodinatesMax[dimension];
    }

    /**
     * Search if the area in argument is bordered to the current area.
     * 
     * @param area
     *            the area to check.
     * @return the dimension in which they are bordered, <code>-1</code> if they aren't.
     */
    public int isBordered(Area area) {
        int i;
        int nbDim = this.coodinatesMax.length;

        for (i = 0; i < nbDim; i++) {
            if (this.getCoordinatesMax(i) == area.getCoordinatesMin(i) ||
                this.getCoordinatesMin(i) == area.getCoordinatesMax(i))
                return i;
        }

        return -1;
    }

    /**
     * Merge two bordered areas.
     * 
     * @param a1
     *            first area.
     * @param a2
     *            second area.
     * @return the merged area.
     * @throws AreaException
     */
    public Area mergeArea(Area a) throws AreaException {
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
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Area))
            throw new IllegalArgumentException();

        Area area = (Area) o;

        int i;
        int nbDim = this.coodinatesMax.length;

        for (i = 0; i < nbDim; i++) {
            if (this.getCoordinatesMax(i) != area.getCoordinatesMax(i) ||
                this.getCoordinatesMin(i) != area.getCoordinatesMin(i))
                return false;
        }

        return true;
    }

    /**
     * Checks if its possible to merge the area in argument with the current area
     * 
     * @param area
     *            the area to check.
     * @return return true if we can merge the area, false otherwise.
     */
    public boolean isValidMergingArea(Area area) {
        int axe = this.isBordered(area);

        if (axe != -1) {
            int myLength = this.coodinatesMax[axe].levenshteinDistance(this.coordinatesMin[axe]);
            int itLength = area.getCoordinatesMax()[axe].levenshteinDistance(area.getCoordinatesMin()[axe]);
            return myLength == itLength;
        } else {
            return false;
        }
    }
}
