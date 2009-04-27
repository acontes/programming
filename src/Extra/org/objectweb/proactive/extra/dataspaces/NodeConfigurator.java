/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.net.URISyntaxException;

import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;


/**
 * Represents immutable Data Spaces configuration for a node. It manages both node-specific and
 * application-specific configuration resulting in Data Spaces implementation for application (
 * {@link DataSpacesImpl}).
 * <p>
 * Objects life cycle:
 * <ol>
 * <li>Instance initialization by default constructor.</li>
 * <li>{@link #configureNode(SpaceConfiguration, Node)} method call for passing node-specific and
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
 * class or in some other way.
 * 
 * @see DataSpacesImpl
 */
public class NodeConfigurator {

    private boolean configured;

    private DefaultFileSystemManager manager;

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
     * @param baseScratchConfiguration
     *            base scratch data space configuration, may be <code>null</code> if node does not
     *            provide a scratch space
     * @param node
     *            node to configure
     * @throws IllegalStateException
     *             when trying to reconfigure already configured instance
     * @throws ConfigurationException
     *             when configuration appears to be wrong during node scratch space initialization
     *             (e.g. capabilities checking)
     * @throws FileSystemException
     *             when VFS configuration creation or scratch initialization fails
     */
    synchronized public void configureNode(BaseScratchSpaceConfiguration baseScratchConfiguration, Node node)
            throws IllegalStateException, FileSystemException, ConfigurationException {
        checkNotConfigured();

        this.manager = VFSFactory.createDefaultFileSystemManager();
        this.node = node;
        if (baseScratchConfiguration != null) {
            nodeScratchSpace = new NodeScratchSpace(node, baseScratchConfiguration);
            try {
                nodeScratchSpace.init(manager);
                configured = true;
            } finally {
                if (!configured) {
                    manager.close();
                    manager = null;
                    nodeScratchSpace = null;
                }
            }
        }
    }

    /**
     * Configures node for a specific application. resulting in creation of configured
     * {@link DataSpacesImpl}.
     * <p>
     * Configuration of a node for an application involves association to provided NamingService and
     * registration of application scratch space for this node, if it exists.
     * <p>
     * This method may be called several times for different applications, after node has been
     * configured through {@link #configureNode(SpaceConfiguration, Node)}. Subsequent calls will
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
        checkConfigured();

        tryCloseAppConfigurator();
        appConfigurator = new NodeApplicationConfigurator();
        boolean appConfigured = false;
        try {
            appConfigurator.configureApplication(namingServiceURL);
            appConfigured = true;
        } finally {
            if (!appConfigured)
                appConfigurator = null;
        }
    }

    /**
     * Returns Data Spaces implementation for an application, if it has been successfully
     * configured.
     * 
     * @return configured implementation of Data Spaces for application or <code>null</code> when
     *         node has not been configured yet (in terms of node-specific or application-specific
     *         configuration)
     */
    synchronized public DataSpacesImpl getDataSpacesImpl() throws IllegalStateException {
        if (appConfigurator == null)
            return null;
        return appConfigurator.getDataSpacesImpl();
    }

    /**
     * Closes all resources opened by this configurator, also possibly created application
     * configuration.
     * <p>
     * More than one call of this method may result in undefined behavior. Any subsequent call to
     * node configuration-specific objects may result in undefined behavior.
     * 
     * @throws IllegalStateException
     *             when node has not been configured yet in terms of node-specific configuration
     */
    synchronized public void close() throws IllegalStateException {
        checkConfigured();

        tryCloseAppConfigurator();
        try {
            if (nodeScratchSpace != null) {
                nodeScratchSpace.close();
            }
        } catch (FileSystemException e) {
            // TODO log or throw
        }
        manager.close();
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
     * 
     * @throws FileSystemException
     *             VFS related exception during scratch space cleaning
     */
    public synchronized void tryCloseAppConfigurator() {
        if (appConfigurator == null)
            return;

        try {
            appConfigurator.close();
        } catch (FileSystemException e) {
            // TODO log
        } finally {
            appConfigurator = null;
        }
    }

    private void checkConfigured() throws IllegalStateException {
        if (!configured)
            throw new IllegalStateException("Node is not configured for Data Spaces");
    }

    private void checkNotConfigured() throws IllegalStateException {
        if (configured)
            throw new IllegalStateException("Node is already configured for Data Spaces");
    }

    public class NodeApplicationConfigurator {

        private SpacesMountManager spacesMountManager;

        private ApplicationScratchSpace applicationScratchSpace;

        private SpacesDirectory cachingDirectory;

        private DataSpacesImpl impl;

        private void configureApplication(String namingServiceURL) throws FileSystemException,
                URISyntaxException, ProActiveException, ConfigurationException {

            // create naming service stub with URL and decorate it with local cache
            // use local variables so GC can collect them if something fails
            final NamingService namingService = Utils.createNamingServiceStub(namingServiceURL);
            final CachingSpacesDirectory cachingDir = new CachingSpacesDirectory(namingService);

            // create scratch data space for this application and register it
            if (nodeScratchSpace != null) {
                applicationScratchSpace = nodeScratchSpace.initForApplication();
                final SpaceInstanceInfo scratchInfo = applicationScratchSpace.getSpaceInstanceInfo();

                boolean registered = false;
                try {
                    cachingDir.register(scratchInfo);
                    registered = true;
                } finally {
                    if (!registered)
                        nodeScratchSpace.close();
                }
            }
            // no exception can be thrown since now
            cachingDirectory = cachingDir;

            // create SpacesMountManager
            spacesMountManager = new SpacesMountManager(manager, cachingDirectory);

            // create implementation object connected to the application's
            // configuration
            impl = new DataSpacesImpl(node, spacesMountManager, cachingDirectory, applicationScratchSpace);
        }

        private DataSpacesImpl getDataSpacesImpl() {
            return impl;
        }

        private void close() throws FileSystemException {
            spacesMountManager.close();
            if (applicationScratchSpace != null) {
                cachingDirectory.unregister(applicationScratchSpace.getSpaceMountingPoint());
                try {
                    applicationScratchSpace.close();
                } finally {
                    applicationScratchSpace = null;
                }
            }
        }
    }
}
