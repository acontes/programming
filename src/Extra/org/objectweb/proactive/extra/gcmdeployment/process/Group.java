package org.objectweb.proactive.extra.gcmdeployment.process;

import org.objectweb.proactive.extra.gcmdeployment.PathElement;


public interface Group {

    /**
     * Set environment variables for this cluster
     * @param env environment variables
     */
    public void setEnvironment(String env);

    /**
     * Set the command path to override the default one
     * @param commandPath path to the command
     */
    public void setCommandPath(PathElement commandPath);
}
