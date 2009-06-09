/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.ProActiveTimeoutException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceNotFoundException;
import org.objectweb.proactive.extra.dataspaces.exceptions.WrongApplicationIdException;


/**
 * Implements {@link PADataSpaces} API for a pair of node and application (with its identifier).
 * <p>
 * Instances of this class are thread-safe. Each instance for given node and application should
 * remain valid as long as this node has Data Spaces configured, for this application, with
 * particular application identifier set on Node during that time. For that reason, instances of
 * this class are typically managed by {@link NodeConfigurator} and {@link DataSpacesNodes} classes.
 */
public class DataSpacesImpl {
    private static final long RESOLVE_BLOCKING_RESEND_PERIOD_MILLIS = 5000;

    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES);

    /**
     * Implementation method for {@link PADataSpaces#getURI(DataSpacesFileObject)}.
     * 
     * @param fileObject
     * @return
     * @see {@link PADataSpaces#getURI(DataSpacesFileObject)}
     */
    public static String getURI(DataSpacesFileObject fileObject) {
        return fileObject.getURI();
    }

    /**
     * @throws ConfigurationException
     *             when expected capabilities are not fulfilled
     */
    private static void checkCapabilitiesOrWound(DataSpacesFileObjectImpl fo, SpaceType type)
            throws ConfigurationException {
        
        Capability[] expected = PADataSpaces.getCapabilitiesForSpaceType(type);

        if (logger.isTraceEnabled())
            logger.trace(String.format("Checking FS capabilities (count: %d) for %s type", expected.length,
                    type));

        try {
            Utils.assertCapabilitiesMatch(expected, fo);
        } catch (ConfigurationException x) {
            logger.error("Resolved space's file system: " + x.getMessage());
            throw x;
        }
    }

    private static void checkIsInputOrOutput(SpaceType type) {
        if (type == SpaceType.SCRATCH) {
            logger.debug("Wrong space type provided to the input/output-related method: " + type);
            throw new IllegalArgumentException("This method can be only used with input or output data space");
        }
    }

    private static void checkIsNotNullName(String name) {
        if (name == null) {
            logger.debug("Null name provided to the input/output-related method");
            throw new IllegalArgumentException("Input/data space name can not be null");
        }
    }

    private static void setFileObjectOwner(final DataSpacesFileObjectImpl fo) {
        final String aoId = Utils.getActiveObjectId(Utils.getCurrentActiveObjectBody());
        fo.setOwnerActiveObjectId(aoId);
    }

    private final SpacesMountManager spacesMountManager;

    private final SpacesDirectory spacesDirectory;

    private final ApplicationScratchSpace appScratchSpace;

    private final long appId;

    /**
     * Create Data Spaces implementation instance. It remains valid as provided services remain
     * valid.
     * 
     * @param node
     *            node configured for Data Spaces application
     * @param smm
     *            spaces mount manager for this application
     * @param sd
     *            spaces directory for this application
     * @param ass
     *            application scratch space for this application; may be <code>null</code> if not
     *            available
     */
    public DataSpacesImpl(Node node, SpacesMountManager smm, SpacesDirectory sd, ApplicationScratchSpace ass) {
        appScratchSpace = ass;
        spacesDirectory = sd;
        spacesMountManager = smm;
        this.appId = Utils.getApplicationId(node);
    }

    /**
     * Implementation (more generic) method for resolveDefaultInput and resolveDefaultOutput.
     * 
     * @param path
     *            of a file inside a data space
     * @return DataSpacesFileObject received from SpacesMountManager instance
     * @throws IllegalArgumentException
     * @throws FileSystemException
     * @throws SpaceNotFoundException
     * @throws ConfigurationException
     * @see {@link PADataSpaces#resolveDefaultInput()}
     * @see {@link PADataSpaces#resolveDefaultOutput()}
     */
    public DataSpacesFileObject resolveDefaultInputOutput(SpaceType type, String path)
            throws IllegalArgumentException,
            FileSystemException, SpaceNotFoundException, ConfigurationException {
        return resolveInputOutput(PADataSpaces.DEFAULT_IN_OUT_NAME, type, path);
    }

    /**
     * Implementation (more generic) method for resolveDefaultInputBlocking and
     * resolveDefaultOutputBlocking.
     * 
     * @param timeoutMillis
     * @param type
     * @param path
     *            of a file inside a data space
     * @return
     * @throws FileSystemException
     * @throws IllegalArgumentException
     * @throws ProActiveTimeoutException
     * @throws ConfigurationException
     * @see {@link PADataSpaces#resolveDefaultInputBlocking(long))}
     * @see {@link PADataSpaces#resolveDefaultOutputBlocking(long))}
     */
    public DataSpacesFileObject resolveDefaultInputOutputBlocking(long timeoutMillis, SpaceType type,
            String path)
            throws IllegalArgumentException, FileSystemException, ProActiveTimeoutException,
            ConfigurationException {
        return resolveInputOutputBlocking(PADataSpaces.DEFAULT_IN_OUT_NAME, timeoutMillis, type, path);
    }

    /**
     * Implementation (more generic) method for resolveInput and resolveOutput.
     * 
     * @param name
     * @param type
     * @param path
     *            of a file inside a data space
     * @return
     * @throws FileSystemException
     * @throws IllegalArgumentException
     * @throws SpaceNotFoundException
     * @throws ConfigurationException
     * @see {@link PADataSpaces#resolveInput(String)}
     * @see {@link PADataSpaces#resolveOutput(String)}
     */
    public DataSpacesFileObject resolveInputOutput(String name, SpaceType type, String path)
            throws FileSystemException, IllegalArgumentException, SpaceNotFoundException,
            ConfigurationException {
        if (logger.isTraceEnabled())
            logger.trace(String.format("Resolving request for %s with name %s", type, name));

        checkIsInputOrOutput(type);
        checkIsNotNullName(name);
        final DataSpacesURI uri;
        try {
            uri = DataSpacesURI.createInOutSpaceURI(appId, type, name, path);
        } catch (IllegalArgumentException x) {
            logger.debug("Illegal specification for resolve " + type, x);
            throw x;
        }

        try {
            final DataSpacesFileObjectImpl fo = spacesMountManager.resolveFile(uri);
            if (logger.isTraceEnabled())
                logger.trace(String.format("Resolved request for %s with name %s (%s)", type, name, uri));

            checkCapabilitiesOrWound(fo, type);
            setFileObjectOwner(fo);
            return fo;
        } catch (SpaceNotFoundException x) {
            logger.debug("Space not found for input/output space with URI: " + uri, x);
            throw x;
        } catch (FileSystemException x) {
            logger.debug("VFS-level problem during resolving input/output space", x);
            throw x;
        }
    }

    /**
     * Implementation (more generic) method for resolveInputBlocking and resolveOutputBlocking.
     * 
     * @param name
     * @param timeoutMillis
     * @param type
     * @param path
     *            of a file inside a data space
     * @return
     * @throws FileSystemException
     * @throws IllegalArgumentException
     * @throws ProActiveTimeoutException
     * @throws ConfigurationException
     * @see {@link PADataSpaces#resolveInputBlocking(String, long)}
     * @see {@link PADataSpaces#resolveOutputBlocking(String, long)}
     */
    public DataSpacesFileObject resolveInputOutputBlocking(String name, long timeoutMillis, SpaceType type,
            String path)
            throws FileSystemException, IllegalArgumentException, ProActiveTimeoutException,
            ConfigurationException {
        if (logger.isTraceEnabled())
            logger.trace(String.format("Resolving blocking request for %s with name %s", type, name));

        checkIsInputOrOutput(type);
        checkIsNotNullName(name);
        if (timeoutMillis < 1) {
            logger.debug("Illegal non-positive timeout specified for blocking resolve request");
            throw new IllegalArgumentException("Specified timeout should be positive integer");
        }
        final DataSpacesURI uri;
        try {
            uri = DataSpacesURI.createInOutSpaceURI(appId, type, name, path);
        } catch (IllegalArgumentException x) {
            logger.debug("Illegal specification for resolve " + type, x);
            throw x;
        }

        final long startTime = System.currentTimeMillis();
        long currTime = startTime;
        while (currTime < startTime + timeoutMillis) {
            try {
                final DataSpacesFileObjectImpl fo = spacesMountManager.resolveFile(uri);
                if (logger.isTraceEnabled()) {
                    final String message = String.format(
                            "Resolved blocking request for %s with name %s (%s)", type, name, uri);
                    logger.trace(message);
                }
                checkCapabilitiesOrWound(fo, type);
                setFileObjectOwner(fo);
                return fo;
            } catch (SpaceNotFoundException e) {
                logger.debug("Space not found for blocking try for input/output space with URI: " + uri, e);

                // request processing may have taken some time
                currTime = System.currentTimeMillis();
                final long sleepTime = Math.min(RESOLVE_BLOCKING_RESEND_PERIOD_MILLIS, startTime +
                    timeoutMillis - currTime);
                try {
                    if (logger.isTraceEnabled())
                        logger.trace("Going sleeping for " + sleepTime);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e1) {
                }
                currTime = System.currentTimeMillis();
            } catch (FileSystemException x) {
                logger.debug("VFS-level problem during blocking resolving input/output space", x);
                throw x;
            }
        }

        if (logger.isDebugEnabled()) {
            final String message = String.format(
                    "Timeout expired for blocking resolve for %s with name %s (%s)", type, name, uri);
            logger.debug(message);
        }
        throw new ProActiveTimeoutException();
    }

    /**
     * @param path
     *            of a file inside a data space
     * @return
     * @throws FileSystemException
     * @throws NotConfiguredException
     * @throws ConfigurationException
     * @see {@link PADataSpaces#resolveScratchForAO()}
     */
    public DataSpacesFileObject resolveScratchForAO(String path) throws FileSystemException,
            NotConfiguredException,
            ConfigurationException {
        logger.trace("Resolving scratch for an Active Object");
        if (appScratchSpace == null) {
            logger.debug("Request scratch data space for AO on node without scratch space configured");
            throw new NotConfiguredException("Scratch data space not configured on this node");
        }

        final Body body = Utils.getCurrentActiveObjectBody();
        try {
            final DataSpacesURI scratchURI = appScratchSpace.getScratchForAO(body);
            final DataSpacesURI queryURI = scratchURI.withPath(path);
            final DataSpacesFileObjectImpl fo = spacesMountManager.resolveFile(queryURI);

            if (logger.isTraceEnabled())
                logger.trace("Resolved scratch for an Active Object: " + queryURI);

            checkCapabilitiesOrWound(fo, SpaceType.SCRATCH);
            setFileObjectOwner(fo);
            return fo;
        } catch (SpaceNotFoundException e) {
            ProActiveLogger.logImpossibleException(logger, e);
            throw new ProActiveRuntimeException("URI of scratch for Active Object can not be resolved", e);
        } catch (FileSystemException x) {
            logger.debug("VFS-level problem during resolving scratch fo AO: ", x);
            throw x;
        }
    }

    /**
     * Implementation (more generic) method for getAllKnownInputNames and getAllKnownInputNames.
     * 
     * @param type
     * @return set of known names
     * @throws IllegalArgumentException
     * @see {@link PADataSpaces#getAllKnownInputNames()}
     * @see {@link PADataSpaces#getAllKnownOutputNames()}
     */
    public Set<String> getAllKnownInputOutputNames(SpaceType type) throws IllegalArgumentException {
        if (logger.isTraceEnabled())
            logger.trace(String.format("Resolving known %s names: ", type));
        checkIsInputOrOutput(type);

        final DataSpacesURI aURI = DataSpacesURI.createURI(appId, type);
        final Set<SpaceInstanceInfo> infos = spacesDirectory.lookupMany(aURI);
        final Set<String> names = new HashSet<String>();

        for (SpaceInstanceInfo sii : infos) {
            names.add(sii.getName());
        }
        if (logger.isTraceEnabled())
            logger.trace(String.format("Resolved known %s names: %s", type, new ArrayList<String>(names)));
        return names;
    }

    /**
     * Implementation (more generic) method for resolveAllKnownInputs and resolveAllKnownOutputs.
     * 
     * @param type
     * @return
     * @throws FileSystemException
     * @throws IllegalArgumentException
     * @throws ConfigurationException
     * @see {@link PADataSpaces#resolveAllKnownInputs()}
     * @see {@link PADataSpaces#resolveAllKnownOutputs()}
     */
    public Map<String, DataSpacesFileObject> resolveAllKnownInputsOutputs(SpaceType type)
            throws FileSystemException,
            IllegalArgumentException, ConfigurationException {
        if (logger.isTraceEnabled())
            logger.trace(String.format("Resolving known %s spaces: ", type));
        checkIsInputOrOutput(type);

        final DataSpacesURI uri = DataSpacesURI.createURI(appId, type);
        final Map<DataSpacesURI, DataSpacesFileObjectImpl> spaces;
        try {
            spaces = spacesMountManager.resolveSpaces(uri);
        } catch (FileSystemException x) {
            logger.debug(String.format("VFS-level problem during resolving known %s spaces: ", type), x);
            throw x;
        }

        final Map<String, DataSpacesFileObject> ret = new HashMap<String, DataSpacesFileObject>(spaces.size());

        for (Entry<DataSpacesURI, DataSpacesFileObjectImpl> entry : spaces.entrySet()) {
            final String name = entry.getKey().getName();
            DataSpacesFileObjectImpl fo = entry.getValue();

            checkCapabilitiesOrWound(fo, type);
            setFileObjectOwner(fo);
            ret.put(name, fo);
        }

        if (logger.isTraceEnabled()) {
            final ArrayList<String> namesList = new ArrayList<String>(ret.keySet());
            logger.trace(String.format("Resolved known %s spaces: %s", type, namesList));
        }

        return ret;
    }

    /**
     * @param uri
     * @return
     * @throws MalformedURIException
     * @throws FileSystemException
     * @throws SpaceNotFoundException
     * @throws ConfigurationException
     * @see {@link PADataSpaces#resolveFile(String)}
     */
    public DataSpacesFileObject resolveFile(String uri) throws MalformedURIException, FileSystemException,
            SpaceNotFoundException, ConfigurationException {
        if (logger.isTraceEnabled())
            logger.trace("Resolving file: " + uri);

        try {
            final DataSpacesURI spaceURI = DataSpacesURI.parseURI(uri);
            if (!spaceURI.isSuitableForHavingPath())
                throw new MalformedURIException("Specified URI represents internal high-level directories");

            final DataSpacesFileObjectImpl fo = spacesMountManager.resolveFile(spaceURI);
            SpaceType type = spaceURI.getSpaceType(); // as isComplete cannot be null

            if (logger.isTraceEnabled())
                logger.trace("Resolved file: " + uri);

            // CCheck if specified URI refers to scratch, and if so, who is its owner.
            if (type == SpaceType.SCRATCH && !Utils.isScratchOwnedByCallingThread(spaceURI))
                type = SpaceType.INPUT;

            checkCapabilitiesOrWound(fo, type);
            setFileObjectOwner(fo);
            return fo;
        } catch (MalformedURIException x) {
            logger.debug("Can not resolve malformed URI: " + uri, x);
            throw x;
        } catch (SpaceNotFoundException x) {
            logger.debug("Can not find space for URI: " + uri, x);
            throw x;
        } catch (FileSystemException x) {
            logger.debug("VFS-level problem during resolving URI: " + uri, x);
            throw x;
        }
    }

    /**
     * Implementation (more generic) method for addDefaultInput and addDefaultOutput.
     * 
     * @param name
     * @param url
     * @param path
     * @param type
     * @return
     * @throws SpaceAlreadyRegisteredException
     * @throws ConfigurationException
     * @see {@link PADataSpaces#addDefaultInput(String, String, String)}
     * @see {@link PADataSpaces#addDefaultOutput(String, String, String)}
     */
    public String addDefaultInputOutput(String url, String path, SpaceType type)
            throws SpaceAlreadyRegisteredException, ConfigurationException, IllegalArgumentException {
        return addInputOutput(PADataSpaces.DEFAULT_IN_OUT_NAME, url, path, type);
    }

    /**
     * Implementation (more generic) method for addInput and addOutput.
     * 
     * @param name
     * @param url
     * @param path
     * @param type
     * @return
     * @throws SpaceAlreadyRegisteredException
     * @throws ConfigurationException
     * @see {@link PADataSpaces#addInput(String, String, String)}
     * @see {@link PADataSpaces#addOutput(String, String, String)}
     */
    public String addInputOutput(String name, String url, String path, SpaceType type)
            throws SpaceAlreadyRegisteredException, ConfigurationException {
        logger.debug("Adding input/output data space");

        final SpaceInstanceInfo spaceInstanceInfo;
        try {
            String hostname = null;
            if (path == null)
                hostname = Utils.getHostname();

            // name and type are checked here 
            final InputOutputSpaceConfiguration config = InputOutputSpaceConfiguration.createConfiguration(
                    url, path, hostname, name, type);
            // url is checked here        
            spaceInstanceInfo = new SpaceInstanceInfo(appId, config);
        } catch (ConfigurationException x) {
            logger.debug("User-added input/output has wrong configuration", x);
            throw x;
        }

        try {
            spacesDirectory.register(spaceInstanceInfo);
        } catch (WrongApplicationIdException x) {
            ProActiveLogger.logImpossibleException(logger, x);
            throw new ProActiveRuntimeException(
                "This application id is not registered in used naming service", x);
        } catch (SpaceAlreadyRegisteredException x) {
            logger.debug(String.format("User-added space %s is already registered", spaceInstanceInfo
                    .getMountingPoint()), x);
            throw x;
        }

        if (logger.isInfoEnabled())
            logger.info("Added input/output data space: " + spaceInstanceInfo);
        return spaceInstanceInfo.getMountingPoint().toString();
    }
}
