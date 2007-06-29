package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment;

public interface CommandBuilder {

    /**
     * Build the command to start the application
     * @param hostInfo Host information to customize the command according to this host type
     * @return The command to be used to start the application
     */
    public String buildCommand(HostInfo hostInfo);
}
