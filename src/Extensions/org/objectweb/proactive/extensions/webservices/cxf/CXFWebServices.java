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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mortbay.jetty.servlet.ServletHolder;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.component.type.ProActiveInterfaceType;
import org.objectweb.proactive.core.httpserver.HTTPServer;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.AbstractWebServices;
import org.objectweb.proactive.extensions.webservices.WebServices;
import org.objectweb.proactive.extensions.webservices.common.MethodUtils;
import org.objectweb.proactive.extensions.webservices.cxf.deployer.PADeployer;
import org.objectweb.proactive.extensions.webservices.cxf.servicedeployer.ServiceDeployer;
import org.objectweb.proactive.extensions.webservices.cxf.servicedeployer.ServiceDeployerItf;
import org.objectweb.proactive.extensions.webservices.exceptions.WebServicesException;


/**
 * @author The ProActive Team
 *
 */
public class CXFWebServices extends AbstractWebServices implements WebServices {

    static private Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    /**
     * Add the CXF servlet to the jetty server and set the initial parameters.
     * 
     */
    private synchronized void initializeServlet() {
        // Retrieve or launch a Jetty server
        // in case of a local exposition
        HTTPServer httpServer = HTTPServer.get();

        if (httpServer.isMapped(WSConstants.SERVLET_PATH))
            return;

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

        if (logger.getLevel() != null && logger.getLevel() == Level.DEBUG) {

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
        }

        // Creates the service
        svrFactory.create();

        logger.debug("Cxf servlet has been deployed on the local Jetty server " +
            "with its embedded ServiceDeployer service located at " + this.url + WSConstants.SERVICES_PATH +
            "ServiceDeployer");
    }

    /**
     * @param url
     */
    public CXFWebServices(String url) {
        super(url);
        initializeServlet();
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.extensions.webservices.WebServices#exposeAsWebService(java.lang.Object, java.lang.String, java.lang.reflect.Method[])
     */
    public void exposeAsWebService(Object o, String urn, Method[] methods) throws WebServicesException {
        MethodUtils.checkMethodsClass(methods);
        PADeployer.deploy(o, this.url, urn, methods, false);

        logger.debug("The object of type '" + o.getClass().getSuperclass().getName() +
            "' has been deployed on " + this.url + WSConstants.SERVICES_PATH + urn + "?wsdl");
        logger.debug("Only the following methods of this object have been deployed: ");
        for (Method method : methods) {
            logger.debug(" - " + method.getName());
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.extensions.webservices.WebServices#exposeAsWebService(java.lang.Object, java.lang.String, java.lang.String[])
     */
    public void exposeAsWebService(Object o, String urn, String[] methodsName) throws WebServicesException {
        // Transforms the array methods' name into an array of
        // methods (of type Method)
        MethodUtils mc = new MethodUtils(o.getClass().getSuperclass());
        ArrayList<Method> methodsArrayList = mc.getCorrespondingMethods(methodsName);
        Method[] methods = new Method[methodsArrayList.size()];
        methodsArrayList.toArray(methods);
        PADeployer.deploy(o, this.url, urn, methods, false);

        logger.debug("The object of type '" + o.getClass().getSuperclass().getName() +
            "' has been deployed on " + this.url + WSConstants.SERVICES_PATH + urn + "?wsdl");
        logger.debug("Only the following methods of this object have been deployed: ");
        for (String name : methodsName) {
            logger.debug(" - " + name);
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.extensions.webservices.WebServices#exposeAsWebService(java.lang.Object, java.lang.String)
     */
    public void exposeAsWebService(Object o, String urn) throws WebServicesException {
        PADeployer.deploy(o, this.url, urn, null, false);

        logger.debug("The object of type '" + o.getClass().getSuperclass().getName() +
            "' has been deployed on " + this.url + WSConstants.SERVICES_PATH + urn + "?wsdl");
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.extensions.webservices.WebServices#unExposeAsWebService(java.lang.String)
     */
    public void unExposeAsWebService(String urn) {
        PADeployer.undeploy(this.url, urn);

        logger.debug("The service '" + urn + "' previously deployed on " + this.url +
            WSConstants.SERVICES_PATH + urn + "?wsdl " + "has been undeployed");
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.extensions.webservices.WebServices#exposeComponentAsWebService(org.objectweb.fractal.api.Component, java.lang.String, java.lang.String[])
     */
    public void exposeComponentAsWebService(Component component, String componentName, String[] interfaceNames)
            throws WebServicesException {
        PADeployer.deployComponent(component, this.url, componentName, interfaceNames);

        for (String name : interfaceNames) {
            logger.debug("The component interface '" + name + "' has been deployed on " + this.url +
                WSConstants.SERVICES_PATH + componentName + "_" + name + "?wsdl");
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.extensions.webservices.WebServices#exposeComponentAsWebService(org.objectweb.fractal.api.Component, java.lang.String)
     */
    public void exposeComponentAsWebService(Component component, String componentName)
            throws WebServicesException {
        PADeployer.deployComponent(component, this.url, componentName, null);

        Object[] interfaces = component.getFcInterfaces();
        for (Object o : interfaces) {
            Interface interface_ = (Interface) o;
            String interfaceName = interface_.getFcItfName();
            if (!interfaceName.contains("-controller") && !interfaceName.equals("component") &&
                !((ProActiveInterfaceType) interface_.getFcItfType()).isFcClientItf()) {

                logger.debug("The component interface '" + interfaceName + "' has been deployed on " +
                    this.url + WSConstants.SERVICES_PATH + componentName + "_" + interfaceName + "?wsdl");
            }
        }
    }

    /* (non-Javadoc)
     * 
     * With CXF, this method
     * can only be used if you have previously deployed all the client interfaces of the component.
     * Otherwise, it will raise an exception trying to undeploy a client interface which has not been
     * deployed before.
     * 
     * @see org.objectweb.proactive.extensions.webservices.WebServices#unExposeComponentAsWebService(org.objectweb.fractal.api.Component, java.lang.String)
     */
    public void unExposeComponentAsWebService(Component component, String componentName) {
        PADeployer.undeployComponent(component, this.url, componentName);

        Object[] interfaces = component.getFcInterfaces();
        for (Object o : interfaces) {
            Interface interface_ = (Interface) o;
            String interfaceName = interface_.getFcItfName();
            if (!interfaceName.contains("-controller") && !interfaceName.equals("component") &&
                !((ProActiveInterfaceType) interface_.getFcItfType()).isFcClientItf()) {
                logger.debug("The component interface '" + interfaceName + "' previously deployed on " +
                    this.url + WSConstants.SERVICES_PATH + componentName + "_" + interfaceName +
                    "?wsdl has been undeployed");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.extensions.webservices.WebServices#unExposeComponentAsWebService(java.lang.String, java.lang.String[])
     */
    public void unExposeComponentAsWebService(String componentName, String[] interfaceNames) {
        PADeployer.undeployComponent(this.url, componentName, interfaceNames);

        for (String name : interfaceNames) {
            logger.debug("The component interface '" + name + "' previously deployed on " + this.url +
                WSConstants.SERVICES_PATH + componentName + "_" + name + "?wsdl has been undeployed");
        }
    }

}