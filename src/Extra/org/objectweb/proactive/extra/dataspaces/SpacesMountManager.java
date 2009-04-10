/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Set;

/**
 * resp: - (internally) manages VFS junctions adding/deleting - (externally)
 * manages mounting / unmounting - creates VirtualFileSystem from instance of
 * VFSManager - contacts CachingSpacesDirectory (or trough SpaceDirectory
 * interface) col: - CachingSpacesDirectory (or SpaceDirectory interface)
 */
public class SpacesMountManager {

	public Object resolveFile(DataSpacesURI uri) {
		return null;
	}

	public Set<Object> resolveSpaces(DataSpacesURI uri) {
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
