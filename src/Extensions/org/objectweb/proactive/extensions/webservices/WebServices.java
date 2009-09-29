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

import java.lang.reflect.Method;

import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.ProActiveException;


/**
 * Deploy and undeploy active objects and components. Methods of this class
 * just call methods of the PADeployer class.
 *
 * @author The ProActive Team
 */
@PublicAPI
public final class WebServices extends WSConstants {

    private static String DEFAULT_FRAMEWORK_IDENTIFIER = "axis2";

    /**
     * Expose an active object as a web service with the methods specified in <code>methods</code>
     *
     * @param wsFrameWork web service framework which will be used to deploy the service (e.g. "axis2", "cxf")
     * @param o The object to expose as a web service
     * @param url The url of the host where the object will be deployed  (typically http://localhost:8080/)
     * @param urn The name of the object
     * @param methods The methods that will be exposed as web services functionalities
     *					 If null, then all methods will be exposed
     * @throws ProActiveException throws a ProActiveException if the web service framework is not authorized
     */
    public static void exposeAsWebService(String wsFrameWork, Object o, String url, String urn,
            String[] methods) throws ProActiveException {
        if (WSConstants.AXIS2_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.axis2.WebServices.exposeAsWebService(o, url, urn,
                    methods);
        } else if (WSConstants.CXF_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.cxf.WebServices.exposeAsWebService(o, url, urn,
                    methods);
        } else {
            throw new ProActiveException("Unknown web service framework identifier: " + wsFrameWork);
        }
    }

    /**
     * Expose an active object as a web service with the methods specified in <code>methods</code>
     *
     * @param wsFrameWork web service framework which will be used to deploy the service (e.g. "axis2", "cxf")
     * @param o The object to expose as a web service
     * @param url The url of the host where the object will be deployed  (typically http://localhost:8080/)
     * @param urn The name of the object
     * @param methods The methods that will be exposed as web services functionalities
     *                   If null, then all methods will be exposed
     * @throws ProActiveException throws a ProActiveException if the web service framework is not authorized
     */
    public static void exposeAsWebService(String wsFrameWork, Object o, String url, String urn,
            Method[] methods) throws ProActiveException {
        if (WSConstants.AXIS2_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.axis2.WebServices.exposeAsWebService(o, url, urn,
                    methods);
        } else if (WSConstants.CXF_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.cxf.WebServices.exposeAsWebService(o, url, urn,
                    methods);
        } else {
            throw new ProActiveException("Unknown web service framework identifier: " + wsFrameWork);
        }
    }

    /**
     * Expose an active object with all its methods as a web service
     *
     * @param wsFrameWork web service framework which will be used to deploy the service (e.g. "axis2", "cxf")
     * @param o The object to expose as a web service
     * @param url The url of the host where the object will be deployed  (typically http://localhost:8080/)
     * @param urn The name of the object
     * @throws ProActiveException throws a ProActiveException if the web service framework is not authorized
     */
    public static void exposeAsWebService(String wsFrameWork, Object o, String url, String urn)
            throws ProActiveException {
        if (WSConstants.AXIS2_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.axis2.WebServices.exposeAsWebService(o, url, urn);
        } else if (WSConstants.CXF_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.cxf.WebServices.exposeAsWebService(o, url, urn);
        } else {
            throw new ProActiveException("Unknown web service framework identifier: " + wsFrameWork);
        }
    }

    /**
     * Undeploy a service
     *
     * @param wsFrameWork web service framework which will be used to deploy the service (e.g. "axis2", "cxf")
     * @param urn The name of the object
     * @param url The url of the web server
     * @throws ProActiveException throws a ProActiveException if the web service framework is not authorized
     */
    public static void unExposeAsWebService(String wsFrameWork, String url, String urn)
            throws ProActiveException {
        if (WSConstants.AXIS2_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.axis2.WebServices.unExposeAsWebService(url, urn);
        } else if (WSConstants.CXF_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.cxf.WebServices.unExposeAsWebService(url, urn);
        } else {
            throw new ProActiveException("Unknown web service framework identifier: " + wsFrameWork);
        }
    }

    /**
     * Expose a component as web service. Each server interface of the component
     * will be accessible by  the urn [componentName]_[interfaceName].
     * Only the interfaces public methods of the specified interfaces in
     * <code>interfaceNames</code> will be exposed.
     *
     * @param wsFrameWork web service framework which will be used to deploy the service (e.g. "axis2", "cxf")
     * @param component The component owning the interfaces that will be deployed as web services.
     * @param url  Web server url  where to deploy the service - typically "http://localhost:8080"
     * @param componentName Name of the component
     * @param interfaceNames Names of the interfaces we want to deploy.
      *							  If null, then all the interfaces will be deployed
     * @throws ProActiveException throws a ProActiveException if the web service framework is not authorized
     */
    public static void exposeComponentAsWebService(String wsFrameWork, Component component, String url,
            String componentName, String[] interfaceNames) throws ProActiveException {
        if (WSConstants.AXIS2_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.axis2.WebServices.exposeComponentAsWebService(
                    component, url, componentName, interfaceNames);
        } else if (WSConstants.CXF_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.cxf.WebServices.exposeComponentAsWebService(
                    component, url, componentName, interfaceNames);
        } else {
            throw new ProActiveException("Unknown web service framework identifier: " + wsFrameWork);
        }
    }

