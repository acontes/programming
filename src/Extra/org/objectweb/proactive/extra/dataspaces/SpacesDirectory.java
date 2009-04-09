/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Set;


/**
 * resp:
	- stores mappings to SpaceInstanceInfos from SpaceURI
	- two public lookup methods - lookup for one / lookup for many
		main use cases:
		- lookup SpaceInstanceInfo for SpaceURI
		- lookup SpaceInstanceInfo for input/output name
		- lookup SpaceInstanceInfos for type
	- register/unregister
col:
	- Naming Service
	- (local)
	- SpaceURI
	- SpaceInstanceInfo
 *
 */
public interface SpacesDirectory {

    public SpaceInstanceInfo lookupFirst(SpaceURI uri);

    public Set<SpaceInstanceInfo> lookupAll(SpaceURI uri);

    /*
     * * TODO: exceptions?
     * 
     * @param uri
     * 
     * @param spaceInstanceInfo
     */
    public void register(SpaceURI uri, SpaceInstanceInfo spaceInstanceInfo);

    /*
     * * TODO: what about returning? exceptions?
     * 
     * @param uri
     */
    public void unregister(SpaceURI uri);
}
