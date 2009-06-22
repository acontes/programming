package org.objectweb.proactive.extensions.structuredp2p.core.overlay.can;

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
     * The peer which has these neighbors.
     */
    private final Peer ownerPeer;

    /**
     * Inferior direction compared to a given peer.
     */
    public static final int INFERIOR_DIRECTION = 0;

    /**
     * Superior direction compared to a given peer.
     */
    public static final int SUPERIOR_DIRECTION = 1;

    /**
     * Neighbors of a {@link Peer} for the managed {@link Zone}. The neighbors are a two-dimensional
     * array of {@link Vector}. Each line corresponds to a dimension. The number of columns is
     * always equal to two. The first column corresponds to the neighbors having a coordinate lower
     * than the current pair on the given dimension. The second column is the reverse. The neighbors
     * are ordered coordinate on a given dimension.
     */
    private List<Peer>[][] neighbors = new Vector[CANOverlay.NB_DIMENSIONS][2];

    /**
     * The zones associated to the neighbors.
     */
    private List<Zone>[][] associatedZones = new Vector[CANOverlay.NB_DIMENSIONS][2];

    /**
     * Constructor.
     */
    public NeighborsDataStructure(Peer masterPeer) {
        this.ownerPeer = masterPeer;

        for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            this.neighbors[i][NeighborsDataStructure.INFERIOR_DIRECTION] = new Vector<Peer>();
            this.neighbors[i][NeighborsDataStructure.SUPERIOR_DIRECTION] = new Vector<Peer>();
            this.associatedZones[i][NeighborsDataStructure.INFERIOR_DIRECTION] = new Vector<Zone>();
            this.associatedZones[i][NeighborsDataStructure.SUPERIOR_DIRECTION] = new Vector<Zone>();
        }
    }

    /**
     * Add a new neighbor at the specified <code>dimension</code>, <code>direction</code>. Warning,
     * the {@link Zone} contained by the peer which is given in parameters must be initialized.
     * 
     * @param remotePeer
     *            the remote peer to add as neighbor.
     * @param dimension
     *            the dimension index (must be in <code>0</code> and
     *            {@link CANOverlay#NB_DIMENSIONS - 1} include).
     * @param direction
     *            the direction ({@link #INFERIOR_DIRECTION} or {@link #SUPERIOR_DIRECTION}).
     * @return <code>true</code> if the neighbor has been add, <code>false</code> otherwise.
     */
    public boolean add(Peer remotePeer, int dimension, int direction) {
        return this.add(remotePeer, ((CANOverlay) remotePeer.getStructuredOverlay()).getZone(), dimension,
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
                    res &= this.add(peer, neighbors.getZone(peer), dim, direction);
                }
            }
        }
        return res;
    }

    /**
     * Add a new neighbor at the specified <code>dimension</code>, <code>direction</code> or updates
     * its zone if it already exists. Warning, the {@link Zone} contained by the peer which is given
     * in parameters must be initialized.
     * 
     * @param remotePeer
     *            the remote peer to add as neighbor.
     * @param zone
     *            the zone associated to the peer specified.
     * @param dimension
     *            the dimension index (must be in <code>0</code> and
     *            {@link CANOverlay#NB_DIMENSIONS - 1} include).
     * @param direction
     *            the direction ({@link #INFERIOR_DIRECTION} or {@link #SUPERIOR_DIRECTION}).
     * @return <code>true</code> if the neighbor has been add, <code>false</code> otherwise.
     */
    public boolean add(Peer remotePeer, Zone zone, int dimension, int direction) {
        if (remotePeer.equals(this.ownerPeer)) {
            return false;
        } else if (this.neighbors[dimension][direction].contains(remotePeer)) {
            return this.updateZone(remotePeer, zone, dimension, direction);
        }

        int index = 0;
        int nextDimension = CANOverlay.getNextDimension(dimension);

        for (Zone selectedZone : this.associatedZones[dimension][direction]) {
            if (zone.getCoordinateMin(nextDimension).compareTo(selectedZone.getCoordinateMin(nextDimension)) < 0) {
                this.associatedZones[dimension][direction].add(index, zone);
                this.neighbors[dimension][direction].add(index, remotePeer);
                return true;
            }
            index++;
        }

        return this.associatedZones[dimension][direction].add(zone) &&
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
                    this.associatedZones[dim][direction].remove(index);
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
     *            the dimension index (must be in <code>0</code> and
     *            {@link CANOverlay#NB_DIMENSIONS - 1} include).
     * @param direction
     *            the direction ({@link #INFERIOR_DIRECTION} or {@link #SUPERIOR_DIRECTION}).
     * @return <code>true</code> if the neighbor has been removed, <code>false</code> otherwise.
     */
    public boolean remove(Peer remotePeer, int dimension, int direction) {
        int index = -1;

        if ((index = this.neighbors[dimension][direction].indexOf(remotePeer)) != -1) {
            this.neighbors[dimension][direction].remove(index);
            this.associatedZones[dimension][direction].remove(index);
            return true;
        }

        return false;
    }

    /**
     * Remove all neighbors for a given <code>dimension</code>, <code>direction</code>.
     * 
     * @param dimension
     *            the dimension index (must be in <code>0</code> and
     *            {@link CANOverlay#NB_DIMENSIONS - 1} include).
     * @param direction
     *            the direction ({@link #INFERIOR_DIRECTION} or {@link #SUPERIOR_DIRECTION}).
     */
    public void removeAll(int dimension, int direction) {
        this.neighbors[dimension][direction].clear();
        this.associatedZones[dimension][direction].clear();
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
     * Returns an {@link Zone} from its associated {@link Peer}.
     * 
     * @param remotePeer
     *            the criteria used in order to find the zone
     * @return the zone found or <code>null</code>.
     */
    public Zone getZone(Peer remotePeer) {
        for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
            for (int direction = 0; direction < 2; direction++) {
                int index = -1;

                if ((index = this.neighbors[dim][direction].indexOf(remotePeer)) != -1) {
                    return this.associatedZones[dim][direction].get(index);
                }
            }
        }

        return null;
    }

    /**
     * Returns an {@link Zone} from its associated {@link Peer}.
     * 
     * @param remotePeer
     *            the criteria used in order to find the zone
     * @param dim
     *            the dimension.
     * @param direction
     *            the direction.
     * @return the zone found or <code>null</code>.
     */
    public Zone getZone(Peer remotePeer, int dim, int direction) {
        int index = -1;

        if ((index = this.neighbors[dim][direction].indexOf(remotePeer)) != -1) {
            return this.associatedZones[dim][direction].get(index);
        }

        return null;
    }

    /**
     * Returns a {@link Peer} from its managed {@link Zone} if it is in the neighbors collection.
     * 
     * @param dimension
     *            the dimension index (must be in <code>0</code> and
     *            {@link CANOverlay#NB_DIMENSIONS - 1} include).
     * @param direction
     *            the direction ({@link #INFERIOR_DIRECTION} or {@link #SUPERIOR_DIRECTION}).
     * @param zone
     *            the criteria used in order to find the peer.
     * @return the peer found or <code>null</code>.
     */
    public Peer getPeer(int dimension, int direction, Zone zone) {
        int index = -1;

        if ((index = this.associatedZones[dimension][direction].indexOf(zone)) != -1) {
            return this.neighbors[dimension][direction].get(index);
        }

        return null;
    }

    /**
     * Returns the neighbors data structure.
     * 
     * @return the neighbors data structure.
     */
    public List<Peer>[][] getNeighbors() {
        return this.neighbors;
    }

    /**
     * Returns the neighbors of the managed zone for the given dimension.
     * 
     * @param dimension
     *            the dimension to use (dimension start to 0 and max is defined by
     *            {@link CANOverlay#NB_DIMENSIONS} - 1).
     * @return the neighbors of the managed zone for the specified dimension.
     */
    public List<Peer>[] getNeighbors(int dimension) {
        return this.neighbors[dimension];
    }

    /**
     * Returns the neighbors of the managed zone for the specified <code>dimension</code> and
     * <code>direction</code>.
     * 
     * @param dimension
     *            the dimension to use (dimension start to <code>0</code> and max is defined by
     *            {@link CANOverlay#NB_DIMENSIONS} - 1).
     * @param direction
     *            the direction ({@link #INFERIOR_DIRECTION} or {@link #SUPERIOR_DIRECTION}).
     * @return the neighbors of the managed zone for the specified dimension.
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
    public Peer getNearestNeighborFrom(Zone ownerZone, Coordinate coordinate, int dim, int direction) {
        int nearest = 0;

        if (ownerZone.getCoordinateMax(CANOverlay.getNextDimension(dim)).compareTo(coordinate) < 0) {
            return this.neighbors[dim][direction].get(0);
        } else if (coordinate.compareTo(ownerZone.getCoordinateMin(CANOverlay.getNextDimension(dim))) < 0) {
            return this.neighbors[dim][direction].get(this.neighbors[dim][direction].size() - 1);
        } else {
            for (int i = 1; i < this.associatedZones[dim][direction].size(); i++) {
                if (this.associatedZones[dim][direction].get(i).getCoordinateMax(
                        CANOverlay.getNextDimension(dim)).compareTo(coordinate) > 0) {
                    nearest = i;
                } else {
                    break;
                }
            }
        }

        return this.neighbors[dim][direction].get(nearest);
    }

    /**
     * Returns the peer which maintains this data structure.
     * 
     * @return the owner peer.
     */
    public Peer getOwnerPeer() {
        return this.ownerPeer;
    }

    /**
     * Update the zone of the specified {@link Peer}.
     * 
     * @param remotePeer
     *            the peer to update.
     * @param zone
     *            the zone to set.
     * @param dimension
     *            the dimension.
     * @param direction
     *            the direction.
     * @return <code>true</code> if the zone has been update, <code>false</code> otherwise.
     */
    public boolean updateZone(Peer remotePeer, Zone zone, int dimension, int direction) {
        int index = this.neighbors[dimension][direction].indexOf(remotePeer);

        if (index == -1) {
            return false;
        }

        return this.associatedZones[dimension][direction].set(index, zone) != null;
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
                    buf += ((CANOverlay) peer.getStructuredOverlay()).getZone() + ",";
                }
                buf += "] " + dim + " ";
            }
            buf += "\n";
        }
        return buf;
    }
}
