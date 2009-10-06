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

package functionalTests.activeobject.webservices.axis2;

import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.log4j.Logger;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.httpserver.HTTPServer;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.AbstractWebServicesFactory;
import org.objectweb.proactive.extensions.webservices.WSConstants;
import org.objectweb.proactive.extensions.webservices.WebServices;
import org.objectweb.proactive.extensions.webservices.WebServicesFactory;

import functionalTests.activeobject.webservices.common.Couple;
import functionalTests.activeobject.webservices.common.HelloWorld;


public class TestHelloWorld {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    private String url;
    private WebServices ws;

    @org.junit.Before
    public void deployHelloWorld() {
        try {
            // Get the HTTP server enabling us to retrieve the jetty
            // port number
            HTTPServer httpServer = HTTPServer.get();
            String port = PAProperties.PA_XMLHTTP_PORT.getValue();
            this.url = "http://localhost:" + port + "/";

            HelloWorld hw = (HelloWorld) PAActiveObject.newActive(
                    "functionalTests.activeobject.webservices.common.HelloWorld", new Object[] {});

            WebServicesFactory wsf = AbstractWebServicesFactory.getWebServicesFactory("axis2");
            ws = wsf.newWebServices(url);

            ws.exposeAsWebService(hw, "HelloWorld");

            Method m1 = hw.getClass().getSuperclass().getMethod("sayText");
            Method m2 = hw.getClass().getSuperclass().getMethod("putTextToSay",
                    new Class<?>[] { String.class });
            Method[] methods = new Method[] { m1, m2 };

            ws.exposeAsWebService(hw, "HelloWorldMethods", methods);

            String[] methodNames = new String[] { "putTextToSay", "sayText" };

            ws.exposeAsWebService(hw, "HelloWorldMethodNames", methodNames);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @org.junit.Test
    public void testHelloWorld() {
        try {
            RPCServiceClient serviceClient = new RPCServiceClient();

            Options options = serviceClient.getOptions();

            EndpointReference targetEPR = new EndpointReference(this.url + WSConstants.SERVICES_PATH +
                "HelloWorld");
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

            Couple cpl1 = new Couple();
            cpl1.setMyInt(1);
            cpl1.setStr1("First");
            Couple cpl2 = new Couple();
            cpl2.setMyInt(2);
            cpl2.setStr1("Second");
            Couple[] couples = new Couple[] { cpl1, cpl2 };

            // Call setCouples
            options.setAction("setCouples");
            op = new QName("setCouples");
            opArgs = new Object[] { couples };
            serviceClient.invokeRobust(op, opArgs);
            logger.info("Called the method 'setCouples': one argument of type Couple[]");

            // Call getCouples
            options.setAction("getCouples");
            op = new QName("getCouples");
            opArgs = new Object[] {};
            returnTypes = new Class[] { Array.newInstance(Couple.class, 2).getClass() };
            response = serviceClient.invokeBlocking(op, opArgs, returnTypes);
            logger.info("Called the method 'getCouples': return a table of Couple");

            Couple[] table = (Couple[]) response[0];
            Couple test1 = table[0];
            Couple test2 = table[1];

            assertTrue(test1.getMyInt() == 1);
            assertTrue(test1.getStr1().equals("First"));
            assertTrue(test2.getMyInt() == 2);
            assertTrue(test2.getStr1().equals("Second"));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        try {
            RPCServiceClient serviceClient = new RPCServiceClient();

            Options options = serviceClient.getOptions();
            EndpointReference targetEPR = new EndpointReference(this.url + WSConstants.SERVICES_PATH +
                "HelloWorldMethods");
            options.setTo(targetEPR);

            // Call putTextToSay
            options.setAction("putTextToSay");
            QName op = new QName("putTextToSay");
            Object[] opArgs = new Object[] { "Hi ProActive Team!" };

            serviceClient.invokeRobust(op, opArgs);
            logger.info("Called the method putTextToSay: " + "one argument is expected but no return");

            options.setAction("sayText");
            op = new QName("sayText");
            opArgs = new Object[] {};
            Class<?>[] returnTypes = new Class[] { String.class };

            Object[] response = serviceClient.invokeBlocking(op, opArgs, returnTypes);
            logger.info("Called the method 'sayText': one return is expected but not argument");

            String text = (String) response[0];
            assertTrue(text.equals("Hi ProActive Team!"));

            response = serviceClient.invokeBlocking(op, opArgs, returnTypes);
            logger.info("Called the method 'sayText': one return is expected but not argument");

            text = (String) response[0];
            assertTrue(text.equals("The list is empty"));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        try {
            RPCServiceClient serviceClient = new RPCServiceClient();

            Options options = serviceClient.getOptions();
            EndpointReference targetEPR = new EndpointReference(this.url + WSConstants.SERVICES_PATH +
                "HelloWorldMethods");
            options.setTo(targetEPR);

            options.setAction("putHelloWorld");
            QName op = new QName("putHelloWorld");

            Object[] opArgs = new Object[] {};

            logger.info("Called the method putHelloWorld: this method should not be exposed");
            logger.info("The normal behaviour is to raise an exception");
            serviceClient.invokeRobust(op, opArgs);

            assertTrue(false);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("This exception was expected");
        }

        try {
            RPCServiceClient serviceClient = new RPCServiceClient();

            Options options = serviceClient.getOptions();
            EndpointReference targetEPR = new EndpointReference(this.url + WSConstants.SERVICES_PATH +
                "HelloWorldMethodNames");
            options.setTo(targetEPR);

            // Call putTextToSay
            options.setAction("putTextToSay");
            QName op = new QName("putTextToSay");
            Object[] opArgs = new Object[] { "Hi ProActive Team!" };

            serviceClient.invokeRobust(op, opArgs);
            logger.info("Called the method putTextToSay: " + "one argument is expected but no return");

            options.setAction("sayText");
            op = new QName("sayText");
            opArgs = new Object[] {};
            Class<?>[] returnTypes = new Class[] { String.class };

            Object[] response = serviceClient.invokeBlocking(op, opArgs, returnTypes);
            logger.info("Called the method 'sayText': one return is expected but not argument");

            String text = (String) response[0];
            assertTrue(text.equals("Hi ProActive Team!"));

            response = serviceClient.invokeBlocking(op, opArgs, returnTypes);
            logger.info("Called the method 'sayText': one return is expected but not argument");

            text = (String) response[0];
            assertTrue(text.equals("The list is empty"));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        try {
            RPCServiceClient serviceClient = new RPCServiceClient();

            Options options = serviceClient.getOptions();
            EndpointReference targetEPR = new EndpointReference(this.url + WSConstants.SERVICES_PATH +
                "HelloWorldMethodNames");
            options.setTo(targetEPR);

            options.setAction("putHelloWorld");
            QName op = new QName("putHelloWorld");

            Object[] opArgs = new Object[] {};

            logger.info("Called the method putHelloWorld: this method should not be exposed");
            logger.info("The normal behaviour is to raise an exception");
            serviceClient.invokeRobust(op, opArgs);

            assertTrue(false);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("This exception was expected");
        }
    }

    @org.junit.After
    public void undeployHelloWorld() {
        try {
            ws.unExposeAsWebService("HelloWorld");
            ws.unExposeAsWebService("HelloWorldMehtods");
            ws.unExposeAsWebService("HelloWorldMethodNames");
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
