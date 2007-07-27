package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.objectweb.proactive.extra.gcmdeployment.GCMApplication.FileTransferBlock;
import static org.objectweb.proactive.extra.gcmdeployment.GCMDeploymentLoggers.GCMD_LOGGER;
import org.objectweb.proactive.extra.gcmdeployment.process.Bridge;
import org.objectweb.proactive.extra.gcmdeployment.process.CommandBuilder;
import org.objectweb.proactive.extra.gcmdeployment.process.Group;
import org.objectweb.proactive.extra.gcmdeployment.process.HostInfo;


public class GCMDeploymentDescriptorImpl implements GCMDeploymentDescriptor {
    private GCMDeploymentParser parser;
    private GCMDeploymentEnvironment environment;
    private GCMDeploymentResources resources;

    public GCMDeploymentDescriptorImpl(File descriptor,
        Set<FileTransferBlock> ftBlocks) {
        try {
            parser = new GCMDeploymentParserImpl(descriptor);
            environment = parser.getEnvironment();
            resources = parser.getResources();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void start(CommandBuilder commandBuilder) {
        // Start Local JVMs
        startLocal(commandBuilder);

        startGroups(commandBuilder);
        startBridges(commandBuilder);
    }

    private void startLocal(CommandBuilder commandBuilder) {
        HostInfo hostInfo = resources.getHostInfo();
        String command = commandBuilder.buildCommand(hostInfo);

        try {
            GCMD_LOGGER.info("Starting a process on localhost");
            GCMD_LOGGER.debug("command= " + command);
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            GCMD_LOGGER.warn("Process creation failed on localhost", e);
        }
    }

    private void startGroups(CommandBuilder commandBuilder) {
        List<Group> groups = resources.getGroups();
        for (Group group : groups) {
            GCMD_LOGGER.info("Starting group id=" + group.getId());
            List<String> commands = group.buildCommands(commandBuilder);

            for (String command : commands) {
                try {
                    GCMD_LOGGER.debug("group id=" + group.getId() +
                        " command= " + command);
                    Runtime.getRuntime().exec(command);
                } catch (IOException e) {
                    GCMD_LOGGER.warn("Group part creation failed", e);
                }
            }
        }
    }

    private void startBridges(CommandBuilder commandBuilder) {
        List<Bridge> bridges = resources.getBridges();
        for (Bridge bridge : bridges) {
            List<String> commands = bridge.buildCommands(commandBuilder);

            GCMD_LOGGER.info("Starting bridge id=" + bridge.getId());

            for (String command : commands) {
                try {
                    GCMD_LOGGER.debug("bridge id=" + bridge.getId() +
                        " command= " + command);
                    Runtime.getRuntime().exec(command);
                } catch (IOException e) {
                    GCMD_LOGGER.warn("Bridge part creation failed", e);
                }
            }
        }
    }

    public long getMaxCapacity() {
        // TODO Auto-generated method stub
        return 0;
    }
}
