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


public class CClient implements BindingController, TestIntentItf, J {

    protected TestIntentItf i;
    protected Map<String, Object> j = new HashMap<String, Object>();

    // BINDING CONTROLLER
    public String[] listFc() {
        String[] result = new String[j.size() + 1];
        j.keySet().toArray(result);
        result[j.size()] = "client";
        return result;
    }

    public Object lookupFc(String s) {
        if (s.equals("client")) {
            return i;
        } else if (s.startsWith("clients")) {
            return j.get(s);
        }
        return null;
    }

    public void bindFc(String s, Object o) {
        if (s.equals("client")) {
            i = (TestIntentItf) o;
        } else if (s.startsWith("clients")) {
            j.put(s, o);
        }
    }

    public void unbindFc(String s) {
        if (s.equals("client")) {
            i = null;
        } else if (s.startsWith("clients")) {
            j.remove(s);
        }
    }

    // FUNCTIONAL INTERFACE
    
    @Override
	public void m() throws Exception {
    	i.m();
	}

	@Override
	public int n() {
		return i.n();
	}
    
    public void m(boolean v) {
        i.m(v);
    }

    public void m(byte v) {
        i.m(v);
    }

    public void m(char v) {
        i.m(v);
    }

    public void m(short v) {
        i.m(v);
    }

    public void m(int v) {
        i.m(v);
    }

    public void m(long v) {
        i.m(v);
    }

    public void m(float v) {
        i.m(v);
    }

    public void m(double v) {
        i.m(v);
    }

    public void m(String v) {
        i.m(v);
    }

    public void m(String[] v) {
        i.m(v);
    }

    public boolean n(boolean v, String[] w) {
        return i.n(v, w);
        //return v | x11; // for write only attribute tests
    }

    public byte n(byte v, String w) {
        return i.n(v, w);
    }

    public char n(char v, double w) {
        return i.n(v, w);
    }

    public short n(short v, float w) {
        return i.n(v, w);
    }

    public int n(int v, long w) {
        return i.n(v, w);
    }

    public long n(long v, int w) {
        return i.n(v, w);
    }

    public float n(float v, short w) {
        return i.n(v, w);
    }

    public double n(double v, char w) {
        return i.n(v, w);
    }

    public String n(String v, byte w) {
        return i.n(v, w);
    }

    public String[] n(String[] v, boolean w) {
        return i.n(v, w);
    }

}
