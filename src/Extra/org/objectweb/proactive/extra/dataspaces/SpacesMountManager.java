/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.impl.VirtualFileSystem;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceNotFoundException;


/**
 * Manages data spaces mountings, connecting VFS and Data Spaces worlds.
 * <p>
 * Manager creates and maintains Apache VFS manager together with VirtualFileSystem instance to
 * provide virtual view of file system for each application. It is able to response to queries for
 * files in data spaces or just data spaces, providing VFS FileObject interface as a result.
 * Returned FileObject instances are applicable for user level code, although some write-limiting
 * may be required for them.
 * <p>
 * To be able to serve requests for files and data spaces, SpaceMountManager must use
 * {@link SpacesDirectory} as a source of information about data spaces, their mounting points and
 * access methods.
 * <p>
 * Manager maintains mountings (in VFS world: "junctions") of data spaces on VFS instance, using
 * lazy on-request strategy in current implementation. Space is mounted only when there is request
 * to provide FileObject for its content. Proper, local or remote access is determined using
 * {@link Utils#getLocalAccessURL(String, String, String)} method. Write-capabilities of returned
 * FileObjects are induced from used protocols' providers - SpacesMountManager does not apply any
 * limitation decorators like {@link BaseWriteLimitingFileObject} on its own.
 * <p>
 * Instances of this class are thread-safe. Also, subsequent requests for the same file using the
 * same manager will result in separate FileObject instances being returned, so there is no
 * concurrency issue related to returned FileObjects. They are not shared. Each instance of manager
 * must be closed using {@link #close()} method when it is not used anymore.
 * 
 * @see SpacesDirectory
 */
