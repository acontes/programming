/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.dataspaces.exceptions.ApplicationAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.WrongApplicationIdException;


/**
 * Naming Service for Data Spaces subsystem.
 * <p>
 * It provides coarse-grained directory of registered applications and their data spaces with access
 * information.
 */
public class NamingService implements SpacesDirectory {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES_NAMING_SERVICE);

    private static void checkApplicationSpaces(long appId, Set<SpaceInstanceInfo> inSet)
            throws WrongApplicationIdException {
        for (SpaceInstanceInfo sii : inSet) {
            if (sii.getAppId() != appId)
                throw new WrongApplicationIdException(
                    "Specified application id doesn't match with one found in DataSpacesURI. Rolling back.");
        }
    }

    private final Set<Long> registeredApplications = new HashSet<Long>();

    private final SpacesDirectoryImpl directory = new SpacesDirectoryImpl();

    /**
     * Registers application along with its spaces definition.
     * 
     * @param appId
     *            application identifier, must be unique
     * @param spaces
     *            bulked input and output spaces definitions for that application or
     *            <code>null</code> if there is no input/output space
     * @throws WrongApplicationIdException
     *             When given appId doesn't match one found in DataSpacesURI of spaces to register
     *             for application.
     * @throws ApplicationAlreadyRegisteredException
     *             When specified application id is already registered.
     */
    synchronized public void registerApplication(long appId, Set<SpaceInstanceInfo> spaces)
            throws ApplicationAlreadyRegisteredException, WrongApplicationIdException {
        logger.debug("Registering application with id " + appId);

        if (isApplicationIdRegistered(appId)) {
            throw new ApplicationAlreadyRegisteredException(
                "Application with the same application id is already registered.");
        }

        if (spaces != null)
            checkApplicationSpaces(appId, spaces);

        registeredApplications.add(appId);
        logger.info("Registered application with id " + appId);
        if (spaces != null) {
            directory.register(spaces);
            if (logger.isInfoEnabled()) {
                for (final SpaceInstanceInfo info : spaces)
                    logger.info("Registered space: " + info);
            }
        }
    }

    /**
     * Unregisters application with specified identifier together with all spaces registered by this
     * application.
     * 
     * @param appId
     *            application identifier
     * @throws WrongApplicationIdException
     *             when specified application id is not registered
     */
    synchronized public void unregisterApplication(long appId) throws WrongApplicationIdException {
        logger.debug("Unregistering application with id " + appId);

        final boolean found = registeredApplications.remove(appId);
        if (!found)
            throw new WrongApplicationIdException("Application with specified appid is not registered.");

        final Set<SpaceInstanceInfo> spaces = lookupAll(DataSpacesURI.createURI(appId));

        if (spaces == null)
            return;

        final Set<DataSpacesURI> uris = new HashSet<DataSpacesURI>(spaces.size());

        for (SpaceInstanceInfo sii : spaces)
            uris.add(sii.getMountingPoint());

        directory.unregister(uris);
        if (logger.isInfoEnabled()) {
            for (final DataSpacesURI uri : uris)
                logger.info("Unregistered space: " + uri);
            logger.info("Unregistered application with id " + appId);
        }
    }

    /**
     * Registers provided data space instance for already registered application. If mounting point
     * of that space instance has been already in the directory, an exception is raised as directory
     * is append-only.
     * <p>
     * Note that this method has more constrained contract than
     * {@link SpacesDirectory#register(SpaceInstanceInfo)} regarding application id.
     * 
     * @param spaceInstanceInfo
     *            space instance info to register (contract: SpaceInstanceInfo mounting point should
     *            be complete)
     * @throws WrongApplicationIdException
     *             when directory is aware of all registered applications and there is no such
     *             application for SpaceInstanceInfo being registered
     * @throws SpaceAlreadyRegisteredException
     *             when directory already contains any space instance under specified mounting point
     * @see SpacesDirectory#register(SpaceInstanceInfo)
     */
    synchronized public void register(SpaceInstanceInfo spaceInstanceInfo)
            throws WrongApplicationIdException, SpaceAlreadyRegisteredException {
        logger.debug("Registering space: " + spaceInstanceInfo);

        final long appid = spaceInstanceInfo.getAppId();

        if (!isApplicationIdRegistered(appid))
            throw new WrongApplicationIdException(
                "There is no application registered with specified application id.");

        directory.register(spaceInstanceInfo);
        logger.info("Registered space: " + spaceInstanceInfo);
    }

    public Set<SpaceInstanceInfo> lookupAll(DataSpacesURI uri) throws IllegalArgumentException {
        if (logger.isTraceEnabled())
            logger.trace("LookupAll query for: " + uri);
        return directory.lookupAll(uri);
    }

    public SpaceInstanceInfo lookupFirst(DataSpacesURI uri) throws IllegalArgumentException {
        if (logger.isTraceEnabled())
            logger.trace("Lookup query for: " + uri);
        return directory.lookupFirst(uri);
    }

    public boolean unregister(DataSpacesURI uri) {
        final boolean result = directory.unregister(uri);
        logger.info("Unregistered space: " + uri);
        return result;
    }

    private boolean isApplicationIdRegistered(long appid) {
        return registeredApplications.contains(appid);
    }
}