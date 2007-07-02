package org.objectweb.proactive.extra.gcmdeployment;

import java.util.List;

import org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.GCMDeploymentDescriptor;


public class VirtualNodeImpl implements VirtualNodeInternal {
    private long requiredCapacity;
    private String id;
    private List<GCMDeploymentDescriptor> providers;

    protected long getRequiredCapacity() {
        return requiredCapacity;
    }

    protected void setRequiredCapacity(long requiredCapacity) {
        this.requiredCapacity = requiredCapacity;
    }

    protected String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    protected List<GCMDeploymentDescriptor> getProviders() {
        return providers;
    }

    protected void setProviders(List<GCMDeploymentDescriptor> providers) {
        this.providers = providers;
    }
}
