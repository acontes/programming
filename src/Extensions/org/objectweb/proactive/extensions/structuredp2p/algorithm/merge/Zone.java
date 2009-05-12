package org.objectweb.proactive.extensions.structuredp2p.algorithm.merge;

import java.util.ArrayList;


public class Zone {

    public int xMin = 0;
    public int xMax = 500;
    public int yMin = 0;
    public int yMax = 500;

    public ArrayList<Zone>[][] neighbors;
    public ArrayList<int[]> splitHistory;

    public Zone() {
        this.neighbors = new ArrayList[2][2];
        this.splitHistory = new ArrayList<int[]>();
    }

    public boolean join(Zone zone) {
        int dimension = 0;
        int direction = 0;
        int directionInv = (direction + 1) % 2;

        // Get the next dimension to split onto
        if (this.splitHistory != null && this.splitHistory.size() > 0) {
            dimension = (this.splitHistory.get(this.splitHistory.size() - 1)[0] + 1) % 2;
        }

        // Nouvelle zone
        // Modifications en x
        if (dimension == 0) {
            zone.xMin = (this.xMin + this.xMax) / 2;
            this.xMax = (this.xMin + this.xMax) / 2;
        } else if (dimension == 1) {
            zone.yMin = (this.yMin + this.yMax) / 2;
            this.yMax = (this.yMin + this.yMax) / 2;
        } else {
            return false;
        }

        zone.splitHistory = this.splitHistory;
        zone.splitHistory.add(new int[] { dimension, directionInv });
        zone.neighbors = this.neighbors;
        zone.neighbors[dimension][directionInv].clear();
        zone.neighbors[dimension][directionInv].add(this);

        this.splitHistory.add(new int[] { dimension, directionInv });
        this.neighbors[dimension][direction].clear();
        this.neighbors[dimension][direction].add(zone);

        return true;
    }

    public Zone leave() {
        if (this.splitHistory.size() > 0) {
            int[] lastOP = this.splitHistory.get(this.splitHistory.size() - 1);
            int dimension = lastOP[0];
            int direction = lastOP[1];

            int nbNeigbors = this.neighbors[dimension][direction].size();

            // If there is just one neighbor, easy
            if (nbNeigbors == 1) {
                this.neighbors[dimension][direction].get(0).merge(this);
            }
            // Else, do the same thing recursively (it's a little heavy with data transfer)
            else if (nbNeigbors > 1) {
                Zone zone = this.neighbors[dimension][direction].get(0).leave();
                zone.xMin = this.xMin;
                zone.yMin = this.yMin;
                zone.xMax = this.xMax;
                zone.yMax = this.yMax;
                zone.splitHistory = this.splitHistory;
                zone.neighbors = this.neighbors;
            }

        }

        for (ArrayList<Zone>[] firstN : this.neighbors) {
            for (ArrayList<Zone> secondN : firstN) {
                for (Zone zone : secondN) {
                    zone.removeNeighbor(this);
                }
            }
        }

        return this;
    }

    public boolean merge(Zone zone) {
        if (this.xMin > zone.xMin) {
            this.xMin = zone.xMin;
        } else if (this.xMin < zone.xMin) {
            this.xMax = zone.xMax;
        } else if (this.yMin > zone.yMin) {
            this.yMin = zone.yMin;
        } else if (this.yMin < zone.yMin) {
            this.yMax = zone.yMax;
        }

        this.splitHistory.remove(this.splitHistory.size() - 1);

        return true;
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
