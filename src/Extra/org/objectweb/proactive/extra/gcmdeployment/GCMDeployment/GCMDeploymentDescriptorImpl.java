package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.objectweb.proactive.extra.gcmdeployment.GCMApplication.FileTransferBlock;
import org.objectweb.proactive.extra.gcmdeployment.process.CommandBuilder;


public class GCMDeploymentDescriptorImpl implements GCMDeploymentDescriptor {
    private GCMDeploymentParser parser;
    private GCMDeploymentEnvironment environment;
    private GCMDeploymentInfrastructure infrastructure;

    public GCMDeploymentDescriptorImpl(File descriptor,
        Set<FileTransferBlock> ftBlocks) {
        parser = new GCMDeploymentParserImpl(descriptor);
        environment = parser.getEnvironment();
        infrastructure = parser.getInfrastructure();
    }

    public void start(CommandBuilder commandBuilder) {
        // TODO Auto-generated method stub
    }

    public long getMaxCapacity() {
        // TODO Auto-generated method stub
        return 0;
    }
}
