/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.net.URISyntaxException;

import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extra.dataspaces.SpaceConfiguration.ScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.exceptions.AlreadyConfiguredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;

/**
 * Represents immutable Data Spaces node-specific configuration. Also produces
 * and manages application-specific configuration -
 * {@link NodeApplicationConfigurator}, indirectly Data Spaces implementation
 * for application.
 * <p>
 * Objects life cycle:
 * <ol>
 * <li>Instance initialization by default constructor.</li>
 * <li>{@link #configureNode(SpaceConfiguration, Node)} method call for passing
 * node-specific and immutable settings. This can be called only once for each
 * instance.</li>
 * <li>{@link #configureApplication(long, String)} method call for configuring
 * application on a node. This involves creation and configuration of
 * {@link NodeApplicationConfigurator}.</li>
 * <li>Obtaining {@link DataSpacesImpl} from application configuration if
 * needed, by {@link #getDataSpacesImpl()}.</li>
 * <li>Closing all created objects by {@link #close()} method call.</li>
 * </ol>
 * <p>
 * Instances of this class are thread-safe. Instances of this class may be
 * managed by {@link DataSpacesNodes} static class.
 * 
 * @see NodeApplicationConfigurator
 * @see DataSpacesImpl
 */
public class NodeConfigurator {

	private boolean configured = false;

	private DefaultFileSystemManager manager = null;

	private NodeScratchSpace nodeScratchSpace;

	private NodeApplicationConfigurator appConfigurator = null;

	/**
	 * Set node-specific immutable settings and initialize components. This
	 * method must be called exactly once for each instance.
	 * 
	 * Scratch space configuration is checked and initialized.
	 * 
	 * @param scratchConfiguration
	 *            scratch data space configuration, may be null if node does not
	 *            provide scratch
	 * @param node
	 *            node to configure
	 * @throws AlreadyConfiguredException
	 *             when trying to reconfigure already configured instance
	 * @throws FileSystemException
	 *             when VFS configuration creation or scratch initialization
	 *             fails
	 * @throws ConfigurationException
	 *             something failed during node scratch space configuration (ex.
	 *             capabilities checking)
	 */
	// FIXME what is the state of configurator after all these exceptions
	synchronized public void configureNode(ScratchSpaceConfiguration scratchConfiguration, Node node)
			throws AlreadyConfiguredException, FileSystemException, ConfigurationException {
		checkNotConfigured();

		manager = VFSFactory.createDefaultFileSystemManager();

		if (scratchConfiguration != null) {
			nodeScratchSpace = new NodeScratchSpace(scratchConfiguration, manager, node);
			nodeScratchSpace.init();
		}
		configured = true;
	}

	/**
	 * Configures node for a specific application.
	 * 
	 * This method may be called several times for different applications, after
	 * node has been configured through
	 * {@link #configureNode(SpaceConfiguration, Node)}. Subsequent calls will
	 * close existing application-specific configuration and create a new one.
	 * 
	 * @param appid
	 *            application id
	 * @param namingServiceURL
	 *            URL of naming service remote object for that application
	 * @throws URISyntaxException
	 *             when exception occurred on namingServiceURL parsing
	 * @throws ProActiveException
	 *             occurred during contacting with NamingService
	 * @throws FileSystemException
	 *             VFS related exception during scratch space creation
	 */
	// FIXME what is the state of configurator after all these exceptions
	synchronized public void configureApplication(long appid, String namingServiceURL)
			throws ProActiveException, URISyntaxException, FileSystemException {
		checkConfigured();
		tryCloseAppConfigurator();

		appConfigurator = new NodeApplicationConfigurator();
		try {
			appConfigurator.configureApplication(appid, namingServiceURL, manager, nodeScratchSpace);
		} catch (AlreadyConfiguredException e) {
			// can't happen for our usage
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns Data Spaces implementation for application, if it is configured.
	 * 
	 * @return configured implementation of Data Spaces for application
	 * @throws NotConfiguredException
	 *             when node has not been configured yet (in terms of
	 *             node-specific or application-specific configuration)
	 */
	synchronized public DataSpacesImpl getDataSpacesImpl() throws NotConfiguredException {
		if (appConfigurator == null)
			throw new NotConfiguredException("Node is not configured for Data Spaces application");
		return appConfigurator.getDataSpacesImpl();
	}

	/**
	 * Closes all resources opened by this configurator, also possibly created
	 * application configuration.
	 * 
	 * More than one call of this method may result in undefined behavior.
	 * 
	 * @throws NotConfiguredException
	 *             when node has not been configured yet.
	 * @throws FileSystemException
	 *             VFS related exception during scratch space cleaning
	 */
	// FIXME try to clean up as much as possible or even just log exceptions(?)
	// FIXME what is the state of configurator after all these exceptions
	synchronized public void close() throws NotConfiguredException, FileSystemException {
		checkConfigured();
		tryCloseAppConfigurator();
		if (nodeScratchSpace != null) {
			nodeScratchSpace.close();
		}
		manager.close();
	}

	/**
	 * Closes application-specific configuration when needed (there is one
	 * opened).
	 * 
	 * If no application is configured, it does nothing.
	 *
	 * @throws FileSystemException
	 *             VFS related exception during scratch space cleaning
	 */
	// FIXME try to clean up as much as possible or even just log exceptions(?)
	// FIXME what is the state of configurator after all these exceptions
	public synchronized void tryCloseAppConfigurator() throws FileSystemException {
		if (appConfigurator != null) {
			try {
				appConfigurator.close();
			} catch (NotConfiguredException e) {
				// can't happen for our usage
				throw new RuntimeException(e);
			}
			appConfigurator = null;
		}
	}

	private void checkConfigured() throws NotConfiguredException {
		if (!configured)
			throw new NotConfiguredException("Node is not configured for Data Spaces");
	}

	private void checkNotConfigured() throws AlreadyConfiguredException {
		if (configured)
			throw new AlreadyConfiguredException("Node is already configured for Data Spaces");
	}
}
