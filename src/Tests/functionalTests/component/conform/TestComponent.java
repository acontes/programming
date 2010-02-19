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
package functionalTests.component.conform;

import static org.junit.Assert.assertEquals;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.Constants;

import functionalTests.component.conform.components.C;
import functionalTests.component.conform.components.CAttributes;
import functionalTests.component.conform.components.I;


public class TestComponent extends Conformtest {
    protected Component boot;
    protected GCMTypeFactory tf;
    protected GenericFactory gf;
    protected ComponentType t;
    protected final static String AC = "attribute-controller/" + PKG + ".CAttributes/false,false,false";
    protected final static String sI = "server/" + PKG + ".I/false,false,false";
    protected final static String cI = "client/" + PKG + ".I/true,false,false";

    @Before
    public void setUp() throws Exception {
        boot = GCM.getBootstrapComponent();
        tf = GCM.getGCMTypeFactory(boot);
        gf = GCM.getGenericFactory(boot);
        t = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType(Constants.ATTRIBUTE_CONTROLLER, CAttributes.class.getName(), false, false,
                        false), tf.createFcItfType("server", I.class.getName(), false, false, false),
                tf.createFcItfType("client", I.class.getName(), true, true, false) });
    }

    // -------------------------------------------------------------------------
    // Test functional and attribute controller interfaces
    // -------------------------------------------------------------------------
    @Test
    public void testParametricPrimitive() throws Exception {
        Component c = gf.newFcInstance(t, parametricPrimitive, C.class.getName());
        GCM.getGCMLifeCycleController(c).startFc();
        I i = (I) c.getFcInterface("server");
        checkInterface(i);

        CAttributes ca = (CAttributes) GCM.getAttributeController(c);
        ca.setX1(true);
        assertEquals(true, ca.getX1());
        ca.setX2((byte) 1);
        assertEquals((byte) 1, ca.getX2());
        ca.setX3((char) 1);
        assertEquals((char) 1, ca.getX3());
        ca.setX4((short) 1);
        assertEquals((short) 1, ca.getX4());
        ca.setX5(1);
        assertEquals(1, ca.getX5());
        ca.setX6(1);
        assertEquals((long) 1, ca.getX6());
        ca.setX7(1);
        assertEquals(1, ca.getX7(), 0);
        ca.setX8(1);
        assertEquals(1, ca.getX8(), 0);
        ca.setX9("1");
        assertEquals("1", ca.getX9());
        ca.setWriteOnlyX11(true);
        assertEquals(true, i.n(false, null));
    }

    @Test(expected = InstantiationException.class)
    public void testParametricPrimitiveWithBadContentClass() throws Exception {
        Component c = gf.newFcInstance(t, parametricPrimitive, TestComponent.class.getName());
    }

    @Test
    @Ignore
    public void testParametricPrimitiveTemplate() throws Exception {
        Component c = gf.newFcInstance(t, parametricPrimitiveTemplate, C.class.getName());

        CAttributes ca = (CAttributes) GCM.getAttributeController(c);
        ca.setX1(true);
        ca.setX2((byte) 1);
        ca.setX3((char) 1);
        ca.setX4((short) 1);
        ca.setX5(1);
        ca.setX6(1);
        ca.setX7(1);
        ca.setX8(1);
        ca.setX9("1");
        ca.setWriteOnlyX11(true);

        c = GCM.getFactory(c).newFcInstance();
        GCM.getGCMLifeCycleController(c).startFc();
        ca = (CAttributes) GCM.getAttributeController(c);

        assertEquals(true, ca.getX1());
        assertEquals((byte) 1, ca.getX2());
        assertEquals((char) 1, ca.getX3());
        assertEquals((short) 1, ca.getX4());
        assertEquals(1, ca.getX5());
        assertEquals((long) 1, ca.getX6());
        assertEquals(1, ca.getX7(), 0);
        assertEquals(1, ca.getX8(), 0);
        assertEquals("1", ca.getX9());
        assertEquals(true, ((I) c.getFcInterface("server")).n(false, null));
    }
}
