/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.component.sca.control;

import java.util.List;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.examples.components.sca.securityintent.SecurityIntentHandler;
import org.objectweb.proactive.extensions.sca.Utils;
import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.SCAIntentController;

import functionalTests.component.sca.SCAComponentTest;
import functionalTests.component.sca.control.components.CClient;
import functionalTests.component.sca.control.components.CServer;
import functionalTests.component.sca.control.components.ExecuteItf;
import functionalTests.component.sca.control.components.IntentHandlerTest;
import functionalTests.component.sca.control.components.TestIntentItf;
import functionalTests.component.sca.control.components.TestIntentItf2;


public class TestSCAIntentController extends SCAComponentTest {
    Component componentA;
    Component componentB;

    public TestSCAIntentController() {
        super();
    }

    @Before
    public void initTest() throws Exception {
        //@snippet-start component_scauserguide_6
        Component boot = Utils.getBootstrapComponent();
        GCMTypeFactory type_factory = GCM.getGCMTypeFactory(boot);
        GenericFactory cf = GCM.getGenericFactory(boot);

        componentA = cf.newFcInstance(type_factory.createFcType(new InterfaceType[] {
                type_factory.createFcItfType(TestIntentItf.SERVER_ITF_NAME, TestIntentItf.class.getName(),
                        TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE),
                type_factory.createFcItfType(TestIntentItf.CLIENT_ITF_NAME, TestIntentItf.class.getName(),
                        TypeFactory.CLIENT, TypeFactory.MANDATORY, TypeFactory.SINGLE),
                type_factory.createFcItfType(TestIntentItf2.SERVER_ITF_NAME, TestIntentItf2.class.getName(),
                        TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE),
                type_factory.createFcItfType("run", ExecuteItf.class.getName(), TypeFactory.SERVER,
                        TypeFactory.MANDATORY, TypeFactory.SINGLE),
                type_factory.createFcItfType(TestIntentItf2.CLIENT_ITF_NAME, TestIntentItf2.class.getName(),
                        TypeFactory.CLIENT, TypeFactory.MANDATORY, TypeFactory.SINGLE) }),
                Constants.PRIMITIVE, new ContentDescription(CClient.class.getName(), new Object[] {}));

        componentB = cf.newFcInstance(type_factory.createFcType(new InterfaceType[] {
                type_factory.createFcItfType(TestIntentItf.SERVER_ITF_NAME, TestIntentItf.class.getName(),
                        TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE),
                type_factory.createFcItfType(TestIntentItf2.SERVER_ITF_NAME, TestIntentItf2.class.getName(),
                        TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE) }),
                Constants.PRIMITIVE, new ContentDescription(CServer.class.getName(), new Object[] {}));
        //@snippet-end component_scauserguide_6
    }

