package org.objectweb.proactive.extensions.structuredp2p.grid2D;

import java.io.Serializable;

/**
 * An aware object is an object which is aware of his neighbors.
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class AwareObject implements Serializable {
	private int x;
	private int y;

	/**
	 * Neighbors of the current AwareObject. This object can have to the maximum
	 * 4 neighbors. Where North <=> neighbors[0], East <=> neighbors[1], Sud <=>
	 * neighbors[2], Weast <=> neighbors[3].
	 */
	private Object[] neighbors = new Object[4];
	private int index = 0;

	/**
	 * The no-argument constructor as commanded by ProActive.
	 */
	public AwareObject() {
	}

	/**
	 * Initialize a new AwareObject with the specified coordinates.
	 * 
	 * @param x
	 *            the x-coordinate in the grid2d.
	 * 
	 * @param y
	 *            the y-coordinate in the grid2d.
	 */
	public AwareObject(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Add a new neighbor for the current object.
	 * 
	 * @param ao
	 *            the neighbor to add.
	 */
	public void add(AwareObject ao) {
		if (this.index == 3) {
			throw new IllegalArgumentException(
					"An AwareObject can't have more than 4 neighbors.");
		}

		this.neighbors[this.index] = ao;
		this.index++;
	}

	/**
	 * Returns the x-coordinate of the current object for the grid to which it
	 * belongs.
	 * 
	 * @return the x-coordinate of the current object for the grid to which it
	 *         belongs.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the y-coordinate of the current object for the grid to which it
	 * belongs.
	 * 
	 * @return the y-coordinate of the current object for the grid to which it
	 *         belongs.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns the neighbors of the current object.
	 * 
	 * @return the neighbors of the current object.
	 */
	public Object[] getNeighbors() {
		return neighbors;
	}

	/**
	 * Sets the x-coordinate.
	 * 
	 * @param x
	 *            the new x-coordinate to set.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Sets the y-coordinate.
	 * 
	 * @param y
	 *            the new y-coordinate to set.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Sets the neighbors of the current object.
	 * 
	 * @param neighbors
	 *            the new neighbors to set.
	 */
	public void setNeighbors(AwareObject[] neighbors) {
		this.neighbors = neighbors;
	}
}
