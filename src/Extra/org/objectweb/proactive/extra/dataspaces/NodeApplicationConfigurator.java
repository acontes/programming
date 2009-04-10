/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.impl.DefaultFileSystemManager;

/**
 * Maintains configuration for application and its life cycle. Each instance can
 * be configured only once.
 */
public class NodeApplicationConfigurator {

	private SpacesMountManager spacesMountManager;

	private ApplicationScratchSpace applicationScratchSpace;

	private SpacesDirectory cachingDirectory;

	private NamingService namingService;

	private DataSpacesImpl impl;

	private SpaceInstanceInfo scratchInfo;

	private boolean configured = false;

	public static NamingService createNamingServiceStub(String url) {
		// TODO
		return new NamingService();
	}

	public static void closeNamingServiceStub(NamingService stub) {
		// TODO Auto-generated method stub
	}

	/**
	 * Builds application-related objects. Can be called only once per instance.
	 * Scenario:
	 * <ol>
	 * <li>Obtains NamingService stub and creates CachingSpacesDirectory for it</li>
	 * <li>Obtains ApplicationScratchSpace for a given <code>appid</code> from
	 * existing NodeScratchSpace.</li>
	 * <li>Obtains scratch SpaceInstanceInfo from ApplicationScratchSpace and
	 * registers it through CachingSpacesDirectory</li>
	 * <li>Creates SpacesMountManager with CachingSpacesDirectory and VFS
	 * manager</li>
	 * <li>Creates DataSpacesImpl for built configuration objects.</li>
	 * </ol>
	 * 
	 * @param appid
	 * @param namingServiceURL
	 * @param manager
	 * @param nodeScratchSpace
	 * @return
	 * @throws IllegalStateException
	 *             when trying to reconfigure the instance.
	 */
	public DataSpacesImpl configureApplication(long appid, String namingServiceURL,
			DefaultFileSystemManager manager, NodeScratchSpace nodeScratchSpace) throws IllegalStateException {

		if (configured)
			throw new IllegalStateException("This instance has been already configured");

		// create naming service stub with URL and decorate it with local cache
		namingService = NodeApplicationConfigurator.createNamingServiceStub(namingServiceURL);
		cachingDirectory = new CachingSpacesDirectory(namingService);

		// create scratch data space for this application and register it
		applicationScratchSpace = nodeScratchSpace.initForApplication(appid);
		scratchInfo = applicationScratchSpace.getSpaceInstanceInfo();
		cachingDirectory.register(scratchInfo);

		// create SpacesMountManager
		// TODO
		spacesMountManager = new SpacesMountManager();

		// create implementation object connected to the application's
		// configuration
		// TODO
		impl = new DataSpacesImpl();

		configured = true;
		return impl;
	}

	/**
	 * Cleans up application-related objects if the have been configured:
	 * <ol>
	 * <li>Closes SpacesMountManager.</li>
	 * <li>Unregisters scratch data space from the cache (and NamingService).</li>
	 * <li>Closes NamingService stub.</li>
	 * <li>Closes ApplicationScratchSpace.</li>
	 * </ol>
	 * 
	 * @throws IllegalStateException
	 *             when instance has not been configured.
	 */
	public void close() throws IllegalStateException {
		if (!configured)
			throw new IllegalStateException("This instance has not been configured");

		spacesMountManager.close();
		cachingDirectory.unregister(scratchInfo.getMountingPoint());
		NodeApplicationConfigurator.closeNamingServiceStub(namingService);
		applicationScratchSpace.close();
	}

	public DataSpacesImpl getDataSpaceImpl() {
		return impl;
	}
}