    /**
     * Expose a component as web service. Each server interface of the component
     * will be accessible by  the urn [componentName]_[interfaceName].
     * All the interfaces public methods of all interfaces will be exposed.
     *
     * @param wsFrameWork web service framework which will be used to deploy the service (e.g. "axis2", "cxf")
     * @param component The component owning the interfaces that will be deployed as web services.
     * @param url  Web server url  where to deploy the service - typically "http://localhost:8080"
     * @param componentName Name of the component
     * @throws ProActiveException throws a ProActiveException if the web service framework is not authorized
     */
    public static void exposeComponentAsWebService(String wsFrameWork, Component component, String url,
            String componentName) throws ProActiveException {
        if (WSConstants.AXIS2_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.axis2.WebServices.exposeComponentAsWebService(
                    component, url, componentName);
        } else if (WSConstants.CXF_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.cxf.WebServices.exposeComponentAsWebService(
                    component, url, componentName);
        } else {
            throw new ProActiveException("Unknown web service framework identifier: " + wsFrameWork);
        }
    }

    /**
     * Undeploy all the client interfaces of a component deployed on a web server. With CXF, this method
     * can only be used if you have previously deployed all the client interfaces of the component.
     * Otherwise, it will raise an exception trying to undeploy a client interface which has not been
     * deployed before.
     *
     * @param wsFrameWork web service framework which will be used to deploy the service (e.g. "axis2", "cxf")
     * @param component  The component owning the services interfaces
     * @param url The url of the web server
     * @param componentName The name of the component
     * @throws ProActiveException throws a ProActiveException if the web service framework is not authorized
     */
    public static void unExposeComponentAsWebService(String wsFrameWork, Component component, String url,
            String componentName) throws ProActiveException {
        if (WSConstants.AXIS2_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.axis2.WebServices.unExposeComponentAsWebService(
                    component, url, componentName);
        } else if (WSConstants.CXF_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.cxf.WebServices.unExposeComponentAsWebService(
                    component, url, componentName);
        } else {
            throw new ProActiveException("Unknown web service framework identifier: " + wsFrameWork);
        }
    }

    /**
     * Undeploy specified interfaces of a component deployed on a web server
     *
     * @param wsFrameWork web service framework which will be used to deploy the service (e.g. "axis2", "cxf")
     * @param url The url of the web server
     * @param componentName The name of the component
     * @param interfaceNames Interfaces to be undeployed
     * @throws ProActiveException throws a ProActiveException if the web service framework is not authorized
     */
    public static void unExposeComponentAsWebService(String wsFrameWork, String url, String componentName,
            String[] interfaceNames) throws ProActiveException {
        if (WSConstants.AXIS2_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.axis2.WebServices.unExposeComponentAsWebService(
                    url, componentName, interfaceNames);
        } else if (WSConstants.CXF_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            org.objectweb.proactive.extensions.webservices.cxf.WebServices.unExposeComponentAsWebService(url,
                    componentName, interfaceNames);
        } else {
            throw new ProActiveException("Unknown web service framework identifier: " + wsFrameWork);
        }
    }

    /**
     * The following methods have been written to keep compatibility with the previous version
     * of web service exposition. They use by default the axis2 framework but it is possible to
     * easily switch form one framework to an other using the <code>setDefaultFrameWork(string wsFrameWork)</code>
     * setter.
     */

    /**
     * DEFAULT_FRAMEWORK_IDENTIFIER getter
     *
     * @return WebServices.DEFAULT_FRAMEWORK_IDENTIFIER
     *
     */
    public static String getDefaultFrameWork() {
        return WebServices.DEFAULT_FRAMEWORK_IDENTIFIER;
    }

    /**
     * DEFAULT_FRAMEWORK_IDENTIFIER getter
     *
     * @param wsFrameWork
     * @throws ProActiveException if wsFrameWork is not an allowed framework identifier
     */
    public static void setDefaultFrameWork(String wsFrameWork) throws ProActiveException {
        if (WSConstants.AXIS2_FRAMEWORK_IDENTIFIER.equals(wsFrameWork) ||
            WSConstants.CXF_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            WebServices.DEFAULT_FRAMEWORK_IDENTIFIER = wsFrameWork;
        } else {
            throw new ProActiveException("Unknown web service framework identifier: " + wsFrameWork);
        }
    }

    /**
     * Expose an active object as a web service
     * @param o The object to expose as a web service
     * @param url The url of the host where the object will be deployed  (typically http://localhost:8080)
     * @param urn The name of the object
     * @param methods The methods that will be exposed as web services functionnalities
     */
    public static void exposeAsWebService(Object o, String url, String urn, String[] methods) {
        try {
            exposeAsWebService(getDefaultFrameWork(), o, url, urn, methods);
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the service on a web server
     * @param urn The name of the object
     * @param url The url of the web server
     */
    public static void unExposeAsWebService(String urn, String url) {
        try {
            unExposeAsWebService(getDefaultFrameWork(), url, urn);
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
    }

    /**
     * Expose a component as webservice. Each server and controller
     * interface of the component will be accessible by  the urn
     * [componentName]_[interfaceName]in order to identify the component an
     * interface belongs to.
     * All the interfaces public methods will be exposed.
     *
     * @param componentName The name of the component
     * @param url  The web server url  where to deploy the service - typically "http://localhost:8080"
     * @param component The component owning the interfaces that will be deployed as web services.
     */
    public static void exposeComponentAsWebService(Component component, String url, String componentName) {
        try {
            exposeComponentAsWebService(getDefaultFrameWork(), component, url, componentName);
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
    }

    /**
     * Undeploy component interfaces on a web server
     * @param componentName The name of the component
     * @param url The url of the web server
     * @param component  The component owning the services interfaces
     */
    public static void unExposeComponentAsWebService(String componentName, String url, Component component) {
        try {
            unExposeComponentAsWebService(getDefaultFrameWork(), component, url, componentName);
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
    }
}
