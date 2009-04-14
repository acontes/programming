/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;

/**
 * Represents immutable configuration for a node. Produces configuration for
 * application by creating NodeApplicationConfigurator. Objects life cycle:
 * <ol>
 * <li>Instance initialization by default constructor.</li>
 * <li><code>configureNode</code> method call for passing node-specific and
 * immutable settings. This can be called only once for each instance.</li>
 * <li><code>configureApplication</code> method call for configuring application
 * on a node. This involves creation of {@link NodeApplicationConfigurator}.</li>
 * <li>Obtaining {@link NodeApplicationConfigurator} if needed.</li>
 * <li>Closing all composite objects by <code>close</code> method call.</li>
 * </ol>
 */
public class NodeConfigurator {

	private SpaceConfiguration scratchConfiguration;

	private boolean configured = false;

	private DefaultFileSystemManager manager = null;

	private NodeScratchSpace nodeScratchSpace;

	private NodeApplicationConfigurator appConfigurator = null;

	/**
	 * <ol>
	 * <li>Stores VFS manager</li>
	 * <li>Stores scratch SpaceConfiguration</li>
	 * <li>Initializes & tests scratch data space</li>
	 * </ol>
	 * 
	 * @param config
	 *            scratch data space configuration
	 * @throws IllegalStateException
	 *             when trying to reconfigure the instance
	 */
	synchronized public void configureNode(SpaceConfiguration config) throws IllegalStateException {
		if (configured)
			throw new IllegalStateException("This instance has been already configured");

		try {
			manager = VFSFactory.createDefaultFileSystemManager();
		} catch (FileSystemException e) {
			// TODO node cannot be used!
			e.printStackTrace();
		}

		scratchConfiguration = config;
		nodeScratchSpace = new NodeScratchSpace(config, manager);
		nodeScratchSpace.init();

		configured = true;
	}

	/**
	 * @param appid
	 * @param namingServiceURL
	 * @return
	 */
	synchronized public DataSpacesImpl configureApplication(long appid, String namingServiceURL) {
		tryCloseAppConfigurator();

		appConfigurator = new NodeApplicationConfigurator();
		return appConfigurator.configureApplication(appid, namingServiceURL, manager, nodeScratchSpace);
	}

	public NodeApplicationConfigurator getNodeApplicationConfigurator() {
		return appConfigurator;
	}

	/**
	 * Closes all components owned by this configurator.
	 */
	synchronized public void close() {
		tryCloseAppConfigurator();
	}

	/**
	 * Helper method for closing when needed.
	 */
	private void tryCloseAppConfigurator() {
		if (appConfigurator != null) {
			appConfigurator.close();
			appConfigurator = null;
		}
	}
}
