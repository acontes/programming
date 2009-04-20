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
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceNotFoundException;

/**
 * Manages data spaces mountings, connecting VFS and Data Spaces worlds.
 * <p>
 * Manager creates and maintains VirtualFileSystem instance to provide virtual
 * view of file system for each application. It is able to response to queries
 * for files in data spaces or just data spaces, providing VFS FileObject
 * interface as a result. Returned FileObject instances are applicable for user
 * level code.
 * <p>
 * To be able to serve requests for files and data spaces, SpaceMountManager
 * must use {@link SpacesDirectory} as a source of information about data
 * spaces, their mounting points and access methods.
 * <p>
 * Manager maintains mountings (in VFS world: "junctions") of data spaces on VFS
 * instance, using lazy on-request strategy in current implementation. Space is
 * mounted only when there is request to provide FileObject for its content.
 * Proper, local or remote access is determined using
 * {@link #getAccessURL(SpaceInstanceInfo)} method.
 * <p>
 * Instances of this class are thread-safe. Each instance of manager must be
 * closed using {@link #close()} method when it is not used anymore.
 * 
 * @see SpacesDirectory
 */
// TODO known issue: how to disallow remote AOs (or even: other local AOs) write
// access to each other scratch
public class SpacesMountManager {
	public static String getAccessURL(final SpaceInstanceInfo spaceInfo) {
		final String spaceHostname = spaceInfo.getHostname();
		if (spaceHostname != null && spaceHostname.equals(Utils.getHostnameForThis())
				&& spaceInfo.getPath() != null) {
			final String path = spaceInfo.getPath();
			// FIXME what about relative paths and windows support?
			if (path.startsWith("/")) {
				return "file://" + path;
			} else {
				return "file:///" + path;
			}
		}

		return spaceInfo.getUrl();
	}

	// FIXME check these two or even merge them to one method
	private static String getVFSResolvePath(final DataSpacesURI uri) {
		// nasty VFS hacks
		return uri.toString().substring(DataSpacesURI.VFS_SCHEME.length());
	}

	private static String getVFSJunctionPath(final DataSpacesURI spaceUri) {
		// nasty VFS hacks
		return spaceUri.toString().substring(DataSpacesURI.VFS_SCHEME.length());
	}

	private final DefaultFileSystemManager vfsManager;
	private final SpacesDirectory directory;
	private final VirtualFileSystem vfs;
	private final Set<DataSpacesURI> mountedSpaces = new HashSet<DataSpacesURI>();

	/*
	 * These two locks represent two levels of synchronization. In any execution
	 * only readLock is acquired or both locks are acquired in constant order
	 * (writeLock, then readLock) to avoid deadlocks.
	 * 
	 * readLock secures access to mountedSpaces and vfs instances. It is the
	 * only lock used for read queries for already mounted data spaces. It is
	 * used to avoid heavy synchronization on queries for already mounted data
	 * spaces.
	 * 
	 * writeLock is responsible for potentially costly synchronization of
	 * queries requiring mounting data spaces or on unmount. Therefore, only one
	 * data space can be mounted or unmounted at a time.
	 * 
	 * Phantom read phenomena can occur for some execution paths acquiring only
	 * readLock, for 2 times. Now it is not an issue actually, as unmount takes
	 * place only within close() method, that is supposed to be called when
	 * object is not used anymore.
	 */
	private final Object readLock = new Object();
	private final Object writeLock = new Object();

	/**
	 * Creates SpaceMountManager instance, that must be finally closed through
	 * {@link #close()} method.
	 * 
	 * @param vfsManager
	 *            configured VFS manager, used for mounting spaces and creating
	 *            VirtualFileSystem instances
	 * @param directory
	 *            data spaces directory to use for serving requests
	 */
	public SpacesMountManager(DefaultFileSystemManager vfsManager, SpacesDirectory directory) {
		this.vfsManager = vfsManager;
		this.directory = directory;
		try {
			final FileObject vfsObject = vfsManager.createVirtualFileSystem(DataSpacesURI.VFS_SCHEME);
			this.vfs = (VirtualFileSystem) vfsObject.getFileSystem();
		} catch (FileSystemException x) {
			// that should really never happen, we know that
			throw new RuntimeException(x);
		}
	}

	/**
	 * Resolves query for concrete URI within Data Spaces virtual tree,
	 * resulting in file-level access to this place.
	 * <p>
	 * If query URI is complete (refers to concrete data space), then returned
	 * FileObject can be safely used to access data in that data spaces, i.e. it
	 * conforms to all Data Spaces guarantees regarding that access.
	 * <p>
	 * If query URI is incomplete (for example, refers only to application id),
	 * then returned FileObject does not provide reliable access to virtual
	 * tree, just to the current state of mountings in that tree.
	 * <p>
	 * This call may block for a while, if {@link SpacesDirectory} need to be
	 * queried for data space and/or data space may need to be mounted.
	 * 
	 * @param queryUri
	 *            Data Spaces URI to get access to; URI may be complete or
	 *            incomplete
	 * @return VFS FileObject that can be used to access this URI content;
	 *         returned FileObject is not opened nor attached in any way
	 * @throws FileSystemException
	 *             indicates VFS related exception during access, like mounting
	 *             problems, I/O errors etc.
	 * @throws SpaceNotFoundException
	 *             when space with that query URI does not exists in
	 *             SpacesDirectory
	 */
	public synchronized FileObject resolveFile(final DataSpacesURI queryUri) throws FileSystemException,
			SpaceNotFoundException {

		if (queryUri.isComplete()) {
			// If it is a complete URI, it is about a space.
			final DataSpacesURI spaceURI = queryUri.withPath(null);
			ensureSpaceIsMounted(spaceURI, null);
		}
		return resolveFileVFS(queryUri);
	}

