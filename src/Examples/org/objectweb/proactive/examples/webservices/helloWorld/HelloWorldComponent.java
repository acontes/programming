/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
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
package org.objectweb.proactive.examples.webservices.helloWorld;

import java.io.File;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extensions.webservices.AbstractWebServicesFactory;
import org.objectweb.proactive.extensions.webservices.WebServices;
import org.objectweb.proactive.extensions.webservices.WebServicesFactory;
import org.objectweb.proactive.extensions.webservices.WebServicesInitActiveFactory;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


/**
 * A simple example to expose an active object as a web service.
 *
 * @author The ProActive Team
 */
//@snippet-start helloworldcomponent
public class HelloWorldComponent implements HelloWorldItf, GoodByeWorldItf {

    private String text;

    public String sayText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public HelloWorldComponent() {
    }

    public String helloWorld(String arg0) {
        return "Hello " + arg0 + " !";
    }

    public String goodByeWorld(String arg0) {
        return "Good Bye " + arg0 + " !";
    }

    public String sayHello() {
        return "Hello ProActive Team !";
    }

    public String sayGoodBye() {
        return "Good bye ProActive Team !";
    }

    public static void main(String[] args) {
        String url = "";
        String wsFrameWork = "";
        File applicationDescriptor = null;
        if (args.length == 1) {
            url = AbstractWebServicesFactory.getLocalUrl();
            wsFrameWork = args[0];
        } else if (args.length == 2) {
            url = args[0];
            wsFrameWork = args[1];
        } else if (args.length == 3) {
            url = args[0];
            applicationDescriptor = new File(args[1]);
            wsFrameWork = args[2];
        } else {
            System.out.println("Wrong number of arguments");
            System.out.println("Usage: HelloWorldComponent [url] wsFrameWork");
            System.out.println("with wsFrameWork should be either \"axis2\" or \"cxf\" ");
            System.exit(0);
        }

        Component boot = null;
        Component comp = null;
        try {
            boot = GCM.getBootstrapComponent();

            GCMTypeFactory tf = GCM.getGCMTypeFactory(boot);
            PAGenericFactory cf = Utils.getPAGenericFactory(boot);

            // type of server component
            ComponentType sType = tf
                    .createFcType(new InterfaceType[] {
                            tf.createFcItfType("hello-world", HelloWorldItf.class.getName(), false, false,
                                    false),
                            tf.createFcItfType("goodbye-world", GoodByeWorldItf.class.getName(), false,
                                    false, false) });

            if (applicationDescriptor != null) {
                System.out.println("Using a deployment");
                GCMApplication gcmad = PAGCMDeployment.loadApplicationDescriptor(applicationDescriptor);
                gcmad.startDeployment();

                GCMVirtualNode hello = gcmad.getVirtualNode("Hello");
                Node node1 = hello.getANode();

                if (hello == null)
                    throw new ProActiveException("Hello virtual node is not defined");

                // create server component
                //                ControllerDescription cd = new ControllerDescription("server", Constants.PRIMITIVE,
                //                        "/user/ffonteno/home/proactive-git/programming/src/Extensions/" +
                //                        "org/objectweb/proactive/extensions/webservices/cxf/initialization/cxf-component-config.xml");
                ControllerDescription cd = new ControllerDescription("server", Constants.PRIMITIVE);
                comp = cf.newFcInstance(sType, cd, new ContentDescription(
                    HelloWorldComponent.class.getName(), null, WebServicesInitActiveFactory
                            .getInitActive(wsFrameWork), null), node1);
            } else {
                System.out.println("Not using a deployment");
                ControllerDescription cd = new ControllerDescription("server", Constants.PRIMITIVE);
                comp = cf.newFcInstance(sType, cd,
                        new ContentDescription(HelloWorldComponent.class.getName()));
            }

            //start the component
            GCM.getGCMLifeCycleController(comp).startFc();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (NoSuchInterfaceException e) {
            e.printStackTrace();
        } catch (IllegalLifeCycleException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            WebServicesFactory wsf = AbstractWebServicesFactory.getWebServicesFactory(wsFrameWork);
            WebServices ws = wsf.getWebServices(url);
            ws.exposeComponentAsWebService(comp, "server");
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
    }
}
//@snippet-end helloworldcomponent
