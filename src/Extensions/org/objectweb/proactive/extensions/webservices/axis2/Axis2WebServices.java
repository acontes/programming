package org.objectweb.proactive.extensions.webservices.axis2;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.axis2.transport.http.AxisServlet;
import org.apache.log4j.Logger;
import org.mortbay.jetty.servlet.ServletHolder;
import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.httpserver.HTTPServer;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.AbstractWebServices;
import org.objectweb.proactive.extensions.webservices.WebServices;
import org.objectweb.proactive.extensions.webservices.axis2.deployer.PADeployer;
import org.objectweb.proactive.extensions.webservices.axis2.util.Util;
import org.objectweb.proactive.extensions.webservices.common.MethodUtils;
import org.objectweb.proactive.extensions.webservices.exceptions.WebServicesException;


public class Axis2WebServices extends AbstractWebServices implements WebServices {

    static private Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    private synchronized void initializeServlet() throws WebServicesException {

        // Retrieve or launch a Jetty server
        // in case of a local exposition
        HTTPServer httpServer = HTTPServer.get();

        if (httpServer.isMapped(WSConstants.SERVLET_PATH))
            return;

        // Create an Axis servlet
        AxisServlet axisServlet = new AxisServlet();

        ServletHolder axisServletHolder = new ServletHolder(axisServlet);

        String tempDir = System.getProperty("java.io.tmpdir");

        // Extracts the axis2.xml file from the proactive.jar archive and return its path
        String axis2XML = Util.extractFromJar(WSConstants.PROACTIVE_JAR, WSConstants.AXIS_XML_ENTRY, tempDir,
                true);
        axisServletHolder.setInitParameter("axis2.xml.path", axis2XML);

        // Extracts the axis2 repository from the proactive.jar archive and return its path
        String axis2Repo = Util.extractFromJar(WSConstants.PROACTIVE_JAR, WSConstants.AXIS_REPOSITORY_ENTRY,
                tempDir, true);
        axisServletHolder.setInitParameter("axis2.repository.path", axis2Repo);

        // Register the Axis Servlet to Jetty
        httpServer.registerServlet(axisServletHolder, WSConstants.SERVLET_PATH);

        // Erases the _axis2 directory created by axis2 when used by jetty
        logger.debug("Erasing temporary files created by axis2 servlet...");
        File f = new File((File) axisServlet.getServletContext()
                .getAttribute("javax.servlet.context.tempdir"), "_axis2");
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (File child : files) {
                if (child.delete()) {
                    logger.debug("   - " + child.getAbsolutePath() + " has been deleted");
                } else {
                    logger.debug("   - " + child.getAbsolutePath() + " has not been deleted");
                }
            }

            if (f.delete()) {
                logger.debug("   - " + f.getAbsolutePath() + " has been deleted");
            } else {
                logger.debug("   - " + f.getAbsolutePath() + " has not been deleted");
            }
        }

        logger.debug("Axis servlet has been deployed on the local Jetty server " +
            "with its embedded ServiceDeployer service located at " + "http://localhost:" +
            PAProperties.PA_XMLHTTP_PORT.getValue() + "/" + WSConstants.SERVICES_PATH + "ServiceDeployer");
    }

    public Axis2WebServices(String url) throws WebServicesException {
        super(url);
        initializeServlet();
    }

    /**
     * Expose an active object as a web service with the methods specified in <code>methods</code>
     *
     * @param o The object to expose as a web service
     * @param url The url of the host where the object will be deployed  (typically http://localhost:8080/)
     * @param urn The name of the object
     * @param methods The methods that will be exposed as web services functionalities
     *                   If null, then all methods will be exposed
     * @throws WebServicesException 
     */
    public void exposeAsWebService(Object o, String urn, String[] methods) throws WebServicesException {
        PADeployer.deploy(o, this.url, urn, methods, false);
    }

    /**
     * Expose an active object as a web service with the methods specified in <code>methods</code>
     *
     * @param o The object to expose as a web service
     * @param url The url of the host where the object will be deployed  (typically http://localhost:8080/)
     * @param urn The name of the object
     * @param methods The methods that will be exposed as web services functionalities
     *                   If null, then all methods will be exposed
     * @throws WebServicesException 
     */
    public void exposeAsWebService(Object o, String urn, Method[] methods) throws WebServicesException {
        ArrayList<String> methodsName = MethodUtils.getCorrespondingMethodsName(methods);
        PADeployer.deploy(o, this.url, urn, methodsName.toArray(new String[methodsName.size()]), false);
    }

    /**
     * Expose an active object with all its methods as a web service
     *
     * @param o The object to expose as a web service
     * @param url The url of the host where the object will be deployed  (typically http://localhost:8080/)
     * @param urn The name of the object
     * @throws WebServicesException 
     */
    public void exposeAsWebService(Object o, String urn) throws WebServicesException {
        PADeployer.deploy(o, this.url, urn, null, false);
    }

    /**
     * Undeploy a service
     *
     * @param urn The name of the object
     * @param url The url of the web server
     * @throws WebServicesException 
     */
    public void unExposeAsWebService(String urn) throws WebServicesException {
        PADeployer.undeploy(this.url, urn);
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
      *                           If null, then all the interfaces will be deployed
     * @throws WebServicesException 
     */
    public void exposeComponentAsWebService(Component component, String componentName, String[] interfaceNames)
            throws WebServicesException {
        PADeployer.deployComponent(component, this.url, componentName, interfaceNames);
    }

    /**
     * Expose a component as web service. Each server interface of the component
     * will be accessible by  the urn [componentName]_[interfaceName].
     * All the interfaces public methods of all interfaces will be exposed.
     *
     * @param component The component owning the interfaces that will be deployed as web services.
     * @param url  Web server url  where to deploy the service - typically "http://localhost:8080"
     * @param componentName Name of the component
     * @throws WebServicesException 
     */
    public void exposeComponentAsWebService(Component component, String componentName)
            throws WebServicesException {
        PADeployer.deployComponent(component, this.url, componentName, null);
    }

    /**
     * Undeploy all the interfaces of a component deployed on a web server
     *
     * @param component  The component owning the services interfaces
     * @param url The url of the web server
     * @param componentName The name of the component
     * @throws WebServicesException 
     */
    public void unExposeComponentAsWebService(Component component, String componentName)
            throws WebServicesException {
        PADeployer.undeployComponent(component, this.url, componentName);
    }

    /**
     * Undeploy specified interfaces of a component deployed on a web server
     *
     * @param url The url of the web server
     * @param componentName The name of the component
     * @param interfaceNames Interfaces to be undeployed
     * @throws WebServicesException 
     */
    public void unExposeComponentAsWebService(String componentName, String[] interfaceNames)
            throws WebServicesException {
        PADeployer.undeployComponent(this.url, componentName, interfaceNames);
    }

}
