/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.HashSet;
import java.util.Set;
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
	public Set<SpaceInstanceInfo> lookupAll(SpaceURI uri) {
		if (uri.isComplete())
			throw new IllegalArgumentException("Space URI must not be complete for this method call");

		final SpaceURI nextKey = SpaceURI.nextSpaceURI(uri);
		final Set<SpaceInstanceInfo> ret = new HashSet<SpaceInstanceInfo>();

		synchronized (data) {
			final SortedMap<SpaceURI, SpaceInstanceInfo> sub = data.subMap(uri, nextKey);

			if (sub.size() == 0)
				return null;
			ret.addAll(sub.values());
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.objectweb.proactive.extra.dataspaces.SpacesDirectory#lookupFirst(
	 * org.objectweb.proactive.extra.dataspaces.SpaceURI)
	 */
	public SpaceInstanceInfo lookupFirst(SpaceURI uri) {
		if (!uri.isComplete())
			throw new IllegalArgumentException("Space URI must be complete for this method call");

		if (uri.getPath() != null)
			throw new IllegalArgumentException("Space URI must not contain path for this method call");

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
