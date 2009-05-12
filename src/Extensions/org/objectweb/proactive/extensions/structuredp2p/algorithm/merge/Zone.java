package org.objectweb.proactive.extensions.structuredp2p.algorithm.merge;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;


public class Zone {

    public int xMin = 0;
    public int xMax = 500;
    public int yMin = 0;
    public int yMax = 500;

    public ArrayList<Zone>[][] neighbors;
    public ArrayList<int[]> splitHistory;

    public Color color;

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
        int dimension = 0;
        int direction = 0;
        int directionInv = 1;

        // Get the next dimension to split onto
        if (zone.splitHistory.size() > 0) {
            dimension = (zone.splitHistory.get(zone.splitHistory.size() - 1)[0] + 1) % 2;
        }

        // New zone
        // Update x dimension
        this.xMax = zone.xMax;
        this.yMax = zone.yMax;
        this.xMin = zone.xMin;
        this.yMin = zone.yMin;

        if (dimension == 0) {
            int x = zone.xMin + ((zone.xMax - zone.xMin) / 2);
            zone.xMin = x;
            this.xMax = x;
        } else if (dimension == 1) {
            int y = zone.yMin + ((zone.yMax - zone.yMin) / 2);
            zone.yMin = y;
            this.yMax = y;
        } else {
            return false;
        }

        this.splitHistory = (ArrayList<int[]>) zone.splitHistory.clone();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                this.neighbors[i][j] = (ArrayList<Zone>) zone.neighbors[i][j].clone();
            }
        }

        this.splitHistory.add(new int[] { dimension, directionInv });
        this.neighbors[dimension][directionInv].clear();
        this.addNeighbor(zone, dimension, directionInv);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (Zone z : this.neighbors[i][j]) {
                    if (i != dimension && j != direction) {
                        z.addNeighbor(this, i, direction);// (direction + 1) % 2);
                    }
                }
            }
        }

        zone.splitHistory.add(new int[] { dimension, direction });
        zone.neighbors[dimension][direction].clear();
        zone.addNeighbor(this, dimension, direction);
        /*
         * System.out.println("Join method [dimension=" + dimension + "; direction=" + direction +
         * "; splitHistorySize=" + this.splitHistory.size() + "]"); System.out.println(this);
         * System.out.println(this.sysoutHistory());
         */

        System.out.println(this.neighborsToString());
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
                Zone zone = this.neighbors[dimension][direction].get(0);

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

    public Boolean addNeighbor(Zone zone, int dimension, int direction) {
        return this.neighbors[dimension][direction].add(zone);
    }

    public Boolean removeNeighbor(Zone zone) {
        for (ArrayList<Zone>[] firstN : this.neighbors) {
            for (ArrayList<Zone> neighbors : firstN) {
                return neighbors.remove(zone);
            }
        }
        /*
         * for (int i = 0; i < 2; i++) { for (int j = 0; j < 2; j++) { for (Zone neighbor :
         * this.neighbors[i][j]) { if (neighbor.equals(zone)) { return
         * this.neighbors[i][j].remove(neighbor); } } } }
         */

        return false;
    }

    public Color getRandomColor() {
        Random rand = new Random();

        int r = rand.nextInt(256);
        int v = rand.nextInt(256);
        int b = rand.nextInt(256);

        if (r + v + b < 477) {
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
        for (ArrayList<Zone>[] firstN : this.neighbors) {
            for (ArrayList<Zone> secondN : firstN) {
                nb += secondN.size();
            }
        }

        String value = "Zone: min=[x=" + this.xMin + "; y=" + this.yMin + "] max=[x=" + this.xMax + "; y=" +
            this.yMax + "] nbNeighbors=" + nb;
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
}
