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
package functionalTests.component.webservices;

import javax.xml.namespace.QName;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.WSConstants;
import org.objectweb.proactive.extensions.webservices.WebServices;

import functionalTests.FunctionalTest;


/**
 * A simple example to expose an active object as a web service.
 * 
 * @author The ProActive Team
 */
public class TestCompositeComponent extends FunctionalTest {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    private String url;

    @Before
    public void deployComposite() throws Exception {
    	// Retrieves the port number of the local Jetty server
        Class.forName("org.objectweb.proactive.extensions.webservices.WebServices");
        String port = PAProperties.PA_XMLHTTP_PORT.getValue();
        url = "http://localhost:" + port + "/";

        Component boot = null;
        Component comp = null;
        Component hello = null;
        Component chooseName = null;

        boot = org.objectweb.fractal.api.Fractal.getBootstrapComponent();

        TypeFactory tf = Fractal.getTypeFactory(boot);
        GenericFactory cf = Fractal.getGenericFactory(boot);

        // type of server component
        ComponentType typeComp = tf.createFcType(new InterfaceType[] { tf.createFcItfType("hello-world",
                HelloNameItf.class.getName(), false, false, false) });

        ComponentType typeHello = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType("hello-world", HelloNameItf.class.getName(), false, false, false),
                tf.createFcItfType("choose-name", ChooseNameItf.class.getName(), true, false, false) });

        ComponentType typeChoose = tf.createFcType(new InterfaceType[] { tf.createFcItfType("choose-name",
                ChooseNameItf.class.getName(), false, false, false) });

        // create server component
        comp = cf.newFcInstance(typeComp, new ControllerDescription("composite", Constants.COMPOSITE), null);
        hello = cf.newFcInstance(typeHello, new ControllerDescription("hello", Constants.PRIMITIVE),
                new ContentDescription(HelloNameComponent.class.getName(), null));
        chooseName = cf.newFcInstance(typeChoose,
                new ControllerDescription("choosename", Constants.PRIMITIVE), new ContentDescription(
                    ChooseNameComponent.class.getName(), null));

        // start the component
        ContentController cc = Fractal.getContentController(comp);
        cc.addFcSubComponent(hello);
        cc.addFcSubComponent(chooseName);
        BindingController bc = Fractal.getBindingController(comp);
        bc.bindFc("hello-world", hello.getFcInterface("hello-world"));
        bc = Fractal.getBindingController(hello);
        bc.bindFc("choose-name", chooseName.getFcInterface("choose-name"));
        Fractal.getLifeCycleController(comp).startFc();

        WebServices.exposeComponentAsWebService(comp, url, "composite", new String[] { "hello-world" });

        logger.info("Deploy a composite as a webservice service on : " + url);
    	
    }
    
    @org.junit.Test
    public void testComposite() throws Exception {
    	
        RPCServiceClient serviceClient = new RPCServiceClient();

        Options options = serviceClient.getOptions();

        EndpointReference targetEPR = new EndpointReference(url + WSConstants.AXIS_SERVICES_PATH +
            "composite_hello-world");

        options.setTo(targetEPR);
        options.setAction("helloName");

        // Call sayText
        QName op = new QName("helloName");

        // Choose a random name
        int index = -1;
        Object[] opArgs = new Object[] { index };
        Class<?>[] returnTypes = new Class[] { String.class };

        Object[] response = serviceClient.invokeBlocking(op, opArgs, returnTypes);

        String result = (String) response[0];
        logger.info("Call to the helloName method returned: " + result);
    }
    
    @After
    public void undeployComposite() throws Exception {
        WebServices.unExposeComponentAsWebService(this.url, "composite", new String[] {"hello-world"});  
    }
}
