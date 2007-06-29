package org.objectweb.proactive.extra.gcmdeployment.GCMApplication;

import java.io.File;
import java.util.Set;


public class ResourceProviderParams {

    /** The GCM Descriptor describing the resources to be used */
    private File GCMDescriptor;

    /** The resource provider ID */
    private String id;

    /** Set of file transfer to be performed by the Resource provider */
    private Set<FileTransferBlock> ftBlocks;

    protected File getGCMDescriptor() {
        return GCMDescriptor;
    }

    protected String getId() {
        return id;
    }

    protected Set<FileTransferBlock> getFtBlocks() {
        return ftBlocks;
    }

    protected void setFtBlocks(Set<FileTransferBlock> ftBlocks) {
        this.ftBlocks = ftBlocks;
    }

    protected void setGCMDescriptor(File descriptor) {
        GCMDescriptor = descriptor;
    }

    protected void setId(String id) {
        this.id = id;
    }
}
