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
		final SpaceURI nextKey = new NextSpaceURIKey(uri);

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

	/**
	 * Helper class for generating next space uri key in the same path level.
	 */
	private class NextSpaceURIKey extends SpaceURI {

		public NextSpaceURIKey(SpaceURI key) {
			super(key);

			// case: appid/type/name/
			// case: appid/type/rt/node/
			if (key.isComplete())
				throw new IllegalArgumentException(
						"Source key uri is complete. Doesn't make sens, giving up.");

			// case: appid/ - just ++
			if (dsType == null) {
				appId++;
				return;
			}

			// case: appid/SCRATCH/rt/ - just build next rt string
			if (dsType == SpaceType.SCRATCH && runtimeId != null) {
				runtimeId = runtimeId + '\0';
				return;
			}

			// case: appid/type/ - paste a next type
			dsType = dsType.succ();

			// case: appid/last_type/ - there was no next type?
			if (dsType == null)
				appId++;
		}
	}
}