    @org.junit.Test
    public void testAddIntentHandler1() throws Exception {
        SCAIntentController scaic = org.objectweb.proactive.extensions.sca.Utils
                .getSCAIntentController(componentA);
        IntentHandler ih = new IntentHandlerTest();
        scaic.addIntentHandler(ih);
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "m"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf2.CLIENT_ITF_NAME));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME, "m"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf2.SERVER_ITF_NAME));
        Assert.assertTrue(scaic.hasIntentHandler());
    }

    @org.junit.Test
    public void testAddIntentHandler2() throws Exception {
        SCAIntentController scaic = org.objectweb.proactive.extensions.sca.Utils
                .getSCAIntentController(componentA);
        IntentHandler ih = new IntentHandlerTest();
        scaic.addIntentHandler(ih, TestIntentItf.CLIENT_ITF_NAME);
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "m"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "n"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf2.CLIENT_ITF_NAME));
        scaic.addIntentHandler(ih, TestIntentItf.SERVER_ITF_NAME);
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME, "m"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME, "n"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf2.SERVER_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler());
    }

    @org.junit.Test
    public void testAddIntentHandler3() throws Exception {
        SCAIntentController scaic = org.objectweb.proactive.extensions.sca.Utils
                .getSCAIntentController(componentA);
        IntentHandler ih = new IntentHandlerTest();
        scaic.addIntentHandler(ih, TestIntentItf.CLIENT_ITF_NAME, "m");
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "m"));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "n"));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf2.CLIENT_ITF_NAME));
        scaic.addIntentHandler(ih, TestIntentItf.SERVER_ITF_NAME, "m");
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME, "m"));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME, "n"));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf2.SERVER_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler());
    }

    @org.junit.Test(expected = NoSuchInterfaceException.class)
    public void testAddIntentHandlerNoSuchInterfaceException() throws Exception {
        SCAIntentController scaic = org.objectweb.proactive.extensions.sca.Utils
                .getSCAIntentController(componentA);
        IntentHandler ih = new IntentHandlerTest();
        scaic.addIntentHandler(ih, "whateverItf");
    }

    @org.junit.Test(expected = NoSuchMethodException.class)
    public void testAddIntentHandlerNoSuchMethodException() throws Exception {
        SCAIntentController scaic = org.objectweb.proactive.extensions.sca.Utils
                .getSCAIntentController(componentA);
        IntentHandler ih = new IntentHandlerTest();
        scaic.addIntentHandler(ih, TestIntentItf.CLIENT_ITF_NAME, "whateverMethod");
    }
    
    @org.junit.Test
    public void testlistIntentHandler() throws Exception {
        SCAIntentController scaic = org.objectweb.proactive.extensions.sca.Utils
                .getSCAIntentController(componentA);
        IntentHandler ih = new IntentHandlerTest();
        {
            scaic.addIntentHandler(ih, TestIntentItf.CLIENT_ITF_NAME, "m");
            List<IntentHandler> tmp1 = scaic.listIntentHandler();
            List<IntentHandler> tmp2 = scaic.listIntentHandler(TestIntentItf.CLIENT_ITF_NAME);
            List<IntentHandler> tmp3 = scaic.listIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "m");
            List<IntentHandler> tmp4 = scaic.listIntentHandler(TestIntentItf.SERVER_ITF_NAME);
            List<IntentHandler> tmp5 = scaic.listIntentHandler(TestIntentItf.SERVER_ITF_NAME, "m");
            Assert.assertEquals(0, tmp1.size());
            Assert.assertEquals(0, tmp2.size());
            Assert.assertEquals(1, tmp3.size());
            Assert.assertEquals(0, tmp4.size());
            Assert.assertEquals(0, tmp5.size());
        }
        {
            scaic.addIntentHandler(ih, TestIntentItf.CLIENT_ITF_NAME);
            List<IntentHandler> tmp1 = scaic.listIntentHandler();
            List<IntentHandler> tmp2 = scaic.listIntentHandler(TestIntentItf.CLIENT_ITF_NAME);
            List<IntentHandler> tmp3 = scaic.listIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "m");
            Assert.assertEquals(0, tmp1.size());
            Assert.assertEquals(1, tmp2.size());
            Assert.assertEquals(2, tmp3.size());
        }
        {
            scaic.addIntentHandler(ih);
            List<IntentHandler> tmp1 = scaic.listIntentHandler();
            List<IntentHandler> tmp2 = scaic.listIntentHandler(TestIntentItf.CLIENT_ITF_NAME);
            List<IntentHandler> tmp3 = scaic.listIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "m");
            List<IntentHandler> tmp4 = scaic.listIntentHandler(TestIntentItf.SERVER_ITF_NAME);
            List<IntentHandler> tmp5 = scaic.listIntentHandler(TestIntentItf.SERVER_ITF_NAME, "m");
            Assert.assertEquals(1, tmp1.size());
            Assert.assertEquals(2, tmp2.size());
            Assert.assertEquals(3, tmp3.size());
            Assert.assertEquals(1, tmp4.size());
            Assert.assertEquals(1, tmp5.size());
        }
    }

    @org.junit.Test
    public void testRemoveIntentHandler1() throws Exception {
        SCAIntentController scaic = org.objectweb.proactive.extensions.sca.Utils
                .getSCAIntentController(componentA);
        IntentHandler ih = new IntentHandlerTest();
        scaic.addIntentHandler(ih);
        scaic.addIntentHandler(ih);
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "m"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf2.CLIENT_ITF_NAME));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME, "m"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf2.SERVER_ITF_NAME));
        Assert.assertTrue(scaic.hasIntentHandler());
        scaic.removeIntentHandler(ih);
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "m"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf2.CLIENT_ITF_NAME));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME, "m"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf2.SERVER_ITF_NAME));
        Assert.assertTrue(scaic.hasIntentHandler());
        scaic.removeIntentHandler(ih);
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "m"));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf2.CLIENT_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME, "m"));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf2.SERVER_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler());
    }

    @org.junit.Test
    public void testRemoveIntentHandler2() throws Exception {
        SCAIntentController scaic = org.objectweb.proactive.extensions.sca.Utils
                .getSCAIntentController(componentA);
        IntentHandler ih = new IntentHandlerTest();
        scaic.addIntentHandler(ih, TestIntentItf.CLIENT_ITF_NAME);
        scaic.addIntentHandler(ih, TestIntentItf.CLIENT_ITF_NAME);
        scaic.addIntentHandler(ih, TestIntentItf.SERVER_ITF_NAME);
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "m"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "n"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf2.CLIENT_ITF_NAME));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME, "m"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME, "n"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf2.SERVER_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler());
        scaic.removeIntentHandler(ih, TestIntentItf.CLIENT_ITF_NAME);
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "m"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "n"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME, "m"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME, "n"));
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.SERVER_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf2.SERVER_ITF_NAME));
    }

    @org.junit.Test
    public void testRemoveIntentHandler3() throws Exception {
        SCAIntentController scaic = org.objectweb.proactive.extensions.sca.Utils
                .getSCAIntentController(componentA);
        IntentHandler ih = new IntentHandlerTest();
        scaic.addIntentHandler(ih, TestIntentItf.CLIENT_ITF_NAME, "m");
        scaic.addIntentHandler(ih, TestIntentItf.CLIENT_ITF_NAME, "m");
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "m"));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "n"));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf2.CLIENT_ITF_NAME));
        Assert.assertFalse(scaic.hasIntentHandler());
        scaic.removeIntentHandler(ih, TestIntentItf.CLIENT_ITF_NAME, "m");
        Assert.assertTrue(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "m"));
        scaic.removeIntentHandler(ih, TestIntentItf.CLIENT_ITF_NAME, "m");
        Assert.assertFalse(scaic.hasIntentHandler(TestIntentItf.CLIENT_ITF_NAME, "m"));
    }

    @org.junit.Test
    public void action() throws Exception {
        //@snippet-start component_scauserguide_5   

        SCAIntentController scaic = org.objectweb.proactive.extensions.sca.Utils
                .getSCAIntentController(componentA);
        IntentHandler y = new IntentHandlerTest();
        scaic.addIntentHandler(y);
        scaic.addIntentHandler(y, TestIntentItf2.CLIENT_ITF_NAME);
        scaic.addIntentHandler(new SecurityIntentHandler("pass"), TestIntentItf.CLIENT_ITF_NAME, "m");
        //@snippet-end component_scauserguide_5
        GCM.getBindingController(componentA).bindFc(TestIntentItf.CLIENT_ITF_NAME,
                componentB.getFcInterface(TestIntentItf.SERVER_ITF_NAME));
        GCM.getBindingController(componentA).bindFc(TestIntentItf2.CLIENT_ITF_NAME,
                componentB.getFcInterface(TestIntentItf2.SERVER_ITF_NAME));
        GCM.getGCMLifeCycleController(componentA).startFc();
        GCM.getGCMLifeCycleController(componentB).startFc();
        TestIntentItf i = (TestIntentItf) componentA.getFcInterface(TestIntentItf.SERVER_ITF_NAME);
        TestIntentItf2 i2 = (TestIntentItf2) componentA.getFcInterface(TestIntentItf2.SERVER_ITF_NAME);
        ExecuteItf i3 = ((ExecuteItf) componentA.getFcInterface("run"));
        try {
            i.m();
            int x = i2.n2();
            i3.execute();
        } catch (Exception e) {
            //fail();
            e.printStackTrace();
        }
        GCM.getGCMLifeCycleController(componentA).stopFc();
        GCM.getGCMLifeCycleController(componentB).stopFc();
        GCM.getBindingController(componentA).unbindFc(TestIntentItf.CLIENT_ITF_NAME);
        GCM.getBindingController(componentA).unbindFc(TestIntentItf2.CLIENT_ITF_NAME);

    }
}
