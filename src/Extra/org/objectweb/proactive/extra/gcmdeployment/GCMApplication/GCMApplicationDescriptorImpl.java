package org.objectweb.proactive.extra.gcmdeployment.GCMApplication;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.CommandBuilder;
import org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.GCMDeploymentDescriptor;
import org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.GCMDeploymentDescriptorImpl;
import org.objectweb.proactive.extra.gcmdeployment.VirtualNode;


public class GCMApplicationDescriptorImpl implements GCMApplicationDescriptor {

    /** The descriptor file */
    private File gadFile = null;

    /** A parser dedicated to this GCM Application descriptor */
    private GCMApplicationParser gadParser = null;

    /** All the Virtual Nodes defined in this application */
    private Map<String, VirtualNode> virtualNodes = null;

    public GCMApplicationDescriptorImpl(String filename)
        throws IllegalArgumentException {
        this(new File(filename));
    }

    public GCMApplicationDescriptorImpl(File file)
        throws IllegalArgumentException {
        gadFile = checkDescriptorFileExist(file);
        try {
            gadParser = new GCMApplicationParserImpl(gadFile);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        // 1. Build ResourceProviderParams
        Set<GCMDeploymentDescriptorParams> gddps;
        gddps = gadParser.getResourceProviders();

        // 2. Parse "application" tag by using a custom Parser
        virtualNodes = gadParser.getVirtualNodes();
        CommandBuilder commandBuilder = gadParser.getCommandBuilder();

        // 3. Call GCMDParser for each GCM Deployment Descriptor
        Set<GCMDeploymentDescriptor> gdds = new HashSet<GCMDeploymentDescriptor>();
        for (GCMDeploymentDescriptorParams gddp : gddps) {
            gdds.add(new GCMDeploymentDescriptorImpl(gddp.getGCMDescriptor(),
                    gddp.getFtBlocks()));
        }

        // 4. Start Resource Providers
        gdds = selectGCMD(virtualNodes, gdds);
        for (GCMDeploymentDescriptor gdd : gdds) {
            gdd.start(commandBuilder);
        }

        /**
         * If this GCMA describes a distributed application. The Runtime has
         * been started and will populate Virtual Nodes etc. We let the user
         * code, interact with its Middleware.
         *
         * if a "script" is described. The command has been started on each
         * machine/VM/core and we can safely return
         */
    }

    /**
     * Checks that descriptor exist, is a file and is readable
     * @param descriptor The File to be checked
     * @throws IllegalArgumentException If the File is does not exist, is not a file or is not readable
     */
    private static File checkDescriptorFileExist(File descriptor)
        throws IllegalArgumentException {
        if (!descriptor.exists()) {
            throw new IllegalArgumentException(descriptor.getName() +
                " does not exist");
        }
        if (!descriptor.isFile()) {
            throw new IllegalArgumentException(descriptor.getName() +
                " is not a file");
        }
        if (!descriptor.canRead()) {
            throw new IllegalArgumentException(descriptor.getName() +
                " is not readable");
        }

        return descriptor;
    }

    /**
     * Select the GCM Deployment descriptor to be used
     *
     * A Virtual Node is a consumer, and a GCM Deployment Descriptor a producer.
     * We try to fulfill the consumers needs with as few as possible producer.
     *
     * @param vns
     *            Virtual Nodes asking for some resources
     * @param gdds
     *            GCM Deployment Descriptor providing some resources
     * @return A
     */
    static private Set<GCMDeploymentDescriptor> selectGCMD(
        Map<String, VirtualNode> vns, Set<GCMDeploymentDescriptor> gdds) {
        // TODO: Implement this method
        return gdds;
    }

    public VirtualNode getVirtualNode(String vnName)
        throws IllegalArgumentException {
        VirtualNode ret = virtualNodes.get(vnName);
        if (ret == null) {
            throw new IllegalArgumentException("Virtual Node " + vnName +
                " does not exist");
        }
        return ret;
    }

    public Map<String, VirtualNode> getVirtualNodes() {
        return virtualNodes;
    }

    public void kill() {
        // TODO Auto-generated method stub
    }

    @SuppressWarnings("unused")
    static public class TestGCMApplicationDescriptorImpl {
    }
}
