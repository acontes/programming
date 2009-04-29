package org.objectweb.proactive.extensions.structuredp2p.grid2D;

import java.io.Serializable;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.api.PAActiveObject;


/**
 * An aware object is an object which is aware of his neighbors.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class AwareObject implements Serializable, InitActive {
    private int x = 0;
    private int y = 0;
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
    public AwareObject(Integer x, Integer y) {
        this.x = x.intValue();
        this.y = y.intValue();
    }

    /**
     * Lookup a {@link AwareObject} by it coordinates from the current object and returns it.
     * 
     * @param x
     *            the x-coordinate to lookup.
     * 
     * @param y
     *            the y-coordinate to lookup.
     * 
     * @return the AwareObject find or null.
     */
    public AwareObject find(int x, int y) {
        if (this.x == x && this.y == y) {
            return this;
        } else if (this.x < x) {
            if (this.eastNeighbor == null) {
                return null;
            }

            return this.eastNeighbor.find(x, y);
        } else if (this.x > x) {
            if (this.westNeighbor == null) {
                return null;
            }

            return this.westNeighbor.find(x, y);
        } else if (this.y < y) {
            if (this.southNeighbor == null) {
                return null;
            }

            return this.southNeighbor.find(x, y);
        } else if (this.y > y) {
            if (this.northNeighbor == null) {
                return null;
            }

            return this.northNeighbor.find(x, y);
        }

        return null;
    }

    /**
     * Returns the x-coordinate of the current object for the grid to which it belongs.
     * 
     * @return the x-coordinate of the current object for the grid to which it belongs.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Returns the y-coordinate of the current object for the grid to which it belongs.
     * 
     * @return the y-coordinate of the current object for the grid to which it belongs.
     */
    public int getY() {
        return this.y;
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
     * Returns the north neighbor of the current object.
     * 
     * @return the northNeighbor of the current object.
     */
    public AwareObject getNorthNeighbor() {
        return this.northNeighbor;
    }

    /**
     * Returns the east neighbor of the current object.
     * 
     * @return the eastNeighbor of the current object.
     */
    public AwareObject getEastNeighbor() {
        return this.eastNeighbor;
    }

    /**
     * Returns the south neighbor of the current object.
     * 
     * @return the southNeighbor of the current object.
     */
    public AwareObject getSouthNeighbor() {
        return this.southNeighbor;
    }

    /**
     * Returns the west neighbor of the current object.
     * 
     * @return the westNeighbor of the current object.
     */
    public AwareObject getWestNeighbor() {
        return this.westNeighbor;
    }

    /**
     * Sets the north neighbor of the current object.
     * 
     * @param northNeighbor
     *            the new northNeighbor to set.
     */
    public void setNorthNeighbor(AwareObject northNeighbor) {
        this.northNeighbor = northNeighbor;
    }

    /**
     * Sets the east neighbor of the current object.
     * 
     * @param eastNeighbor
     *            the new eastNeighbor to set.
     */
    public void setEastNeighbor(AwareObject eastNeighbor) {
        this.eastNeighbor = eastNeighbor;
    }

    /**
     * Sets the south neighbor of the current object.
     * 
     * @param southNeighbor
     *            the new southNeighbor to set.
     */
    public void setSouthNeighbor(AwareObject southNeighbor) {
        this.southNeighbor = southNeighbor;
    }

    /**
     * Sets the west neighbor of the current object.
     * 
     * @param westNeighbor
     *            the new westNeighbor to set.
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

    /**
     * Returns a description for a given neighbor.
     * 
     * @param neighbor
     *            the neighbor we want to get description.
     * 
     * @return a description for a given neighbor.
     */
    private String getNeighborDescription(AwareObject neighbor) {
        StringBuffer buf = new StringBuffer();

        if (neighbor == null) {
            buf.append("null");
        } else {
            buf.append("x=" + neighbor.getX() + ",");
            buf.append("y=" + neighbor.getY());
        }

        return buf.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer buf = new StringBuffer("AwareObject x=" + this.x + ", y=" + this.y + "\n");
        buf.append("\n");
        buf.append("\t\t");
        buf.append(getNeighborDescription(this.northNeighbor));
        buf.append("\n");
        buf.append("\t\t ^ \t\t\n");
        buf.append("\t\t | \n");
        buf.append(getNeighborDescription(this.westNeighbor));
        buf.append(" <- \t x \t -> ");
        buf.append(getNeighborDescription(this.eastNeighbor));
        buf.append("\n");
        buf.append("\t\t |\n");
        buf.append("\t\t v\n");
        buf.append("\t\t");
        buf.append(getNeighborDescription(this.southNeighbor));
        buf.append("\n");

        return buf.toString();
    }

    /**
     * {@inheritDoc}
     */
    public void initActivity(Body arg0) {
        this.stub = (AwareObject) PAActiveObject.getStubOnThis();
    }
}
