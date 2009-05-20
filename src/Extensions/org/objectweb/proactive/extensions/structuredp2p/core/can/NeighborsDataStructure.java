package org.objectweb.proactive.extensions.structuredp2p.core.can;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


/**
 * Data structure used in order to store the neighbors of a {@link Peer}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings( { "serial" })
public class NeighborsDataStructure implements Iterable<Peer>, Serializable {

    /**
     * Inferior direction compared to a given peer.
     */
    public static final int INFERIOR_DIRECTION = 0;

    /**
     * Superior direction compared to a given peer.
     */
    public static final int SUPERIOR_DIRECTION = 1;

    /**
     * Neighbors of a {@link Peer} for the managed {@link Area}. The neighbors are a two-dimensional
     * array of {@link Vector}. Each line corresponds to a dimension. The number of columns is
     * always equal to two. The first column corresponds to the neighbors having a coordinate lower
     * than the current pair on the given dimension. The second column is the reverse. The neighbors
     * are ordered coordinate on a given dimension.
     */
    private Vector<Peer>[][] neighbors = new Vector[CANOverlay.NB_DIMENSIONS][2];

    /**
     * The areas associated to the neighbors
     */
    private Vector<Area>[][] associatedAreas = new Vector[CANOverlay.NB_DIMENSIONS][2];

    /**
     * Constructor
     */
    public NeighborsDataStructure() {
        for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            this.neighbors[i][NeighborsDataStructure.INFERIOR_DIRECTION] = new Vector<Peer>();
            this.neighbors[i][NeighborsDataStructure.SUPERIOR_DIRECTION] = new Vector<Peer>();
            this.associatedAreas[i][NeighborsDataStructure.INFERIOR_DIRECTION] = new Vector<Area>();
            this.associatedAreas[i][NeighborsDataStructure.SUPERIOR_DIRECTION] = new Vector<Area>();
        }
    }

    /**
     * Constructor.
     * 
     * @param neighbors
     *            the neighbors to set.
     */
    public NeighborsDataStructure(Vector[][] neighbors, Vector[][] associatedAreas) {
        this.neighbors = neighbors;
    }

    /**
     * Add a new neighbor at the specified <code>dimension</code>, <code>direction</code>. Warning,
     * the {@link Area} contained by the peer which is given in parameters must be initialized.
     * 
     * @param remotePeer
     *            the remote peer to add as neighbor.
     * @param dimension
     *            the dimension index (must be in <code>0</code> and {@link #NB_DIMENSIONS - 1}
     *            include).
     * @param direction
     *            the direction ({@link #INFERIOR_DIRECTION} or {@link #SUPERIOR_DIRECTION}).
     * @return <code>true</code> if the neighbor has been add, <code>false</code> otherwise.
     */
    public boolean add(Peer remotePeer, int dimension, int direction) {
        return this.add(remotePeer, ((CANOverlay) remotePeer.getStructuredOverlay()).getArea(), dimension,
                direction);
    }

    /**
     * Add all the neighbors of the given <code>NeighborsDataStructure</code>.
     * 
     * @param neighbors
     *            the neighbors to add.
     * @return <code>true</code> if the add has succeeded, <code>false</code> otherwise.
     */
    public boolean addAll(NeighborsDataStructure neighbors) {
        boolean res = true;
        for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
            for (int direction = 0; direction < 2; direction++) {
                for (Peer peer : neighbors.getNeighbors(dim, direction)) {
                    res &= this.add(peer, dim, direction);
                }
            }
        }
        return res;
    }

    /**
     * Add a new neighbor at the specified <code>dimension</code>, <code>direction</code>. Warning,
     * the {@link Area} contained by the peer which is given in parameters must be initialized.
     * 
     * @param remotePeer
     *            the remote peer to add as neighbor.
     * @param area
     *            the area associated to the peer specified.
     * @param dimension
     *            the dimension index (must be in <code>0</code> and {@link #NB_DIMENSIONS - 1}
     *            include).
     * @param direction
     *            the direction ({@link #INFERIOR_DIRECTION} or {@link #SUPERIOR_DIRECTION}).
     * @return <code>true</code> if the neighbor has been add, <code>false</code> otherwise.
     */
    public boolean add(Peer remotePeer, Area area, int dimension, int direction) {
        int index = 0;
        int nextDimension = NeighborsDataStructure.getNextDimension(dimension);

        for (Area selectedArea : this.associatedAreas[dimension][direction]) {
            if (area.getCoordinateMin(nextDimension).compareTo(selectedArea.getCoordinateMin(nextDimension)) < 0) {
                this.associatedAreas[dimension][direction].add(index, area);
                this.neighbors[dimension][direction].add(index, remotePeer);
                return true;
            }
            index++;
        }

        return this.associatedAreas[dimension][direction].add(area) &&
            this.neighbors[dimension][direction].add(remotePeer);
    }

    /**
     * Remove the specified {@link Peer} if it is contained by the data structure.
     * 
     * @param remotePeer
     *            the remote peer to remove.
     * @return <code>true</code> if the neighbor has been removed, <code>false</code> otherwise.
     */
    public boolean remove(Peer remotePeer) {
        for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
            for (int direction = 0; direction < 2; direction++) {
                int index = -1;

                if ((index = this.neighbors[dim][direction].indexOf(remotePeer)) != -1) {
                    this.neighbors[dim][direction].remove(index);
                    this.associatedAreas[dim][direction].remove(index);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Remove the given {@link Peer} on the specified <code>dimension</code>, <code>direction</code>
     * .
     * 
     * @param remotePeer
     *            the peer to remove.
     * @param dimension
     *            the dimension index (must be in <code>0</code> and {@link #NB_DIMENSIONS - 1}
     *            include).
     * @param direction
     *            the direction ({@link #INFERIOR_DIRECTION} or {@link #SUPERIOR_DIRECTION}).
     * @return <code>true</code> if the neighbor has been removed, <code>false</code> otherwise.
     */
    public boolean remove(Peer remotePeer, int dimension, int direction) {
        int index = -1;

        if ((index = this.neighbors[dimension][direction].indexOf(remotePeer)) != -1) {
            this.neighbors[dimension][direction].remove(index);
            this.associatedAreas[dimension][direction].remove(index);
            return true;
        }

        return false;
    }

    /**
     * Remove all neighbors for a given <code>dimension</code>, <code>direction</code>.
     * 
     * @param dimension
     *            the dimension index (must be in <code>0</code> and {@link #NB_DIMENSIONS - 1}
     *            include).
     * @param direction
     *            the direction ({@link #INFERIOR_DIRECTION} or {@link #SUPERIOR_DIRECTION}).
     */
    public void removeAll(int dimension, int direction) {
        this.neighbors[dimension][direction].clear();
        this.associatedAreas[dimension][direction].clear();
    }

    /**
     * Remove all neighbors of the current data structure.
     */
    public void removeAll() {
        for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
            for (int direction = 0; direction < 2; direction++) {
                this.removeAll(dim, direction);
            }
        }
    }

    /**
     * Indicates if the data structure contains the specified {@link Peer} as neighbor.
     * 
     * @param remotePeer
     *            the neighbor to check.
     * @return <code>true</code> if the data structure contains the peer as neighbor,
     *         <code>false</code> otherwise.
     */
    public boolean hasNeighbor(Peer remotePeer) {
        for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
            for (int direction = 0; direction < 2; direction++) {
                if (this.neighbors[dim][direction].contains(remotePeer)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Indicates if the data structure contains the specified {@link Peer} as neighbor at the
     * specified <code>dimension</code> and <code>direction</code>.
     * 
     * @param remotePeer
     *            the neighbor to check.
     * @param dim
     *            the dimension.
     * @param direction
     *            the direction.
     * 
     * @return <code>true</code> if the data structure contains the peer as neighbor,
     *         <code>false</code> otherwise.
     */
    public boolean hasNeighbor(Peer remotePeer, int dim, int direction) {
        return this.neighbors[dim][direction].contains(remotePeer);
    }

    /**
     * Returns an {@link Area} from its associated {@link Peer}.
     * 
     * @param remotePeer
     *            the criteria used in order to find the area
     * @return the area found or <code>null</code>.
     */
    public Area getArea(Peer remotePeer) {
        for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
            for (int direction = 0; direction < 2; direction++) {
                int index = -1;

                if ((index = this.neighbors[dim][direction].indexOf(remotePeer)) != -1) {
                    return this.associatedAreas[dim][direction].get(index);
                }
            }
        }

        return null;
    }

    /**
     * Returns an {@link Area} from its associated {@link Peer}.
     * 
     * @param remotePeer
     *            the criteria used in order to find the area
     * @param dim
     *            the dimension.
     * @param direction
     *            the direction.
     * @return the area found or <code>null</code>.
     */
    public Area getArea(Peer remotePeer, int dim, int direction) {
        int index = -1;

        if ((index = this.neighbors[dim][direction].indexOf(remotePeer)) != -1) {
            return this.associatedAreas[dim][direction].get(index);
        }

        return null;
    }

    /**
     * Returns a {@link Peer} from its managed {@link Area} if it is in the neighbors collection.
     * 
     * @param dimension
     *            the dimension index (must be in <code>0</code> and {@link #NB_DIMENSIONS - 1}
     *            include).
     * @param direction
     *            the direction ({@link #INFERIOR_DIRECTION} or {@link #SUPERIOR_DIRECTION}).
     * @param area
     *            the criteria used in order to find the peer.
     * @return the peer found or <code>null</code>.
     */
    public Peer getPeer(int dimension, int direction, Area area) {
        int index = -1;

        if ((index = this.associatedAreas[dimension][direction].indexOf(area)) != -1) {
            return this.neighbors[dimension][direction].get(index);
        }

        return null;
    }

    /**
     * Returns the neighbors data structure.
     * 
     * @return the neighbors data structure.
     */
    public List[][] getNeighbors() {
        return this.neighbors;
    }

    /**
     * Returns the neighbors of the managed area for the given dimension.
     * 
     * @param dimension
     *            the dimension to use (dimension start to 0 and max is defined by
     *            {@link #NB_DIMENSIONS} - 1).
     * @return the neighbors of the managed area for the specified dimension.
     */
    public List[] getNeighbors(int dimension) {
        return this.neighbors[dimension];
    }

    /**
     * Returns the neighbors of the managed area for the specified <code>dimension</code> and
     * <code>direction</code>.
     * 
     * @param dimension
     *            the dimension to use (dimension start to <code>0</code> and max is defined by
     *            {@link #NB_DIMENSIONS} - 1).
     * @param direction
     *            the direction ({@link #INFERIOR_DIRECTION} or {@link #SUPERIOR_DIRECTION}).
     * @return the neighbors of the managed area for the specified dimension.
     */
    public List<Peer> getNeighbors(int dimension, int direction) {
        return this.neighbors[dimension][direction];
    }

    /**
     * Returns the neighbor which is the nearest of the given {@link Coordinate} for the specified
     * <code>dimension</code>, <code>direction</code>.
     * 
     * @param coordinate
     *            the coordinate to check.
     * @param dim
     *            the dimension.
     * @param direction
     *            the direction.
     */
    public Peer getNearestNeighborFrom(Coordinate coordinate, int dim, int direction) {
        Area nearest = null;
        int distance;
        int minDistance = Integer.MAX_VALUE;

        for (Area area : this.associatedAreas[dim][direction]) {
            if ((distance = Integer.parseInt(coordinate.getValue()) -
                Integer.parseInt(area.getCoordinateMax(dim + 1).getValue())) < minDistance) {
                if (distance < minDistance) {
                    minDistance = distance;
                }
                nearest = area;
            }
        }

        return this.neighbors[dim][direction].get(this.associatedAreas[dim][direction].indexOf(nearest));
    }

    /**
     * Returns the next dimension following the specified dimension.
     * 
     * @param dimension
     *            the specified dimension.
     * @return the next dimension following the specified dimension.
     */
    public static int getNextDimension(int dimension) {
        return (dimension + 1) % 2;
    }

    /**
     * Update the area of the specified {@link Peer}.
     * 
     * @param remotePeer
     *            the peer to update.
     * @param area
     *            the area to set.
     * @param dimension
     *            the dimension.
     * @param direction
     *            the direction.
     * @return <code>true</code> if the are has been update, <code>false</code> otherwise.
     */
    public boolean updateArea(Peer remotePeer, Area area, int dimension, int direction) {
        int index = this.neighbors[dimension][direction].indexOf(remotePeer);

        if (index == -1) {
            return false;
        }

        /*
         * Peer peer = this.neighbors[dimension][direction].get(index); CANOverlay overlay =
         * (CANOverlay) peer.getStructuredOverlay(); overlay.setArea(area);
         * peer.setStructuredOverlay(overlay); this.neighbors[dimension][direction].set(index,
         * peer);
         */

        return this.associatedAreas[dimension][direction].set(index, area) != null;
    }

    /**
     * Returns the number of neighbors the data structure manages.
     * 
     * @return the number of neighbors the data structure manages.
     */
    public int size() {
        int size = 0;

        for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
            for (int direction = 0; direction < 2; direction++) {
                size += this.neighbors[dim][direction].size();
            }
        }
        return size;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Peer> iterator() {
        ArrayList<Peer> list = new ArrayList<Peer>();

        for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
            for (int direction = 0; direction < 2; direction++) {
                list.addAll(this.neighbors[dim][direction]);
            }
        }

        return list.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        String buf = "";

        for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
            for (int direction = 0; direction < 2; direction++) {
                buf += "[";

                for (Peer peer : this.neighbors[dim][direction]) {
                    buf += ((CANOverlay) peer.getStructuredOverlay()).getArea() + ",";
                }
                buf += "] " + dim + " ";
            }
            buf += "\n";
        }
        return buf;
    }
}
