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

package functionalTests.component.webservices.cxf;

import static org.junit.Assert.assertTrue;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.httpserver.HTTPServer;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.AbstractWebServicesFactory;
import org.objectweb.proactive.extensions.webservices.WSConstants;
import org.objectweb.proactive.extensions.webservices.WebServices;
import org.objectweb.proactive.extensions.webservices.WebServicesFactory;

import functionalTests.component.webservices.common.GoodByeWorldItf;
import functionalTests.component.webservices.common.HelloWorldComponent;
import functionalTests.component.webservices.common.HelloWorldItf;


public class TestHelloWorldComponent {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    private String url;
    private Component comp;
    private WebServices ws;

    @org.junit.Before
    public void deployHelloWorldComponent() {

        try {
            // Get the HTTP server enabling us to retrieve the jetty
            // port number
            HTTPServer httpServer = HTTPServer.get();
            String port = PAProperties.PA_XMLHTTP_PORT.getValue();
            this.url = "http://localhost:" + port + "/";

            Component boot = org.objectweb.fractal.api.Fractal.getBootstrapComponent();

            TypeFactory tf = Fractal.getTypeFactory(boot);
            GenericFactory cf = Fractal.getGenericFactory(boot);

            ComponentType typeComp = tf.createFcType(new InterfaceType[] {
                    tf.createFcItfType("hello-world", HelloWorldItf.class.getName(), false, false, false),
                    tf
                            .createFcItfType("good-bye-world", GoodByeWorldItf.class.getName(), false, false,
                                    false) });

            comp = cf.newFcInstance(typeComp, new ControllerDescription("server", Constants.PRIMITIVE),
                    new ContentDescription(HelloWorldComponent.class.getName(), null));

            Fractal.getLifeCycleController(comp).startFc();

            WebServicesFactory wsf = AbstractWebServicesFactory.getWebServicesFactory("cxf");
            ws = wsf.newWebServices(url);

            ws.exposeComponentAsWebService(comp, "server", new String[] { "hello-world" });

            ws.exposeComponentAsWebService(comp, "server2");

            logger.info("Deployed an hello-world interface as a webservice service on : " + url);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @org.junit.Test
    public void testHelloWorldComponent() {

        try {

            ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
            factory.setServiceClass(HelloWorldItf.class);
            factory.setAddress(url + WSConstants.SERVICES_PATH + "server_hello-world");
            HelloWorldItf client = (HelloWorldItf) factory.create();

            client.putHelloWorld();
            logger.info("Called the method putHelloWorld: no argument and no return is expected");

            boolean containsHello = client.contains("Hello world!");
            logger.info("Called the method contains: one argument and one return are expected");

            assertTrue(containsHello);

            client.putTextToSay("Good bye world!");
            logger.info("Called the method 'putTextToSay': one argument is expected but no return");

            String text = client.sayText();
            logger.info("Called the method 'sayText': one return is expected but not argument");
            assertTrue(text.equals("Hello world!"));

            text = client.sayText();
            logger.info("Called the method 'sayText': one return is expected but not argument");
            assertTrue(text.equals("Good bye world!"));

            text = client.sayText();
            logger.info("Called the method 'sayText': one return is expected but not argument");
            assertTrue(text.equals("The list is empty"));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        try {

            ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
            factory.setServiceClass(HelloWorldItf.class);
            factory.setAddress(url + WSConstants.SERVICES_PATH + "server2_hello-world");
            HelloWorldItf client = (HelloWorldItf) factory.create();

            client.putTextToSay("Hi ProActive Team!");
            logger.info("Called the method putTextToSay: " + "one argument is expected but no return");

            String text = client.sayText();
            assertTrue(text.equals("Hi ProActive Team!"));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        try {

            ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
            factory.setServiceClass(GoodByeWorldItf.class);
            factory.setAddress(url + WSConstants.SERVICES_PATH + "server2_good-bye-world");
            GoodByeWorldItf client = (GoodByeWorldItf) factory.create();

            client.putGoodByeWorld();
            logger.info("Called the method 'putGoodByeWorld': " + "one argument is expected but no return");

            String text = client.sayText();
            assertTrue(text.equals("Good bye world!"));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @org.junit.After
    public void undeployHelloWorldComponent() {
        try {
            ws.unExposeComponentAsWebService("server", new String[] { "hello-world" });
            ws.unExposeComponentAsWebService(this.comp, "server2");
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
