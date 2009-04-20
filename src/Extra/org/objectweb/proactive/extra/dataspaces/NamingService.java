/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.proactive.extra.dataspaces.exceptions.ApplicationAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.WrongApplicationIdException;

/**
 * Manages application register/unregister process in space directory.
 * Implements SpacesDirectory. TODO: Remote accessible.
 */
public class NamingService implements SpacesDirectory {

	private final Set<Long> registeredApplications = new HashSet<Long>();

	private final SpacesDirectoryImpl directory = new SpacesDirectoryImpl();

	/**
	 * Registers application along with its spaces definition.
	 * 
	 * @param appid
	 *            application identifier
	 * 
	 * @param spaces
	 *            bulked input and output definitions
	 * 
	 * @throws WrongApplicationIdException
	 *             When given appid doesn't match with one found in
	 *             DataSpacesURI
	 * @throws ApplicationAlreadyRegisteredException
	 *             When specified application id is already registered.
	 */
	synchronized public void registerApplication(long appid, Set<SpaceInstanceInfo> spaces)
			throws ApplicationAlreadyRegisteredException, WrongApplicationIdException {

		if (isApplicationIdRegistered(appid)) {
			throw new ApplicationAlreadyRegisteredException(
					"Application with the same application id is already registered.");
		}
		registeredApplications.add(appid);

		if (spaces != null) {
			processSpacesSet(appid, spaces);
			directory.register(spaces);
		}
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

		final Set<SpaceInstanceInfo> spaces = lookupAll(DataSpacesURI.createURI(appid));
		final Set<DataSpacesURI> uris = new HashSet<DataSpacesURI>(spaces.size());

		for (SpaceInstanceInfo sii : spaces)
			uris.add(sii.getMountingPoint());

		directory.unregister(uris);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.objectweb.proactive.extra.dataspaces.SpacesDirectoryImpl#register
	 * (org.objectweb.proactive.extra.dataspaces.SpaceInstanceInfo)
	 */
	synchronized public void register(SpaceInstanceInfo spaceInstanceInfo)
			throws WrongApplicationIdException, SpaceAlreadyRegisteredException {

		final long appid = spaceInstanceInfo.getAppId();

		if (!isApplicationIdRegistered(appid))
			throw new WrongApplicationIdException(
					"The is no application registered with specified application id.");

		directory.register(spaceInstanceInfo);
	}

	public Set<SpaceInstanceInfo> lookupAll(DataSpacesURI uri) throws IllegalArgumentException {
		return directory.lookupAll(uri);
	}

	public SpaceInstanceInfo lookupFirst(DataSpacesURI uri) throws IllegalArgumentException {
		return directory.lookupFirst(uri);
	}

	public boolean unregister(DataSpacesURI uri) {
		return directory.unregister(uri);
	}

	private boolean isApplicationIdRegistered(long appid) {
		return registeredApplications.contains(appid);
	}

	private void processSpacesSet(long appid, Set<SpaceInstanceInfo> inSet)
			throws WrongApplicationIdException {

		for (SpaceInstanceInfo sii : inSet) {

			if (sii.getAppId() != appid)
				throw new WrongApplicationIdException(
						"Specified application id doesn't match with one found in DataSpacesURI. Rolling back.");
		}
	}
}