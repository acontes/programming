/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
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

package functionalTests.activeobject.webservices;

import javax.xml.namespace.QName;
import java.lang.reflect.Array;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.WSConstants;
import org.objectweb.proactive.extensions.webservices.WebServices;


public class TestHelloWorld {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    private String url;

    @Before
    public void deployHelloWorld() throws Exception {

        // Loading the WebServices class enables us to retrieve the jetty
        // port number
        Class.forName("org.objectweb.proactive.extensions.webservices.WebServices");
        String port = PAProperties.PA_XMLHTTP_PORT.getValue();
        this.url = "http://localhost:" + port + "/";

        HelloWorld hw = (HelloWorld) PAActiveObject.newActive(
                "functionalTests.activeobject.webservices.HelloWorld", new Object[] {});
        WebServices.exposeAsWebService(hw, this.url, "HelloWorld");
    }

    @org.junit.Test
    public void testHelloWorld() throws Exception {
        RPCServiceClient serviceClient = new RPCServiceClient();

        Options options = serviceClient.getOptions();

        EndpointReference targetEPR = new EndpointReference(this.url + WSConstants.AXIS_SERVICES_PATH +
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

        Boolean isListed = (Boolean) response[0];
        logger.info("Called the method contains: one argument and one return are expected");

        if (isListed) {
            logger.info("'Hello world !' is in the list");
            logger.info("Inserting 'Good bye world!'");

            // Call putTextToSay
            options.setAction("putTextToSay");
            op = new QName("putTextToSay");
            opArgs = new Object[] { "Good bye world!" };

            serviceClient.invokeRobust(op, opArgs);

            logger.info("Called the method putTextToSay: " + "one argument is expected but no return");
        } else {
            throw new ProActiveException("'Hello World!' is not in the list "
                + "or the contains method of the HelloWorld service does not properly");
        }

        // Call sayText
        options.setAction("sayText");
        op = new QName("sayText");
        opArgs = new Object[] {};
        returnTypes = new Class[] { String.class };

        response = serviceClient.invokeBlocking(op, opArgs, returnTypes);

        String text = (String) response[0];
        logger.info("Called the method 'sayText': one return is expected but not argument");
        logger.info("'sayText' returned " + text);

        response = serviceClient.invokeBlocking(op, opArgs, returnTypes);

        text = (String) response[0];
        logger.info("Called the method 'sayText': one return is expected but not argument");
        logger.info("'sayText' returned " + text);

        response = serviceClient.invokeBlocking(op, opArgs, returnTypes);

        text = (String) response[0];
        logger.info("Called the method 'sayText': one return is expected but not argument");
        logger.info("'sayText' returned " + text);

        // Call sayHello
        options.setAction("sayHello");
        op = new QName("sayHello");
        opArgs = new Object[] {};
        returnTypes = new Class[] { String.class };

        response = serviceClient.invokeBlocking(op, opArgs, returnTypes);

        text = (String) response[0];
        logger.info("Called the method 'sayHello': inherited method");
        logger.info("'sayHello' returned " + text);

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

        // Call getCouples
        options.setAction("getCouples");
        op = new QName("getCouples");
        opArgs = new Object[] {};
        returnTypes = new Class[] { Array.newInstance(Couple.class, 2).getClass() };
        response = serviceClient.invokeBlocking(op, opArgs, returnTypes);

        Couple[] table = (Couple[]) response[0];
        Couple test1 = table[0];
        Couple test2 = table[1];

        logger.info("Called the method 'getCouples': return a table of Couple");
        logger.info("test1 = " + test1.getStr1() + ", " + test1.getMyInt());
        logger.info("test2 = " + test2.getStr1() + ", " + test2.getMyInt());
    }

    @After
    public void undeployHelloWorld() throws Exception {
        WebServices.unExposeAsWebService(this.url, "HelloWorld");
    }
}
