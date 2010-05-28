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
package functionalTests.component.sca;

import static org.junit.Assert.assertEquals;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.extensions.component.sca.SCAConfig;
import org.objectweb.proactive.extensions.component.sca.Utils;
import org.objectweb.proactive.extensions.component.sca.control.SCAPropertyController;

import functionalTests.ComponentTest;
import functionalTests.component.sca.components.C;
import functionalTests.component.sca.components.CAttributes;


public class TestSCAPropertyController extends ComponentTest {
    protected Component boot;
    protected GCMTypeFactory tf;
    protected GenericFactory gf;
    protected ComponentType t;

    @Before
    public void setUp() throws Exception {
        //-Dsca.provider=org.objectweb.proactive.core.component.sca.SCAFractive
        SCAConfig.SCA_PROVIDER.setValue("org.objectweb.proactive.extensions.component.sca.SCAFractive");
        boot = Utils.getBootstrapComponent();
        tf = GCM.getGCMTypeFactory(boot);
        gf = GCM.getGenericFactory(boot);
        t = tf.createFcType(new InterfaceType[] { tf.createFcItfType(Constants.ATTRIBUTE_CONTROLLER,
                CAttributes.class.getName(), false, false, false) });
    }

    // -----------------------------------------------------------------------------------
    // Full test
    // -----------------------------------------------------------------------------------
    @Test
    public void testSCAPropertyController() throws Exception {
        Component c = gf.newFcInstance(t, "primitive", C.class.getName());
        GCM.getGCMLifeCycleController(c).startFc();
        SCAPropertyController scac = Utils.getSCAPropertyController(c);
        scac.init();
        scac.setValue("x1", true);
        assertEquals(new Boolean(true), scac.getValue("x1"));
        scac.setValue("x2", (byte) 1);
        assertEquals((byte) 1, scac.getValue("x2"));
        scac.setValue("x3", (char) 1);
        assertEquals((char) 1, scac.getValue("x3"));
        scac.setValue("x4", (short) 1);
        assertEquals((short) 1, scac.getValue("x4"));
        scac.setValue("x5", 1);
        assertEquals(1, scac.getValue("x5"));
        scac.setValue("x6", 1);
        assertEquals((long) 1, scac.getValue("x6"));
        scac.setValue("x7", 1);
        assertEquals((float) 1, scac.getValue("x7"));
        scac.setValue("x8", 1);
        assertEquals((double) 1, scac.getValue("x8"));
        scac.setValue("x9", "1");
        assertEquals("1", scac.getValue("x9"));
    }
}
