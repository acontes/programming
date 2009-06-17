/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces.vfs;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.Selectors;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.dataspaces.Utils;
import org.objectweb.proactive.extra.dataspaces.core.ApplicationScratchSpace;
import org.objectweb.proactive.extra.dataspaces.core.BaseScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.core.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.core.NodeScratchSpace;
import org.objectweb.proactive.extra.dataspaces.core.ScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.core.SpaceInstanceInfo;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException;


/**
 * Implementation of {@link NodeScratchSpace} using Apache Commons VFS library.
 */
public class VFSNodeScratchSpaceImpl implements NodeScratchSpace {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES_CONFIGURATOR);

    private BaseScratchSpaceConfiguration baseScratchConfiguration;

    private Node node;

    private boolean configured;

    private FileObject partialSpaceFile;

    private DefaultFileSystemManager fileSystemManager;

    /**
     * Inner class to implement {@link ApplicationScratchSpace} interface.
     */
    private class AppScratchSpaceImpl implements ApplicationScratchSpace {
        private final FileObject spaceFile;

        private final Map<String, DataSpacesURI> scratches = new HashMap<String, DataSpacesURI>();

        private final SpaceInstanceInfo spaceInstanceInfo;

        private AppScratchSpaceImpl() throws FileSystemException, ConfigurationException {
            logger.debug("Initializing application node scratch space");
            final long appId = Utils.getApplicationId(node);
            final String appIdString = Long.toString(appId);
            final String runtimeId = Utils.getRuntimeId(node);
            final String nodeId = Utils.getNodeId(node);

            try {
                this.spaceFile = createEmptyDirectoryRelative(partialSpaceFile, appIdString);
                spaceFile.close();
            } catch (org.apache.commons.vfs.FileSystemException x) {
                logger.error("Could not create directory for application scratch space", x);
                throw new FileSystemException(x);
            }
            try {
                final ScratchSpaceConfiguration scratchSpaceConf = baseScratchConfiguration
                        .createScratchSpaceConfiguration(runtimeId, nodeId, appIdString);
                this.spaceInstanceInfo = new SpaceInstanceInfo(appId, runtimeId, nodeId, scratchSpaceConf);
            } catch (ConfigurationException x) {
                logger.error("Invalid scratch space configuration, removing created files", x);
                close();
                throw x;
            }
            logger.info("Initialized application node scratch space");
        }

        public void close() throws FileSystemException {
            logger.debug("Closing application scratch space");
            try {
                try {
                    final int filesNumber = spaceFile.delete(Selectors.SELECT_ALL);
                    logger.debug("Deleted " + filesNumber + " files in scratch application directory");
                } finally {
                    // just a hint
                    spaceFile.close();
                }
            } catch (org.apache.commons.vfs.FileSystemException e) {
                throw new FileSystemException(e);
            }
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
                    // TODO we could use VFSSpacesMountManagerImpl for that if it returned FileObject,
                    // so we can avoid unnecessarily double mounting resulting in opening, 
                    // closing and opening again the same file
                    createEmptyDirectoryRelative(spaceFile, aoid).close();
                    // just a hint
                    spaceFile.close();
                } catch (org.apache.commons.vfs.FileSystemException x) {
                    logger.error(String.format(
                            "Could not create directory for Active Object (id: %s) scratch", aoid), x);
                    throw new FileSystemException(x);
                }
                uri = spaceInstanceInfo.getMountingPoint().withActiveObjectId(aoid);
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

    public synchronized void init(Node node, BaseScratchSpaceConfiguration conf) throws FileSystemException,
            ConfigurationException, IllegalStateException {
        logger.debug("Initializing node scratch space");
        if (configured) {
            logger.error("Attempting to configure already configured node scratch space");
            throw new IllegalStateException("Instance already configured");
        }

        this.node = node;
        this.baseScratchConfiguration = conf;

        try {
            fileSystemManager = VFSFactory.createDefaultFileSystemManager();
        } catch (org.apache.commons.vfs.FileSystemException x) {
            logger.error("Could not create and configure VFS manager", x);
            throw new FileSystemException(x);
        }

        try {
            final String nodeId = Utils.getNodeId(node);
            final String runtimeId = Utils.getRuntimeId(node);
            final String localAccessUrl = Utils.getLocalAccessURL(baseScratchConfiguration.getUrl(),
                    baseScratchConfiguration.getPath(), Utils.getHostname());
            final String partialSpacePath = Utils.appendSubDirs(localAccessUrl, runtimeId, nodeId);

            logger.debug("Accessing scratch space location: " + partialSpacePath);
            try {
                partialSpaceFile = fileSystemManager.resolveFile(partialSpacePath);
                checkCapabilities(partialSpaceFile.getFileSystem());
                partialSpaceFile.delete(Selectors.EXCLUDE_SELF);
                partialSpaceFile.createFolder();
                if (!partialSpaceFile.isWriteable()) {
                    throw new org.apache.commons.vfs.FileSystemException(
                        "Created directory is unexpectedly not writable");
                }
                // just a hint
                partialSpaceFile.close();
            } catch (org.apache.commons.vfs.FileSystemException x) {
                logger.error("Could not initialize scratch space at: " + partialSpacePath);
                throw new FileSystemException(x);
            }
            configured = true;
            logger.info("Initialized node scratch space at: " + partialSpacePath);
        } finally {
            if (!configured)
                fileSystemManager.close();
        }
    }

    public synchronized ApplicationScratchSpace initForApplication() throws FileSystemException,
            IllegalStateException, ConfigurationException {

        checkIfConfigured();
        return new AppScratchSpaceImpl();
    }

    public synchronized void close() throws IllegalStateException {
        logger.debug("Closing node scratch space");
        checkIfConfigured();

        try {
            final FileObject fRuntime = partialSpaceFile.getParent();

            // rm -r node
            partialSpaceFile.delete(Selectors.SELECT_ALL);

            // try to remove runtime file
            // IMPORTANT FIXME: it seems that despite of VFS FileObject documentation, 
            // looking at AbstractFileObject docs suggests that it does not implement this 
            // delete-if-empty behavior! at least, it appears to be not atomic (and probably may be never atomic
            // as some protocols may not support this kind of atomic operation?)
            // refreshing file before deleting may minimize the risk of delete-when-non-empty behavior 
            fRuntime.refresh();
            try {
                final boolean deleted = fRuntime.delete();
                if (deleted)
                    logger.debug("Scratch directory for whole runtime was deleted (considered as empty)");
                else
                    logger
                            .debug("Scratch directory for whole runtime was not deleted (not considered as empty)");
            } catch (org.apache.commons.vfs.FileSystemException x) {
                logger.debug(
                        "Could not delete scratch directory for whole runtime - perhaps it was not empty", x);
            }

            // it is probably not needed to close files if manager is closed, but with VFS you never know...
            fRuntime.close();
            partialSpaceFile.close();
        } catch (org.apache.commons.vfs.FileSystemException x) {
            ProActiveLogger.logEatedException(logger, "Could not close correctly node scratch space", x);
        } finally {
            this.fileSystemManager.close();
        }
        logger.info("Closed node scratch space");
    }

    private FileObject createEmptyDirectoryRelative(final FileObject parent, final String path)
            throws org.apache.commons.vfs.FileSystemException {

        FileObject f = parent.resolveFile(path);
        f.delete(Selectors.EXCLUDE_SELF);
        f.createFolder();
        return f;
    }

    private void checkCapabilities(FileSystem fs) throws ConfigurationException {
        // let's have at least those capabilities that scratch space does
        // final Capability[] expected = PADataSpaces.getCapabilitiesForSpaceType(SpaceType.SCRATCH);

        // but you never know what is there.. therefore:
        final Capability[] expected = new Capability[] { Capability.CREATE, Capability.DELETE,
                Capability.GET_TYPE, Capability.LIST_CHILDREN, Capability.READ_CONTENT,
                Capability.WRITE_CONTENT };

        for (int i = 0; i < expected.length; i++) {
            final Capability capability = expected[i];

            if (fs.hasCapability(capability))
                continue;

            logger.error("Scratch file system does not support capability: " + capability);
            throw new ConfigurationException("Scratch file system does not support capability: " + capability);
        }
    }

    private void checkIfConfigured() throws IllegalStateException {
        if (!configured) {
            logger.error("Attempting to perform operation on not configured node scratch space");
            throw new IllegalStateException("Instance not configured");
        }
    }
}
