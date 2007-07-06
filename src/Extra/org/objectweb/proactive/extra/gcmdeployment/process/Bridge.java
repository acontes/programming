package org.objectweb.proactive.extra.gcmdeployment.process;

import org.objectweb.proactive.extra.gcmdeployment.PathElement;


public interface Bridge {

    /**
     * Set environment variables for this cluster
     * @param env environment variables
     */
    public void setEnvironment(String env);

    /**
     * Set the destination host
     * @param hostname destination host as FQDN or IP address
     */
    public void setHostname(String hostname);

    /**
     * Username to be used on the destination host
     * @param username an username
     */
    public void setUsername(String username);

    /**
     * Set the command path to override the default one
     * @param commandPath path to the command
     */
    public void setCommandPath(PathElement commandPath);
}
