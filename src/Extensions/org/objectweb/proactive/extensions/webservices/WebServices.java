package org.objectweb.proactive.extensions.webservices;

import java.lang.reflect.Method;

import org.objectweb.fractal.api.Component;


public interface WebServices {

    /**
     * Expose an active object as a web service with the methods specified in <code>methods</code>
     *
     * @param o The object to expose as a web service
     * @param url The url of the host where the object will be deployed  (typically http://localhost:8080/)
     * @param urn The name of the object
     * @param methods The methods that will be exposed as web services functionalities
     *                   If null, then all methods will be exposed
     */
    public void exposeAsWebService(Object o, String urn, String[] methods);

    /**
     * Expose an active object as a web service with the methods specified in <code>methods</code>
     *
     * @param o The object to expose as a web service
     * @param url The url of the host where the object will be deployed  (typically http://localhost:8080/)
     * @param urn The name of the object
     * @param methods The methods that will be exposed as web services functionalities
     *                   If null, then all methods will be exposed
     */
    public void exposeAsWebService(Object o, String urn, Method[] methods);

    /**
     * Expose an active object with all its methods as a web service
     *
     * @param o The object to expose as a web service
     * @param url The url of the host where the object will be deployed  (typically http://localhost:8080/)
     * @param urn The name of the object
     */
    public void exposeAsWebService(Object o, String urn);

    /**
     * Undeploy a service
     *
     * @param urn The name of the object
     * @param url The url of the web server
     */
    public void unExposeAsWebService(String urn);

    /**
     * Expose a component as a web service. Each server interface of the component
     * will be accessible by  the urn [componentName]_[interfaceName].
     * Only the interfaces public methods of the specified interfaces in
     * <code>interfaceNames</code> will be exposed.
     *
     * @param component The component owning the interfaces that will be deployed as web services.
     * @param url  Web server url  where to deploy the service - typically "http://localhost:8080"
     * @param componentName Name of the component
     * @param interfaceNames Names of the interfaces we want to deploy.
      *                           If null, then all the interfaces will be deployed
     */
    public void exposeComponentAsWebService(Component component, String componentName, String[] interfaceNames);

    /**
     * Expose a component as web service. Each server interface of the component
     * will be accessible by  the urn [componentName]_[interfaceName].
     * All the interfaces public methods of all interfaces will be exposed.
     *
     * @param component The component owning the interfaces that will be deployed as web services.
     * @param url  Web server url  where to deploy the service - typically "http://localhost:8080"
     * @param componentName Name of the component
     */
    public void exposeComponentAsWebService(Component component, String componentName);

    /**
     * Undeploy all the client interfaces of a component deployed on a web server. With CXF, this method
     * can only be used if you have previously deployed all the client interfaces of the component.
     * Otherwise, it will raise an exception trying to undeploy a client interface which has not been
     * deployed before.
     *
     * @param component  The component owning the services interfaces
     * @param url The url of the web server
     * @param componentName The name of the component
     */
    public void unExposeComponentAsWebService(Component component, String componentName);

    /**
     * Undeploy specified interfaces of a component deployed on a web server
     *
     * @param url The url of the web server
     * @param componentName The name of the component
     * @param interfaceNames Interfaces to be undeployed
     */
    public void unExposeComponentAsWebService(String componentName, String[] interfaceNames);
}
