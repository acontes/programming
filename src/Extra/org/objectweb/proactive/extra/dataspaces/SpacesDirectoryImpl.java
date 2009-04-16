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

	final private SortedMap<DataSpacesURI, SpaceInstanceInfo> data = new TreeMap<DataSpacesURI, SpaceInstanceInfo>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.objectweb.proactive.extra.dataspaces.SpacesDirectory#lookupAll(org
	 * .objectweb.proactive.extra.dataspaces.DataSpacesURI)
	 */
	public Set<SpaceInstanceInfo> lookupAll(DataSpacesURI uri) {
		if (uri.isComplete())
			throw new IllegalArgumentException("Space URI must not be complete for this method call");

		final DataSpacesURI nextKey = uri.nextURI();
		final Set<SpaceInstanceInfo> ret = new HashSet<SpaceInstanceInfo>();

		synchronized (data) {
			final SortedMap<DataSpacesURI, SpaceInstanceInfo> sub = data.subMap(uri, nextKey);

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
	 * org.objectweb.proactive.extra.dataspaces.DataSpacesURI)
	 */
	public SpaceInstanceInfo lookupFirst(DataSpacesURI uri) {
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
		final DataSpacesURI mpoint;

		// get mounting point URI that cannot be null
		synchronized (data) {
			mpoint = spaceInstanceInfo.getMountingPoint();

			if (data.containsKey(mpoint))
				throw new IllegalArgumentException("Mapping for a given space URI is already registered");
			data.put(mpoint, spaceInstanceInfo);
		}
	}

	/**
	 * Helper method for bulked registration as obtaining lock is done only
	 * once.
	 * 
	 * @param ssis
	 */
	protected void register(Set<SpaceInstanceInfo> ssis) {

		synchronized (data) {
			for (SpaceInstanceInfo ssi : ssis)
				data.put(ssi.getMountingPoint(), ssi);
		}
	}

	/**
	 * Helper method for bulked unregistration as obtaining lock is done only
	 * once.
	 * 
	 * @param uris
	 */
	protected void unregister(Set<DataSpacesURI> uris) {
		synchronized (data) {
			for (DataSpacesURI key : uris)
				data.remove(key);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.objectweb.proactive.extra.dataspaces.SpacesDirectory#unregister(org
	 * .objectweb.proactive.extra.dataspaces.DataSpacesURI)
	 */
	public boolean unregister(DataSpacesURI uri) {

		synchronized (data) {
			if (!data.containsKey(uri))
				return false;

			data.remove(uri);
		}
		return true;
	}
}
