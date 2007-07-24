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
        try {
            parser = new GCMDeploymentParserImpl(descriptor);
            environment = parser.getEnvironment();
            infrastructure = parser.getInfrastructure();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void start(CommandBuilder commandBuilder) {
        // TODO Auto-generated method stub
    }

    public long getMaxCapacity() {
        // TODO Auto-generated method stub
        return 0;
    }
}
