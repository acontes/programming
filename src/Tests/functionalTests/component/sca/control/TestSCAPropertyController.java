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
package functionalTests.component.sca.control;

import static org.junit.Assert.assertEquals;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.extensions.sca.Utils;
import org.objectweb.proactive.extensions.sca.control.SCAPropertyController;

import functionalTests.component.sca.SCAComponentTest;
import functionalTests.component.sca.control.components.PropertyComponent;
import functionalTests.component.sca.control.components.SerializableObj;


public class TestSCAPropertyController extends SCAComponentTest {
    protected Component boot;
    protected GCMTypeFactory tf;
    protected GenericFactory gf;
    protected ComponentType t;
    protected Component component;

    @Before
    public void setUp() throws Exception {
        boot = Utils.getBootstrapComponent();
        tf = GCM.getGCMTypeFactory(boot);
        gf = GCM.getGenericFactory(boot);
        t = tf.createFcType(new InterfaceType[] {});
        component = gf.newFcInstance(t, "primitive", PropertyComponent.class.getName());
    }

    // -----------------------------------------------------------------------------------
    // Full test
    // -----------------------------------------------------------------------------------

    @org.junit.Test
    public void testContainsDeclaredPropertyName() throws Exception {
        GCM.getGCMLifeCycleController(component).startFc();
        SCAPropertyController scac = Utils.getSCAPropertyController(component);
        Assert.assertTrue(scac.containsDeclaredPropertyName("x1"));
        Assert.assertTrue(scac.containsDeclaredPropertyName("x2"));
        Assert.assertTrue(scac.containsDeclaredPropertyName("x3"));
        Assert.assertTrue(scac.containsDeclaredPropertyName("x4"));
        Assert.assertTrue(scac.containsDeclaredPropertyName("x5"));
        Assert.assertTrue(scac.containsDeclaredPropertyName("x6"));
        Assert.assertTrue(scac.containsDeclaredPropertyName("x7"));
        Assert.assertTrue(scac.containsDeclaredPropertyName("x8"));
        Assert.assertTrue(scac.containsDeclaredPropertyName("x9"));
        Assert.assertTrue(scac.containsDeclaredPropertyName("x10"));
        Assert.assertTrue(scac.containsDeclaredPropertyName("x11"));
    }

    @org.junit.Test
    public void testGetDeclaredPropertyNames() throws Exception {
        GCM.getGCMLifeCycleController(component).startFc();
        SCAPropertyController scac = Utils.getSCAPropertyController(component);
        String[] listProperties = scac.getDeclaredPropertyNames();
        Assert.assertEquals("x1", listProperties[0]);
        Assert.assertEquals("x2", listProperties[1]);
        Assert.assertEquals("x3", listProperties[2]);
        Assert.assertEquals("x4", listProperties[3]);
        Assert.assertEquals("x5", listProperties[4]);
        Assert.assertEquals("x6", listProperties[5]);
        Assert.assertEquals("x7", listProperties[6]);
        Assert.assertEquals("x8", listProperties[7]);
        Assert.assertEquals("x9", listProperties[8]);
        Assert.assertEquals("x10", listProperties[9]);
        Assert.assertEquals("x11", listProperties[10]);
    }

    @org.junit.Test
    public void testGetDeclaredPropertyType() throws Exception {
        GCM.getGCMLifeCycleController(component).startFc();
        SCAPropertyController scac = Utils.getSCAPropertyController(component);
        Assert.assertEquals(scac.getDeclaredPropertyType("x1"), Boolean.class);
        Assert.assertEquals(scac.getDeclaredPropertyType("x2"), Byte.class);
        Assert.assertEquals(scac.getDeclaredPropertyType("x3"), Character.class);
        Assert.assertEquals(scac.getDeclaredPropertyType("x4"), Short.class);
        Assert.assertEquals(scac.getDeclaredPropertyType("x5"), Integer.class);
        Assert.assertEquals(scac.getDeclaredPropertyType("x6"), Long.class);
        Assert.assertEquals(scac.getDeclaredPropertyType("x7"), Float.class);
        Assert.assertEquals(scac.getDeclaredPropertyType("x8"), Double.class);
        Assert.assertEquals(scac.getDeclaredPropertyType("x9"), String.class);
        Assert.assertEquals(scac.getDeclaredPropertyType("x10"), String[].class);
        Assert.assertEquals(scac.getDeclaredPropertyType("x11"), Object.class);
    }

