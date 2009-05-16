package org.objectweb.proactive.extensions.structuredp2p.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.CANOverlay;

/**
 * Collection used in order to store the neighbors of a {@link Peer}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("unchecked")
public class NeighborsArray implements Iterable<Peer> {

	/**
	 * Inferior direction compared to a given peer.
	 */
	public static final int INFERIOR_DIRECTION = 0;

	/**
	 * Superior direction compared to a given peer.
	 */
	public static final int SUPERIOR_DIRECTION = 1;

	/**
	 * Neighbors of a {@link Peer} for the managed {@link Area}. The neighbors
	 * are a two-dimensional array of {@link HashMap}. Each line corresponds to
	 * a dimension. The number of columns is always equal to two. The first
	 * column corresponds to the neighbors having a coordinate lower than the
	 * current pair on the given dimension. The second column is the reverse.
	 */
	private HashMap<Peer, Area>[][] neighbors = new HashMap[CANOverlay.NB_DIMENSIONS][2];

	/**
	 * Constructor
	 */
	public NeighborsArray() {
		for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
			this.neighbors[i][NeighborsArray.INFERIOR_DIRECTION] = new HashMap<Peer, Area>();
			this.neighbors[i][NeighborsArray.SUPERIOR_DIRECTION] = new HashMap<Peer, Area>();
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param neighbors
	 *            the neighbors to set.
	 */
	public NeighborsArray(HashMap<Peer, Area>[][] neighbors) {
		this.neighbors = neighbors;
	}

	/**
	 * Add a new neighbor at the specified <code>dimension</code>,
	 * <code>direction</code>. Warning, the {@link Area} contained by the peer
	 * which is given in parameters must be initialized.
	 * 
	 * @param remotePeer
	 *            the remote peer to add as neighbor.
	 * @param dimension
	 *            the dimension index (must be in 0 and {@link #NB_DIMENSIONS -
	 *            1} include).
	 * @param direction
	 *            the direction ({@link #INFERIOR_DIRECTION} or
	 *            {@link #SUPERIOR_DIRECTION}).
	 * @return true if the neighbor has been add, false if not or if the given
	 *         peer is already neighbor.
	 */
	public boolean add(Peer remotePeer, int dimension, int direction) {
		if (this.neighbors[dimension][direction].put(remotePeer,
				((CANOverlay) remotePeer.getStructuredOverlay()).getArea()) == null) {
			return true;
		}

		return false;
	}

	/**
	 * Add a new neighbor at the specified <code>dimension</code>,
	 * <code>direction</code>. Warning, the {@link Area} contained by the peer
	 * which is given in parameters must be initialized.
	 * 
	 * @param remotePeer
	 *            the remote peer to add as neighbor.
	 * @param area
	 *            the area associated to the peer.
	 * @param dimension
	 *            the dimension index (must be in 0 and {@link #NB_DIMENSIONS -
	 *            1} include).
	 * @param direction
	 *            the direction ({@link #INFERIOR_DIRECTION} or
	 *            {@link #SUPERIOR_DIRECTION}).
	 * @return true if the neighbor has been add, false if not or if the given
	 *         peer is already neighbor.
	 */
	public boolean add(Peer remotePeer, Area area, int dimension, int direction) {
		if (this.neighbors[dimension][direction].put(remotePeer, area) == null) {
			return true;
		}

		return false;
	}

	/**
	 * Remove the given {@link Peer} on the chosen <code>dimension</code>,
	 * <code>direction</code>.
	 * 
	 * @param remotePeer
	 *            the remote peer to remove.
	 * @param dimension
	 *            the dimension index (must be in 0 and {@link #NB_DIMENSIONS -
	 *            1} include).
	 * @param direction
	 *            the direction ({@link #INFERIOR_DIRECTION} or
	 *            {@link #SUPERIOR_DIRECTION}).
	 * @return true if the neighbor has been add, false otherwise.
	 */
	public boolean remove(Peer remotePeer, int dimension, int direction) {
		if (this.neighbors[dimension][direction].remove(remotePeer) != null) {
			return true;
		}

		return false;
	}

	/**
	 * Remove all neighbors for a given <code>dimension</code>,
	 * <code>direction</code>.
	 * 
	 * @param dimension
	 *            the dimension index (must be in 0 and {@link #NB_DIMENSIONS -
	 *            1} include).
	 * @param direction
	 *            the direction ({@link #INFERIOR_DIRECTION} or
	 *            {@link #SUPERIOR_DIRECTION}).
	 */
	public void removeAll(int dimension, int direction) {
		this.neighbors[dimension][direction].clear();
	}

	/**
	 * Returns an {@link Area} from its associated {@link Peer}.
	 * 
	 * @param remotePeer
	 *            the criteria used in order to find the area
	 * @return the area found or null.
	 */
	public Area getArea(Peer remotePeer) {
		Area areaFound = null;
		for (HashMap<Peer, Area>[] neighborsArray : this.neighbors) {
			for (HashMap<Peer, Area> neighbors : neighborsArray) {
				areaFound = neighbors.get(remotePeer);
				if (areaFound != null) {
					return areaFound;
				}
			}
		}

		return null;
	}

	/**
	 * Returns a {@link Peer} from its managed {@link Area} if it is in the
	 * neighbors collection.
	 * 
	 * @param dimension
	 *            the dimension index (must be in 0 and {@link #NB_DIMENSIONS -
	 *            1} include).
	 * @param direction
	 *            the direction ({@link #INFERIOR_DIRECTION} or
	 *            {@link #SUPERIOR_DIRECTION}).
	 * @param area
	 *            the criteria used in order to find the peer.
	 * @return the peer found or null.
	 */
	public Peer getPeer(int dimension, int direction, Area area) {
		return this.findAreaByPeer(this.neighbors[dimension][direction], area);
	}

	/**
	 * Returns the neighbors of the managed area for the given dimension.
	 * 
	 * @param dimension
	 *            the dimension to use (dimension start to 0 and max is defined
	 *            by {@link #NB_DIMENSIONS} - 1).
	 * @return the neighbors of the managed area for the given dimension.
	 */
	public HashMap<Peer, Area>[] getNeighbors(int dimension) {
		return this.neighbors[dimension];
	}

	/**
	 * Returns the neighbors of the managed area for the given dimension and
	 * order.
	 * 
	 * @param dimension
	 *            the dimension to use (dimension start to 0 and max is defined
	 *            by {@link #NB_DIMENSIONS} - 1).
	 * @param direction
	 *            the direction ({@link #INFERIOR_DIRECTION} or
	 *            {@link #SUPERIOR_DIRECTION}).
	 * @return the neighbors of the managed area for the given dimension.
	 */
	public Set<Peer> getNeighbors(int dimension, int direction) {
		return this.neighbors[dimension][direction].keySet();
	}

	/**
	 * Returns the neighbors data structure.
	 * 
	 * @return the neighbors data structure.
	 */
	public HashMap<Peer, Area>[][] getNeighbors() {
		return this.neighbors;
	}

	/**
	 * Returns a {@link Peer} from its managed {@link Area} if it is in the
	 * neighbors collection.
	 * 
	 * @param map
	 *            the map to iterate.
	 * @param area
	 *            the criteria used in order to find the peer.
	 * @return the peer found or null.
	 */
	public Peer findAreaByPeer(HashMap<Peer, Area> map, Area area) {
		Iterator<Entry<Peer, Area>> it = map.entrySet().iterator();
		Entry<Peer, Area> find;
		while (it.hasNext()) {
			find = it.next();
			if (find.getValue() == area) {
				return find.getKey();
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<Peer> iterator() {
		ArrayList<Peer> list = new ArrayList<Peer>();

		for (HashMap<Peer, Area>[] neighborsArray : this.neighbors) {
			for (HashMap<Peer, Area> neighbors : neighborsArray) {
				for (Peer peer : neighbors.keySet()) {
					list.add(peer);
				}
			}
		}

		return list.iterator();
	}
}
