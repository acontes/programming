package org.objectweb.proactive.extensions.structuredp2p.grid2D;

import java.io.Serializable;

import org.objectweb.proactive.api.PAActiveObject;

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
	private AwareObject stub;

	private AwareObject northNeighbor = null;
	private AwareObject eastNeighbor = null;
	private AwareObject southNeighbor = null;
	private AwareObject westNeighbor = null;

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
		this.stub = (AwareObject) PAActiveObject.getStubOnThis();
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
	 * Returns the stub associated to the current object.
	 * 
	 * @return the stub associated to the current object.
	 */
	public AwareObject getStub() {
		return this.stub;
	}

	/**
	 * @return the northNeighbor
	 */
	public AwareObject getNorthNeighbor() {
		return northNeighbor;
	}

	/**
	 * @return the eastNeighbor
	 */
	public AwareObject getEastNeighbor() {
		return eastNeighbor;
	}

	/**
	 * @return the southNeighbor
	 */
	public AwareObject getSouthNeighbor() {
		return southNeighbor;
	}

	/**
	 * @return the westNeighbor
	 */
	public AwareObject getWestNeighbor() {
		return westNeighbor;
	}

	/**
	 * @param northNeighbor
	 *            the northNeighbor to set
	 */
	public void setNorthNeighbor(AwareObject northNeighbor) {
		this.northNeighbor = northNeighbor;
	}

	/**
	 * @param eastNeighbor
	 *            the eastNeighbor to set
	 */
	public void setEastNeighbor(AwareObject eastNeighbor) {
		this.eastNeighbor = eastNeighbor;
	}

	/**
	 * @param southNeighbor
	 *            the southNeighbor to set
	 */
	public void setSouthNeighbor(AwareObject southNeighbor) {
		this.southNeighbor = southNeighbor;
	}

	/**
	 * @param westNeighbor
	 *            the westNeighbor to set
	 */
	public void setWestNeighbor(AwareObject westNeighbor) {
		this.westNeighbor = westNeighbor;
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

	public String toString() {
		StringBuffer buf = new StringBuffer("AwareObject x=" + this.x + ", y="
				+ this.y + "\n");
		buf.append("  north = " + this.northNeighbor + "\n");
		buf.append("  east = " + this.eastNeighbor + "\n");
		buf.append("  south = " + this.southNeighbor + "\n");
		buf.append("  west = " + this.westNeighbor + "\n");

		return buf.toString();
	}
}
