/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces.core;

import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.dataspaces.core.naming.CachingSpacesDirectory;
import org.objectweb.proactive.extra.dataspaces.core.naming.NamingService;
import org.objectweb.proactive.extra.dataspaces.core.naming.SpacesDirectory;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException;
import org.objectweb.proactive.extra.dataspaces.vfs.VFSNodeScratchSpaceImpl;
import org.objectweb.proactive.extra.dataspaces.vfs.VFSSpacesMountManagerImpl;


/**
 * Represents immutable Data Spaces configuration for a node. It manages both node-specific and
 * application-specific configuration resulting in Data Spaces implementation for application (
 * {@link DataSpacesImpl}).
 * <p>
 * Objects life cycle:
 * <ol>
 * <li>Instance initialization by default constructor.</li>
 * <li>{@link #configureNode(Node, SpaceConfiguration)} method call for passing node-specific and
 * immutable settings. This can be called only once for each instance.</li>
 * <li>{@link #configureApplication(long, String)} method call for configuring application on a
 * node.</li>
 * <li>Obtaining {@link DataSpacesImpl} from application configuration if needed, by
 * {@link #getDataSpacesImpl()}.</li>
 * <li>Possibly subsequent {@link #configureApplication(long, String)} calls reconfiguring node for
 * given application.</li>
 * <li>Closing all created objects by {@link #close()} method call.</li>
 * </ol>
 * <p>
 * Instances of this class are thread-safe. They can be managed by {@link DataSpacesNodes} static
 * class or in some other way. It is assumed that Node's application identifier will not change
 * between {@link #configureApplication(String)} and {@link #tryCloseAppConfigurator()} calls.
 * 
 * @see DataSpacesImpl
 */
