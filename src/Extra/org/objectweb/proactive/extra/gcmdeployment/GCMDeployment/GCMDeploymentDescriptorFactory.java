package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment;

import org.objectweb.proactive.extra.gcmdeployment.GCMApplication.GCMDeploymentDescriptorParams;


public class GCMDeploymentDescriptorFactory {
    public static GCMDeploymentDescriptor createDescriptor(
        GCMDeploymentDescriptorParams params) {
        return new GCMDeploymentDescriptorImpl(params.getGCMDescriptor(),
            params.getFtBlocks());
    }
}
