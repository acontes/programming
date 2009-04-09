/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Set;


/**
 * resp:
	- (internally) manages VFS junctions adding/deleting
	- (externally) manages mounting / unmounting
	- creates VirtualFileSystem from instance of VFSManager
	- contacts CachingSpacesDirectory (or trough SpaceDirectory interface)
col:
	- CachingSpacesDirectory (or SpaceDirectory interface)
 */
public class SpacesMountManager {

    public Object resolveFile(SpaceURI spaceURI, String path) {
        return null;
    }

    public Object resolveSpace(SpaceURI spaceURI) {
        return null;
    }

    public Set<Object> resolveSpaceMany(SpaceURI spaceURI) {
        return null;
    }
}