public class NodeConfigurator {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES_CONFIGURATOR);

    private boolean configured;

    private NodeScratchSpace nodeScratchSpace;

    private NodeApplicationConfigurator appConfigurator;

    private Node node;

    /**
     * Set node-specific immutable settings and initialize components. This method must be called
     * exactly once for each instance.
     * <p>
     * Scratch space configuration is checked and initialized.
     * <p>
     * State of an instance remains not configured if exception appears.
     * 
     * @param node
     *            node to configure
     * @param baseScratchConfiguration
     *            base scratch data space configuration, may be <code>null</code> if node does not
     *            provide a scratch space
     * 
     * @throws IllegalStateException
     *             when trying to reconfigure already configured instance
     * @throws ConfigurationException
     *             when configuration appears to be wrong during node scratch space initialization
     *             (e.g. capabilities checking)
     * @throws FileSystemException
     *             when VFS creation or scratch initialization fails
     */
    synchronized public void configureNode(Node node, BaseScratchSpaceConfiguration baseScratchConfiguration)
            throws IllegalStateException, FileSystemException, ConfigurationException {
        logger.debug("Configuring node for Data Spaces");
        checkNotConfigured();

        this.node = node;
        if (baseScratchConfiguration != null) {
            // TODO as provider will be implemented, we can move this check elsewhere? for now it's ok.
            if (baseScratchConfiguration.getUrl() == null) {
                logger.error("Space configuration is not complete, no remote access URL provided");
                logger.error("ProActive provider is not implemented");
                throw new ConfigurationException(
                    "Space configuration is not complete, no remote access URL provided");
            }

            final NodeScratchSpace configuringScratchSpace = new VFSNodeScratchSpaceImpl();
            configuringScratchSpace.init(node, baseScratchConfiguration);
            this.nodeScratchSpace = configuringScratchSpace;
        }
        configured = true;
        logger.info("Node configured for Data Spaces");
    }

    /**
     * Configures node for a specific application. resulting in creation of configured
     * {@link DataSpacesImpl}.
     * <p>
     * Configuration of a node for an application involves association to provided NamingService and
     * registration of application scratch space for this node, if it exists. Application identifier
     * is grabbed from current node state. That application identifier should remain stable until
     * {@link #tryCloseAppConfigurator()} call.
     * <p>
     * This method may be called several times for different applications, after node has been
     * configured through {@link #configureNode(Node, SpaceConfiguration)}. Subsequent calls will
     * close existing application-specific configuration and create a new one.
     * <p>
     * If configuration fails, instance of this class remains not configured for an application, any
     * subsequent {@link #getDataSpaceImpl()} call will throw {@link IllegalStateException} until
     * successful configuration.
     * 
     * @param namingServiceURL
     *            URL of naming service remote object for that application
     * @throws IllegalStateException
     *             when node has not been configured yet in terms of node-specific configuration
     * @throws URISyntaxException
     *             when exception occurred on namingServiceURL parsing
     * @throws ProActiveException
     *             when exception occurred during contacting with NamingService
     * @throws ConfigurationException
     *             when configuration appears to be wrong during scratch space initialization (e.g.
     *             capabilities checking)
     * @throws FileSystemException
     *             VFS related exception during scratch data space creation
     */
    synchronized public void configureApplication(String namingServiceURL) throws IllegalStateException,
            FileSystemException, ProActiveException, ConfigurationException, URISyntaxException {
        logger.debug("Configuring node for Data Spaces application");
        checkConfigured();

        tryCloseAppConfigurator();
        appConfigurator = new NodeApplicationConfigurator();
        boolean appConfigured = false;
        try {
            appConfigurator.configure(namingServiceURL);
            appConfigured = true;
        } finally {
            if (!appConfigured)
                appConfigurator = null;
        }
        logger.info("Node configured for Data Spaces application");
    }

    /**
     * Returns Data Spaces implementation for an application, if it has been successfully
     * configured.
     * 
     * @return configured implementation of Data Spaces for application or <code>null</code> when
     *         node has not been configured yet (in terms of node-specific or application-specific
     *         configuration)
     */
    synchronized public DataSpacesImpl getDataSpacesImpl() {
        if (appConfigurator == null) {
            logger.debug("Requested unavailable Data Spaces implementation for an application");
            return null;
        }
        return appConfigurator.getDataSpacesImpl();
    }

    /**
     * Closes all resources opened by this configurator, also possibly created application
     * configuration.
     * <p>
     * Any subsequent call on node configuration-specific objects after calling this method may
     * result in undefined behavior.
     * 
     * @throws IllegalStateException
     *             when node is not configured in terms of node-specific configuration
     */
    synchronized public void close() throws IllegalStateException {
        logger.debug("Closing Data Spaces node configuration");
        checkConfigured();

        tryCloseAppConfigurator();
        if (nodeScratchSpace != null)
            nodeScratchSpace.close();
        nodeScratchSpace = null;
        configured = false;
        logger.info("Data Space node configuration closed, resources released");
    }

    /**
     * Closes application-specific configuration when needed (there is one opened).
     * <p>
     * That involves unregistering application scratch space from NamingService and closing all
     * objects configured for produced {@link DataSpacesImpl}. {@link DataSpacesImpl} will not be
     * usable after this call.
     * <p>
     * If no application is configured, it does nothing. If closing fails, application-specific
     * configuration will be silently deleted.
     */
    public synchronized void tryCloseAppConfigurator() {
        if (appConfigurator == null)
            return;

        logger.debug("Closing Data Spaces application node configuration");
        appConfigurator.close();
        appConfigurator = null;
        logger.info("Closed Data Spaces application node configuration");
    }

    private void checkConfigured() throws IllegalStateException {
        if (!configured) {
            logger.error("Attempting to perform operation on not configured node");
            throw new IllegalStateException("Node is not configured for Data Spaces");
        }
    }

    private void checkNotConfigured() throws IllegalStateException {
        if (configured) {
            logger.error("Attempting to configure already configured node");
            throw new IllegalStateException("Node is already configured for Data Spaces");
        }
    }

    private class NodeApplicationConfigurator {

        private SpacesMountManager spacesMountManager;

        private ApplicationScratchSpace applicationScratchSpace;

        private SpacesDirectory cachingDirectory;

        private DataSpacesImpl impl;

        private void configure(String namingServiceURL) throws FileSystemException, URISyntaxException,
                ProActiveException, ConfigurationException {

            // create naming service stub with URL and decorate it with local cache
            // use local variables so GC can collect them if something fails
            final NamingService namingService;
            try {
                namingService = NamingService.createNamingServiceStub(namingServiceURL);
            } catch (ProActiveException x) {
                logger.error("Could not access Naming Service", x);
                throw x;
            } catch (URISyntaxException x) {
                logger.error("Wrong Naming Service URI", x);
                throw x;
            }
            final CachingSpacesDirectory cachingDir = new CachingSpacesDirectory(namingService);

            // create scratch data space for this application and register it
            if (nodeScratchSpace != null) {
                applicationScratchSpace = nodeScratchSpace.initForApplication();
                final SpaceInstanceInfo scratchInfo = applicationScratchSpace.getSpaceInstanceInfo();

                boolean registered = false;
                try {
                    cachingDir.register(scratchInfo);
                    registered = true;
                    logger.info("Scratch space for application registered");
                } finally {
                    if (!registered) {
                        logger.error("Could not register application scratch space to Naming Service");
                        nodeScratchSpace.close();
                    }
                }
            }
            // no exception can be thrown since now
            cachingDirectory = cachingDir;

            // create VFSSpacesMountManagerImpl
            spacesMountManager = new VFSSpacesMountManagerImpl(cachingDirectory);

            // create implementation object connected to the application's
            // configuration
            impl = new DataSpacesImpl(node, spacesMountManager, cachingDirectory, applicationScratchSpace);
        }

        private DataSpacesImpl getDataSpacesImpl() {
            return impl;
        }

        private void close() {
            spacesMountManager.close();
            if (applicationScratchSpace != null) {
                cachingDirectory.unregister(applicationScratchSpace.getSpaceMountingPoint());
                try {
                    applicationScratchSpace.close();
                } catch (FileSystemException x) {
                    ProActiveLogger.logEatedException(logger,
                            "Could not close correctly application scratch space", x);
                }
                applicationScratchSpace = null;
            }
        }
    }
}