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
        int directionInv = (direction + 1) % 2;

        // Get the next dimension to split onto
        if (this.splitHistory.size() > 0) {
            System.out.println(this.splitHistory.get(this.splitHistory.size() - 1)[0]);
            dimension = (this.splitHistory.get(this.splitHistory.size() - 1)[0] + 1) % 2;
        }

        // New zone
        // Update x dimension
        if (dimension == 0) {
            zone.xMin = (this.xMin + this.xMax) / 2;
            this.xMax = (this.xMin + this.xMax) / 2;
        } else if (dimension == 1) {
            zone.yMin = (this.yMin + this.yMax) / 2;
            this.yMax = (this.yMin + this.yMax) / 2;
        } else {
            return false;
        }

        zone.splitHistory = (ArrayList<int[]>) this.splitHistory.clone();
        zone.splitHistory.add(new int[] { dimension, directionInv });
        zone.neighbors = this.neighbors;
        zone.neighbors[dimension][directionInv].clear();
        zone.neighbors[dimension][directionInv].add(this);

        this.splitHistory.add(new int[] { dimension, direction });
        this.neighbors[dimension][direction].clear();
        this.neighbors[dimension][direction].add(zone);

        System.out.println("Join method [dimension=" + dimension + "; direction=" + direction +
            "; splitHistorySize=" + this.splitHistory.size() + "]");
        System.out.println(this);
        this.sysoutHistory();

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
                        return this.neighbors[i][j].remove(neighbor);
                    }
                }
            }
        }

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
        String value = "Zone: min=[x=" + this.xMin + "; y=" + this.yMin + "] max=[x=" + this.xMax + "; y=" +
            this.yMax + "]";
        return value;
    }

    public void sysoutHistory() {
        System.out.println("SplitHistory [");
        for (int[] tab : this.splitHistory) {
            System.out.println("\t[dim=" + tab[0] + "; dir=" + tab[1] + "]");
        }
        System.out.println("]");
    }
}
