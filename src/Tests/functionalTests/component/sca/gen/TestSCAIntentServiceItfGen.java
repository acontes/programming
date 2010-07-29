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
package functionalTests.component.sca.gen;

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
import org.objectweb.proactive.extensions.component.sca.control.SCAIntentController;
import org.objectweb.proactive.extensions.component.sca.gen.IntentServiceItfGenerator;

import functionalTests.component.conform.Conformtest;
import functionalTests.component.conform.components.CAttributes;
import functionalTests.component.conform.components.I;
import functionalTests.component.sca.components.SecurityIntentHandler;
import functionalTests.component.sca.components.TestIntentComponent;
import functionalTests.component.sca.components.TestIntentItf;


public class TestSCAIntentServiceItfGen extends Conformtest {
    protected Component boot;
    protected GCMTypeFactory tf;
    protected GenericFactory gf;
    protected ComponentType t;
    protected final static String AC = "attribute-controller/" + PKG + ".CAttributes/false,false,false";
    protected final static String sI = "server/" + PKG + ".I/false,false,false";
    protected final static String cI = "client/" + PKG + ".I/true,false,false";

    @Before
    public void setUp() throws Exception {
        SCAConfig.SCA_PROVIDER.setValue("org.objectweb.proactive.extensions.component.sca.SCAFractive");
        boot = Utils.getBootstrapComponent();
        tf = GCM.getGCMTypeFactory(boot);
        gf = GCM.getGenericFactory(boot);
        t = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType(Constants.ATTRIBUTE_CONTROLLER, CAttributes.class.getName(), false, false,
                        false),
                tf.createFcItfType("server", TestIntentItf.class.getName(), false, false, false),
                tf.createFcItfType("client", I.class.getName(), true, true, false) });
    }

    // -------------------------------------------------------------------------
    // Test functional and attribute controller interfaces
    // -------------------------------------------------------------------------
    @Test
    public void testParametricPrimitive() throws Exception {
        Component c = gf.newFcInstance(t, parametricPrimitive, TestIntentComponent.class.getName());
        GCM.getGCMLifeCycleController(c).startFc();
        TestIntentItf i = (TestIntentItf) c.getFcInterface("server");
        i.m();
        SCAIntentController scaic = Utils.getSCAIntentController(c);
        scaic.addFcIntentHandler(new SecurityIntentHandler(""));
        scaic.addFcIntentHandler(new SecurityIntentHandler(""));
        TestIntentItf objIn = (TestIntentItf) IntentServiceItfGenerator.instance().generateClass(i, c, 2);
        objIn.n();
        objIn.m();
        checkInterface(objIn);
    }
}
