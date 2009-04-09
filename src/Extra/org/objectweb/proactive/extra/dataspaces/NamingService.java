/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Set;

/**
 * Manages application register/unregister process in space directory. TODO:
 * Remote accessible.
 */
public class NamingService implements SpacesDirectory {

	/*
	 * * Registers application along with its spaces definition.
	 * 
	 * @param appid application identifier
	 * 
	 * @param inputSpaces bulked inputs definitions
	 * 
	 * @param outputSpaces bulked outputs definitions
	 */
	public void registerApplication(long appid, Set<SpaceInstanceInfo> inputSpaces,
			Set<SpaceInstanceInfo> outputSpaces) {
	}

	/*
	 * * Unregisters application under specified identifier.
	 * 
	 * @param appid application identifier
	 */
	public void unregisterApplication(long appid) {

	}

	public Set<SpaceInstanceInfo> lookupAll(SpaceURI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public SpaceInstanceInfo lookupFirst(SpaceURI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public void register(SpaceInstanceInfo spaceInstanceInfo) {
		// TODO Auto-generated method stub

	}

	public void unregister(SpaceURI uri) {
		// TODO Auto-generated method stub

	}

}
