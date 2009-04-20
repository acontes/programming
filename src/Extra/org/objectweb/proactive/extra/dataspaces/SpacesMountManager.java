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
 * resp: - (internally) manages VFS junctions adding/deleting - (externally)
 * manages mounting / unmounting - creates VirtualFileSystem from instance of
 * VFSManager - contacts CachingSpacesDirectory (or trough SpaceDirectory
 * interface) col: - CachingSpacesDirectory (or SpaceDirectory interface)
 */
// TODO more efficient synchronization
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

	public synchronized FileObject resolveFile(final DataSpacesURI uri) throws FileSystemException,
			SpaceNotFoundException {

		if (uri.isComplete()) {
			// If it is complete query, it is about a space.
			final DataSpacesURI spaceURI = uri.withPath(null);
			ensureSpaceIsMounted(spaceURI);
		}
		return resolveFileVFS(uri);
	}

	public synchronized Map<DataSpacesURI, FileObject> resolveSpaces(final DataSpacesURI queryUri)
			throws FileSystemException {

		final Map<DataSpacesURI, FileObject> result = new HashMap<DataSpacesURI, FileObject>();

		final Set<SpaceInstanceInfo> spaces = directory.lookupAll(queryUri);
		for (final SpaceInstanceInfo space : spaces) {
			ensureSpaceIsMounted(space);
			final DataSpacesURI spaceUri = space.getMountingPoint();
			final FileObject fo = resolveFileVFS(spaceUri);
			result.put(spaceUri, fo);
		}
		return result;
	}

	/**
	 * Removes all junctions, mounted file systems. Forgets about the
	 * VirtualFileSystem instance.
	 */
	public synchronized void close() {
		for (final DataSpacesURI spaceUri : new ArrayList<DataSpacesURI>(mountedSpaces)) {
			try {
				unmountSpace(spaceUri);
			} catch (FileSystemException e) {
				// FIXME log and ignore?
			} catch (SpaceNotFoundException e) {
				// FIXME log and ignore?
			}
		}
		vfs.close();
	}

	private void ensureSpaceIsMounted(final DataSpacesURI spaceURI) throws SpaceNotFoundException,
			FileSystemException {
		if (!mountedSpaces.contains(spaceURI)) {
			final SpaceInstanceInfo info = directory.lookupFirst(spaceURI);
			if (info == null) {
				throw new SpaceNotFoundException(
						"Requested data space is not registered in spaces directory.");
			}
			mountSpace(info);
		}
	}

	private void ensureSpaceIsMounted(final SpaceInstanceInfo spaceInfo) throws FileSystemException {
		if (!mountedSpaces.contains(spaceInfo.getMountingPoint())) {
			mountSpace(spaceInfo);
		}
	}

	private void mountSpace(final SpaceInstanceInfo spaceInfo) throws FileSystemException {
		final DataSpacesURI mountingPoint = spaceInfo.getMountingPoint();
		final FileObject mountedRoot = vfsManager.resolveFile(getAccessURL(spaceInfo));

		vfs.addJunction(getVFSJunctionPath(mountingPoint), mountedRoot);
		if (!mountedSpaces.add(mountingPoint)) {
			throw new RuntimeException("Unexpected internal error - overmounting already mounted space");
		}
	}

	private void unmountSpace(final DataSpacesURI spaceUri) throws FileSystemException,
			SpaceNotFoundException {

		final FileSystem fs = resolveFile(spaceUri).getFileSystem();
		vfsManager.closeFileSystem(fs);
		vfs.removeJunction(getVFSJunctionPath(spaceUri));
		mountedSpaces.remove(spaceUri);
	}

	private FileObject resolveFileVFS(final DataSpacesURI uri) throws FileSystemException {
		return vfsManager.resolveFile(vfs.getRoot(), getVFSResolvePath(uri));
	}
}
