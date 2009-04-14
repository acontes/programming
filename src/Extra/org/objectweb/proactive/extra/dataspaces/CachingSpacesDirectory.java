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
	 *      (org.objectweb.proactive.extensions.dataspaces.DataSpacesURI)
	 */
	public Set<SpaceInstanceInfo> lookupAll(DataSpacesURI uri) {

		if (uri.isComplete())
			throw new IllegalArgumentException("Space URI must not be complete for this method call");

		final Set<SpaceInstanceInfo> ret = remoteDirectory.lookupAll(uri);
		localDirectory.register(ret);

		return ret;
	}

	/**
	 * Try in cache, if not found try remotely.
	 * 
	 * @see org.objectweb.proactive.extensions.dataspaces.SpacesDirectory#lookupFirst
	 *      (org.objectweb.proactive.extensions.DataSpacesURI.DataSpacesURI)
	 */
	public SpaceInstanceInfo lookupFirst(DataSpacesURI uri) {

		if (!uri.isComplete())
			throw new IllegalArgumentException("Space URI must be complete for this method call");

		if (uri.getPath() != null)
			throw new IllegalArgumentException("Space URI must not contain path for this method call");

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
	 * (org.objectweb.proactive.extensions.dataspaces.DataSpacesURI,
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
	 * (org.objectweb.proactive.extensions.dataspaces.DataSpacesURI)
	 */
	public boolean unregister(DataSpacesURI uri) {
		localDirectory.unregister(uri);
		return remoteDirectory.unregister(uri);
	}
}
