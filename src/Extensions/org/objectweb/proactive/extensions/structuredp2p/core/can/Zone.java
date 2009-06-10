package org.objectweb.proactive.extensions.structuredp2p.core.can;

import java.io.IOException;
import java.io.Serializable;

import org.objectweb.proactive.core.util.converter.MakeDeepCopy;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.exception.ZoneException;


/**
 * An zone indicates the space which is managed by a {@link Peer}. The minimum coordinates are the
 * left higher corner. The maximum coordinates are the corner lower right.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class Zone implements Serializable {

    /**
     * The minimal value we manage.
     */
    public static String MIN_COORD = "0";

    /**
     * The maximal value we manage.
     */
    public static String MAX_COORD = "1.0";

    /**
     * The minimum coordinates.
     */
    private Coordinate[] coordinatesMin;

    /**
     * The maximum coordinates.
     */
    private Coordinate[] coordinatesMax;

    /**
     * Constructor. Create the biggest zone with all coordinates.
     */
    public Zone() {
        Coordinate[] minCoords = new Coordinate[CANOverlay.NB_DIMENSIONS];
        Coordinate[] maxCoords = new Coordinate[CANOverlay.NB_DIMENSIONS];

        for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            minCoords[i] = new Coordinate(Zone.MIN_COORD);
            maxCoords[i] = new Coordinate(Zone.MAX_COORD);
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
     * @throws ZoneException
     *             this exception is generated when a zone has the coordinate min and max equals at
     *             the same dimension.
     */
    public Zone(Coordinate[] min, Coordinate[] max) throws ZoneException {
        for (int i = 0; i < min.length; i++) {
            if (min[i].compareTo(max[i]) >= 0) {
                System.out.println("min = " + min[i] + ", max = " + max[i]);
                throw new ZoneException("Zone is not correctly formed.");
            }
        }

        this.coordinatesMin = min;
        this.coordinatesMax = max;
    }

    /**
     * Returns the minimum coordinates that indicates the zone which is managed by a peer.
     * 
     * @return the minimum coordinates that indicates the zone which is managed by a peer.
     */
    public Coordinate[] getCoordinatesMin() {
        return this.coordinatesMin;
    }

    /**
     * Returns the coordinates of the zone.
     * 
     * @return the coordinates of the zone.
     */
    public Coordinate[][] getCoordinates() {
        return new Coordinate[][] { this.getCoordinatesMin(), this.getCoordinatesMax() };
    }

    /**
     * Returns the maximum coordinates that indicates the zone which is managed by a peer.
     * 
     * @return the maximum coordinates that indicates the zone which is managed by a peer.
     */
    public Coordinate[] getCoordinatesMax() {
        return this.coordinatesMax;
    }

    /**
     * Returns the minimum coordinate at a specified dimension that indicates the zone which is
     * managed by a peer.
     * 
     * @param dimension
     *            the dimension on which we recover the coordinates.
     * @return the minimum coordinate at the specified dimension that indicates the zone which is
     *         managed by a peer.
     */
    public Coordinate getCoordinateMin(int dimension) {
        return this.coordinatesMin[dimension];
    }

    /**
     * Returns the maximum coordinate at a specified dimension that indicates the zone which is
     * managed by a peer.
     * 
     * @param dimension
     *            the dimension on which we recover the coordinates.
     * @return the maximum coordinate at a specified dimension that indicates the zone which is
     *         managed by a peer.
     */
    public Coordinate getCoordinateMax(int dimension) {
        return this.coordinatesMax[dimension];
    }

    /**
     * Check if the zone in argument is bordered to the current zone at the dimension in argument.
     * 
     * WARNING : if an Zone is bordered with an another Zone on the dimension 1 of two dimension,
     * the parameter dimension to specify is 0 because the two Zone share the same Coordinate on the
     * 0 dimension and not the dimension 1.
     * 
     * @param zone
     *            the zone to check.
     * @param dimension
     *            the dimension to check.
     * @return <code>true</code> if they are bordered, else <code>false</code>
     */
    public Boolean isBordered(Zone zone, int dimension) {
        boolean dimRes = false;
        boolean borderRes = false;

        for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
            if (dim == dimension) {
                dimRes = (this.getCoordinateMin(dim).equals(zone.getCoordinateMax(dim)) || this
                        .getCoordinateMax(dim).equals(zone.getCoordinateMin(dim)));
            } else {
                borderRes |= (this.getCoordinateMin(dim).isBetween(zone.getCoordinateMin(dim),
                        zone.getCoordinateMax(dim)) || this.getCoordinateMax(dim).isBetween(
                        zone.getCoordinateMin(dim), zone.getCoordinateMax(dim))) ||
                    zone.getCoordinateMin(dim).isBetween(this.getCoordinateMin(dim),
                            this.getCoordinateMax(dim)) ||
                    zone.getCoordinateMax(dim).isBetween(this.getCoordinateMin(dim),
                            this.getCoordinateMax(dim));
            }
        }

        return dimRes && borderRes;
    }

    /**
     * Returns the border dimension between this zone and the argument.
     * 
     * @param zone
     *            the zone to check.
     * @return the border dimension, <code>-1</code> if they are not bordered.
     */
    public int getBorderedDimension(Zone zone) {
        int i;
        int nbDim = this.coordinatesMax.length;

        for (i = 0; i < nbDim; i++) {
            if (this.isBordered(zone, i)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns two zones representing the original one splitted into two following a dimension.
     * 
     * @param dimension
     *            the dimension.
     * @return two zones representing the original one splitted into two following a dimension.
     * @throws ZoneException
     * @see #split(int, Coordinate).
     */
    public Zone[] split(int dimension) throws ZoneException {
        return this.split(dimension, Coordinate.getMiddle(this.getCoordinateMin(dimension), this
                .getCoordinateMax(dimension)));
    }

    /**
     * Returns two zones representing the original one splitted into two following a dimension at
     * the specified coordinate.
     * 
     * @param dimension
     *            the dimension.
     * @param coordinate
     *            the coordinate.
     * @return two zones representing the original one splitted into two following a dimension at
     *         the specified coordinate.
     * @throws ZoneException
     */
    public Zone[] split(int dimension, Coordinate coordinate) throws ZoneException {
        Coordinate[] coordinatesMaxCopy = null;
        Coordinate[] coordinatesMinCopy = null;

        try {
            coordinatesMaxCopy = (Coordinate[]) MakeDeepCopy.WithObjectStream.makeDeepCopy(this
                    .getCoordinatesMax());
            coordinatesMinCopy = (Coordinate[]) MakeDeepCopy.WithObjectStream.makeDeepCopy(this
                    .getCoordinatesMin());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        coordinatesMaxCopy[dimension] = coordinate;
        coordinatesMinCopy[dimension] = coordinate;

        return new Zone[] { new Zone(this.getCoordinatesMin(), coordinatesMaxCopy),
                new Zone(coordinatesMinCopy, this.getCoordinatesMax()) };
    }

    /**
     * Merges two bordered zones.
     * 
     * @param zone
     *            the zone to which we merge the current.
     * @return the merged zone.
     * @throws ZoneException
     */
    public Zone merge(Zone zone) throws ZoneException {
        int border = this.getBorderedDimension(zone);

        if (border == -1) {
            throw new ZoneException("Zones are not bordered : " + this + " | " + zone + ".");
        } else {
            // FIXME test also the load balancing to choose the good zone
            // merge the two zones
            Coordinate[] minCoord = this.coordinatesMin.clone();
            Coordinate[] maxCoord = this.coordinatesMax.clone();

            minCoord[border] = Coordinate.min(this.getCoordinateMin(border), zone.getCoordinateMin(border));
            maxCoord[border] = Coordinate.max(this.getCoordinateMax(border), zone.getCoordinateMax(border));

            return new Zone(minCoord, maxCoord);
        }
    }

    /**
     * Indicates if the zone contains the specified coordinate following the given dimension.
     * 
     * @param dimension
     *            the dimension.
     * @param coordinate
     *            the coordinate to check.
     * @return <code>0</code> if the current coordinate is in the zone, <code>-1</code> if the
     *         coordinate is lexicographically less than the minimal coordinate of the zone and
     *         <code>1</code> if the coordinate is lexicographically grater than the maximal
     *         coordinate of the zone and .
     */
    public int contains(int dimension, Coordinate coordinate) {
        boolean isGreaterThanMin = this.getCoordinateMin(dimension).compareTo(coordinate) <= 0;
        boolean isLessThanMax = this.getCoordinateMax(dimension).compareTo(coordinate) > 0;

        if (!isLessThanMax) {
            return 1;
        } else if (!isGreaterThanMin) {
            return -1;
        }

        return 0;
    }

    /**
     * Check if the coordinates in arguments are in the managed zone.
     * 
     * @param coordinates
     *            the coordinates to check.
     * 
     * @return true if the coordinates are in the zone, false otherwise.
     */
    public boolean contains(Coordinate[] coordinates) {
        int i;
        boolean res = true;

        for (i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            if (!(res &= (this.contains(i, coordinates[i]) == 0))) {
                return false;
            }
        }

        return res;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (!(o instanceof Zone)) {
            throw new IllegalArgumentException();
        }

        Zone zone = (Zone) o;

        int i;
        int nbDim = this.coordinatesMax.length;

        for (i = 0; i < nbDim; i++) {
            if (!this.getCoordinateMax(i).equals(zone.getCoordinateMax(i)) ||
                !this.getCoordinateMin(i).equals(zone.getCoordinateMin(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("Zone=(");

        for (int i = 0; i < this.coordinatesMin.length; i++) {
            buf.append(this.coordinatesMin[i].getValue());
            if (i != this.coordinatesMin.length - 1) {
                buf.append(",");
            }
        }

        buf.append(")-->(");

        for (int i = 0; i < this.coordinatesMax.length; i++) {
            buf.append(this.coordinatesMax[i].getValue());
            if (i != this.coordinatesMax.length - 1) {
                buf.append(",");
            }
        }

        buf.append(")");

        return buf.toString();
    }
}
