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
package org.objectweb.proactive.examples.webservices.helloWorld;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.extensions.webservices.WebServices;


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

    public String helloWorld(String name) {
        return "Hello " + name + " !";
    }

    public String goodByeWorld(String name) {
        return "Good Bye " + name + " !";
    }

    public String sayHello() {
        return "Hello ProActive Team !";
    }

    public String sayGoodBye() {
        return "Good bye ProActive Team !";
    }

    public static void main(String[] args) {
        String url;
        if (args.length == 0) {
            url = "http://localhost:8080/";
        } else {
            url = args[0];
        }

        Component boot = null;
        Component comp = null;
        try {
            boot = org.objectweb.fractal.api.Fractal.getBootstrapComponent();

            TypeFactory tf = Fractal.getTypeFactory(boot);
            GenericFactory cf = Fractal.getGenericFactory(boot);

            // type of server component
            ComponentType sType = tf
                    .createFcType(new InterfaceType[] {
                            tf.createFcItfType("hello-world", HelloWorldItf.class.getName(), false, false,
                                    false),
                            tf.createFcItfType("goodbye-world", GoodByeWorldItf.class.getName(), false,
                                    false, false) });
            // create server component
            comp = cf.newFcInstance(sType, new ControllerDescription("server", Constants.PRIMITIVE),
                    new ContentDescription(HelloWorldComponent.class.getName()));
            //start the component
            Fractal.getLifeCycleController(comp).startFc();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (NoSuchInterfaceException e) {
            e.printStackTrace();
        } catch (IllegalLifeCycleException e) {
            e.printStackTrace();
        }

        WebServices.exposeComponentAsWebService(comp, url, "server", new String[] { "hello-world",
                "goodbye-world" });
    }
}
//@snippet-end helloworldcomponent
