package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment;

import java.io.File;
import java.util.Set;

import org.objectweb.proactive.extra.gcmdeployment.GCMApplication.FileTransferBlock;
import org.objectweb.proactive.extra.gcmdeployment.process.CommandBuilder;


public class GCMDeploymentDescriptorImpl implements GCMDeploymentDescriptor {
    public GCMDeploymentDescriptorImpl(File descriptor,
        Set<FileTransferBlock> ftBlocks) {
    }

    public void start(CommandBuilder commandBuilder) {
        // TODO Auto-generated method stub
    }

    public long getMaxCapacity() {
        // TODO Auto-generated method stub
        return 0;
    }
}
