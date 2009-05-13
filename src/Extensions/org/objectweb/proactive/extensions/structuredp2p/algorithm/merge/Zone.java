package org.objectweb.proactive.extensions.structuredp2p.algorithm.merge;

import java.awt.Color;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;


public class Zone {

    public int xMin = 0;
    public int xMax = 500;
    public int yMin = 0;
    public int yMax = 500;

    public ArrayList<Zone>[][] neighbors;
    public ArrayList<int[]> splitHistory;

    public Color color;

    public Random rand = new Random();

    public Zone() {
        this.neighbors = new ArrayList[2][2];

        for (int i = 0; i < 2; i++) {
            this.neighbors[i][0] = new ArrayList<Zone>();
            this.neighbors[i][1] = new ArrayList<Zone>();
        }

        this.splitHistory = new ArrayList<int[]>();
        this.color = this.getRandomColor();
    }

    public boolean join(Zone zone) {
        int dimension = this.rand.nextInt(2);
        int direction = this.rand.nextInt(2);
        int directionInv = (direction + 1) % 2;

        // Get the next dimension to split onto
        if (zone.splitHistory.size() > 0) {
            dimension = (zone.splitHistory.get(zone.splitHistory.size() - 1)[0] + 1) % 2;
        }

        // New zone
        // Update x coordinates
        this.xMax = zone.xMax;
        this.yMax = zone.yMax;
        this.xMin = zone.xMin;
        this.yMin = zone.yMin;

        if (dimension == 0) {
            int x = zone.xMin + ((zone.xMax - zone.xMin) / 2);
            if (direction == 0) {
                zone.xMin = x;
                this.xMax = x;
            } else {
                zone.xMax = x;
                this.xMin = x;
            }
        } else if (dimension == 1) {
            int y = zone.yMin + ((zone.yMax - zone.yMin) / 2);
            if (direction == 0) {
                zone.yMax = y;
                this.yMin = y;
            } else {
                zone.yMin = y;
                this.yMax = y;
            }
        }

        // History
        /*
         * this.splitHistory = (ArrayList<int[]>) zone.splitHistory.clone();
         * this.splitHistory.add(new int[] { dimension, directionInv }); zone.splitHistory.add(new
         * int[] { dimension, direction });
         */

        // Neighbors
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                this.neighbors[i][j] = new ArrayList<Zone>();
                this.neighbors[i][j].addAll(zone.neighbors[i][j]);
                System.out.println(this.neighbors[i][j].size() + " neighbors at [" + i + "][" + j + "]");
            }
        }

        this.addNeighbor(zone, dimension, directionInv);
        zone.addNeighbor(this, dimension, direction);

        this.checkNeighbors();
        zone.checkNeighbors();

        return true;
    }

    public void update(Zone zone, int dimension, int direction) {
        for (Zone z : this.neighbors[dimension][direction]) {
            z.removeNeighbor(this, dimension, (direction + 1) % 2);
        }

        this.neighbors[dimension][direction].clear();
        this.addNeighbor(zone, dimension, direction);
    }

    public void checkNeighbors() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int jInv = (j + 1) % 2;

                int size = this.neighbors[i][j].size();
                ArrayList<Zone> zonesToRemove = new ArrayList<Zone>();
                for (int k = 0; k < size; k++) {
                    Zone zone = this.neighbors[i][j].get(k);

                    int d = this.getBorderDimension(zone);
                    if (d == i) {
                        zone.addNeighbor(this, i, jInv);
                    } else {
                        zonesToRemove.add(zone);
                    }
                }

                for (Zone z : zonesToRemove) {
                    z.removeNeighbor(this);
                    this.removeNeighbor(z);
                }
            }
        }
    }

    public Zone leave() {
        if (this.splitHistory.size() > 0) {
            int[] lastOP = this.splitHistory.get(this.splitHistory.size() - 1);
            this.splitHistory.remove(this.splitHistory.size() - 1);
            int dimension = lastOP[0];
            int direction = lastOP[1];
            // int direction = (lastOP[1] + 1) % 2;

            int nbNeigbors = this.neighbors[dimension][direction].size();

            // If there is just one neighbor, easy
            if (nbNeigbors == 1) {
                Zone zone = this.neighbors[dimension][direction].get(0);

                if (dimension == 0) {
                    zone.xMin = Math.min(zone.xMin, this.xMin);
                    zone.xMax = Math.max(zone.xMax, this.xMax);
                } else if (dimension == 1) {
                    zone.yMin = Math.min(zone.yMin, this.yMin);
                    zone.yMax = Math.max(zone.yMax, this.yMax);
                }
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
            } else {
                System.out.println("No more peers " + this);
            }

        }

        for (ArrayList<Zone>[] firstN : this.neighbors) {
            for (ArrayList<Zone> secondN : firstN) {
                for (Zone zone : secondN) {
                    zone.removeNeighbor(this);
                }
            }
        }

        this.xMax = -1;
        this.yMax = -1;
        this.xMin = -1;
        this.yMin = -1;

        return this;
    }

    public boolean contains(int x, int y) {
        if (x >= this.xMin && x < this.xMax && y >= this.yMin && y < this.yMax) {
            return true;
        }

        return false;
    }

    public boolean addNeighbor(Zone zone, int dimension, int direction) {
        if (this.equals(zone)) {
            throw new ConcurrentModificationException("Can't add itself as a neighbor");
        }

        return (!zone.hasNeighbor(this, dimension, direction) && this.neighbors[dimension][direction]
                .add(zone));
    }

    public boolean removeNeighbor(Zone zone) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (this.neighbors[i][j].contains(zone)) {
                    boolean ret = this.neighbors[i][j].remove(zone);
                    // System.out.println(this + " removes at [" + i + "][" + j + "] " + zone +
                    // " (exists? " +
                    // ret + ")");
                    return ret;
                }
            }
        }

        return false;
    }

    public boolean removeNeighbor(Zone zone, int dimension, int direction) {
        boolean ret = this.neighbors[dimension][direction].remove(zone);
        // System.out.println(this + " removes at [" + dimension + "][" + direction + "] " + zone +
        // " (exists? " + ret + ")");
        return ret;
    }

    public Color getRandomColor() {
        int r = this.rand.nextInt(256);
        int v = this.rand.nextInt(256);
        int b = this.rand.nextInt(256);

        if (r + v + b < 420) {
            return this.getRandomColor();
        }

        return new Color(r, v, b);
    }

    public static void main(String argv[]) {
        Zone zone1 = new Zone();
        Zone zone2 = new Zone();
        Zone zone3 = new Zone();

        zone2.join(zone1);
        zone3.join(zone2);
    }

    public String toString() {
        int nb = 0;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                nb += this.neighbors[i][j].size();
            }
        }

        String value = "Zone: min=[x=" + this.xMin + "; y=" + this.yMin + "] max=[x=" + this.xMax + "; y=" +
            this.yMax + "]";// nbNeighbors=" + nb;
        // value += "\n" + this.historyToString();
        return value;
    }

    public String historyToString() {
        String value = "SplitHistory [\n";
        for (int[] tab : this.splitHistory) {
            value += "\t[dim=" + tab[0] + "; dir=" + tab[1] + "]\n";
        }
        value += "]";

        return value;
    }

    public String neighborsToString() {
        String value = "Neighbors [\n";

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (Zone neighbor : this.neighbors[i][j]) {
                    value += "\t" + neighbor + "\n";
                }
            }
        }
        value += "]";

        return value;
    }

    public int getBorderDimension(Zone zone) {
        if (this.isBordered(zone, 0)) {
            return 0;
        } else if (this.isBordered(zone, 1)) {
            return 1;
        }

        return -1;
    }

    public boolean isBordered(Zone zone, int dimension) {
        boolean ret = false;
        if (dimension == 0) {
            ret = ((this.xMin == zone.xMax || this.xMax == zone.xMin) && (((this.yMin <= zone.yMin && zone.yMin < this.yMax) || (this.yMin < zone.yMax && zone.yMax <= this.yMax)) || ((zone.yMin <= this.yMin && this.yMin < zone.yMax) || (zone.yMin < this.yMax && this.yMax <= zone.yMax))));
        } else if (dimension == 1) {
            ret = ((this.yMin == zone.yMax || this.yMax == zone.yMin) && (((this.xMin <= zone.xMin && zone.xMin < this.xMax) || (this.xMin < zone.xMax && zone.xMax <= this.xMax)) || ((zone.xMin <= this.xMin && this.xMin < zone.xMax) || (zone.xMin < this.xMax && this.xMax <= zone.xMax))));
        } else {
            throw new IllegalArgumentException("The dimension must be 0 or 1.");
        }

        return ret;
    }

    public boolean hasNeighbor(Zone zone, int dimension, int direction) {
        return this.neighbors[dimension][direction].contains(zone);
    }

    public boolean equals(Object o) {
        Zone zone = (Zone) o;

        return (this.xMin == zone.xMin && this.xMax == zone.xMax && this.yMin == zone.yMin && this.yMax == zone.yMax);
    }
}
