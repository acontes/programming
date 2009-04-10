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

    private Object[] neighbors = new Object[4];
    private int index = 0;

    /*
     * * The no-argument constructor as commanded by ProActive.
     */
    public AwareObject() {

    }

    /*
     * *
     * 
     * @param x
     * 
     * @param y
     */
    public AwareObject(int x, int y) {
        this.x = x;
        this.y = y;

    }

    public void add(AwareObject e) {
        this.neighbors[this.index] = e;
        this.index++;
    }

    /*
     * * Returns the x-coordinate of the current object for the grid to which it belongs.
     * 
     * @return the x-coordinate of the current object for the grid to which it belongs.
     */
    public int getX() {
        return x;
    }

    /*
     * * Returns the y-coordinate of the current object for the grid to which it belongs.
     * 
     * @return the y-coordinate of the current object for the grid to which it belongs.
     */
    public int getY() {
        return y;
    }

    /*
     * *
     * 
     * @return
     */
    public Object[] getNeighbors() {
        return neighbors;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setNeighbors(Object[] neighbors) {
        this.neighbors = neighbors;
    }

}
