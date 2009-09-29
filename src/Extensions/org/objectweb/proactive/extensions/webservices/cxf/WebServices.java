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
package org.objectweb.proactive.extensions.webservices.cxf;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.log4j.Logger;
import org.mortbay.jetty.servlet.ServletHolder;
import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.httpserver.HTTPServer;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.common.MethodUtils;
import org.objectweb.proactive.extensions.webservices.cxf.deployer.PADeployer;
import org.objectweb.proactive.extensions.webservices.cxf.servicedeployer.ServiceDeployer;
import org.objectweb.proactive.extensions.webservices.cxf.servicedeployer.ServiceDeployerItf;


/**
 * Deploy and undeploy active objects and components using CXF.
 * Methods of this class just call methods of the PADeployer class.
 *
 * @author The ProActive Team
 */
@PublicAPI
public final class WebServices extends WSConstants {

    static private Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    /**
     * Static block in charge of deploying the ServiceDeployer service into the jetty server
     */
    static {

        // Retrieve or launch a Jetty server
        // in case of a local exposition
        HTTPServer httpServer = HTTPServer.get();

        try {

            // Creates a CXF servlet and register it
            // to the Jetty server
            CXFServlet cxf = new CXFServlet();
            ServletHolder CXFServletHolder = new ServletHolder(cxf);
            httpServer.registerServlet(CXFServletHolder, WSConstants.SERVLET_PATH);

            // Configures the bus
            Bus bus = cxf.getBus();
            BusFactory.setDefaultBus(bus);

            /*
             * Configure the service
             */
            ServerFactoryBean svrFactory = new ServerFactoryBean();
            svrFactory.setAddress("/ServiceDeployer");
            svrFactory.setServiceClass(ServiceDeployerItf.class);
            svrFactory.setServiceBean(new ServiceDeployer());

            /*
             * Attaches a list of in-interceptors
             * In our case, only a logger is attached in order to be able
             * to see input soap messages
             */
            List<Interceptor> inInterceptors = new ArrayList<Interceptor>();
            LoggingInInterceptor loggingInInterceptor = new LoggingInInterceptor();
            inInterceptors.add(loggingInInterceptor);
            svrFactory.setInInterceptors(inInterceptors);

            /*
             * Attaches a list of out-interceptors
             * In our case, only a logger is attached in order to be able
             * to see output soap messages
             */
            List<Interceptor> outInterceptors = new ArrayList<Interceptor>();
            LoggingOutInterceptor loggingOutInterceptor = new LoggingOutInterceptor();
            outInterceptors.add(loggingOutInterceptor);
            svrFactory.setOutInterceptors(outInterceptors);

            // Creates the service
            svrFactory.create();

            logger
                    .info("The service ServiceDeployer has been deployed on the jetty server located at http://localhost:" +
                        PAProperties.PA_XMLHTTP_PORT.getValue() +
                        "/" +
                        WSConstants.SERVICES_PATH +
                        "ServiceDeployer");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
    public static void exposeAsWebService(Object o, String url, String urn, Method[] methods) {
        logger.info("Trying to expose " + o.getClass().getSuperclass().getName() + " at " + url +
            WSConstants.SERVICES_PATH + urn);
        try {
            MethodUtils.checkMethodsClass(methods);
            PADeployer.deploy(o, url, urn, methods, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Expose an active object as a web service with the methods specified in <code>methods</code>
     *
     * @param o The object to expose as a web service
     * @param url The url of the host where the object will be deployed  (typically http://localhost:8080/)
     * @param urn The name of the object
     * @param methods The methods that will be exposed as web services functionalities
     *                   If null, then all methods will be exposed
     */
    public static void exposeAsWebService(Object o, String url, String urn, String[] methodsName) {
        logger.info("Trying to expose " + o.getClass().getSuperclass().getName() + " at " + url +
            WSConstants.SERVICES_PATH + urn);
        try {
            // Transforms the array methods' name into an array of
            // methods (of type Method)
            MethodUtils mc = new MethodUtils(o.getClass().getSuperclass());
            ArrayList<Method> methodsArrayList = mc.getCorrespondingMethods(methodsName);
            Method[] methods = new Method[methodsArrayList.size()];
            methodsArrayList.toArray(methods);

            PADeployer.deploy(o, url, urn, methods, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Expose an active object with all its methods as a web service
     *
     * @param o The object to expose as a web service
     * @param url The url of the host where the object will be deployed  (typically http://localhost:8080/)
     * @param urn The name of the object
     */
    public static void exposeAsWebService(Object o, String url, String urn) {
        logger.info("Trying to expose " + o.getClass().getSuperclass().getName() + " at " + url +
            WSConstants.SERVICES_PATH + urn);
        PADeployer.deploy(o, url, urn, null, false);
    }

    /**
     * Undeploy a service
     *
     * @param urn The name of the object
     * @param url The url of the web server
     */
    public static void unExposeAsWebService(String url, String urn) {
        logger.info("Trying to unexpose the service " + urn + " at " + url + WSConstants.SERVICES_PATH + urn);
        PADeployer.undeploy(url, urn);
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
        PADeployer.undeployComponent(component, url, componentName);
    }

    /**
     * Undeploy specified interfaces of a component deployed on a web server
     *
     * @param url The url of the web server
     * @param componentName The name of the component
     * @param interfaceNames Interfaces to be undeployed
     */
    public static void unExposeComponentAsWebService(String url, String componentName, String[] interfaceNames) {
        PADeployer.undeployComponent(url, componentName, interfaceNames);
    }
}
