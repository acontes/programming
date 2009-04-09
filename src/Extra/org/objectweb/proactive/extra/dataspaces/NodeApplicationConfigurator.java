/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileSystemManager;

/**
 * resp: - maintaines configuration for application - creation logic: creates
 * scratch SpaceInstanceInfo from SpaceConfiguration and app id connects to
 * NamingService and creates CachingSpacesDirectory for that NamingService.
 * registers scratch SpaceInstanceInfo through CachingSpacesDirectory creates
 * SpacesMountManager with CachingSpacesDirectory and VFS manager - cleaning up:
 * - closes SpacesMountManager (removes all junctions, mounted file systems,
 * forget about the VirtualFileSystem instance) - unregisters scratch from
 * NamingService (and closes connection?) - removes scratch data space directory
 * content - creates and returns DataSpacesImpl col: DataSpacesImpl
 * SpacesMountManager CachingSpacesDirectory AppScratchSpace
 * 
 */
public class NodeApplicationConfigurator {

	public DataSpacesImpl configureApplication(long appid, String namingServiceURL,
			FileSystemManager manager, NodeScratchSpace scratch) {
		return null;
	}

	public void close() {
	}
}
