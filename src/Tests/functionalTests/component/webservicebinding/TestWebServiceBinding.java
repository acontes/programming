/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
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
package functionalTests.component.webservicebinding;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactory;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.extensions.webservices.WSConstants;
import org.objectweb.proactive.extensions.webservices.WebServices;

import functionalTests.ComponentTest;


/**
 * Test web service binding.
 *
 * @author The ProActive Team
 */
public class TestWebServiceBinding extends ComponentTest {
    public static int NUMBER_SERVERS = 3;
    public static String SERVER_DEFAULT_NAME = "Server";
    public static String SERVER_SERVICES_NAME = "Services";
    public static String SERVER_SERVICEMULTICAST_NAME = "Service";

    private Component boot;
    private TypeFactory tf;
    private GenericFactory gf;
    private String url;
    private Component[] servers;
    private ComponentType componentType;

    @Before
    public void setUpAndDeployServers() throws Exception {
        boot = Fractal.getBootstrapComponent();
        tf = Fractal.getTypeFactory(boot);
        gf = Fractal.getGenericFactory(boot);

        // Load the WebServices class to retrieve the jetty port number
        Class.forName("org.objectweb.proactive.extensions.webservices.WebServices");
        String port = PAProperties.PA_XMLHTTP_PORT.getValue();
        url = "http://localhost:" + port + "/";
        ComponentType sType = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType(SERVER_SERVICES_NAME, Services.class.getName(),
                        ProActiveTypeFactory.SERVER, ProActiveTypeFactory.MANDATORY,
                        ProActiveTypeFactory.SINGLE),
                tf.createFcItfType(SERVER_SERVICEMULTICAST_NAME, Service.class.getName(),
                        ProActiveTypeFactory.SERVER, ProActiveTypeFactory.MANDATORY,
                        ProActiveTypeFactory.SINGLE) });
        servers = new Component[NUMBER_SERVERS];
        for (int i = 0; i < NUMBER_SERVERS; i++) {
            servers[i] = gf.newFcInstance(sType, new ControllerDescription(SERVER_DEFAULT_NAME + i,
                Constants.PRIMITIVE), new ContentDescription(Server.class.getName()));
            Fractal.getLifeCycleController(servers[i]).startFc();
            WebServices.exposeComponentAsWebService(servers[i], url, SERVER_DEFAULT_NAME + i);
        }

        componentType = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType("Runner", Runner.class.getName(), ProActiveTypeFactory.SERVER,
                        ProActiveTypeFactory.MANDATORY, ProActiveTypeFactory.SINGLE),
                tf.createFcItfType(Client.SERVICES_NAME, Services.class.getName(),
                        ProActiveTypeFactory.CLIENT, ProActiveTypeFactory.MANDATORY,
                        ProActiveTypeFactory.SINGLE),
                ((ProActiveTypeFactory) tf).createFcItfType(Client.SERVICEMULTICASTREAL_NAME,
                        ServiceMulticast.class.getName(), ProActiveTypeFactory.CLIENT,
                        ProActiveTypeFactory.OPTIONAL, ProActiveTypeFactory.MULTICAST_CARDINALITY),
                tf.createFcItfType(Client.SERVICEMULTICASTFALSE_NAME, ServiceMulticast.class.getName(),
                        ProActiveTypeFactory.CLIENT, ProActiveTypeFactory.OPTIONAL,
                        ProActiveTypeFactory.SINGLE) });
    }

    @Test
    public void testWebServicesBindingWithPrimitiveComponent() throws Exception {
        Component client = gf.newFcInstance(componentType, new ControllerDescription("Client",
            Constants.PRIMITIVE), new ContentDescription(Client.class.getName()));
        Fractal.getBindingController(client).bindFc(Client.SERVICES_NAME,
                url + WSConstants.AXIS_SERVICES_PATH + SERVER_DEFAULT_NAME + "0_" + SERVER_SERVICES_NAME);
        for (int i = 0; i < NUMBER_SERVERS; i++) {
            Fractal.getBindingController(client).bindFc(
                    Client.SERVICEMULTICASTREAL_NAME,
                    url + WSConstants.AXIS_SERVICES_PATH + SERVER_DEFAULT_NAME + i + "_" +
                        SERVER_SERVICEMULTICAST_NAME);
        }
        Fractal.getLifeCycleController(client).startFc();
        Runner runner = (Runner) client.getFcInterface("Runner");
        Assert.assertTrue("Failed to invoke web services with primitive component", runner.execute()
                .booleanValue());
    }

    @Test
    public void testWebServicesBindingWithCompositeComponent() throws Exception {
        Component composite = gf.newFcInstance(componentType, new ControllerDescription("Composite",
            Constants.COMPOSITE), null);
        Component client = gf.newFcInstance(componentType, new ControllerDescription("Client",
            Constants.PRIMITIVE), new ContentDescription(Client.class.getName()));
        Fractal.getContentController(composite).addFcSubComponent(client);
        Fractal.getBindingController(composite).bindFc("Runner", client.getFcInterface("Runner"));
        Fractal.getBindingController(client).bindFc(Client.SERVICES_NAME,
                composite.getFcInterface(Client.SERVICES_NAME));
        //        Fractal.getBindingController(client).bindFc(Client.SERVICEMULTICASTFALSE_NAME,
        //                composite.getFcInterface(Client.SERVICEMULTICASTREAL_NAME));
        Fractal.getBindingController(composite).bindFc(Client.SERVICES_NAME,
                url + WSConstants.AXIS_SERVICES_PATH + SERVER_DEFAULT_NAME + "0_" + SERVER_SERVICES_NAME);
        //        for (int i = 0; i < NUMBER_SERVERS; i++) {
        //            Fractal.getBindingController(composite).bindFc(
        //                    Client.SERVICEMULTICASTREAL_NAME,
        //                    url + WSConstants.AXIS_SERVICES_PATH + SERVER_DEFAULT_NAME + i + "_" +
        //                        SERVER_SERVICEMULTICAST_NAME);
        //        }
        Fractal.getLifeCycleController(composite).startFc();
        Runner runner = (Runner) composite.getFcInterface("Runner");
        Assert.assertTrue("Failed to invoke web services with composite component", runner.execute()
                .booleanValue());
    }

    @Test
    @Ignore
    public void testWebServicesBindingWithADL() throws Exception {
        Factory factory = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();
        Map<Object, Object> context = new HashMap<Object, Object>();
        Component composite = (Component) factory.newComponent(
                "functionalTests.component.webservices.adl.Composite", context);
        Fractal.getLifeCycleController(composite).startFc();
        Runner runner = (Runner) composite.getFcInterface("Runner");
        Assert.assertTrue("Failed to invoke web services with composite component", runner.execute()
                .booleanValue());
    }

    @Test
    public void testWebServicesBindingWithWSCallerError() throws Exception {
        Component client = gf.newFcInstance(componentType, new ControllerDescription("Client",
            Constants.PRIMITIVE), new ContentDescription(Client.class.getName()));
        try {
            Fractal.getBindingController(client).bindFc(
                    Client.SERVICES_NAME,
                    url + WSConstants.AXIS_SERVICES_PATH + SERVER_DEFAULT_NAME + "0_" + SERVER_SERVICES_NAME +
                        "(WSCallerError)");
            fail();
        } catch (IllegalBindingException ibe) {
            ibe.printStackTrace();
        }
    }

    @Test
    public void testWebServicesBindingWithURLError1() throws Exception {
        Component client = gf.newFcInstance(componentType, new ControllerDescription("Client",
            Constants.PRIMITIVE), new ContentDescription(Client.class.getName()));
        try {
            Fractal.getBindingController(client).bindFc(Client.SERVICES_NAME, "ErrorURL");
            fail();
        } catch (IllegalBindingException ibe) {
            ibe.printStackTrace();
        }
    }

    @Test
    public void testWebServicesBindingWithURLError2() throws Exception {
        Component client = gf.newFcInstance(componentType, new ControllerDescription("Client",
            Constants.PRIMITIVE), new ContentDescription(Client.class.getName()));
        Fractal.getBindingController(client).bindFc(Client.SERVICES_NAME,
                url + WSConstants.AXIS_SERVICES_PATH + "ErrorURL");
        Fractal.getLifeCycleController(client).startFc();
        Runner runner = (Runner) client.getFcInterface("Runner");
        Assert.assertFalse("Successful access to a non existing URL", runner.execute().booleanValue());
    }

    @Test
    public void testWebServicesBindingWithMethodError() throws Exception {
        ComponentType cType = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType("Runner", Runner.class.getName(), ProActiveTypeFactory.SERVER,
                        ProActiveTypeFactory.MANDATORY, ProActiveTypeFactory.SINGLE),
                tf.createFcItfType(Client.SERVICEERROR_NAME, ServiceError.class.getName(),
                        ProActiveTypeFactory.CLIENT, ProActiveTypeFactory.MANDATORY,
                        ProActiveTypeFactory.SINGLE) });
        Component client = gf.newFcInstance(cType, new ControllerDescription("Client", Constants.PRIMITIVE),
                new ContentDescription(Client.class.getName()));
        Fractal.getBindingController(client).bindFc(Client.SERVICEERROR_NAME,
                url + WSConstants.AXIS_SERVICES_PATH + SERVER_DEFAULT_NAME + "0_" + SERVER_SERVICES_NAME);
        Fractal.getLifeCycleController(client).startFc();
        Runner runner = (Runner) client.getFcInterface("Runner");
        Assert.assertFalse("Successful access to a non existing method", runner.execute().booleanValue());
    }

    @After
    public void undeployServers() throws Exception {
        for (int i = 0; i < NUMBER_SERVERS; i++) {
            WebServices.unExposeComponentAsWebService(servers[i], url, SERVER_DEFAULT_NAME + i);
        }
    }
}
