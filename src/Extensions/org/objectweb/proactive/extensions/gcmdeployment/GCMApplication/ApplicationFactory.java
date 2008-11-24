package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication;

import static org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers.GCMA_LOGGER;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.executable.ApplicationExecutable;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.mpi.ApplicationMPI;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.proactive.ApplicationProActive;


;

public class ApplicationFactory {
    private Map<String, Class<? extends Application>> map;

    public ApplicationFactory() {
        this.map = new HashMap<String, Class<? extends Application>>();

        registerApplication(ApplicationProActive.class);
        registerApplication(ApplicationExecutable.class);
        registerApplication(ApplicationMPI.class);

        // TODO:  Be able to load user provided Application Parser
    }

    /**
     * Returns the ApplicationParser associated to this nodeName
     * 
     * @param name
     *            the name of the node to be parsed
     * 
     * @throws IllegalArgumentException
     *             If no suitable parser is found
     */
    public Application getApplicationParser(String nodeName) throws IllegalArgumentException {
        Application ap = null;

        Class<? extends Application> cl = map.get(nodeName);
        if (cl != null) {
            try {
                ap = cl.newInstance();
            } catch (Exception e) {
                GCMA_LOGGER.error("Unable to create an Application Parser for " + nodeName);
                throw new IllegalArgumentException("Failed to create an application parser for " + nodeName,
                    e);
            }
        } else {
            GCMA_LOGGER.error("Unable to get an Application Parser for " + nodeName);
            throw new IllegalArgumentException("No ApplicationParser registered to parse " + nodeName);
        }

        return ap;
    }

    private void registerApplication(Class<? extends Application> cl) {
        Application ap;
        try {
            // Since interfaces don't allow to define static methods we must instanciate an object
            ap = cl.newInstance();
            map.put(ap.getNodeName(), cl);
        } catch (Exception e) {
            GCMA_LOGGER.warn("Unable to register application parser " + cl.getCanonicalName(), e);
        }

    }
}
