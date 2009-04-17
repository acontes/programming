/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Set;

import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.WrongApplicationIdException;

/**
 * Stores mappings to SpaceInstanceInfos from DataSpacesURI and provides methods
 * for registering and lookup. The most common lookups are:
 * <ul>
 * <li>lookup SpaceInstanceInfo for DataSpacesURI</li>
 * <li>lookup SpaceInstanceInfo for input/output name</li>
 * <li>lookup SpaceInstanceInfos for type</li>
 * </ul>
 */
public interface SpacesDirectory {

	/**
	 * Lookup for space instance info with given mounting point URI (must be
	 * complete).
	 * 
	 * @param uri
	 *            mounting point URI of data space to look up
	 * @return SpaceInstanceInfo mapping or null if not available
	 * @throws IllegalArgumentException
	 *             when specified DataSpacesURI is not complete or contains path
	 */
	public SpaceInstanceInfo lookupFirst(DataSpacesURI uri) throws IllegalArgumentException;

	/**
	 * Lookup for all SpaceInstanceInfo with root of its mounting point that
	 * matches specified URI (<code>ls -R</code> like). Subsequent method calls
	 * may return different result - the same or with new elements.
	 * 
	 * @param uri
	 *            root URI to look up
	 * @return SpaceInstanceInfo mappings or null if none is available
	 * @throws IllegalArgumentException
	 *             when specified URI is complete
	 */
	public Set<SpaceInstanceInfo> lookupAll(DataSpacesURI uri) throws IllegalArgumentException;

	/**
	 * Registers new space instance info. If mounting point of that space
	 * instance has been already in the directory, an exception is raised as
	 * directory is append-only.
	 * 
	 * @param spaceInstanceInfo
	 *            - space instance info to register
	 * @throws WrongApplicationIdException
	 *             when directory is aware of all registered applications and
	 *             there is no such application for SpaceInstanceInfo being
	 *             registered
	 * @throws SpaceAlreadyRegisteredException
	 *             when directory already contains any space instance under
	 *             specified mounting point
	 */
	public void register(SpaceInstanceInfo spaceInstanceInfo) throws WrongApplicationIdException,
			SpaceAlreadyRegisteredException;

	/**
	 * Unregisters space instance info specified by DataSpacesURI.
	 * 
	 * @param uri
	 *            - mounting point URI that is to be unregistered
	 * @return <code>true</code> if space instance with given DataSpacesURI has
	 *         been found; <code>false</code> otherwise
	 */
	public boolean unregister(DataSpacesURI uri);
}
