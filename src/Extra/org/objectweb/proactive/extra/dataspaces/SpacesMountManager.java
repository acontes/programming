/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.dataspaces.adapter.vfs.VFSFileObjectAdapter;
import org.objectweb.proactive.extra.dataspaces.api.DataSpacesFileObject;
import org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException;
import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceNotFoundException;


// WISH: extract interface
/**
 * Manages data spaces mountings and file accessing, connecting Data Spaces and VFS worlds in this
 * implementation.
 * <p>
 * Manager creates and maintains Apache VFS manager to pose virtual view provider of file system for
 * each application. It is able to response to queries for files in data spaces or just data spaces,
 * providing {@link DataSpacesFileObject} as a result. Returned object is associated with specific
 * active object. It is applicable for user level code with access limited to files with URIs
 * suitable for user path.
 * <p>
 * To be able to serve requests for files and data spaces, SpaceMountManager must use
 * {@link SpacesDirectory} as a source of information about data spaces, their mounting points and
 * access methods.
 * <p>
 * Manager maintains mountings of data spaces in local Map, using lazy on-request strategy in
 * current implementation. Space is mounted only when there is request to provide FileObject for its
 * content. Proper, local or remote access is determined using
 * {@link Utils#getLocalAccessURL(String, String, String)} method. Write-capabilities of returned
 * FileObjects are induced from used protocols' providers, but SpacesMountManager applies also
 * restriction policies for returned FileObjects, to conform with general Data Spaces guarantees, as
 * described and implemented in {@link DataSpacesLimitingFileObject}.
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

    private final DefaultFileSystemManager vfsManager;
    private final SpacesDirectory directory;
    private final Map<DataSpacesURI, FileObject> mountedSpaces = new HashMap<DataSpacesURI, FileObject>();

    // TODO: check if this synchronization is really needed after we removed VirtualFileSystem
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
        } catch (org.apache.commons.vfs.FileSystemException x) {
            logger.error("Could not create and configure VFS manager", x);
            throw new FileSystemException(x);
        }
        logger.info("Mount manager initialized, VFS instance created");
    }

    /**
     * Resolves query for URI within Data Spaces virtual tree, resulting in file-level access to
     * this place. Provided URI should be suitable for user path.
     * <p>
     * This call may block for a while, if {@link SpacesDirectory} need to be queried for data space
     * and/or data space may need to be mounted.
     * 
     * @param queryUri
     *            Data Spaces URI to get access to
     * @param ownerActiveObjectId
     *            Id of active object requesting this file, that will become owner of returned
     *            {@link DataSpacesFileObject} instance. May be <code>null</code>, which corresponds
     *            to anonymous (unimportant) owner.
     * @return {@link DataSpacesFileObject} instance that can be used to access this URI content;
     *         returned DataSpacesFileObject is not opened nor attached in any way; this
     *         DataSpacesFileObject instance will never be shared, i.e. individual instances are
     *         returned for subsequent queries (even the same queries).
     * @throws FileSystemException
     *             indicates VFS related exception during access, like mounting problems, I/O errors
     *             etc.
     * @throws SpaceNotFoundException
     *             when space with that query URI does not exists in SpacesDirectory
     * @throws IllegalArgumentException
     *             when provided queryUri is not suitable for user path
     * @see DataSpacesURI#isSuitableForUserPath()
     */
    public DataSpacesFileObject resolveFile(final DataSpacesURI queryUri, final String ownerActiveObjectId)
            throws FileSystemException, SpaceNotFoundException {

        if (logger.isDebugEnabled())
            logger.debug("File access request: " + queryUri);

        if (!queryUri.isSuitableForUserPath()) {
            logger.error("Requested URI is not suitable for user path");
            throw new IllegalArgumentException("Requested URI is not suitable for user path");
        }

        final DataSpacesURI spaceURI = queryUri.getSpacePartOnly();
        // it is about a concrete space, nothing abstract
        ensureSpaceIsMounted(spaceURI, null);

        return doResolveFile(queryUri, ownerActiveObjectId);
    }

    /**
     * Resolve query for URI without space part being fully defined, resulting in file-level access
     * to all data spaces that shares this common prefix. Requested result spaces must be suitable
     * for user path.
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
     * @param ownerActiveObjectId
     *            Id of active object requesting this files, that will become owner of returned
     *            {@link DataSpacesFileObject} instances. May be <code>null</code>, which
     *            corresponds to anonymous (unimportant) owner.
     * @return map of data spaces URIs that match the query, pointing to
     *         {@link DataSpacesFileObject} instances that can be used to access their content;
     *         returned DataSpacesFileObject are not opened nor attached in any way; these
     *         DataSpacesFileObject instances will never be shared, i.e. another instances are
     *         returned for subsequent queries (even the same queries).
     * @throws FileSystemException
     *             indicates VFS related exception during access, like mounting problems, I/O errors
     *             etc.
     * @throws IllegalArgumentException
     *             when provided queryUri has space part fully defined or some of resolved spaces
     *             URIs are not suitable for user path
     * @see DataSpacesURI#isSpacePartFullyDefined()
     * @see DataSpacesURI#isSuitableForUserPath()
     */
    public Map<DataSpacesURI, DataSpacesFileObject> resolveSpaces(final DataSpacesURI queryUri,
            final String ownerActiveObjectId) throws FileSystemException {

        final Map<DataSpacesURI, DataSpacesFileObject> result = new HashMap<DataSpacesURI, DataSpacesFileObject>();
        if (logger.isDebugEnabled())
            logger.debug("Spaces access request: " + queryUri);

        final Set<SpaceInstanceInfo> spaces = directory.lookupMany(queryUri);
        for (final SpaceInstanceInfo space : spaces) {
            final DataSpacesURI spaceUri = space.getMountingPoint();
            if (!spaceUri.isSuitableForUserPath()) {
                logger.error("Resolved space is not suitable for user path: " + spaceUri);
                throw new IllegalArgumentException("Resolved space is not suitable for user path: " +
                    spaceUri);
            }
            try {
                ensureSpaceIsMounted(spaceUri, space);
            } catch (SpaceNotFoundException e) {
                ProActiveLogger.logImpossibleException(logger, e);
                throw new RuntimeException(e);
            }
            result.put(spaceUri, doResolveFile(spaceUri, ownerActiveObjectId));
        }
        return result;
    }

    /**
     * Closes this manager instance.
     * <p>
     * Closing it indicates unmounting mounted data spaces. Any further access to already opened
     * FileObject within these data spaces or any call of this instance may result in undefined
     * behavior for caller.
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
                for (final DataSpacesURI spaceUri : new ArrayList<DataSpacesURI>(mountedSpaces.keySet())) {
                    unmountSpace(spaceUri);
                }
                vfsManager.close();
            }
        }
        logger.info("Mount manager closed");
    }

    private void ensureSpaceIsMounted(final DataSpacesURI spaceURI, SpaceInstanceInfo info)
            throws SpaceNotFoundException, FileSystemException {

        final boolean mounted;
        synchronized (readLock) {
            mounted = mountedSpaces.containsKey(spaceURI);
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
                    if (mountedSpaces.containsKey(spaceURI))
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
        } catch (org.apache.commons.vfs.FileSystemException x) {
            logger.warn(String.format("Could not access URL %s to mount %s", accessUrl, mountingPoint));
            throw new FileSystemException(x);
        }

        synchronized (readLock) {
            if (mountedSpaces.containsKey(mountingPoint)) {
                logger.error("Internal error - overmounting already mounted space: " + mountingPoint);
                throw new RuntimeException("Unexpected internal error - overmounting already mounted space");
            }
            mountedSpaces.put(mountingPoint, mountedRoot);
        }
        logger.info(String.format("Mounted space: %s (access URL: %s)", mountingPoint, accessUrl));
    }

    /*
     * Assumed to be called within writeLock and readLock, mountedSpaces contains specified spaceUri
     */
    private void unmountSpace(final DataSpacesURI spaceUri) {
        final FileObject spaceRoot = mountedSpaces.remove(spaceUri);
        final FileSystem spaceFileSystem = spaceRoot.getFileSystem();

        // we may not need to close FileObject, but with VFS you never know...
        try {
            spaceRoot.close();
        } catch (org.apache.commons.vfs.FileSystemException x) {
            ProActiveLogger.logEatedException(logger, String.format(
                    "Could not close data space %s root file object", spaceUri), x);
        }
        vfsManager.closeFileSystem(spaceFileSystem);
        logger.info("Unmounted space: " + spaceUri);
    }

    // uri should be always suitable for user path
    private DataSpacesFileObject doResolveFile(final DataSpacesURI uri, final String ownerActiveObjectId)
            throws FileSystemException {

        synchronized (readLock) {
            final DataSpacesURI spacePart = uri.getSpacePartOnly();
            final String relativeToSpace = uri.getRelativeToSpace();

            try {
                if (!mountedSpaces.containsKey(spacePart)) {
                    throw new FileSystemException("Could not access file that should exist (be mounted)");
                }

                final FileObject file;
                final FileObject spaceRoot = mountedSpaces.get(spacePart);
                if (relativeToSpace == null)
                    file = spaceRoot;
                else
                    file = spaceRoot.resolveFile(relativeToSpace);
                final DataSpacesLimitingFileObject limitingFile = new DataSpacesLimitingFileObject(file,
                    spacePart, spaceRoot.getName(), ownerActiveObjectId);
                return new VFSFileObjectAdapter(limitingFile, spacePart, spaceRoot.getName());
            } catch (org.apache.commons.vfs.FileSystemException x) {
                logger.warn("Could not access file that should exist (be mounted): " + uri);
                throw new FileSystemException(x);
            } catch (MalformedURIException e) {
                ProActiveLogger.logImpossibleException(logger, e);
                throw new ProActiveRuntimeException(e);
            }
        }
    }
}