	/**
	 * Resolve query for incomplete URI, resulting in file-level access to all
	 * data spaces that shares this common prefix.
	 * <p>
	 * For any URI query, returned set contains all spaces (URIs) that match
	 * defined components in queried URI, allowing them to have any values for
	 * undefined components.
	 * <p>
	 * e.g. for <code>vfs:///123/input/</code> query you may get
	 * <code>vfs:///123/input/default/</code> and
	 * <code>vfs:///123/input/abc/</code> as a result, but not
	 * <code>vfs:///123/output/</code>
	 * <p>
	 * This call may block for a while, as {@link SpacesDirectory} need to be
	 * queried for data spaces and/or some data spaces may need to be mounted.
	 * 
	 * @param queryUri
	 *            Data Spaces URI to query for; must be incomplete URI, not
	 *            pointing to any concrete data space
	 * @return map of data spaces URIs that match the query, pointing to VFS
	 *         FileObjects that can be used to access their content; returned
	 *         FileObjects are not opened nor attached in any way
	 * @throws FileSystemException
	 *             indicates VFS related exception during access, like mounting
	 *             problems, I/O errors etc.
	 * @throws IllegalArgumentException
	 *             when provided queryUri is complete
	 */
	public synchronized Map<DataSpacesURI, FileObject> resolveSpaces(final DataSpacesURI queryUri)
			throws FileSystemException {
		final Map<DataSpacesURI, FileObject> result = new HashMap<DataSpacesURI, FileObject>();

		final Set<SpaceInstanceInfo> spaces = directory.lookupAll(queryUri);
		for (final SpaceInstanceInfo space : spaces) {
			final DataSpacesURI spaceUri = space.getMountingPoint();
			try {
				ensureSpaceIsMounted(spaceUri, space);
			} catch (SpaceNotFoundException e) {
				// impossible to happen for space info argument available
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
	 * Closing it indicates unmounting mounted data spaces and closing
	 * VirtualFileSystem instance. Any further access to already opened
	 * FileObject within these data spaces or any call of this instance may
	 * result in undefined behavior for caller.
	 * <p>
	 * VFS manager instance nor SpacesDirectory instance provided at constructor
	 * are not closed in any way.
	 * <p>
	 * Subsequent calls to these method may result in undefined behavior.
	 */
	public synchronized void close() {
		synchronized (writeLock) {
			synchronized (readLock) {
				for (final DataSpacesURI spaceUri : new ArrayList<DataSpacesURI>(mountedSpaces)) {
					try {
						unmountSpace(spaceUri);
					} catch (FileSystemException e) {
						// FIXME log and ignore?
					}
				}
				vfs.close();
			}
		}
	}

	private void ensureSpaceIsMounted(final DataSpacesURI spaceURI, SpaceInstanceInfo info)
			throws SpaceNotFoundException, FileSystemException {
		final boolean mounted;
		synchronized (readLock) {
			mounted = mountedSpaces.contains(spaceURI);
		}

		if (!mounted) {
			if (info == null) {
				info = directory.lookupFirst(spaceURI);
			}
			if (info == null) {
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
	 * TODO: support concurrent mounting of more than one data space at a time
	 * if needed (requires a little bit more complex synchronization)
	 */
	private void mountSpace(final SpaceInstanceInfo spaceInfo) throws FileSystemException {
		final DataSpacesURI mountingPoint = spaceInfo.getMountingPoint();
		final FileObject mountedRoot = vfsManager.resolveFile(getAccessURL(spaceInfo));

		synchronized (readLock) {
			vfs.addJunction(getVFSJunctionPath(mountingPoint), mountedRoot);
			if (!mountedSpaces.add(mountingPoint)) {
				throw new RuntimeException("Unexpected internal error - overmounting already mounted space");
			}
		}
	}

	/*
	 * Assumed to be called within writeLock and readLock
	 */
	private void unmountSpace(final DataSpacesURI spaceUri) throws FileSystemException {
		final FileSystem fs = resolveFileVFS(spaceUri).getFileSystem();
		mountedSpaces.remove(spaceUri);
		vfs.removeJunction(getVFSJunctionPath(spaceUri));
		vfsManager.closeFileSystem(fs);
	}

	private FileObject resolveFileVFS(final DataSpacesURI uri) throws FileSystemException {
		synchronized (readLock) {
			return vfsManager.resolveFile(vfs.getRoot(), getVFSResolvePath(uri));
		}
	}
}
