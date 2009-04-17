/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.extra.dataspaces.exceptions.AlreadyConfiguredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.WrongApplicationIdException;

/**
 * Maintains Data Spaces application-specific node configuration and its life
 * cycle, producing Data Spaces implementation, {@link DataSpacesImpl}.
 * 
 * Each instance can be configured only once. Objects life cycle is following:
 * <ol>
 * <li>Instance initialization by default constructor.</li>
 * <li>
 * {@link #configureApplication(long, String, DefaultFileSystemManager, NodeScratchSpace)}
 * method call for passing application specific settings. This can be called
 * only once.</li>
 * <li>Obtaining {@link DataSpacesImpl} when needed.</li>
 * <li>Closing all opened resources by calling {@link #close()} method.</li>
 * </ol>
 * 
 * Instances of this class are thread-safe. This class is intended to be created
 * and managed by higher-level {@link NodeConfigurator}.
 * 
 * @see NodeConfigurator
 * @see DataSpacesImpl
 */
public class NodeApplicationConfigurator {

	private SpacesMountManager spacesMountManager;

	private ApplicationScratchSpace applicationScratchSpace;

	private SpacesDirectory cachingDirectory;

	private NamingService namingService;

	private DataSpacesImpl impl;

	private SpaceInstanceInfo scratchInfo;

	/**
	 * Configures node for application, resulting in creation of configured
	 * {@link DataSpacesImpl}. Can be called only once per instance.
	 * 
	 * Configuration of a node for an application involves association to
	 * provided NamingService and registration of application scratch space for
	 * this node, if it exists.
	 * 
	 * @param appid
	 *            application id
	 * @param namingServiceURL
	 *            URL of naming service remote object for that application
	 * @param manager
	 *            VFS manager used to access data
	 * @param nodeScratchSpace
	 *            configured node scratch space, may be null if there is no
	 *            scratch space configured for this node
	 * @throws AlreadyConfiguredException
	 *             when trying to reconfigure node already configured for
	 *             application
	 */
	synchronized public void configureApplication(long appid, String namingServiceURL,
			DefaultFileSystemManager manager, NodeScratchSpace nodeScratchSpace)
			throws AlreadyConfiguredException {

		checkNotConfigured();

		// create naming service stub with URL and decorate it with local cache
		namingService = Utils.createNamingServiceStub(namingServiceURL);
		cachingDirectory = new CachingSpacesDirectory(namingService);

		// create scratch data space for this application and register it
		if (nodeScratchSpace != null) {
			applicationScratchSpace = nodeScratchSpace.initForApplication(appid);
			scratchInfo = applicationScratchSpace.getSpaceInstanceInfo();
			try {
				cachingDirectory.register(scratchInfo);
			} catch (WrongApplicationIdException e) {
				// FIXME think more about passing it
				throw new ProActiveRuntimeException("DataSpaces catched exception that should not occure", e);
			} catch (SpaceAlreadyRegisteredException e) {
				// FIXME think more about passing it
			}
		}

		// create SpacesMountManager
		spacesMountManager = new SpacesMountManager(manager, cachingDirectory);

		// create implementation object connected to the application's
		// configuration
		impl = new DataSpacesImpl(spacesMountManager, cachingDirectory, applicationScratchSpace, appid);
	}

	/**
	 * Cleans up application-related objects if they have been configured.
	 * 
	 * That involves unregistering application scratch space from NamingService
	 * and closing all objects configured for produced {@link DataSpacesImpl}.
	 * {@link DataSpacesImpl} will not be usable after this call.
	 * 
	 * More than one call of this method may result in undefined behavior.
	 * 
	 * @throws NotConfiguredException
	 *             when node has not been configured for application
	 */
	synchronized public void close() throws NotConfiguredException {
		checkConfigured();

		spacesMountManager.close();
		if (applicationScratchSpace != null) {
			cachingDirectory.unregister(scratchInfo.getMountingPoint());
			applicationScratchSpace.close();
		}
		Utils.closeNamingServiceStub(namingService);
	}

	/**
	 * Returns configured Data Spaces implementation for an application.
	 * 
	 * This instance is valid only until {@link #close()} is called.
	 * 
	 * @return configured Data Spaces implementation for an application
	 * @throws NotConfiguredException
	 *             when node has not been configured for application
	 */
	synchronized public DataSpacesImpl getDataSpacesImpl() throws NotConfiguredException {
		checkConfigured();

		return impl;
	}

	private void checkConfigured() throws NotConfiguredException {
		if (impl == null)
			throw new NotConfiguredException("Node is not configured for Data Spaces application");
	}

	private void checkNotConfigured() throws AlreadyConfiguredException {
		if (impl != null)
			throw new AlreadyConfiguredException("Node is already configured for Data Spaces application");
	}
}
