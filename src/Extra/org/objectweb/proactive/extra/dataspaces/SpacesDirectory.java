/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Set;

/**
 * Stores mappings to SpaceInstanceInfos from DataSpacesURI and provides methods for
 * registering and lookup. The most common lookups are:
 * <ul>
 * <li>lookup SpaceInstanceInfo for DataSpacesURI</li>
 * <li>lookup SpaceInstanceInfo for input/output name</li>
 * <li>lookup SpaceInstanceInfos for type</li>
 * </ul>
 */
public interface SpacesDirectory {

	/**
	 * Lookup for space instance info with given mounting point uri.
	 * 
	 * @param uri
	 *            mounting point uri of data space to look up
	 * @return SpaceInstanceInfo mapping or null if not available
	 * @throws IllegalArgumentException
	 *             when specified DataSpacesURI is not complete or contains path
	 */
	public SpaceInstanceInfo lookupFirst(DataSpacesURI uri) throws IllegalArgumentException;

	/**
	 * Lookup for all space instance info that have specified uri as a root of
	 * their mounting point (<code>ls -R</code> like). Subsequent method calls
	 * may return different result - the same or with new elements.
	 * 
	 * @param uri
	 *            root uri to look up
	 * @return SpaceInstanceInfo mappings or null if none is available
	 * @throws IllegalArgumentException
	 *             when specified uri is complete
	 */
	public Set<SpaceInstanceInfo> lookupAll(DataSpacesURI uri) throws IllegalArgumentException;

	/**
	 * Registers new space instance info. If mounting point of that space
	 * instance has been already in the directory, the old entry is replaced
	 * with the new one.
	 * 
	 * @param spaceInstanceInfo
	 *            - space instance info to register
	 * @throws IllegalStateException
	 *             when directory is aware of all registered applications and
	 *             there is no such application for SpaceInstanceInfo being
	 *             registered
	 */
	public void register(SpaceInstanceInfo spaceInstanceInfo) throws IllegalStateException;

	/**
	 * Unregisters space instance info specified by DataSpacesURI.
	 * 
	 * @param uri
	 *            - mounting point uri that is to be unregistered
	 * @return <code>true</code> if space instance with given DataSpacesURI has been
	 *         found; <code>false</code> otherwise
	 */
	public boolean unregister(DataSpacesURI uri);
}
