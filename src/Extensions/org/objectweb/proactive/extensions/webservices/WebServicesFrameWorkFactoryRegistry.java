package org.objectweb.proactive.extensions.webservices;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.axis2.Axis2WebServicesFactory;
import org.objectweb.proactive.extensions.webservices.cxf.CXFWebServicesFactory;


public class WebServicesFrameWorkFactoryRegistry {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);
    protected static Hashtable<String, Class<? extends WebServicesFactory>> webServicesFactories;

    static {
        // set the default supported protocols
        webServicesFactories = new Hashtable<String, Class<? extends WebServicesFactory>>();
        webServicesFactories.put("axis2", Axis2WebServicesFactory.class);
        webServicesFactories.put("cxf", CXFWebServicesFactory.class);

        Iterator<WebServicesFactorySPI> iter = ServiceRegistry.lookupProviders(WebServicesFactorySPI.class);
        while (iter.hasNext()) {
            WebServicesFactorySPI webServicesFactorySPI = iter.next();

            String frameWorkId = webServicesFactorySPI.getFrameWorkId();
            Class<? extends WebServicesFactory> cl = webServicesFactorySPI.getFactoryClass();

            if (!webServicesFactories.contains(frameWorkId)) {
                logger.debug("Web Service Factory provider <" + frameWorkId + ", " + cl + "> found");
                webServicesFactories.put(frameWorkId, cl);
            }
        }
    }

    public static void put(String framework, Class<? extends WebServicesFactory> factory) {
        webServicesFactories.put(framework, factory);
    }

    public static void remove(String framework) {
        webServicesFactories.remove(framework);
    }

    public static Class<? extends WebServicesFactory> get(String frameWork) {
        return webServicesFactories.get(frameWork);
    }

    public static Enumeration<String> keys() {
        return webServicesFactories.keys();
    }

    public static boolean isValidFrameWork(String frameWork) {
        return webServicesFactories.containsKey(frameWork);
    }
}
