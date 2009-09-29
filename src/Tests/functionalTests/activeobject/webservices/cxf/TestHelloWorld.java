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

package functionalTests.activeobject.webservices.cxf;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.log4j.Logger;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.httpserver.HTTPServer;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.WSConstants;
import org.objectweb.proactive.extensions.webservices.WebServices;

import functionalTests.activeobject.webservices.common.Couple;
import functionalTests.activeobject.webservices.common.HelloWorld;


public class TestHelloWorld {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    private String url;

    @org.junit.Before
    public void deployHelloWorld() throws Exception {
        try {
            // Get the HTTP server enabling us to retrieve the jetty
            // port number
            HTTPServer httpServer = HTTPServer.get();
            String port = PAProperties.PA_XMLHTTP_PORT.getValue();
            this.url = "http://localhost:" + port + "/";

            HelloWorld hw = (HelloWorld) PAActiveObject.newActive(
                    "functionalTests.activeobject.webservices.common.HelloWorld", new Object[] {});
            WebServices.exposeAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, hw, this.url, "HelloWorld");

            Method m1 = hw.getClass().getSuperclass().getMethod("sayText");
            Method m2 = hw.getClass().getSuperclass().getMethod("putTextToSay",
                    new Class<?>[] { String.class });
            Method[] methods = new Method[] { m1, m2 };
            WebServices.exposeAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, hw, this.url,
                    "HelloWorldMethods", methods);

            String[] methodNames = new String[] { "putTextToSay", "sayText" };
            WebServices.exposeAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, hw, this.url,
                    "HelloWorldMethodNames", methodNames);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @org.junit.Test
    public void testHelloWorld() {

        ClientFactoryBean factory = new ClientFactoryBean();
        factory.setServiceClass(HelloWorld.class);
        factory.setAddress(url + WSConstants.SERVICES_PATH + "HelloWorld");
        Client client = factory.create();

        Object[] res;
        String text;
        try {

            client.invoke("putHelloWorld");
            logger.info("Called the method putHelloWorld: no argument and no return is expected");

            res = client.invoke("contains", "Hello world!");
            logger.info("Called the method contains: one argument and one return are expected");
            assertTrue((Boolean) res[0]);

            client.invoke("putTextToSay", "Good bye world!");
            logger.info("Called the method putTextToSay: " + "one argument is expected but no return");

            res = client.invoke("sayText");
            logger.info("Called the method 'sayText': one return is expected but not argument");

            text = (String) res[0];
            assertTrue(text.equals("Hello world!"));

            res = client.invoke("sayText");
            logger.info("Called the method 'sayText': one return is expected but not argument");

            text = (String) res[0];
            assertTrue(text.equals("Good bye world!"));

            res = client.invoke("sayText");
            logger.info("Called the method 'sayText': one return is expected but not argument");

            text = (String) res[0];
            assertTrue(text.equals("The list is empty"));

            res = client.invoke("sayHello");
            logger.info("Called the method 'sayHello': inherited method");

            text = (String) res[0];
            assertTrue(text.equals("Hello!"));

            Couple cpl1 = new Couple();
            cpl1.setMyInt(1);
            cpl1.setStr1("First");
            Couple cpl2 = new Couple();
            cpl2.setMyInt(2);
            cpl2.setStr1("Second");
            Couple[] couples = new Couple[] { cpl1, cpl2 };

            client.invoke("setCouples", new Object[] { couples });
            logger.info("Called the method 'setCouples': one argument of type Couple[]");

            res = client.invoke("getCouples");
            logger.info("Called the method 'getCouples': return a table of Couple");
            Couple[] table = (Couple[]) res[0];
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

        ClientFactoryBean factoryMethods = new ClientFactoryBean();
        factoryMethods.setServiceClass(HelloWorld.class);
        factoryMethods.setAddress(url + WSConstants.SERVICES_PATH + "HelloWorldMethods");
        Client clientMethods = factoryMethods.create();

        try {
            clientMethods.invoke("putTextToSay", new Object[] { "Hi ProActive Team!" });
            logger.info("Called the method putTextToSay: " + "one argument is expected but no return");

            res = clientMethods.invoke("sayText");
            logger.info("Called the method 'sayText': one return is expected but not argument");

            text = (String) res[0];
            assertTrue(text.equals("Hi ProActive Team!"));

            res = clientMethods.invoke("sayText");
            logger.info("Called the method 'sayText': one return is expected but not argument");

            text = (String) res[0];
            assertTrue(text.equals("The list is empty"));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        try {
            logger.info("Called the method putHelloWorld: this method should not be exposed");
            logger.info("The normal behaviour is to raise an exception");
            clientMethods.invoke("putHelloWorld");

            assertTrue(false);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("This exception was expected");
        }

        ClientFactoryBean factoryMethodNames = new ClientFactoryBean();
        factoryMethodNames.setServiceClass(HelloWorld.class);
        factoryMethodNames.setAddress(url + WSConstants.SERVICES_PATH + "HelloWorldMethodNames");
        Client clientMethodNames = factoryMethodNames.create();

        try {

            clientMethodNames.invoke("putTextToSay", "Hi ProActive Team!");
            logger.info("Called the method 'putTextToSay': " + "one argument is expected but no return");

            res = clientMethodNames.invoke("sayText");
            logger.info("Called the method 'sayText': one return is expected but not argument");

            text = (String) res[0];
            assertTrue(text.equals("Hi ProActive Team!"));

            res = clientMethodNames.invoke("sayText");
            logger.info("Called the method 'sayText': one return is expected but not argument");

            text = (String) res[0];
            assertTrue(text.equals("The list is empty"));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        try {
            logger.info("Called the method putHelloWorld: this method should not be exposed");
            logger.info("The normal behaviour is to raise an exception");
            clientMethodNames.invoke("putHelloWorld");

            assertTrue(false);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("This exception was expected");
        }
    }

    @org.junit.After
    public void undeployHelloWorld() {
        try {
            WebServices.unExposeAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, this.url, "HelloWorld");
            WebServices.unExposeAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, this.url,
                    "HelloWorldMethods");
            WebServices.unExposeAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, this.url,
                    "HelloWorldMethodNames");
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
