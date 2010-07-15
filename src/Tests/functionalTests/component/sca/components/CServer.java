/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
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
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.component.sca.components;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.fractal.api.control.BindingController;

import functionalTests.component.conform.components.I;
import functionalTests.component.conform.components.J;


public class CServer implements I, J {

    protected I i;
    protected Map<String, Object> j = new HashMap<String, Object>();

    // FUNCTIONAL INTERFACE
    public void m(boolean v) {
        //i.m(v);
    }

    public void m(byte v) {
    }

    public void m(char v) {
    }

    public void m(short v) {
    }

    public void m(int v) {
        System.err.println("inside public void m(int v) time is " + System.currentTimeMillis());
    }

    public void m(long v) {
    }

    public void m(float v) {
    }

    public void m(double v) {
    }

    public void m(String v) {
    }

    public void m(String[] v) {
    }

    public boolean n(boolean v, String[] w) {
        return v | true; // for write only attribute tests
    }

    public byte n(byte v, String w) {
        return v;
    }

    public char n(char v, double w) {
        return v;
    }

    public short n(short v, float w) {
        return v;
    }

    public int n(int v, long w) {
        if (i != null) {
            // for interceptors tests
            return (w == 0) ? v : i.n(v + 1, w - 1);
        } else if (j.size() > 0) {
            // for interceptors tests
            return (w == 0) ? v : ((I) j.values().iterator().next()).n(v + 1, w - 1);
        } else {
            return v;
        }
    }

    public long n(long v, int w) {
        return v;
    }

    public float n(float v, short w) {
        return v;
    }

    public double n(double v, char w) {
        return v;
    }

    public String n(String v, byte w) {
        return v;
    }

    public String[] n(String[] v, boolean w) {
        return v;
    }
}
