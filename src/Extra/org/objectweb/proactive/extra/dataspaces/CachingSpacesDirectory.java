/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Collection;

/**
 * 
 *
 */
public class CachingSpacesDirectory implements SpacesDirectory {

	private final SpacesDirectory localDirectory;

	private final SpacesDirectory remoteDirectory;

	public CachingSpacesDirectory() {
		localDirectory = new SpacesDirectoryImpl();
		remoteDirectory = new SpacesDirectoryImpl();
	}

	/**
	 * This method call is always delegated remotely.
	 * 
	 * @see org.objectweb.proactive.extensions.dataspaces.SpacesDirectory#lookupAll
	 *      (org.objectweb.proactive.extensions.dataspaces.SpaceURI)
	 */
	public Collection<SpaceInstanceInfo> lookupAll(SpaceURI uri) {
		return remoteDirectory.lookupAll(uri);
	}

	/**
	 * Try in cache, if not found try remotely.
	 * 
	 * @see org.objectweb.proactive.extensions.dataspaces.SpacesDirectory#lookupFirst
	 *      (org.objectweb.proactive.extensions.dataspaces.SpaceURI)
	 */
	public SpaceInstanceInfo lookupFirst(SpaceURI uri) {
		localDirectory.lookupFirst(uri);
		return null;
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
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.objectweb.proactive.extensions.dataspaces.SpacesDirectory#unregister
	 * (org.objectweb.proactive.extensions.dataspaces.SpaceURI)
	 */
	public void unregister(SpaceURI uri) {
		// TODO Auto-generated method stub

	}

}