public class SpacesMountManager {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES_MOUNT_MANAGER);

    // Apache VFS like these kind of games...
    private static final String SCHEME_VFS_HACKED = DataSpacesURI.SCHEME.substring(0, DataSpacesURI.SCHEME
            .length() - 1);

    private static String getVFSPath(final DataSpacesURI uri) {
        // another nasty Apache VFS hacks
        return uri.toString().substring(DataSpacesURI.SCHEME.length());
    }

    private final DefaultFileSystemManager vfsManager;
    private final SpacesDirectory directory;
    private final VirtualFileSystem vfs;
    private final Set<DataSpacesURI> mountedSpaces = new HashSet<DataSpacesURI>();

    /*
     * These two locks represent two levels of synchronization. In any execution only readLock is
     * acquired or both locks are acquired in constant order (writeLock, then readLock) to avoid
     * deadlocks.
     * 
     * readLock secures access to mountedSpaces and vfs instances. It is the only lock used for read
     * queries for already mounted data spaces. It is used to avoid heavy synchronization on queries
     * for already mounted data spaces.
     * 
     * writeLock is responsible for potentially costly synchronization of queries requiring mounting
     * data spaces or on unmount. Therefore, only one data space can be mounted or unmounted at a
     * time.
     * 
     * Phantom read phenomena can occur for some execution paths acquiring only readLock, for 2
     * times. Now it is not an issue actually, as unmount takes place only within close() method,
     * that is supposed to be called when object is not used anymore.
     */
    private final Object readLock = new Object();
    private final Object writeLock = new Object();

    /**
     * Creates SpaceMountManager instance, that must be finally closed through {@link #close()}
     * method.
     * 
     * @param directory
     *            data spaces directory to use for serving requests
     * @throws FileSystemException
     *             when VFS configuration fails
     */
    public SpacesMountManager(SpacesDirectory directory) throws FileSystemException {
        logger.debug("Initializing spaces mount manager");
        this.directory = directory;

        try {
            // FIXME: unless VFS-256 is fixed, this manager will always return FileObjects with broken
            // delete(FileSelector) method. Anyway, it is rather better to do it this way, than returning
            // shared FileObjects with broken concurrency 
            this.vfsManager = VFSFactory.createDefaultFileSystemManager(false);
        } catch (FileSystemException x) {
            logger.error("Could not create and configure VFS manager", x);
            throw x;
        }

        try {
            final FileObject vfsObject = vfsManager.createVirtualFileSystem(SCHEME_VFS_HACKED);
            this.vfs = (VirtualFileSystem) vfsObject.getFileSystem();
        } catch (FileSystemException x) {
            ProActiveLogger.logImpossibleException(logger, x);
            vfsManager.close();
            throw new RuntimeException(x);
        }
        logger.info("Mount manager initialized, VFS instance created");
    }

    /**
     * Resolves query for concrete URI within Data Spaces virtual tree, resulting in file-level
     * access to this place.
     * <p>
     * If query URI is suitable for having path, then returned FileObject can be safely used to
     * access data in that data spaces, i.e. it conforms to all Data Spaces guarantees regarding
     * that access.
     * <p>
     * If query URI is not suitable for having path (for example, refers only to application id),
     * then returned FileObject does not provide reliable access to virtual tree, just to the
     * current state of mountings in that tree. Such queries are not recommended.
     * <p>
     * This call may block for a while, if {@link SpacesDirectory} need to be queried for data space
     * and/or data space may need to be mounted.
     * 
     * @param queryUri
     *            Data Spaces URI to get access to
     * @return VFS FileObject that can be used to access this URI content; returned FileObject is
     *         not opened nor attached in any way; this FileObject instance will never be shared,
     *         i.e. individual instances are returned for subsequent queries (even the same queries)
     * @throws FileSystemException
     *             indicates VFS related exception during access, like mounting problems, I/O errors
     *             etc.
     * @throws SpaceNotFoundException
     *             when space with that query URI does not exists in SpacesDirectory
     */
    public FileObject resolveFile(final DataSpacesURI queryUri) throws FileSystemException,
            SpaceNotFoundException {
        if (logger.isDebugEnabled())
            logger.debug("File access request: " + queryUri);

        if (queryUri.isSpacePartFullyDefined()) {
            // it is about a concrete space, nothing abstract
            final DataSpacesURI spaceURI = queryUri.getSpacePartOnly();
            ensureSpaceIsMounted(spaceURI, null);
        }
        if (!queryUri.isSuitableForHavingPath()) {
            logger.warn("Accessing URI not usuitable for user (not suitable for having path): " + queryUri);
        }
        return resolveFileVFS(queryUri);
    }

    /**
     * Resolve query for URI without space part being fully defined, resulting in file-level access
     * to all data spaces that shares this common prefix.
     * <p>
     * For any URI query, returned set contains all spaces (URIs) that match defined components in
     * queried URI, allowing them to have any values for undefined components.
     * <p>
     * e.g. for <code>vfs:///123/input/</code> query you may get
     * <code>vfs:///123/input/default/</code> and <code>vfs:///123/input/abc/</code> as a result,
     * but not <code>vfs:///123/output/</code>
     * <p>
     * This call may block for a while, as {@link SpacesDirectory} need to be queried for data
     * spaces and/or some data spaces may need to be mounted.
     * 
     * @param queryUri
     *            Data Spaces URI to query for; must be URI without space part being fully defined,
     *            i.e. not pointing to any concrete data space
     * @return map of data spaces URIs that match the query, pointing to VFS FileObjects that can be
     *         used to access their content; returned FileObjects are not opened nor attached in any
     *         way; these FileObject instances will never be shared, i.e. another instances are
     *         returned for subsequent queries (even the same queries)
     * @throws FileSystemException
     *             indicates VFS related exception during access, like mounting problems, I/O errors
     *             etc.
     * @throws IllegalArgumentException
     *             when provided queryUri has space part fully defined
     * @see DataSpacesURI#isSpacePartFullyDefined()
     */
    public Map<DataSpacesURI, FileObject> resolveSpaces(final DataSpacesURI queryUri)
            throws FileSystemException {
        if (logger.isDebugEnabled())
            logger.debug("Spaces access request: " + queryUri);
        final Map<DataSpacesURI, FileObject> result = new HashMap<DataSpacesURI, FileObject>();

        final Set<SpaceInstanceInfo> spaces = directory.lookupMany(queryUri);
        for (final SpaceInstanceInfo space : spaces) {
            final DataSpacesURI spaceUri = space.getMountingPoint();
            try {
                ensureSpaceIsMounted(spaceUri, space);
            } catch (SpaceNotFoundException e) {
                ProActiveLogger.logImpossibleException(logger, e);
                throw new RuntimeException(e);
            }
            final FileObject fo = resolveFileVFS(spaceUri);
            result.put(spaceUri, fo);
        }
        return result;
    }

    /**
     * Closes this manager instance.
     * <p>
     * Closing it indicates unmounting mounted data spaces and closing VirtualFileSystem instance.
     * Any further access to already opened FileObject within these data spaces or any call of this
     * instance may result in undefined behavior for caller.
     * <p>
     * VFS manager instance nor SpacesDirectory instance provided at constructor are not closed in
     * any way.
     * <p>
     * Subsequent calls to these method may result in undefined behavior.
     */
    public void close() {
        logger.debug("Closing mount manager");
        synchronized (writeLock) {
            synchronized (readLock) {
                for (final DataSpacesURI spaceUri : new ArrayList<DataSpacesURI>(mountedSpaces)) {
                    try {
                        unmountSpace(spaceUri);
                    } catch (FileSystemException e) {
                        final String message = String.format("Could not properly unmount %s (ignoring)",
                                spaceUri);
                        ProActiveLogger.logEatedException(logger, message, e);
                    }
                }
                vfs.close();
                vfsManager.close();
            }
        }
        logger.info("Mount manager closed");
    }

    private void ensureSpaceIsMounted(final DataSpacesURI spaceURI, SpaceInstanceInfo info)
            throws SpaceNotFoundException, FileSystemException {
        final boolean mounted;
        synchronized (readLock) {
            mounted = mountedSpaces.contains(spaceURI);
        }

        if (!mounted) {
            if (info == null) {
                info = directory.lookupOne(spaceURI);
            }
            if (info == null) {
                logger.warn("Could not find data space in spaces directory: " + spaceURI);
                throw new SpaceNotFoundException(
                    "Requested data space is not registered in spaces directory.");
            }

            synchronized (writeLock) {
                // kind of double-checked lock ->
                // check once more within writeLock
                synchronized (readLock) {
                    if (mountedSpaces.contains(spaceURI))
                        return;
                }
                mountSpace(info);
            }
        }
    }

    /*
     * Assumed to be called within writeLock
     * 
     * TODO: support concurrent mounting of more than one data space at a time if needed (requires a
     * little bit more complex synchronization)
     */
    private void mountSpace(final SpaceInstanceInfo spaceInfo) throws FileSystemException {
        final DataSpacesURI mountingPoint = spaceInfo.getMountingPoint();
        final String accessUrl = Utils.getLocalAccessURL(spaceInfo.getUrl(), spaceInfo.getPath(), spaceInfo
                .getHostname());
        final FileObject mountedRoot;
        try {
            mountedRoot = vfsManager.resolveFile(accessUrl);
        } catch (FileSystemException x) {
            logger.warn(String.format("Could not access URL %s to mount %s", accessUrl, mountingPoint));
            throw x;
        }

        synchronized (readLock) {
            try {
                vfs.addJunction(getVFSPath(mountingPoint), mountedRoot);
            } catch (FileSystemException x) {
                logger.warn(String.format("Could not mount already accessed URL %s as %s", accessUrl,
                        mountingPoint));
                vfsManager.closeFileSystem(mountedRoot.getFileSystem());
                throw x;
            }

            if (!mountedSpaces.add(mountingPoint)) {
                logger.error("Internal error - overmounting already mounted space: " + mountingPoint);
                throw new RuntimeException("Unexpected internal error - overmounting already mounted space");
            }
        }
        logger.info(String.format("Mounted space: %s (access URL: %s)", mountingPoint, accessUrl));
    }

    /*
     * Assumed to be called within writeLock and readLock
     */
    private void unmountSpace(final DataSpacesURI spaceUri) throws FileSystemException {
        mountedSpaces.remove(spaceUri);
        try {
            vfs.removeJunction(getVFSPath(spaceUri));
        } finally {
            final FileObject spaceRoot = resolveFileVFS(spaceUri);
            try {
                // we may not need to close FileObject, but with VFS you never know...
                spaceRoot.close();
            } catch (FileSystemException x) {
                ProActiveLogger.logEatedException(logger, String.format(
                        "Could not close data space %s root file object", spaceUri), x);
            }
            final FileSystem spaceFileSystem = spaceRoot.getFileSystem();
            vfsManager.closeFileSystem(spaceFileSystem);
        }
        logger.info("Unmounted space: " + spaceUri);
    }

    private FileObject resolveFileVFS(final DataSpacesURI uri) throws FileSystemException {
        synchronized (readLock) {
            try {
                return vfsManager.resolveFile(vfs.getRoot(), getVFSPath(uri));
            } catch (FileSystemException x) {
                logger.warn("Could not access file that should exist (be mounted) in VFS: " + uri);
                throw x;
            }
        }
    }
}
