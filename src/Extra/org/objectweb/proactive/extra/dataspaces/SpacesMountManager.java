/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Set;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;

/**
 * resp: - (internally) manages VFS junctions adding/deleting - (externally)
 * manages mounting / unmounting - creates VirtualFileSystem from instance of
 * VFSManager - contacts CachingSpacesDirectory (or trough SpaceDirectory
 * interface) col: - CachingSpacesDirectory (or SpaceDirectory interface)
 */
public class SpacesMountManager {
	private DefaultFileSystemManager vfsManager;
	private SpacesDirectory directory;
	private FileObject vfs;

	public SpacesMountManager(DefaultFileSystemManager vfsManager, SpacesDirectory directory) {
		this.vfsManager = vfsManager;
		this.directory = directory;
		// this.vfs = vfsManager.createVirtualFileSystem("vfs:///");
	}

	public FileObject resolveFile(DataSpacesURI uri) {
		return null;
	}

	public Set<FileObject> resolveSpaces(DataSpacesURI uri) {
		return null;
	}

	/**
	 * Removes all junctions, mounted file systems. Forgets about the
	 * VirtualFileSystem instance.
	 */
	public void close() {
		// TODO auto generated
	}
}
