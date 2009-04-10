/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.HashSet;
import java.util.Set;

/**
 * Manages application register/unregister process in space directory.
 * Implements SpacesDirectory. TODO: Remote accessible.
 */
public class NamingService extends SpacesDirectoryImpl implements SpacesDirectory {

	private final Set<Long> registeredApplications = new HashSet<Long>();

	/**
	 * Registers application along with its spaces definition.
	 * 
	 * @param appid
	 *            application identifier
	 * 
	 * @param inputSpaces
	 *            bulked inputs definitions
	 * 
	 * @param outputSpaces
	 *            bulked outputs definitions
	 * 
	 * @throws IllegalArgumentException
	 *             When given appid doesn't match with one found in SpaceURI or
	 *             found the same entry in input and output spaces set. In these
	 *             cases the register operation is rolled back.
	 * @throws IllegalStateException
	 *             When specified application id is already registered.
	 */
	synchronized public void registerApplication(long appid, Set<SpaceInstanceInfo> inputSpaces,
			Set<SpaceInstanceInfo> outputSpaces) throws IllegalArgumentException {

		if (isApplicationIdRegistered(appid)) {
			throw new IllegalStateException("Application with the same application id is already registered.");
		}
		registeredApplications.add(appid);

		final Set<SpaceInstanceInfo> spaces = new HashSet<SpaceInstanceInfo>();

		processSpacesSet(appid, inputSpaces, spaces);
		processSpacesSet(appid, outputSpaces, spaces);

		register(spaces);
	}

	/**
	 * Unregisters application under specified identifier.
	 * 
	 * @param appid
	 *            application identifier
	 */
	synchronized public void unregisterApplication(long appid) {
		final boolean found;

		found = registeredApplications.remove(appid);

		if (!found)
			throw new IllegalStateException("Application with specified appid is not registered.");

		final Set<SpaceInstanceInfo> spaces = lookupAll(SpaceURI.createApplicationSpacesURI(appid));
		final Set<SpaceURI> uris = new HashSet<SpaceURI>(spaces.size());

		for (SpaceInstanceInfo sii : spaces)
			uris.add(sii.getMountingPoint());

		unregister(uris);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.objectweb.proactive.extra.dataspaces.SpacesDirectoryImpl#register
	 * (org.objectweb.proactive.extra.dataspaces.SpaceInstanceInfo)
	 */
	@Override
	synchronized public void register(SpaceInstanceInfo spaceInstanceInfo) {
		final long appid = spaceInstanceInfo.getAppId();

		if (!isApplicationIdRegistered(appid))
			throw new IllegalStateException("The is no application registered with specified application id.");

		super.register(spaceInstanceInfo);
	}

	private boolean isApplicationIdRegistered(long appid) {
		return registeredApplications.contains(appid);
	}

	private void processSpacesSet(long appid, Set<SpaceInstanceInfo> inSet,
			final Set<SpaceInstanceInfo> outSet) {

		if (inSet == null || outSet == null)
			return;

		for (SpaceInstanceInfo sii : inSet) {

			if (sii.getAppId() != appid)
				throw new IllegalArgumentException(
						"Specified application id doesn't match with one found in SpaceURI. Rolling back.");

			if (!outSet.add(sii))
				throw new IllegalArgumentException(
						"Duplicate entry in input and output spaces sets. Rolling back.");
		}
	}
}