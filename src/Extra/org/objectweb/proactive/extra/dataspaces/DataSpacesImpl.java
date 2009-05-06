/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.vfs.FileObject;
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
 * Implements {@link PADataSpaces} API for a pair of node and application.
 * <p>
 * Instances of this class are thread-safe. Each instance for given node and application should
 * remain valid as long as this node has Data Spaces configured, for this application. For that
 * reason, instances of this class are typically managed by {@link NodeConfigurator} and
 * {@link DataSpacesNodes} classes.
 */
public class DataSpacesImpl {
    private static final long RESOLVE_BLOCKING_RESEND_PERIOD_MILLIS = 5000;

    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES);

    private static void checkIsInputOrOutput(SpaceType type) {
        if (type == SpaceType.SCRATCH)
            throw new IllegalArgumentException("This method can be only used with input or output data space");
    }

    private static void checkIsNotNullName(String name) {
        if (name == null)
            throw new IllegalArgumentException("Input/data space name can not be null");
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
     * @see {@link PADataSpaces#resolveDefaultInput()}
     * @see {@link PADataSpaces#resolveDefaultOutput()}
     * @return FileObject received from SpacesMountManager instance
     * @throws IllegalArgumentException
     * @throws FileSystemException
     * @throws SpaceNotFoundException
     */
    public FileObject resolveDefaultInputOutput(SpaceType type) throws IllegalArgumentException,
            FileSystemException, SpaceNotFoundException {
        return resolveInputOutput(DataSpacesURI.DEFAULT_IN_OUT_NAME, type);
    }

    /**
     * Implementation (more generic) method for resolveDefaultInputBlocking and
     * resolveDefaultOutputBlocking.
     * 
     * @see {@link PADataSpaces#resolveDefaultInputBlocking(long))}
     * @see {@link PADataSpaces#resolveDefaultOutputBlocking(long))}
     * @param timeoutMillis
     * @param type
     * @return
     * @throws FileSystemException
     * @throws IllegalArgumentException
     * @throws ProActiveTimeoutException
     */
    public FileObject resolveDefaultInputOutputBlocking(long timeoutMillis, SpaceType type)
            throws IllegalArgumentException, FileSystemException, ProActiveTimeoutException {
        return resolveInputOutputBlocking(DataSpacesURI.DEFAULT_IN_OUT_NAME, timeoutMillis, type);
    }

    /**
     * Implementation (more generic) method for resolveInput and resolveOutput.
     * 
     * @see {@link PADataSpaces#resolveInput(String)}
     * @see {@link PADataSpaces#resolveOutput(String)}
     * @param name
     * @param type
     * @return
     * @throws FileSystemException
     * @throws IllegalArgumentException
     * @throws SpaceNotFoundException
     */
    public FileObject resolveInputOutput(String name, SpaceType type) throws FileSystemException,
            IllegalArgumentException, SpaceNotFoundException {
        checkIsInputOrOutput(type);
        checkIsNotNullName(name);
        final DataSpacesURI uri = DataSpacesURI.createInOutSpaceURI(appId, type, name);

        return spacesMountManager.resolveFile(uri);
    }

    /**
     * Implementation (more generic) method for resolveInputBlocking and resolveOutputBlocking.
     * 
     * @see {@link PADataSpaces#resolveInputBlocking(String, long)}
     * @see {@link PADataSpaces#resolveOutputBlocking(String, long)}
     * @param name
     * @param timeoutMillis
     * @param type
     * @return
     * @throws FileSystemException
     * @throws IllegalArgumentException
     * @throws ProActiveTimeoutException
     */
    public FileObject resolveInputOutputBlocking(String name, long timeoutMillis, SpaceType type)
            throws FileSystemException, IllegalArgumentException, ProActiveTimeoutException {
        checkIsInputOrOutput(type);
        checkIsNotNullName(name);
        if (timeoutMillis < 1)
            throw new IllegalArgumentException("Specified timeout should be positive integer");
        final DataSpacesURI uri = DataSpacesURI.createInOutSpaceURI(appId, type, name);

        final long startTime = System.currentTimeMillis();
        long currTime = startTime;
        while (currTime < startTime + timeoutMillis) {
            try {
                return spacesMountManager.resolveFile(uri);
            } catch (SpaceNotFoundException e) {
                // request processing may have taken some time
                currTime = System.currentTimeMillis();
                final long sleepTime = Math.min(RESOLVE_BLOCKING_RESEND_PERIOD_MILLIS, startTime +
                    timeoutMillis - currTime);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e1) {
                }
                currTime = System.currentTimeMillis();
            }
        }
        throw new ProActiveTimeoutException();
    }

    /**
     * @see {@link PADataSpaces#resolveScratchForAO()}
     * @return
     * @throws FileSystemException
     * @throws NotConfiguredException
     */
    public FileObject resolveScratchForAO() throws FileSystemException, NotConfiguredException {
        if (appScratchSpace == null)
            throw new NotConfiguredException("Scratch data space not configured on this node");

        final Body body = Utils.getCurrentActiveObjectBody();
        final DataSpacesURI scratchURI = appScratchSpace.getScratchForAO(body);
        try {
            return spacesMountManager.resolveFile(scratchURI);
        } catch (SpaceNotFoundException e) {
            ProActiveLogger.logImpossibleException(logger, e);
            throw new ProActiveRuntimeException("URI of scratch for Active Object can not be resolved", e);
        }
    }

    /**
     * Implementation (more generic) method for getAllKnownInputNames and getAllKnownInputNames.
     * 
     * @see {@link PADataSpaces#getAllKnownInputNames()}
     * @see {@link PADataSpaces#getAllKnownOutputNames()}
     * @param type
     * @return set of known names
     * @throws IllegalArgumentException
     */
    public Set<String> getAllKnownInputOutputNames(SpaceType type) throws IllegalArgumentException {
        checkIsInputOrOutput(type);

        final DataSpacesURI aURI = DataSpacesURI.createURI(appId, type);
        final Set<SpaceInstanceInfo> infos = spacesDirectory.lookupMany(aURI);
        final Set<String> names = new HashSet<String>();

        for (SpaceInstanceInfo sii : infos) {
            names.add(sii.getName());
        }
        return names;
    }

    /**
     * Implementation (more generic) method for resolveAllKnownInputs and resolveAllKnownOutputs.
     * 
     * @see {@link PADataSpaces#resolveAllKnownInputs()}
     * @see {@link PADataSpaces#resolveAllKnownOutputs()}
     * @param type
     * @return
     * @throws FileSystemException
     * @throws IllegalArgumentException
     */
    public Map<String, FileObject> resolveAllKnownInputsOutputs(SpaceType type) throws FileSystemException,
            IllegalArgumentException {
        checkIsInputOrOutput(type);

        final DataSpacesURI uri = DataSpacesURI.createURI(appId, type);
        final Map<DataSpacesURI, FileObject> spaces = spacesMountManager.resolveSpaces(uri);
        final Map<String, FileObject> ret = new HashMap<String, FileObject>(spaces.size());

        for (Entry<DataSpacesURI, FileObject> entry : spaces.entrySet()) {
            final String name = entry.getKey().getName();
            ret.put(name, entry.getValue());
        }
        return ret;
    }

    /**
     * @see {@link PADataSpaces#resolveFile(String)}
     * @param uri
     * @return
     * @throws MalformedURIException
     * @throws FileSystemException
     * @throws SpaceNotFoundException
     */
    public FileObject resolveFile(String uri) throws MalformedURIException, FileSystemException,
            SpaceNotFoundException {
        final DataSpacesURI spaceURI = DataSpacesURI.parseURI(uri);
        if (!spaceURI.isComplete())
            throw new MalformedURIException("Specified URI must be complete");

        return spacesMountManager.resolveFile(spaceURI);
    }

    /**
     * @see {@link PADataSpaces#getURI(FileObject)}
     * @param fileObject
     * @return
     */
    public String getURI(FileObject fileObject) {
        return fileObject.getName().getURI();
    }

    /**
     * Implementation (more generic) method for addDefaultInput and addDefaultOutput.
     * 
     * @see {@link PADataSpaces#addDefaultInput(String, String, String)}
     * @see {@link PADataSpaces#addDefaultOutput(String, String, String)}
     * @param name
     * @param url
     * @param path
     * @param type
     * @return
     * @throws SpaceAlreadyRegisteredException
     * @throws ConfigurationException
     */
    public String addDefaultInputOutput(String url, String path, SpaceType type)
            throws SpaceAlreadyRegisteredException, ConfigurationException, IllegalArgumentException {
        return addInputOutput(DataSpacesURI.DEFAULT_IN_OUT_NAME, url, path, type);
    }

    /**
     * Implementation (more generic) method for addInput and addOutput.
     * 
     * @see {@link PADataSpaces#addInput(String, String, String)}
     * @see {@link PADataSpaces#addOutput(String, String, String)}
     * @param name
     * @param url
     * @param path
     * @param type
     * @return
     * @throws SpaceAlreadyRegisteredException
     * @throws ConfigurationException
     */
    public String addInputOutput(String name, String url, String path, SpaceType type)
            throws SpaceAlreadyRegisteredException, ConfigurationException {
        final String hostname = Utils.getHostname();
        // name and type are checked here 
        final InputOutputSpaceConfiguration config = InputOutputSpaceConfiguration.createConfiguration(url,
                path, hostname, name, type);
        // url is checked here        
        final SpaceInstanceInfo spaceInstanceInfo = new SpaceInstanceInfo(appId, config);

        try {
            spacesDirectory.register(spaceInstanceInfo);
        } catch (WrongApplicationIdException x) {
            ProActiveLogger.logImpossibleException(logger, x);
            throw new ProActiveRuntimeException(
                "This application id is not registered in used naming service", x);
        }
        return spaceInstanceInfo.getMountingPoint().toString();
    }
}
