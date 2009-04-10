/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Set;

/**
 * Decorator of SpacesDirectory that caches SpaceInstanceInfo in its
 * SpacesDirectoryImpl instance.
 */
public class CachingSpacesDirectory implements SpacesDirectory {

	private final SpacesDirectoryImpl localDirectory;

	private final SpacesDirectory remoteDirectory;

	public CachingSpacesDirectory(SpacesDirectory directoryToCache) {
		localDirectory = new SpacesDirectoryImpl();
		remoteDirectory = directoryToCache;
	}

	/**
	 * This method call is always delegated remotely.
	 * 
	 * @see org.objectweb.proactive.extensions.dataspaces.SpacesDirectory#lookupAll
	 *      (org.objectweb.proactive.extensions.dataspaces.SpaceURI)
	 */
	public Set<SpaceInstanceInfo> lookupAll(SpaceURI uri) {
		return remoteDirectory.lookupAll(uri);
	}

	/**
	 * Try in cache, if not found try remotely.
	 * 
	 * @see org.objectweb.proactive.extensions.dataspaces.SpacesDirectory#lookupFirst
	 *      (org.objectweb.proactive.extensions.dataspaces.SpaceURI)
	 */
	public SpaceInstanceInfo lookupFirst(SpaceURI uri) {
		SpaceInstanceInfo sii = localDirectory.lookupFirst(uri);

		if (sii != null)
			return sii;

		sii = remoteDirectory.lookupFirst(uri);

		if (sii != null)
			localDirectory.register(sii);
		return sii;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.objectweb.proactive.extensions.dataspaces.SpacesDirectory#register
	 * (org.objectweb.proactive.extensions.dataspaces.SpaceURI,
	 * org.objectweb.proactive.extensions.dataspaces.SpaceInstanceInfo)
	 */
	public void register(SpaceInstanceInfo spaceInstanceInfo) {
		remoteDirectory.register(spaceInstanceInfo);
		localDirectory.register(spaceInstanceInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.objectweb.proactive.extensions.dataspaces.SpacesDirectory#unregister
	 * (org.objectweb.proactive.extensions.dataspaces.SpaceURI)
	 */
	public void unregister(SpaceURI uri) {
		// TODO if unregisters can fail, check the proper order or catch
		// exceptions or sth.
		localDirectory.unregister(uri);
		remoteDirectory.unregister(uri);
	}
}
