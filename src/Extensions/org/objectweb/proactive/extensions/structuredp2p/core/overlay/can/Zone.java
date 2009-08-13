package org.objectweb.proactive.extensions.structuredp2p.core.overlay.can;

import java.io.IOException;
import java.io.Serializable;

import org.objectweb.proactive.core.util.converter.MakeDeepCopy;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.LexicographicCoordinate;


/**
 * An zone indicates the space which is managed by a {@link Peer}. The minimum coordinates
 * correspond to the left higher corner. The maximum coordinates correspond to the corner lower
 * right.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1, 07/09/2009
 */
@SuppressWarnings("serial")
public class Zone implements Serializable {

    /**
     * The maximal value to manage.
     */
    public static final String COORDINATE_MAX = "http://z";

    /**
     * The minimal value to manage.
     */
    public static final String COORDINATE_MIN = "http://a";

    /**
     * The type of coordinate to use.
     */
    public static final Class<? extends Coordinate> COORDINATE_TYPE = LexicographicCoordinate.class;

    /**
     * The maximum coordinates.
     */
    private Coordinate[] coordinatesMax;

    /**
     * The minimum coordinates.
     */
    private Coordinate[] coordinatesMin;

    /**
     * Constructor. Create the biggest zone with all coordinates.
     */
    public Zone() {
        Coordinate[] minCoords = new Coordinate[CANOverlay.NB_DIMENSIONS];
        Coordinate[] maxCoords = new Coordinate[CANOverlay.NB_DIMENSIONS];

        for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {

            Coordinate coordinate = null;
            try {
                coordinate = Zone.COORDINATE_TYPE.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            coordinate.setValue(Zone.COORDINATE_MIN);
            minCoords[i] = coordinate;

            try {
                coordinate = Zone.COORDINATE_TYPE.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            coordinate.setValue(Zone.COORDINATE_MAX);
            maxCoords[i] = coordinate;
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
        this.coordinatesMin = min;
        this.coordinatesMax = max;
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
     * Indicates if the zone contains the specified coordinate following the given dimension.
     * 
     * @param dimension
     *            the dimension.
     * @param coordinate
     *            the coordinate to check.
     * @return <code>0</code> if the current coordinate is in the zone, <code>-1</code> if the
     *         coordinate is taller than the minimal coordinate of the zone and <code>1</code> if
     *         the coordinate is greater than the maximal coordinate of the zone and .((CANOverlay)
     *         this.firstPeer.getStructuredOverlay()).getZone() .getCoordinatesMin()
     */
    public int contains(int dimension, Coordinate coordinate) {
        if (coordinate.compareTo(this.getCoordinateMax(dimension)) >= 0) {
            return 1;
        } else if (coordinate.compareTo(this.getCoordinateMin(dimension)) < 0) {
            return -1;
        }

        return 0;
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
     * Returns the minimum coordinates that indicates the zone which is managed by a peer.
     * 
     * @return the minimum coordinates that indicates the zone which is managed by a peer.
     */
    public Coordinate[] getCoordinatesMin() {
        return this.coordinatesMin;
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
                borderRes |= (Coordinate.isBetween(this.getCoordinateMin(dim), zone.getCoordinateMin(dim),
                        zone.getCoordinateMax(dim)) || Coordinate.isBetween(this.getCoordinateMax(dim), zone
                        .getCoordinateMin(dim), zone.getCoordinateMax(dim))) ||
                    Coordinate.isBetween(zone.getCoordinateMin(dim), this.getCoordinateMin(dim), this
                            .getCoordinateMax(dim)) ||
                    Coordinate.isBetween(zone.getCoordinateMax(dim), this.getCoordinateMin(dim), this
                            .getCoordinateMax(dim));
            }
        }

        return dimRes && borderRes;
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
            Coordinate[] minCoord = null;
            Coordinate[] maxCoord = null;
            try {
                minCoord = (Coordinate[]) MakeDeepCopy.WithObjectStream.makeDeepCopy(this.coordinatesMin);
                maxCoord = (Coordinate[]) MakeDeepCopy.WithObjectStream.makeDeepCopy(this.coordinatesMax);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            minCoord[border] = Coordinate.min(this.getCoordinateMin(border), zone.getCoordinateMin(border));
            maxCoord[border] = Coordinate.max(this.getCoordinateMax(border), zone.getCoordinateMax(border));

            return new Zone(minCoord, maxCoord);
        }
    }

    /**
     * Returns two zones representing the original one splitted into two following a dimension.
     * 
     * @param dimension
     *            the dimension.
     * @return two zones representing the original one splitted into two following a dimension.
     * @throws ZoneException
     * @see Zone#split(int, Coordinate)
     */
    public Zone[] split(int dimension) throws ZoneException {
        return this.split(dimension, this.getCoordinateMin(dimension).getMiddleWith(
                this.getCoordinateMax(dimension)));
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
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("(");

        for (int i = 0; i < this.coordinatesMin.length; i++) {
            buf.append(this.coordinatesMin[i].getValue());
            if (i != this.coordinatesMin.length - 1) {
                buf.append(",");
            }
        }

        buf.append(") to (");

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
