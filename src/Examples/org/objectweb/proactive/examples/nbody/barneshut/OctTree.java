/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.examples.nbody.barneshut;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.examples.nbody.common.Cube;
import org.objectweb.proactive.examples.nbody.common.Point3D;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;


@ActiveObject
public class OctTree implements Serializable {

    /** max body in a cube while universe's division */
    public static final int MAX_BODIES_IN_DOMAIN = 1;

    /** value for the approximation */
    public static final double THETA = 1.0;

    /** mass of node */
    private double mass;

    /** position of mass center of node */
    private double massCenterx;
    private double massCentery;
    private double massCenterz;

    /** list of sons of this node */
    private List<OctTree> sons;

    /** if this node have a child (not a leaf) */
    private boolean hasChild;

    /** the cube representing this node */
    private Cube cube;

    /** the radius of the cube */
    private double radius;

    /** list of all planets */
    private List<Planet> listPlanets;

    //////////          C  O  N  S  T  R  U  C  T  O  R  S          //////////

    /**
     * Required by ProActive
     */
    public OctTree() {
    }

    /**
     * Fill the entire OctTree with good mass and center of mass
     * At beginning only leafs have a mass and mass center initialized (in createOctTree)
     * At the end of function, all tree's nodes have their attributes initialized
     * with the good value
     * @result List for going up the values of the leafs, first argument in the list
     * is mass, then mass center x, mass center y, mass center z.
     */
    private List<Double> computeCenterOfMass() {
        if (hasChild) {
            @SuppressWarnings("unchecked")
            List<Double>[] lmass = new ArrayList[8];

            // Calculate recursively the values of his sons
            for (int i = 0; i < 8; i++) {
                if (sons.get(i) != null) {
                    lmass[i] = sons.get(i).computeCenterOfMass();
                }
            }

            double masse = 0;
            double massCenterCx = 0;
            double massCenterCy = 0;
            double massCenterCz = 0;
            double ma;
            for (int i = 0; i < 8; i++) {
                if (lmass[i] != null) {
                    ma = ((Double) lmass[i].get(0)).doubleValue();
                    masse += ma;
                    massCenterCx += ma * ((Double) lmass[i].get(1)).doubleValue();
                    massCenterCy += ma * ((Double) lmass[i].get(2)).doubleValue();
                    massCenterCz += ma * ((Double) lmass[i].get(3)).doubleValue();
                }
            }
            mass = masse;
            massCenterx = massCenterCx / masse;
            massCentery = massCenterCy / masse;
            massCenterz = massCenterCz / masse;
        }

        // If it's a leaf, we return the values
        // Also if the node have finished its calculs
        List<Double> ltmp = new ArrayList<Double>(4);
        ltmp.add(new Double(mass));
        ltmp.add(new Double(massCenterx));
        ltmp.add(new Double(massCentery));
        ltmp.add(new Double(massCenterz));

        return ltmp;
    }

    /**
     * Calculates the force exerted to a Planet
     * @param pl the Planet which we want to calculate the force on
     * @result the force exerted
     */
    public Force computeForce(Planet pl) {
        Force f = new Force(0.0, 0.0, 0.0);

        double x = massCenterx;
        double y = massCentery;
        double z = massCenterz;

        // calculate the distance between the Planet and the mass center of this node
        double r = distance(pl, x, y, z);

        // If the distance is too small, we increase it a little
        if (r < pl.diameter + 10) {
            r = pl.diameter + 10;
        }

        // If it's a leaf or if we approximate (second comparison)
        if (!hasChild || radius / r < THETA) {
            // We removed pl.mass because, it is removed also in the calcul of
            // movement in the Planet's class (fonction moveWithForce)
            double coeff = 9.81 * mass / (r * r);

            Force fo = new Force(coeff * (x - pl.x), coeff * (y - pl.y), coeff * (z - pl.z));
            return fo;
        } else { // Then we compute the force on all of sons
            for (int i = 0; i < 8; i++) {
                if (sons.get(i) != null) {
                    f.add(((OctTree) sons.get(i)).computeForce(pl));
                }
            }

            return f;
        }
    }

    /**
     * Compute the distance between a planet and a Point
     * @param a a Planet
     * @param ax x-coordinate
     * @param ay y-coordinate
     * @param az z-coordinate
     * @result The distance between the Planet and the Point
     */
    public static double distance(Planet a, double ax, double ay, double az) {
        double x = ax - a.x;
        double y = ay - a.y;
        double z = az - a.z;
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Return the number of node of the OctTree
     * No more used...
     */
    public int size() {
        if (!hasChild)
            return 1;
        else {
            int cmp = 0;
            for (int i = 0; i < 8; i++) {
                if (sons.get(i) != null) {
                    cmp += ((OctTree) sons.get(i)).size();
                }
            }
            return cmp + 1;
        }
    }

    //////////          A C C E S S O R S  & &  M O D I F I C A T O R S          //////////
    public double getMass() {
        return mass;
    }

    public void setMass(Double m) {
        mass = m.doubleValue();
    }

    public double getMassCenterx() {
        return massCenterx;
    }

    public double getMassCentery() {
        return massCentery;
    }

    public double getMassCenterz() {
        return massCenterz;
    }

    public void setMassCenterx(double newMassCenterx) {
        massCenterx = newMassCenterx;
    }

    public void setMassCentery(double newMassCentery) {
        massCentery = newMassCentery;
    }

    public void setMassCenterz(double newMassCenterz) {
        massCenterz = newMassCenterz;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double rad) {
        radius = rad;
    }

    public List getListPlanets() {
        return listPlanets;
    }

    public boolean getHasChild() {
        return hasChild;
    }

    public void setHasChild(boolean child) {
        hasChild = child;
    }

    /**
     * For displaying a OctTree (debug)
     */
    @Override
    public String toString() {
        String ch = "Masse : " + mass + "\n";
        ch += "Cube x : " + cube.x + " - Cube y : " + cube.y + " - Cube z : " + cube.z + " - Width : " +
            cube.width + "\n";
        if (hasChild) {
            ch += "Descente dans les fils\n";
            ch += "---------------------\n";
            for (int i = 0; i < 8; i++) {
                if (sons.get(i) != null) {
                    ch += "fils no " + i + "\n";
                    ch += ((OctTree) sons.get(i)).toString();
                }
            }
        }
        ch += "---------------------\n";
        return ch;
    }
}
