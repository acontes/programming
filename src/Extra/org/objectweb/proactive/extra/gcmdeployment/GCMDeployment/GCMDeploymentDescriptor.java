package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment;

public interface GCMDeploymentDescriptor {

    /**
     * Start the deployment
     *
     * The first step is to perform all required file transfers. Then
     * Use the CommandBuilder to build the command to be launched.
     */
    public void start(CommandBuilder commandBuilder);
}
