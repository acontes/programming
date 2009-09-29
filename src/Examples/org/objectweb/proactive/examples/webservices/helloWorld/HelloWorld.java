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
package org.objectweb.proactive.examples.webservices.helloWorld;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAMobileAgent;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.annotation.ActiveObject;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extensions.webservices.WebServices;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


/**
 * A simple example to expose an active object as a web service.
 *
 * @author The ProActive Team
 */
@ActiveObject
public class HelloWorld implements Serializable {

    private final static Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    LinkedList<String> textsToSay = new LinkedList<String>();

    public HelloWorld() {
    }

    public String helloWorld() {
        return "Hello world !";
    }

    // This method is used to check
    // that it is not inserted in the wsdl
    // and not callable.
    public String toString() {
        return "HelloWorld";
    }

    public void putTextToSay(String textToSay) {
        this.textsToSay.add(textToSay);
    }

    public String sayText() {
        if (this.textsToSay.isEmpty()) {
            return "The list is empty";
        } else {
            return this.textsToSay.poll();
        }
    }

    public void moveTo(String origin, String destination) {
        try {
            logger.info("Migrating AO from " + origin + " to " + destination);
            logger.info("Migration started");
            PAMobileAgent.migrateTo(destination);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            String url = "";
            boolean GCMDeployment;
            boolean onANode;
            if (args.length == 1) {
                GCMDeployment = false;
                onANode = false;
            } else if (args.length == 2) {
                GCMDeployment = true;
                onANode = false;
            } else if (args.length == 3) {
                GCMDeployment = true;
                onANode = true;
            } else {
                logger.info("Wrong number of arguments:");
                logger.info("Usage: java HelloWorld [url]");
                return;
            }
            url = args[0];
            HelloWorld hw;

            if (GCMDeployment) {
                logger.info("Using a GCM Deployment");
                File applicationDescriptor = new File(args[1]);
                GCMApplication gcmad = PAGCMDeployment.loadApplicationDescriptor(applicationDescriptor);

                if (!onANode) {
                    logger.info("Deploying AO locally");
                    hw = (HelloWorld) PAActiveObject.newActive(
                            "org.objectweb.proactive.examples.webservices.helloWorld.HelloWorld",
                            new Object[] {});
                } else {
                    logger.info("Deploying AO on a node");
                    gcmad.startDeployment();

                    GCMVirtualNode hello = gcmad.getVirtualNode("Hello");
                    Node node1 = hello.getANode();
                    String origin = node1.getNodeInformation().getURL();
                    logger.info("Node Address = " + origin);

                    if (hello == null)
                        throw new ProActiveException("Hello virtual node is not defined");

                    hw = (HelloWorld) PAActiveObject.newActive(
                            "org.objectweb.proactive.examples.webservices.helloWorld.HelloWorld",
                            new Object[] {}, node1);

                    GCMVirtualNode helloMigration = gcmad.getVirtualNode("Hello");
                    String destination = helloMigration.getANode().getNodeInformation().getURL();
                    hw.moveTo(origin, destination);
                }
            } else {
                logger.info("Not using a GCM Deployment");

                hw = (HelloWorld) PAActiveObject
                        .newActive("org.objectweb.proactive.examples.webservices.helloWorld.HelloWorld",
                                new Object[] {});
            }

            WebServices.exposeAsWebService(hw, url, "HelloWorld", new String[] { "putTextToSay", "sayText",
                    "helloWorld" });
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
