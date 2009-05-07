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
package org.objectweb.proactive.examples.webservices.c3dWS.geom;

/**
 * A class for making rays (lines in 3D), which have a start point, and a direction.
 */
final public class Ray implements java.io.Serializable {
    public Vec P;
    public Vec D;

    public Ray(Vec pnt, Vec dir) {
        P = new Vec(pnt.getX(), pnt.getY(), pnt.getZ());
        D = new Vec(dir.getX(), dir.getY(), dir.getZ());
        D.normalize();
    }

    /** This is very dangerous to use, as a 0,0 line is not a line ! */
    public Ray() {
        P = new Vec();
        D = new Vec();
    }

    /**
     * Works out the point which lies on this line, at distance t from origine.
     * @returns V = P + D * t
     */
    public Vec point(double t) {
        return new Vec(P.getX() + (D.getX() * t), P.getY() + (D.getY() * t), P.getZ() + (D.getZ() * t));
    }

    @Override
    public String toString() {
        return "{ Po = " + P.toString() + " dir= " + D.toString() + "}";
    }
}
