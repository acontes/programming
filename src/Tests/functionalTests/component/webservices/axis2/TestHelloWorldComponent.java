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

package functionalTests.component.webservices.axis2;

import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
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

            WebServicesFactory wsf = AbstractWebServicesFactory.getWebServicesFactory("axis2");
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
            RPCServiceClient serviceClient = new RPCServiceClient();

            Options options = serviceClient.getOptions();

            EndpointReference targetEPR = new EndpointReference(this.url + WSConstants.SERVICES_PATH +
                "server_hello-world");
            options.setTo(targetEPR);

            // Call putHelloWorld
            options.setAction("putHelloWorld");
            QName op = new QName("putHelloWorld");
            Object[] opArgs = new Object[] {};

            serviceClient.invokeRobust(op, opArgs);

            logger.info("Called the method putHelloWorld: no argument and no return is expected");

            // Call contains
            options.setAction("contains");
            op = new QName("contains");
            opArgs = new Object[] { "Hello world!" };
            Class<?>[] returnTypes = new Class[] { boolean.class };

            Object[] response = serviceClient.invokeBlocking(op, opArgs, returnTypes);
            logger.info("Called the method contains: one argument and one return are expected");

            assertTrue((Boolean) response[0]);

            // Call putTextToSay
            options.setAction("putTextToSay");
            op = new QName("putTextToSay");
            opArgs = new Object[] { "Good bye world!" };

            serviceClient.invokeRobust(op, opArgs);
            logger.info("Called the method putTextToSay: " + "one argument is expected but no return");

            // Call sayText
            options.setAction("sayText");
            op = new QName("sayText");
            opArgs = new Object[] {};
            returnTypes = new Class[] { String.class };

            response = serviceClient.invokeBlocking(op, opArgs, returnTypes);
            logger.info("Called the method 'sayText': one return is expected but not argument");

            String text = (String) response[0];
            assertTrue(text.equals("Hello world!"));

            response = serviceClient.invokeBlocking(op, opArgs, returnTypes);
            logger.info("Called the method 'sayText': one return is expected but not argument");

            text = (String) response[0];
            assertTrue(text.equals("Good bye world!"));

            response = serviceClient.invokeBlocking(op, opArgs, returnTypes);
            logger.info("Called the method 'sayText': one return is expected but not argument");

            text = (String) response[0];
            assertTrue(text.equals("The list is empty"));

            // Call sayHello
            options.setAction("sayHello");
            op = new QName("sayHello");
            opArgs = new Object[] {};
            returnTypes = new Class[] { String.class };

            response = serviceClient.invokeBlocking(op, opArgs, returnTypes);
            logger.info("Called the method 'sayHello': inherited method");

            text = (String) response[0];
            assertTrue(text.equals("Hello!"));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        try {
            RPCServiceClient serviceClient = new RPCServiceClient();

            Options options = serviceClient.getOptions();

            EndpointReference targetEPR = new EndpointReference(this.url + WSConstants.SERVICES_PATH +
                "server_hello-world");
            options.setTo(targetEPR);

            // Call putHelloWorld
            options.setAction("putGoodByeWorld");
            QName op = new QName("putGoodByeWorld");
            Object[] opArgs = new Object[] {};

            logger.info("Called the method putGoodByeWorld: no argument and no return is expected");
            logger.info("This call should raise an exception");
            serviceClient.invokeRobust(op, opArgs);

            assertTrue(false);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("This exception is normal");
        }

        try {
            RPCServiceClient serviceClient = new RPCServiceClient();

            Options options = serviceClient.getOptions();

            EndpointReference targetEPR = new EndpointReference(this.url + WSConstants.SERVICES_PATH +
                "server_hello-world");
            options.setTo(targetEPR);

            // Call putHelloWorld
            options.setAction("fakeMethod");
            QName op = new QName("fakeMethod");
            Object[] opArgs = new Object[] {};

            logger.info("Called the method fakeMethod: no argument and no return is expected");
            logger.info("This call should raise an exception");
            serviceClient.invokeRobust(op, opArgs);

            assertTrue(false);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("This exception is normal");
        }

        try {
            RPCServiceClient serviceClient = new RPCServiceClient();

            Options options = serviceClient.getOptions();

            EndpointReference targetEPR = new EndpointReference(this.url + WSConstants.SERVICES_PATH +
                "server2_hello-world");
            options.setTo(targetEPR);
            options.setAction("putTextToSay");
            QName op = new QName("putTextToSay");
            Object[] opArgs = new Object[] { "Hi ProActive Team!" };

            serviceClient.invokeRobust(op, opArgs);
            logger.info("Called the method putTextToSay: " + "one argument is expected but no return");

            // Call sayText
            options.setAction("sayText");
            op = new QName("sayText");
            opArgs = new Object[] {};
            Class<?>[] returnTypes = new Class[] { String.class };

            Object[] response = serviceClient.invokeBlocking(op, opArgs, returnTypes);
            logger.info("Called the method 'sayText': one return is expected but not argument");

            String text = (String) response[0];
            assertTrue(text.equals("Hi ProActive Team!"));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        try {
            RPCServiceClient serviceClient = new RPCServiceClient();

            Options options = serviceClient.getOptions();

            EndpointReference targetEPR = new EndpointReference(this.url + WSConstants.SERVICES_PATH +
                "server2_good-bye-world");
            options.setTo(targetEPR);
            options.setAction("putGoodByeWorld");
            QName op = new QName("putGoodByeWorld");
            Object[] opArgs = new Object[] {};

            serviceClient.invokeRobust(op, opArgs);
            logger.info("Called the method 'putGoodByeWorld': one argument is expected but no return");

            // Call sayText
            options.setAction("sayText");
            op = new QName("sayText");
            opArgs = new Object[] {};
            Class<?>[] returnTypes = new Class[] { String.class };

            Object[] response = serviceClient.invokeBlocking(op, opArgs, returnTypes);
            logger.info("Called the method 'sayText': one return is expected but not argument");

            String text = (String) response[0];
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
