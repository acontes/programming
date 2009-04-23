package org.objectweb.proactive.extensions.structuredp2p.core;

import org.objectweb.proactive.extensions.structuredp2p.core.exception.AreaException;


/**
 * An area indicates the space which is managed by a peer. The minimum coordinates are the left
 * higher corner. The maximum coordinates are the corner lower right.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class Area {

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
     * @return the dimension in which they are bordered, <code>-1</code> if they aren't.
     */
    public int isBorder(Area area) {
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
     *            first area
     * @param a2
     *            second area
     * @return the merged area
     * @throws AreaException
     */
    public static Area mergeAreas(Area a1, Area a2) throws AreaException {
        int border = a1.isBorder(a2);

        if (border == -1)
            throw new AreaException("Areas are not bordered.");

        // TODO how to split ??
        return new Area(null, null);
    }

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
}
