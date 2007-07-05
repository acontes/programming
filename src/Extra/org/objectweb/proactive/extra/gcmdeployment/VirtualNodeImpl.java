package org.objectweb.proactive.extra.gcmdeployment;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.extra.gcmdeployment.GCMApplication.FileTransferBlock;
import org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.GCMDeploymentDescriptor;


public class VirtualNodeImpl implements VirtualNodeInternal {
    private long requiredCapacity;
    private String id;
    private List<GCMDeploymentDescriptor> providers;

    /** All File Transfer Block associated to this VN */
    private List<FileTransferBlock> fts;

    public VirtualNodeImpl() {
        fts = new ArrayList<FileTransferBlock>();
    }

    public long getRequiredCapacity() {
        return requiredCapacity;
    }

    public void setRequiredCapacity(long requiredCapacity) {
        this.requiredCapacity = requiredCapacity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<GCMDeploymentDescriptor> getProviders() {
        return providers;
    }

    public void setProviders(List<GCMDeploymentDescriptor> providers) {
        this.providers = providers;
    }

    public void addFileTransfertBlock(FileTransferBlock ftb) {
        fts.add(ftb);
    }

    public String getName() {
        return id;
    }
}
