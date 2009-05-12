package org.objectweb.proactive.extensions.structuredp2p.algorithm.merge;

import java.util.ArrayList;


public class Zone {

    public int xMin;
    public int xMax;
    public int yMin;
    public int yMax;

    private ArrayList<Zone>[][] neighbors;
    private ArrayList<int[]> splitHistory;

    public Zone(Zone zone) {
        this.neighbors = new ArrayList[2][2];
        this.splitHistory = new ArrayList<int[]>();
    }

    public boolean join(Zone zone) {
        // TODO
        return false;
    }

    public boolean merge(Zone zone) {
        // TODO
        return false;
    }

    public boolean contains(int x, int y) {
        if (x >= this.xMin && x < this.xMax && y >= this.yMin && y < this.yMax) {
            return true;
        }

        return false;
    }

    public Boolean addNeighbor(Zone zone, int dimension, int direction) {
        return this.neighbors[dimension][direction].add(zone);
    }

    public Boolean removeNeighbor(Zone zone) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (Zone neighbor : this.neighbors[i][j]) {
                    if (neighbor.equals(zone)) {
                        this.neighbors[i][j].remove(neighbor);
                    }
                }
            }
        }

        return false;
    }
}
