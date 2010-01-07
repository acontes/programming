/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of
 * 						   Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org
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
 * If needed, contact us to obtain a release under GPL Version 2.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.component.conform;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.util.Fractal;

import functionalTests.component.conform.components.C;


public class TestContentControllerTemplate extends TestContentController {
    @Override
    protected void setUpComponents() throws Exception {
        c = gf.newFcInstance(t, compositeTemplate, null);
        d = gf.newFcInstance(t, compositeTemplate, null);
        e = gf.newFcInstance(t, primitiveTemplate, C.class.getName());
    }

    @Test
    @Ignore
    public void testInstanceContent() throws Exception {
        Component r = gf.newFcInstance(t, compositeTemplate, null);
        Fractal.getContentController(r).addFcSubComponent(c);
        Fractal.getContentController(r).addFcSubComponent(d);
        Fractal.getContentController(c).addFcSubComponent(e);
        Fractal.getContentController(d).addFcSubComponent(e);

        Component root = Fractal.getFactory(r).newFcInstance();
        Component[] comps = Fractal.getContentController(root).getFcSubComponents();
        assertEquals(2, comps.length);
        Component[] cComps = Fractal.getContentController(comps[0]).getFcSubComponents();
        Component[] dComps = Fractal.getContentController(comps[1]).getFcSubComponents();
        assertEquals(1, cComps.length);
        assertEquals(1, dComps.length);
        assertEquals(cComps[0], dComps[0]);
    }
}
