package org.objectweb.proactive.extensions.webservices;

import java.util.HashMap;

import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.extensions.webservices.exceptions.UnknownFrameWorkException;


public abstract class AbstractWebServicesFactory implements WebServicesFactory {

    final protected static HashMap<String, WebServicesFactory> activatedWebServicesFactory;
    final protected HashMap<String, WebServices> activatedWebServices;

    static {
        activatedWebServicesFactory = new HashMap<String, WebServicesFactory>();
    }

    protected AbstractWebServicesFactory() {
        activatedWebServices = new HashMap<String, WebServices>();
    }

    public static WebServicesFactory getWebServicesFactory(String frameWorkId)
            throws UnknownFrameWorkException {
        if (frameWorkId == null) {
            frameWorkId = PAProperties.PA_WEBSERVICES_FRAMEWORK.getValue();
        }

        try {
            WebServicesFactory wsf = activatedWebServicesFactory.get(frameWorkId);
            if (wsf != null) {
                return wsf;
            } else {
                Class<?> wsfClazz = WebServicesFrameWorkFactoryRegistry.get(frameWorkId);

                if (wsfClazz != null) {
                    WebServicesFactory o = (WebServicesFactory) wsfClazz.newInstance();

                    activatedWebServicesFactory.put(frameWorkId, o);

                    return o;
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        throw new UnknownFrameWorkException("There is no WebServicesFactory defined for the framework: " +
            frameWorkId);
    }

    /** Return the default WebServicesFactory
     * 
     * @return return the web service factory associated to the default framework
     * @throws UnknownFrameWorkException if the default communication protocol is not known
     */
    public static WebServicesFactory getDefaultWebServicesFactory() throws UnknownFrameWorkException {
        String protocol = PAProperties.PA_WEBSERVICES_FRAMEWORK.getValue();
        return getWebServicesFactory(protocol);
    }

    public static String getLocalUrl() {
        return "http://localhost:" + PAProperties.PA_XMLHTTP_PORT.getValue() + "/";
    }

    public abstract WebServices newWebServices(String url);

    public final WebServices getWebServices(String url) {
        WebServices ws = activatedWebServices.get(url);
        if (ws != null) {
            System.out.println("already got");
            return ws;
        } else {
            System.out.println("never got");
            ws = newWebServices(url);
            activatedWebServices.put(url, ws);
            return ws;
        }
    }
}
