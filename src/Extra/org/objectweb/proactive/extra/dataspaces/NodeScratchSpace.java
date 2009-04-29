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
import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;


// FIXME leave data or remove all directories?
// TODO complete logs

/**
 * Manages scratch data spaces directories and supports DS scratch access logic.
 */
public class NodeScratchSpace {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES_CONFIGURATOR);

    private final BaseScratchSpaceConfiguration baseScratchConfiguration;

    private final Node node;

    private boolean configured;

    private FileObject partialSpaceFile;

    /**
     * Inner class to implement {@link ApplicationScratchSpace} interface.
     */
    private class AppScratchSpaceImpl implements ApplicationScratchSpace {
        private final FileObject spaceFile;

        private final Map<String, DataSpacesURI> scratches = new HashMap<String, DataSpacesURI>();

        private final SpaceInstanceInfo spaceInstanceInfo;

        private AppScratchSpaceImpl() throws FileSystemException, ConfigurationException {
            final long appId = Utils.getApplicationId(node);
            final String appIdString = Long.toString(appId);
            final String runtimeId = Utils.getRuntimeId(node);
            final String nodeId = Utils.getNodeId(node);

            try {
                this.spaceFile = createEmptyDirectoryRelative(partialSpaceFile, appIdString);
            } catch (FileSystemException x) {
                logger.error("Could not create directory for application scratch space", x);
                throw x;
            }

            // TODO: what is following comment about?
            // or change it and use absolute configuration-created path
            try {
                final ScratchSpaceConfiguration scratchSpaceConf = baseScratchConfiguration
                        .createScratchSpaceConfiguration(runtimeId, nodeId, appIdString);
                this.spaceInstanceInfo = new SpaceInstanceInfo(appId, runtimeId, nodeId, scratchSpaceConf);
            } catch (ConfigurationException x) {
                logger.error("Invalid scratch space configuration", x);
                throw x;
            }
        }

        public void close() throws FileSystemException {
            logger.debug("Closing application scratch space");
            spaceFile.delete(Selectors.SELECT_ALL);
            logger.info("Closed application scratch space");
        }

        public synchronized DataSpacesURI getScratchForAO(Body body) throws FileSystemException {
            // TODO performance can be improved using more fine-grained synchronization
            final String aoid = Utils.getActiveObjectId(body);
            if (logger.isDebugEnabled())
                logger.debug("Request for scratch for Active Object with id: " + aoid);

            final DataSpacesURI uri;
            if (!scratches.containsKey(aoid)) {
                try {
                    createEmptyDirectoryRelative(spaceFile, aoid);
                } catch (FileSystemException x) {
                    logger.error(String.format(
                            "Could not create directory for Active Object (id: %s) scratch", aoid));
                    throw x;
                }
                uri = spaceInstanceInfo.getMountingPoint().withPath(aoid);
                logger.info(String
                        .format("Created scratch for Active Object with id: %s, URI: %s", aoid, uri));
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

    /**
     * Provided configuration should contain a remote access already defined (e.g. after starting up
     * the PAProvider. It is not checked here explicitly, but will be thrown as an exception during
     * {@link NodeScratchSpace#initForApplication()} method call.
     * 
     * @param node
     * @param conf
     */
    public NodeScratchSpace(Node node, BaseScratchSpaceConfiguration conf) {
        this.baseScratchConfiguration = conf;
        this.node = node;
    }

    // TODO check "other stuff" like os permissions in more explicit way?
    /**
     * Initializes instance (and all related configuration objects) on a node and performs file
     * system configuration and accessing tests.
     * <p>
     * Local access will be used (if is defined) for accessing scratch data space. Any existing
     * files for this scratch Data Space will be silently deleted.
     * <p>
     * Can be called only once for each instance.
     * 
     * @param fileSystemManager
     * @throws IllegalStateException
     *             when instance has been already configured
     * @throws FileSystemException
     *             occurred during VFS operation
     * @throws ConfigurationException
     *             when checking FS capabilities
     * @see {@link Utils#getLocalAccessURL(String, String, String)}
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

        partialSpaceFile = fileSystemManager.resolveFile(partialSpacePath);
        checkCapabilities(partialSpaceFile.getFileSystem());
        partialSpaceFile.delete(Selectors.EXCLUDE_SELF);
        partialSpaceFile.createFolder();
        configured = true;
    }

    /**
     * Initializes scratch data space for an application that is running on a Node for which
     * NodeScratchSpace has been configured and initialized by
     * {@link #init(DefaultFileSystemManager)}.
     * <p>
     * Local access will be used (if is defined) for accessing scratch data space. Any existing
     * files for this scratch Data Space will be silently deleted. Subsequent calls for the same
     * 
     * @return instance for creating and accessing scratch of concrete AO
     * @throws FileSystemException
     * @throws IllegalStateException
     * @throws ConfigurationException
     *             when provided information is not enough to build a complete space definition
     *             (e.g. lack of remote access defined)
     * @see {@link Utils#getLocalAccessURL(String, String, String)}
     */
    public synchronized ApplicationScratchSpace initForApplication() throws FileSystemException,
            IllegalStateException, ConfigurationException {

        checkIfConfigured();
        return new AppScratchSpaceImpl();
    }

    /**
     * Removes initialized node-related files on finalization. If no other scratch data space
     * remains within this runtime, runtime-related files are also removed.
     * 
     * @throws FileSystemException
     * @throws IllegalStateException
     */
    public synchronized void close() throws FileSystemException, IllegalStateException {
        checkIfConfigured();

        final FileObject fRuntime = partialSpaceFile.getParent();

        // rm -r node
        partialSpaceFile.delete(Selectors.SELECT_ALL);

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
