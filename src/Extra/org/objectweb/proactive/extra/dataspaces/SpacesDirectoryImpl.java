/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Implementation of SpacesDirectory interface.
 * 
 * @see org.objectweb.proactive.extra.dataspaces.SpacesDirectory
 */
public class SpacesDirectoryImpl implements SpacesDirectory {

	final private SortedMap<SpaceURI, SpaceInstanceInfo> data = new TreeMap<SpaceURI, SpaceInstanceInfo>();

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.objectweb.proactive.extra.dataspaces.SpacesDirectory#lookupAll(org
	 * .objectweb.proactive.extra.dataspaces.SpaceURI)
	 */
	public Collection<SpaceInstanceInfo> lookupAll(SpaceURI uri) {
		final SpaceURI nextKey = SpaceURI.nextSpaceURI(uri);

		synchronized (data) {
			final SortedMap<SpaceURI, SpaceInstanceInfo> sub = data.subMap(uri, nextKey);

			if (sub.size() > 0)
				return sub.values();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.objectweb.proactive.extra.dataspaces.SpacesDirectory#lookupFirst(
	 * org.objectweb.proactive.extra.dataspaces.SpaceURI)
	 */
	public SpaceInstanceInfo lookupFirst(SpaceURI uri) {
		synchronized (data) {
			return data.get(uri);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.objectweb.proactive.extra.dataspaces.SpacesDirectory#register(org
	 * .objectweb.proactive.extra.dataspaces.SpaceInstanceInfo)
	 */
	public void register(SpaceInstanceInfo spaceInstanceInfo) {
		final SpaceURI mpoint;

		// get mounting point URI that cannot be null
		synchronized (data) {
			mpoint = spaceInstanceInfo.getMountingPoint();
			data.put(mpoint, spaceInstanceInfo);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.objectweb.proactive.extra.dataspaces.SpacesDirectory#unregister(org
	 * .objectweb.proactive.extra.dataspaces.SpaceURI)
	 */
	public void unregister(SpaceURI uri) {
		synchronized (data) {
			data.remove(uri);
		}
	}
}
