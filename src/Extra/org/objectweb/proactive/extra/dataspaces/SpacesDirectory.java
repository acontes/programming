/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Set;

import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.WrongApplicationIdException;


/**
 * Directory of data spaces information. It allows registering and unregistering space instances (
 * {@link SpaceInstanceInfo}: space with defined URI and access-related information) and performing
 * lookup-queries by space URI for these space-related information. It acts as a sort of "space URI"
 * to "space info" map.
 * <p>
 * Directory is assumed to work in append-only mode for each URI for some period of time, finally
 * allowing to remove information about removed space. It means, that once space with some URI is
 * registered, the directory should provide the same information for each query for that URI during
 * some period, until this space is explicitly removed from the directory. In other words, space
 * information should never be overridden, and should be removed from directory only where
 * interested parties that may have ask for this space, are aware of that.
 * <p>
 * Directory may be used for lookups for space with particular URI (
 * {@link #lookupOne(DataSpacesURI)}) or more abstract queries ({@link #lookupMany(DataSpacesURI)}).
 */
public interface SpacesDirectory {

    /**
     * Lookup for space instance info with given complete mounting point URI.
     * 
     * @param uri
     *            mounting point URI of data space to look up (must be complete)
     * @return SpaceInstanceInfo for that URI or <code>null</code> if there is no such URI
     *         registered
     * @throws IllegalArgumentException
     *             when specified DataSpacesURI is not complete or contains path
     */
    public SpaceInstanceInfo lookupOne(DataSpacesURI uri) throws IllegalArgumentException;

    /**
     * Lookup for all SpaceInstanceInfo with root of its mounting point that matches specified URI (
     * <code>ls -R</code> like).
     * 
     * @param uri
     *            root URI to look up
     * @return SpaceInstanceInfo mappings or null if none is available
     * @throws IllegalArgumentException
     *             when specified URI is complete
     */
    public Set<SpaceInstanceInfo> lookupMany(DataSpacesURI uri) throws IllegalArgumentException;

    /**
     * Registers new space instance info. If mounting point of that space instance has been already
     * in the directory, an exception is raised as directory is append-only.
     * 
     * @param spaceInstanceInfo
     *            space instance info to register (contract: SpaceInstanceInfo mounting point should
     *            be complete)
     * @throws WrongApplicationIdException
     *             when directory is aware of all registered applications and there is no such
     *             application for SpaceInstanceInfo being registered
     * @throws SpaceAlreadyRegisteredException
     *             when directory already contains any space instance under specified mounting point
     */
    public void register(SpaceInstanceInfo spaceInstanceInfo) throws WrongApplicationIdException,
            SpaceAlreadyRegisteredException;

    /**
     * Unregisters space instance info specified by DataSpacesURI.
     * 
     * @param uri
     *            mounting point URI that is to be unregistered
     * @return <code>true</code> if space instance with given DataSpacesURI has been found;
     *         <code>false</code> otherwise
     * @throws IllegalArgumentException
     *             when specified DataSpacesURI is not complete or contains path
     */
    public boolean unregister(DataSpacesURI uri) throws IllegalArgumentException;
}
