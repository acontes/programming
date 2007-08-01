package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.objectweb.proactive.extra.gcmdeployment.GCMApplication.FileTransferBlock;
import static org.objectweb.proactive.extra.gcmdeployment.GCMDeploymentLoggers.GCMD_LOGGER;
import org.objectweb.proactive.extra.gcmdeployment.process.Bridge;
import org.objectweb.proactive.extra.gcmdeployment.process.CommandBuilder;
import org.objectweb.proactive.extra.gcmdeployment.process.Group;
import org.objectweb.proactive.extra.gcmdeployment.process.HostInfo;
import org.objectweb.proactive.extra.gcmdeployment.process.bridge.BridgeDummy;
import org.objectweb.proactive.extra.gcmdeployment.process.commandbuilder.CommandBuilderDummy;
import org.objectweb.proactive.extra.gcmdeployment.process.group.GroupDummy;
import org.objectweb.proactive.extra.gcmdeployment.process.hostinfo.HostInfoImpl;


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

    /**
     * This constructor is only for unit Testing. <strong>Do not use it</strong>
     */
    private GCMDeploymentDescriptorImpl() {
    }

    public void start(CommandBuilder commandBuilder) {
        // Start Local JVMs
        startLocal(commandBuilder);

        startGroups(commandBuilder);
        startBridges(commandBuilder);
    }

    private void startLocal(CommandBuilder commandBuilder) {
        HostInfo hostInfo = resources.getHostInfo();
        if (hostInfo != null) {
            // Something needs to be started on this host
            String command = commandBuilder.buildCommand(hostInfo);

            GCMD_LOGGER.info("Starting a process on localhost");
            GCMD_LOGGER.debug("command= " + command);
            execute(command);
        }
    }

    private void startGroups(CommandBuilder commandBuilder) {
        List<Group> groups = resources.getGroups();
        for (Group group : groups) {
            GCMD_LOGGER.info("Starting group id=" + group.getId());
            List<String> commands = group.buildCommands(commandBuilder);

            for (String command : commands) {
                GCMD_LOGGER.debug("group id=" + group.getId() + " command= " +
                    command);

                execute(command);
            }
        }
    }

    private void startBridges(CommandBuilder commandBuilder) {
        List<Bridge> bridges = resources.getBridges();
        for (Bridge bridge : bridges) {
            List<String> commands = bridge.buildCommands(commandBuilder);

            GCMD_LOGGER.info("Starting bridge id=" + bridge.getId() +
                " #commands=" + commands.size());

            for (String command : commands) {
                GCMD_LOGGER.debug("bridge id=" + bridge.getId() + " command= " +
                    command);
                execute(command);
            }
        }
    }

    public long getMaxCapacity() {
        // TODO Auto-generated method stub
        return 0;
    }

    static private boolean execute(String command) {
        try {
            System.out.println("command=" + command);
            Process p = Runtime.getRuntime().exec(new String[] { command });
            String line;

            BufferedReader br;

            br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = br.readLine()) != null) {
                System.err.println(line);
            }

            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            GCMD_LOGGER.warn("Cannot execute: " + command, e);
            return false;
        }

        return true;
    }

    @SuppressWarnings("unused")
    static public class UnitTestGCMDeploymentDescriptorImpl {
        @Test
        public void test() {
            CommandBuilder commandBuilder = new CommandBuilderDummy("sleep 60");

            HostInfo hostInfo = new HostInfoImpl();

            List<String> groupCommands = new ArrayList<String>();
            groupCommands.add("ssh duff");
            groupCommands.add("ssh naruto");
            GroupDummy group = new GroupDummy(groupCommands);
            group.setHostInfo(hostInfo);

            BridgeDummy bridge = new BridgeDummy("ssh cheypa");
            bridge.setId("dummyBridge");
            bridge.addGroup(group);

            GCMDeploymentResources resources = new GCMDeploymentResources();
            // resources.addBridge(bridge);
            resources.addGroup(group);

            GCMDeploymentDescriptorImpl gcmd = new GCMDeploymentDescriptorImpl();
            gcmd.resources = resources;

            GCMD_LOGGER.warn("Starting...");
            gcmd.start(commandBuilder);
        }

        public static void main(String[] args) {
            UnitTestGCMDeploymentDescriptorImpl test = new UnitTestGCMDeploymentDescriptorImpl();
            test.test();
        }
    }
}
