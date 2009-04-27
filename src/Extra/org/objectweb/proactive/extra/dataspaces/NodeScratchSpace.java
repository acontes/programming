/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.Selectors;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;


/**
 * 
 */

// FIXME leave data or remove all directories?
public class NodeScratchSpace {

    private final BaseScratchSpaceConfiguration baseScratchConfiguration;

    private final Node node;

    private boolean configured = false;

    private FileObject fPartialSpace;

    private class AppScratchSpaceImpl implements ApplicationScratchSpace {
        private final FileObject fSpace;

        private final Map<String, DataSpacesURI> scratches = new HashMap<String, DataSpacesURI>();

        private final SpaceInstanceInfo spaceInstanceInfo;

        private AppScratchSpaceImpl() throws FileSystemException, ConfigurationException {
            final long appId = Utils.getApplicationId(node);
            final String appIdString = Long.toString(appId);
            final String runtimeId = Utils.getRuntimeId(node);
            final String nodeId = Utils.getNodeId(node);

            this.fSpace = createEmptyDirectoryRelative(fPartialSpace, appIdString);

            // or change it and use absolute configuration-created path
            final ScratchSpaceConfiguration scratchSpaceConf = baseScratchConfiguration
                    .createScratchSpaceConfiguration(runtimeId, nodeId, appIdString);
            this.spaceInstanceInfo = new SpaceInstanceInfo(appId, runtimeId, nodeId, scratchSpaceConf);
        }

        public void close() throws FileSystemException {
            fSpace.delete(Selectors.SELECT_ALL);
        }

        public synchronized DataSpacesURI getScratchForAO(String aoid) throws FileSystemException {
            DataSpacesURI uri;

            if (!scratches.containsKey(aoid)) {
                createEmptyDirectoryRelative(fSpace, aoid);
                uri = spaceInstanceInfo.getMountingPoint().withPath(aoid);
                scratches.put(aoid, uri);
            } else
                uri = scratches.get(aoid);

            return uri;
        }

        public SpaceInstanceInfo getSpaceInstanceInfo() {
            return spaceInstanceInfo;
        }

        public DataSpacesURI getSpaceMountingPoint() {
            return spaceInstanceInfo.getMountingPoint();
        }
    }

    public NodeScratchSpace(Node node, BaseScratchSpaceConfiguration conf) {
        this.baseScratchConfiguration = conf;
        this.node = node;
    }

    // TODO check "other stuff" like os permissions in more explicit way?
    /**
     * @throws IllegalStateException
     *             when instance has been already configured
     * @throws FileSystemException
     *             occurred during VFS operation
     * @throws ConfigurationException
     *             when checking FS capabilities
     */
    public synchronized void init(DefaultFileSystemManager fileSystemManager) throws FileSystemException,
            ConfigurationException, IllegalStateException {

        if (configured)
            throw new IllegalStateException("Instance already configured");

        final String nodeId = Utils.getNodeId(node);
        final String runtimeId = Utils.getRuntimeId(node);
        final String localAccessUrl = Utils.getLocalAccessURL(baseScratchConfiguration.getUrl(),
                baseScratchConfiguration.getPath(), Utils.getHostname());
        final String partialSpacePath = Utils.appendSubDirs(localAccessUrl, runtimeId, nodeId);

        fPartialSpace = fileSystemManager.resolveFile(partialSpacePath);
        checkCapabilities(fPartialSpace.getFileSystem());
        fPartialSpace.delete(Selectors.EXCLUDE_SELF);
        fPartialSpace.createFolder();
        configured = true;
    }

    /**
     * @return
     * @throws FileSystemException
     * @throws IllegalStateException
     * @throws ConfigurationException
     *             when provided information is not enough to build a complete space definition
     */
    public synchronized ApplicationScratchSpace initForApplication() throws FileSystemException,
            IllegalStateException, ConfigurationException {

        checkIfConfigured();
        return new AppScratchSpaceImpl();
    }

    /**
     * Removes initialized directory on finalization.
     * 
     * @throws FileSystemException
     * @throws IllegalStateException
     */
    public synchronized void close() throws FileSystemException, IllegalStateException {
        checkIfConfigured();

        final FileObject fRuntime = fPartialSpace.getParent();

        // rm -r node
        fPartialSpace.delete(Selectors.SELECT_ALL);

        // try to remove runtime file
        fRuntime.delete();
    }

    private FileObject createEmptyDirectoryRelative(final FileObject parent, final String path)
            throws FileSystemException {

        FileObject f = parent.resolveFile(path);
        f.delete(Selectors.EXCLUDE_SELF);
        f.createFolder();
        return f;
    }

    private void checkCapabilities(FileSystem fs) throws ConfigurationException {
        final Capability[] expected = new Capability[] { Capability.CREATE, Capability.DELETE,
                Capability.GET_TYPE, Capability.LIST_CHILDREN, Capability.READ_CONTENT,
                Capability.WRITE_CONTENT };

        for (int i = 0; i < expected.length; i++) {
            final Capability capability = expected[i];
            if (!fs.hasCapability(capability))
                throw new ConfigurationException("Specified file system provider does not support " +
                    capability);
        }
    }

    private void checkIfConfigured() throws IllegalStateException {
        if (!configured)
            throw new IllegalStateException("Instance not configured");
    }
}
