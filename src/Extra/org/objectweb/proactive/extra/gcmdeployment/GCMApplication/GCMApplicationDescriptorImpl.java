package org.objectweb.proactive.extra.gcmdeployment.GCMApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
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
    Map<String, VirtualNode> virtualNodes = null;

    /** A CommandBuilder to be passed to Resource Providers */
    CommandBuilder commandBuilder = null;
    /** Parameters to be passed to start the Resource Providers */
    Set<ResourceProviderParams> rpps = null;
    Set<GCMDeploymentDescriptor> gdds = null;

    public GCMApplicationDescriptorImpl(String filename)
        throws IOException, FileNotFoundException {
        this(new File(filename));
    }

    public GCMApplicationDescriptorImpl(File file)
        throws IOException, FileNotFoundException {
        // FIXME Choose how to report errors (ProActive exception or specialized
        // exceptions ?)
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName() + " does not exist");
        }
        if (!file.isFile()) {
            throw new IOException("TODO throw the right exception here");
        }
        if (!file.canRead()) {
            throw new IOException("TODO throw the right exception here");
        }

        gadParser = new GCMApplicationParserImpl(gadFile);

        // TODO 1. Build ResourceProviderParams
        rpps = gadParser.getResourceProviders();

        // TODO 2. Parse "application" tag by using a custom Parser
        virtualNodes = gadParser.getVirtualNodes();
        commandBuilder = gadParser.getCommandBuilder();

        // TODO 3. Call GCMDParser for each GCM Deployment Descriptor
        gdds = new HashSet<GCMDeploymentDescriptor>();
        for (ResourceProviderParams rpp : rpps) {
            gdds.add(new GCMDeploymentDescriptorImpl(rpp.getGCMDescriptor(),
                    rpp.getFtBlocks()));
        }

        // TODO 4. Start Resource Providers
        // TODO 4.1 Choose which one to start
        Set<GCMDeploymentDescriptor> selectedGdds = selectGCMD(virtualNodes,
                gdds);

        for (GCMDeploymentDescriptor gdd : selectedGdds) {
            gdd.start();
        }

        /**
         * If this GCMA describes a distributed application. The Runtime has
         * been started and will populate Virtual Nodes etc. We let the user
         * code, interact with its middleware.
         *
         * if a "script" is described. The command has been started on each
         * machine/VM/core and we can safely return
         */
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

    @SuppressWarnings("unused")
    static public class TestGCMApplicationDescriptorImpl {
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
}
