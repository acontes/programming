/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.net.URISyntaxException;

import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
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
	 * State of an instance remains not configured as exception appears.
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
	synchronized public void configureNode(ScratchSpaceConfiguration scratchConfiguration, Node node)
			throws AlreadyConfiguredException, FileSystemException, ConfigurationException {
		checkNotConfigured();

		manager = VFSFactory.createDefaultFileSystemManager();

		if (scratchConfiguration != null) {
			nodeScratchSpace = new NodeScratchSpace(scratchConfiguration);
			nodeScratchSpace.init(manager, node);
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
	 * If configuration fails any subsequent {@link #getDataSpaceImpl()} call
	 * will throw {@link NotConfiguredException} until successful configuration.
	 *
	 * @param appid
	 *            application id
	 * @param namingServiceURL
	 *            URL of naming service remote object for that application
	 * @throws URISyntaxException
	 *             when exception occurred on namingServiceURL parsing
	 * @throws ProActiveException
	 *             occurred during contacting the NamingService
	 * @throws FileSystemException
	 *             VFS related exception during scratch space creation
	 */
	synchronized public void configureApplication(long appid, String namingServiceURL)
			throws FileSystemException, ProActiveException, URISyntaxException {
		checkConfigured();
		tryCloseAppConfigurator();

		NodeApplicationConfigurator conf = new NodeApplicationConfigurator();
		try {
			appConfigurator.configureApplication(appid, namingServiceURL, manager, nodeScratchSpace);
			appConfigurator = conf;
		} catch (AlreadyConfiguredException e) {
			// can't happen for our usage
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns Data Spaces implementation for application, if it has been
	 * successfully configured.
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
	 * More than one call of this method may result in undefined behavior. Any
	 * subsequent call to node configuration-specific objects may be undefined.
	 */
	synchronized public void close() {
		tryCloseAppConfigurator();

		try {
			if (nodeScratchSpace != null) {
				nodeScratchSpace.close();
			}
		} catch (NotConfiguredException e) {
			// can't happen for our usage
			// TODO log or throw
		} catch (FileSystemException e) {
			// TODO log or throw
		}
		manager.close();
	}

	/**
	 * Closes application-specific configuration when needed (there is one
	 * opened).
	 * 
	 * If no application is configured, it does nothing. If closing fails,
	 * application-specific configuration will be silently deleted.
	 *
	 * @throws FileSystemException
	 *             VFS related exception during scratch space cleaning
	 */
	public synchronized void tryCloseAppConfigurator() {
		if (appConfigurator != null) {
			try {
				appConfigurator.close();
			} catch (NotConfiguredException e) {
				// can't happen for our usage
				// TODO log or throw
				throw new RuntimeException(e);
			} catch (FileSystemException e) {
				// TODO log
			} finally {
				appConfigurator = null;
			}
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
