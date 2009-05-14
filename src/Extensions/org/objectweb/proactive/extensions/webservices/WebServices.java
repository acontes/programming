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
package org.objectweb.proactive.extensions.webservices;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.axis2.transport.http.AxisServlet;
import org.apache.log4j.Logger;
import org.globus.ogce.beans.filetransfer.util.FileToTransfer;
import org.mortbay.jetty.servlet.ServletHolder;
import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.httpserver.HTTPServer;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.deployer.PADeployer;
import org.objectweb.proactive.extensions.webservices.util.Util;


/**
 * Deploy and undeploy active objects and components. Methods of this class
 * just call methods of the PADeployer class.
 * 
 * @author The ProActive Team
 */
@PublicAPI
public final class WebServices extends WSConstants {

    static private Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    static {

        // Retrieve or launch a Jetty server
        // in case of a local exposition
        HTTPServer httpServer = HTTPServer.get();

        // Create an Axis servlet
        AxisServlet axisServlet = new AxisServlet();

        ServletHolder axisServletHolder = new ServletHolder(axisServlet);

        String tempDir = System.getProperty("java.io.tmpdir");

        String axis2XML = Util.extractFromJar(WSConstants.PROACTIVE_JAR, WSConstants.AXIS_XML_ENTRY, tempDir,
                true);
        axisServletHolder.setInitParameter("axis2.xml.path", axis2XML);

        String axis2Repo = Util.extractFromJar(WSConstants.PROACTIVE_JAR, WSConstants.AXIS_REPOSITORY_ENTRY,
                tempDir, true);
        axisServletHolder.setInitParameter("axis2.repository.path", axis2Repo);

        // Register the Axis Servlet to Jetty
        httpServer.registerServlet(axisServletHolder, WSConstants.AXIS_SERVLET);

        logger.info("Erasing temporary files created by axis2 servlet...");
        File f = new File((File) axisServlet.getServletContext()
                .getAttribute("javax.servlet.context.tempdir"), "_axis2");
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (File child : files) {
                if (child.delete()) {
                    logger.info("   - " + child.getAbsolutePath() + " has been deleted");
                } else {
                    logger.info("   - " + child.getAbsolutePath() + " has not been deleted");
                }
            }

            if (f.delete()) {
                logger.info("   - " + f.getAbsolutePath() + " has been deleted");
            } else {
                logger.info("   - " + f.getAbsolutePath() + " has not been deleted");
            }
        }

        logger.info("Deployed axis servlet on the local Jetty server");
    }

    /**
     * Expose an active object as a web service with the methods specified in <code>methods</code> 
     * 
     * @param o The object to expose as a web service
     * @param url The url of the host where the object will be deployed  (typically http://localhost:8080/)
     * @param urn The name of the object
     * @param methods The methods that will be exposed as web services functionalities
     *					 If null, then all methods will be exposed
     */
    public static void exposeAsWebService(Object o, String url, String urn, String[] methods) {
        PADeployer.deploy(o, url, urn, methods, false);
    }

    /**
     * Expose an active object with all its methods as a web service
     * 
     * @param o The object to expose as a web service
     * @param url The url of the host where the object will be deployed  (typically http://localhost:8080/)
     * @param urn The name of the object
     */
    public static void exposeAsWebService(Object o, String url, String urn) {
        PADeployer.deploy(o, url, urn, null, false);
    }

    /**
     * Undeploy a service
     *
     * @param urn The name of the object
     * @param url The url of the web server
     */
    public static void unExposeAsWebService(String url, String urn) {
        PADeployer.unDeploy(url, urn);
    }

    /**
     * Expose a component as web service. Each server interface of the component 
     * will be accessible by  the urn [componentName]_[interfaceName].
     * Only the interfaces public methods of the specified interfaces in 
     * <code>interfaceNames</code> will be exposed.
     *
     * @param component The component owning the interfaces that will be deployed as web services.
     * @param url  Web server url  where to deploy the service - typically "http://localhost:8080"
     * @param componentName Name of the component
     * @param interfaceNames Names of the interfaces we want to deploy.
      *							  If null, then all the interfaces will be deployed
     */
    public static void exposeComponentAsWebService(Component component, String url, String componentName,
            String[] interfaceNames) {
        PADeployer.deployComponent(component, url, componentName, interfaceNames);
    }

    /**
     * Expose a component as web service. Each server interface of the component 
     * will be accessible by  the urn [componentName]_[interfaceName].
     * All the interfaces public methods of all interfaces will be exposed.
     *
     * @param component The component owning the interfaces that will be deployed as web services.
     * @param url  Web server url  where to deploy the service - typically "http://localhost:8080"
     * @param componentName Name of the component
     */
    public static void exposeComponentAsWebService(Component component, String url, String componentName) {
        PADeployer.deployComponent(component, url, componentName, null);
    }

    /**
     * Undeploy all the interfaces of a component deployed on a web server
     *
     * @param component  The component owning the services interfaces
     * @param url The url of the web server
     * @param componentName The name of the component
     */
    public static void unExposeComponentAsWebService(Component component, String url, String componentName) {
        PADeployer.unDeployComponent(component, url, componentName);
    }

    /**
     * Undeploy specified interfaces of a component deployed on a web server
     *
     * @param url The url of the web server
     * @param componentName The name of the component
     * @param interfaceNames Interfaces to be undeployed
     */
    public static void unExposeComponentAsWebService(String url, String componentName, String[] interfaceNames) {
        PADeployer.unDeployComponent(url, componentName, interfaceNames);
    }
}