    @org.junit.Test
    public void testContainsPropertyName() throws Exception {
        GCM.getGCMLifeCycleController(component).startFc();
        SCAPropertyController scac = Utils.getSCAPropertyController(component);
        scac.setValue("x1", true);
        Assert.assertTrue(scac.containsPropertyName("x1"));
        Assert.assertFalse(scac.containsPropertyName("x2"));
        Assert.assertFalse(scac.containsPropertyName("x3"));
    }

    @org.junit.Test
    public void testGetPropertyNames() throws Exception {
        GCM.getGCMLifeCycleController(component).startFc();
        SCAPropertyController scac = Utils.getSCAPropertyController(component);
        scac.setValue("x1", true);
        String[] listProperties = scac.getPropertyNames();
        Assert.assertEquals(1, listProperties.length);
        Assert.assertEquals(listProperties[0], "x1");
    }

    @org.junit.Test
    public void testGetType() throws Exception {
        GCM.getGCMLifeCycleController(component).startFc();
        SCAPropertyController scac = Utils.getSCAPropertyController(component);
        scac.setValue("x1", true);
        Assert.assertEquals(scac.getType("x1"), Boolean.class);
        Assert.assertNull(scac.getType("x2"));
        Assert.assertNull(scac.getType("x3"));
        Assert.assertNull(scac.getType("x4"));
        Assert.assertNull(scac.getType("x5"));
        Assert.assertNull(scac.getType("x6"));
        Assert.assertNull(scac.getType("x7"));
        Assert.assertNull(scac.getType("x8"));

        scac.setValue("x9", "x9");
        assertEquals(scac.getType("x9"), String.class);

        String[] list = new String[] { "1", "2", "3" };
        scac.setValue("x10", list);
        assertEquals(scac.getType("x10"), String[].class);

        SerializableObj obj1 = new SerializableObj();
        scac.setValue("x11", obj1);
        assertEquals(scac.getType("x11"), SerializableObj.class);
    }

    @Test
    //@snippet-start component_scauserguide_2
    public void testGetValueGetValue() throws Exception {
        GCM.getGCMLifeCycleController(component).startFc();
        SCAPropertyController scac = Utils.getSCAPropertyController(component);
        scac.setValue("x1", true);
        assertEquals(true, scac.getValue("x1"));
        scac.setValue("x2", (byte) 1);
        assertEquals((byte) 1, scac.getValue("x2"));
        scac.setValue("x3", (char) 1);
        assertEquals((char) 1, scac.getValue("x3"));

        scac.setValue("x4", (short) 1);
        assertEquals((short) 1, scac.getValue("x4"));
        scac.setValue("x5", 1);
        assertEquals(1, scac.getValue("x5"));
        scac.setValue("x6", (long) 1);
        assertEquals((long) 1, scac.getValue("x6"));

        scac.setValue("x7", (float) 1);
        assertEquals((float) 1, scac.getValue("x7"));
        scac.setValue("x8", (double) 1);
        assertEquals((double) 1, scac.getValue("x8"));
        scac.setValue("x9", "x9");
        assertEquals("x9", scac.getValue("x9"));

        String[] list = new String[] { "1", "2", "3" };
        scac.setValue("x10", list);
        Assert.assertArrayEquals("OKAY", list, (Object[]) scac.getValue("x10"));

        SerializableObj obj1 = new SerializableObj();
        scac.setValue("x11", obj1);
        SerializableObj obj2 = (SerializableObj) scac.getValue("x11");
        Assert.assertEquals(obj1.x, obj2.x);
    }
    //@snippet-end component_scauserguide_2
}
