/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Set;

/**
 * resp: - stores mappings to SpaceInstanceInfos from SpaceURI - two public
 * lookup methods - lookup for one / lookup for many main use cases: - lookup
 * SpaceInstanceInfo for SpaceURI - lookup SpaceInstanceInfo for input/output
 * name - lookup SpaceInstanceInfos for type - register/unregister col: - Naming
 * Service - (local) - SpaceURI - SpaceInstanceInfo
 * 
 */
public interface SpacesDirectory {

	/**
	 * Lookup for space instance info with given mounting point uri.
	 * 
	 * @param uri
	 *            mounting point uri of data space to look up
	 * @return SpaceInstanceInfo mapping or null if not available
	 */
	public SpaceInstanceInfo lookupFirst(SpaceURI uri);

	/**
	 * Lookup for all space instance info that have specified uri as a root of
	 * their mounting point (<code>ls -R</code> like).
	 * 
	 * @param uri
	 *            root uri to look up
	 * @return SpaceInstanceInfo mappings or null if none is available
	 */
	public Set<SpaceInstanceInfo> lookupAll(SpaceURI uri);

	/**
	 * Registers new space instance info. If mounting point of that space
	 * instance has been already in the directory, the old entry is replaced
	 * with the new one.
	 * 
	 * @param spaceInstanceInfo
	 *            - space instance info to register
	 */
	public void register(SpaceInstanceInfo spaceInstanceInfo);

	/**
	 * Unregisters TODO: what about returning? exceptions?
	 * 
	 * @param uri
	 *            - mounting point uri that is to be unregistered
	 */
	public void unregister(SpaceURI uri);
}
